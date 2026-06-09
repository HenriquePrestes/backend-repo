package plataformaSaude.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    private final JwtEncoder jwtEncoder;

    public HomeController(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder; // Injetado pelo Spring
    }

    @GetMapping("/home")
    public ResponseEntity<Map<String, String>> home(@AuthenticationPrincipal OidcUser oidcUser) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self") // Identifica o emissor do token
                .issuedAt(now) // Data de emissão
                .expiresAt(now.plusSeconds(3600)) // Expira em 1 hora
                .subject(oidcUser.getSubject()) // ID único do usuário
                .claim("email", oidcUser.getEmail()) // Email do usuário
                .claim("name", oidcUser.getFullName()) // Nome completo
                .build();

        String jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(); // Gera o JWT assinado

        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome " + oidcUser.getFullName());
        response.put("jwt", jwt);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public String root() {
        return "Bem-vindo à página inicial!";
    }
}