package com.NMCNPM.ABT_bio.configuration;

import com.NMCNPM.ABT_bio.service.AuthenticationService;
import com.NMCNPM.ABT_bio.service.CustomOAuth2UserService;
import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
        @Value("${app.frontend.url}")
        @NonFinal
        protected String frontEndUrl;

        private final CustomJwtDecoder customJwtDecoder;
        private final CustomOAuth2UserService customOAuth2UserService;
        private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

        String[] PUBLIC_ENDPOINT = {
                        // Auth endpoints
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/forgot-password",
                        "/api/auth/change-forgot",
                        "/api/auth/verify",
                        "/api/auth/resend-verification",
                        "/oauth2/**",

                        // Product & Category Public endpoints
                        "/api/categories", // Xem danh sách danh mục
                        "/api/products/best-selling", // Xem top bán chạy
//                        "/api/products", // Xem danh sách sản phẩm (Search)
                        "/api/shopdetail/{id}"
        };

        String[] ORIGIN = {
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:5173",
                "https://manix-ai.vercel.app",
                "https://bryn-isolating-progressively.ngrok-free.dev"
        };
        @Bean
        public TokenBlacklistFilter tokenBlacklistFilter(AuthenticationService authenticationService) {
                return new TokenBlacklistFilter(authenticationService);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenBlacklistFilter tokenBlacklistFilter) throws Exception {
                // trả 503
//                http.addFilterBefore(maintenanceFilter(), org.springframework.security.web.session.DisableEncodeUrlFilter.class);

                http.cors(Customizer.withDefaults());
                http.csrf(AbstractHttpConfigurer::disable);

                http.authorizeHttpRequests(request -> request
                                .requestMatchers(PUBLIC_ENDPOINT).permitAll()
                                .requestMatchers("/api/auth/register").permitAll()
                                .requestMatchers("/api/auth/forgot-password").permitAll()
                                .requestMatchers("/api/auth/login").permitAll()
                                .requestMatchers("/api/auth/change-forgot").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                                .requestMatchers("/api/auth/verify").permitAll()
                                .requestMatchers("/api/auth/resend-verification").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()

                                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/search/best-selling").permitAll()

                                .requestMatchers(HttpMethod.GET, "/api/products/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/products/popular").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/blog").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/blog/**").permitAll()

                                .requestMatchers(HttpMethod.GET, "/api/seller-public/{sellerId}/products").permitAll()

                                .requestMatchers(HttpMethod.GET, "/api/shops/**").permitAll()

                                // Cho phép Webhook truy cập không cần đăng nhập
                                .requestMatchers("/api/payments/webhook/**").permitAll()
                                // Cho phép API thanh toán trả về (nếu có)
                                .requestMatchers("/api/payments/payos/**", "/api/payments/paypal/**").authenticated()

                                .requestMatchers(HttpMethod.GET, "/api/admin/category/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/categories/best-selling-products").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/banks").permitAll()
                                .requestMatchers("/oauth2/**").permitAll()
                                // Cho phép Actuator Health Check
                                .requestMatchers("/actuator/**").permitAll()
                                // Client error reporting (FE gửi lỗi về, không cần auth)
                                .requestMatchers(HttpMethod.POST, "/api/client-errors").permitAll()
                                .anyRequest().authenticated());

                http.oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                                .userService(customOAuth2UserService))
                                .successHandler(oAuth2AuthenticationSuccessHandler));

                // JWT Resource Server
                http.oauth2ResourceServer(oauth2 -> oauth2
                                .bearerTokenResolver(bearerTokenResolver())
                                .jwt(jwtConfigurer -> jwtConfigurer
                                                .decoder(customJwtDecoder)
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

                http.addFilterBefore(tokenBlacklistFilter,
                        org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter.class);

                http.exceptionHandling(
                                exception -> exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

                return http.build();
        }

        @Bean
        public BearerTokenResolver bearerTokenResolver() {
                DefaultBearerTokenResolver resolver = new DefaultBearerTokenResolver();

                return request -> {
                        String path = request.getRequestURI();
                        String method = request.getMethod();

                        if (path.startsWith("/api/auth/change-password") ||
                                        path.startsWith("/api/auth/logout") ||
                                        path.startsWith("/api/auth/me") ||

                                        path.startsWith("/api/admin") ||

                                        path.startsWith("/api/blog/admin") ||
                                        path.startsWith("/api/blog/my-posts") ||

                                        method.equals("POST") && path.startsWith("/api/banks") ||

                                        path.startsWith("/api/notifications") ||

                                        path.startsWith("/api/payments")) {
                                return resolver.resolve(request);
                        }

                        // ===== AUTH PUBLIC =====
                        if (path.equals("/api/auth/register") ||
                                        path.equals("/api/auth/login") ||
                                        path.equals("/api/auth/forgot-password") ||
                                        path.equals("/api/auth/change-forgot") ||
                                        path.equals("/api/auth/verify") ||
                                        path.equals("/api/auth/resend-verification") ||
                                        path.equals("/api/auth/refresh")) {
                                return null;
                        }

                        // ===== PUBLIC PRODUCTS & CATEGORY =====
                        if ((method.equals("GET") && path.startsWith("/api/categories")) ||
                                        (method.equals("GET") && path.startsWith("/api/category/")) ||
                                        (method.equals("GET") && path.equals("/api/products")) ||
                                        (method.equals("GET") && path.equals("/api/products/")) ||
                                        (method.equals("GET") && path.startsWith("/api/products/best-selling")) ||
                                        (method.equals("GET") && path.startsWith("/api/products/popular")) ||
                                        (method.equals("GET") && path.startsWith("/api/blog")) ||
                                        (method.equals("GET") && path.startsWith("/api/banks")) ||
                                        (method.equals("GET") && path.startsWith("/api/seller-public")) ||
                                        (method.equals("GET") && path.startsWith("/api/seller-public/")) ||
                                        (method.equals("GET") && path.startsWith("/api/shops"))
                        ) {
                                return null;
                        }

                        // ===== PAYMENT WEBHOOK =====
                        if (path.startsWith("/api/payments/webhook")) {
                                return null;
                        }

                        // ===== OAUTH2 & ACTUATOR & CLIENT-ERRORS =====
                        if (path.startsWith("/oauth2") ||
                                        path.startsWith("/actuator") ||
                                        path.equals("/api/client-errors")) {
                                return null;
                        }

                        // ===== CÁC API KHÁC → xử lý token bình thường =====
                        return resolver.resolve(request);
                };
        }

        @Bean // set lại tên SCOPE_ -> ROLE_
        JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

                JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

                return jwtAuthenticationConverter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(10);
        }

//        @Bean
//        public jakarta.servlet.Filter maintenanceFilter() {
//                return (request, response, chain) -> {
//                        jakarta.servlet.http.HttpServletResponse httpResponse = (jakarta.servlet.http.HttpServletResponse) response;
//                        httpResponse.setStatus(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE.value());
//                        httpResponse.setContentType("application/json;charset=UTF-8");
//                        httpResponse.getWriter().write("{\"code\": 503, \"message\": \"Hệ thống đang bảo trì để cập nhật. Vui lòng quay lại sau!\"}");
//                };
//        }

        @Bean
        public CorsFilter corsFilter() {
                CorsConfiguration corsConfiguration = new CorsConfiguration();

                Arrays.stream(ORIGIN).forEach(corsConfiguration::addAllowedOrigin);
                // corsConfiguration.addAllowedOriginPattern("*");
                corsConfiguration.addAllowedOrigin(frontEndUrl);
                corsConfiguration.setAllowCredentials(true);
                corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                corsConfiguration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();

                urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

                return new CorsFilter(urlBasedCorsConfigurationSource);
        }

}