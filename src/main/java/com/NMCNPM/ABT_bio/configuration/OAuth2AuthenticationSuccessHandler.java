package com.NMCNPM.ABT_bio.configuration;

//import com.example.MMOGrocery.utils.CookieUtils;
import com.NMCNPM.ABT_bio.dto.response.AuthenticationResponse;
import com.NMCNPM.ABT_bio.repository.UserIdentityRepository;
import com.NMCNPM.ABT_bio.service.AuthenticationService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthenticationService authenticationService;
    private final UserIdentityRepository userIdentityRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @PostConstruct
    public void init() {
        setDefaultTargetUrl(frontendUrl + "/login/google/callback");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
    ) throws IOException {

        try {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
            OAuth2User oAuth2User = oauthToken.getPrincipal();

            String email = oAuth2User.getName();

            if (email == null) {
                log.error("Email not found from OAuth2 provider");
                getRedirectStrategy().sendRedirect(
                        request,
                        response,
                        frontendUrl + "/login?error=email_not_found"
                );
                return;
            }

            AuthenticationResponse authResponse =
                    authenticationService.generateTokenPairForOAuth2(email, provider);

            String redirectUrl = frontendUrl + "/login/google/callback"
                    + "#accessToken=" + URLEncoder.encode(authResponse.getToken(), StandardCharsets.UTF_8)
                    + "&refreshToken=" + URLEncoder.encode(authResponse.getRefreshToken(), StandardCharsets.UTF_8);

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 authentication success handler error", e);
            getRedirectStrategy().sendRedirect(
                    request,
                    response,
                    frontendUrl + "/login?error=authentication_failed"
            );
        }
    }
}
