package com.NMCNPM.ABT_bio.service;

import com.NMCNPM.ABT_bio.entity.UserIdentity;
import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.enums.IdentityProviderEnum;
import com.NMCNPM.ABT_bio.enums.RoleEnum;
import com.NMCNPM.ABT_bio.enums.UserStatusEnum;
import com.NMCNPM.ABT_bio.repository.UserIdentityRepository;
import com.NMCNPM.ABT_bio.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    UserRepository userRepository;
    UserIdentityRepository userIdentityRepository;

    AuthenticationService authenticationService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // Gọi hàm của DefaultOAuth2UserService để lấy thông tin từ Google
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("Google attributes: {}", oAuth2User.getAttributes());

        // Lấy provider name (google, facebook, etc.)
        String providerName = userRequest.getClientRegistration().getRegistrationId();
        IdentityProviderEnum provider = IdentityProviderEnum.valueOf(providerName.toUpperCase());

        // Lấy thông tin từ Google
        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatar = oAuth2User.getAttribute("picture");

        log.info("Email: {}, Name: {}, Provider: {}", email, name, provider);

        if (name == null) name = "User-" + providerId;

        // Kiểm tra xem user đã tồn tại trong DB chưa
        Optional<UserIdentity> userAuth =
                userIdentityRepository.findByProviderAndProviderUserId(provider, providerId);

        UserIdentity userIdentity;

        if (userAuth.isEmpty()) {

            // Tạo user mới nếu chưa tồn tại
            Users newUser = Users.builder()
                    .contactEmail(email) // Email có thể null
                    .contactPhone(null)
                    .fullName(name)
                    .avatarUrl(avatar)
                    .role(RoleEnum.USER)
                    .status(UserStatusEnum.ACTIVE) // Set status mặc định
                    .verified(true) // Google user coi như đã verify email
                    .build();

            userRepository.save(newUser);

            userIdentity = UserIdentity.builder()
                    .provider(provider)
                    .providerUserId(providerId)
                    .email(email) // Lưu email vào identity để đối chiếu
                    .user(newUser)
                    .verified(true)
                    .build();

            userIdentityRepository.save(userIdentity);

            log.info("New user created from OAuth2:: {}", email);
        } else {
            // Update thông tin nếu cần (ví dụ avatar mới)
            userIdentity = userAuth.get();
            userIdentity.getUser().setAvatarUrl(avatar);
            userIdentity.setEmail(email); // Cập nhật lại email phòng khi user đổi email Google
            userIdentityRepository.save(userIdentity);

            log.info("User already exists, updated avatar: {}", email);
        }

        return oAuth2User;
    }
}
