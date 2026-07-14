package com.NMCNPM.ABT_bio.configuration;

import com.nimbusds.jose.JOSEException;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${app.jwt.signerKey}")
    private String SIGNER_KEY;

//    @Value("${spring.security.oauth2.client.provider.google.jwk-set-uri}")
//    private String googleJwkSetUri;
    //
//    @Value("${spring.security.oauth2.client.provider.google.issuer-uri}")
//    private String googleIssuerUri;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @PostConstruct
    public void init() {
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(SIGNER_KEY.getBytes(), "HmacSHA512");

        nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        return nimbusJwtDecoder.decode(token);
    }
}
