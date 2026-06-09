package plataformaSaude.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plataformaSaude.model.RefreshToken;
import plataformaSaude.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken criarRefreshToken(String username) {
        // Remove refresh tokens antigos do mesmo usuário
        refreshTokenRepository.deleteByUsername(username);

        // Cria novo token
        RefreshToken novo = new RefreshToken(
                UUID.randomUUID().toString(),
                username,
                Instant.now().plusSeconds(60 * 60 * 24) // expira em 24h
        );
        return refreshTokenRepository.save(novo);
    }

    public Optional<RefreshToken> validarRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(t -> t.getExpiracao().isAfter(Instant.now()));
    }
}
