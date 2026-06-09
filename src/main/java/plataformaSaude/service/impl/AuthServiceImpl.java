package plataformaSaude.service.impl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import plataformaSaude.service.AuthService;
import plataformaSaude.service.UsuarioService;
import plataformaSaude.model.Usuario;

import java.time.Instant;
import java.time.LocalDate;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioService usuarioService;
    private final JwtEncoder jwtEncoder;

    @Value("${app.reset-token.issuer:MedFast}")
    private String issuer;

    // token válido por 15 minutos
    private static final long EXPIRATION_SECONDS = 15 * 60;

    public AuthServiceImpl(UsuarioService usuarioService, JwtEncoder jwtEncoder) {
        this.usuarioService = usuarioService;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    @Transactional
    public String verifyCodeAndGenerateToken(String email, String code) {
        if (email == null || email.isBlank() || code == null || code.isBlank()) {
            throw new RuntimeException("Email e código são obrigatórios.");
        }

        // busca o usuário e valida token/expiração
        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        String token = usuario.getResetPasswordToken();
        LocalDate expiry = usuario.getResetPasswordTokenExpiryDate();

        if (token == null || !token.equals(code) || expiry == null || expiry.isBefore(LocalDate.now())) {
            throw new RuntimeException("Código inválido ou expirado.");
        }

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRATION_SECONDS))
                .subject(email)
                .claim("reset_password", true)
                .build();

        // Invalida o token no banco para evitar reuso: busca o usuário novamente, remove e salva
        Usuario u = usuarioService.buscarPorEmail(email);
        if (u != null) {
            u.setResetPasswordToken(null);
            u.setResetPasswordTokenExpiryDate(null);
            usuarioService.salvar(u);
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}