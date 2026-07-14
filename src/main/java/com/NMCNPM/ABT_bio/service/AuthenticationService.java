package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.dto.request.*;
import com.NMCNPM.ABT_bio.dto.response.AuthUserProjectionResponse;
import com.NMCNPM.ABT_bio.dto.response.AuthenticationResponse;
import com.NMCNPM.ABT_bio.dto.response.IntrospectResponse;
import com.NMCNPM.ABT_bio.entity.InvalidatedToken;
import com.NMCNPM.ABT_bio.entity.UserIdentity;
import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.enums.IdentityProviderEnum;
import com.NMCNPM.ABT_bio.enums.RoleEnum;
import com.NMCNPM.ABT_bio.enums.UserStatusEnum;
import com.NMCNPM.ABT_bio.exception.AppException;
import com.NMCNPM.ABT_bio.exception.ErrorCode;
import com.NMCNPM.ABT_bio.repository.InvalidTokenRepository;
import com.NMCNPM.ABT_bio.repository.UserIdentityRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    InvalidTokenRepository invalidatedTokenRepository;
    UserIdentityRepository userIdentityRepository;
    UserRepository userRepository;

    @NonFinal
    @Value("${app.jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${app.jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${app.jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Tìm UserIdentity (không dùng projection)
        UserIdentity identity = userIdentityRepository
                .findByProviderAndProviderUserId(IdentityProviderEnum.LOCAL, request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Users user = identity.getUser();

        // Check status
        if (user.getStatus() == UserStatusEnum.BANNED) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        boolean authenticated = false;

        if (identity.getPasswordHash() != null) {
            authenticated = passwordEncoder.matches(request.getPassword(), identity.getPasswordHash());
        }

        if (!authenticated) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        // Generate tokens
        AuthUserProjectionResponse authUser = buildAuthUserProjection(identity);
        var accessToken = generateAccessToken(authUser, identity.getProviderUserId(), "LOCAL");
        var refreshToken = generateRefreshToken(authUser, identity.getProviderUserId(), "LOCAL");

        return AuthenticationResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .isAuthenticated(true)
                .build();
    }

    @Transactional
    public void register(RegisterRequest request) {
        // Check trùng email
        if (userIdentityRepository.existsByProviderAndProviderUserId(IdentityProviderEnum.LOCAL, request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Tìm Role mặc định (USER)
        RoleEnum role = RoleEnum.USER;

        // Tạo User trước
        Users newUser = Users.builder()
                .fullName(request.getFullName())
                .contactEmail(request.getEmail())
                .status(UserStatusEnum.ACTIVE)
                .role(role)
                .verified(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // Lưu user để có id
        newUser = userRepository.save(newUser);

        // Tạo Identity
        UserIdentity identity = UserIdentity.builder()
                .user(newUser)
                .provider(IdentityProviderEnum.LOCAL)
                .providerUserId(request.getEmail())
                .email(request.getEmail())
                .verified(true)
                .createdAt(Instant.now())
                .build();
        userIdentityRepository.save(identity);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        UserIdentity identity = userIdentityRepository
                .findByProviderAndProviderUserId(IdentityProviderEnum.LOCAL, email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), identity.getPasswordHash())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        // Đổi mật khẩu mới
        identity.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        userIdentityRepository.save(identity);
    }

    public AuthUserProjectionResponse getMyProfile(String email) {
        UserIdentity identity = userIdentityRepository
                .findByProviderAndProviderUserId(IdentityProviderEnum.LOCAL, email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return buildAuthUserProjection(identity);
    }

    private AuthUserProjectionResponse buildAuthUserProjection(UserIdentity identity) {
        Users user = identity.getUser();

        return AuthUserProjectionResponse.builder()
                .userId(user.getId())
                .email(identity.getEmail())
                .role(user.getRole().toString())
                .status(user.getStatus())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .contactEmail(user.getContactEmail())
                .build();
    }

    private String generateAccessToken(AuthUserProjectionResponse user, String providerUserId, String provider) {
        return generateToken(user, providerUserId, provider, "access", VALID_DURATION);
    }

    private String generateRefreshToken(AuthUserProjectionResponse user, String providerUserId, String provider) {
        return generateToken(user, providerUserId, provider, "refresh", REFRESHABLE_DURATION);
    }

    private String generateToken(AuthUserProjectionResponse user, String providerUserId, String provider, String type, long duration) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(providerUserId)
                .issuer("nhom5.dev")
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(duration, ChronoUnit.SECONDS)))
                .jwtID(UUID.randomUUID().toString())
                .claim("userId", user.getUserId().toString())
                .claim("scope", buildScope(user))
                .claim("status", user.getStatus().toString())
                .claim("type", type)
                .claim("provider", provider)
                .claim("contactEmail", user.getContactEmail())
                .claim("fullName", user.getFullName())
                .claim("shopId", user.getShopId() != null ? user.getShopId().toString() : null)
                .build();

        JWSObject jws = new JWSObject(header, new Payload(claims.toJSONObject()));
        try {
            jws.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jws.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(AuthUserProjectionResponse user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (user.getRole() != null) {
            stringJoiner.add("ROLE_" + user.getRole());
        }
        return stringJoiner.toString();
    }

    // Token validation methods remain the same
    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh) ?
                new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                        .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli()) :
                signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(jwsVerifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        return signedJWT;
    }

    public void logout(LogoutRequest request) {
        try {
            var jwtToken = verifyToken(request.getToken(), true);
            String jti = jwtToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = jwtToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jti)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (Exception e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Transactional
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);
        var claims = signedJWT.getJWTClaimsSet();

        if (!"refresh".equals(claims.getStringClaim("type"))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

//        invalidatedTokenRepository.save(InvalidatedToken.builder()
//                .id(claims.getJWTID())
//                .expiryTime(claims.getExpirationTime())
//                .build());

        invalidatedTokenRepository.insertIgnore(
                claims.getJWTID(),
                claims.getExpirationTime()
        );

        var email = claims.getSubject();
        var provider = claims.getStringClaim("provider");

        IdentityProviderEnum providerEnum;
        try {
            providerEnum = IdentityProviderEnum.valueOf(provider);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        UserIdentity identity = userIdentityRepository
                .findByProviderAndProviderUserId(providerEnum, email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (identity.getUser().getStatus() == UserStatusEnum.BANNED)
            throw new AppException(ErrorCode.UNAUTHORIZED);

        AuthUserProjectionResponse authUser = buildAuthUserProjection(identity);
        var newAccessToken = generateAccessToken(authUser, identity.getProviderUserId(), provider);
        var newRefreshToken = generateRefreshToken(authUser, identity.getProviderUserId(), provider);

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .isAuthenticated(true)
                .build();
    }

    public AuthenticationResponse generateTokenPairForOAuth2(String email, String provider) {
        var authUser = userIdentityRepository.findAuthUserByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (authUser.getStatus() == UserStatusEnum.BANNED)
            throw new AppException(ErrorCode.UNAUTHORIZED);

        var accessToken = generateAccessToken(authUser, email, provider);
        var refreshToken = generateRefreshToken(authUser, email, provider);

        return AuthenticationResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .isAuthenticated(true)
                .build();
    }

    public boolean isTokenValid(String token) {
        try {
            // introspect kiểm tra signature + expiry + blacklist
            return introspect(IntrospectRequest.builder()
                    .token(token)
                    .build()
            ).isValid();
        } catch (Exception e) {
            return false;
        }
    }
}