package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plataformaSaude.model.RefreshToken;
import plataformaSaude.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    RefreshTokenService refreshTokenService;

    @Test
    void criarRefreshToken_DeveExcluirTokensAntigosESalvarNovo() {
        // arrange
        String username = "teste@teste.com";
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // act
        RefreshToken tokenGerado = refreshTokenService.criarRefreshToken(username);

        // assert
        verify(refreshTokenRepository).deleteByUsername(username);
        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken salvo = captor.getValue();
        assertThat(salvo.getUsername()).isEqualTo(username);
        assertThat(salvo.getToken()).isNotBlank();
        assertThat(salvo.getExpiracao()).isAfter(Instant.now());
    }

    @Test
    void validarRefreshToken_DeveRetornarTokenQuandoValido() {
        RefreshToken token = new RefreshToken("abc", "user", Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.findByToken("abc")).thenReturn(Optional.of(token));

        Optional<RefreshToken> result = refreshTokenService.validarRefreshToken("abc");

        assertThat(result).contains(token);
    }

    @Test
    void validarRefreshToken_DeveRetornarVazioQuandoExpirado() {
        RefreshToken token = new RefreshToken("abc", "user", Instant.now().minusSeconds(3600));
        when(refreshTokenRepository.findByToken("abc")).thenReturn(Optional.of(token));

        Optional<RefreshToken> result = refreshTokenService.validarRefreshToken("abc");

        assertThat(result).isEmpty();
    }

    @Test
    void validarRefreshToken_DeveRetornarVazioQuandoNaoExiste() {
        when(refreshTokenRepository.findByToken("abc")).thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.validarRefreshToken("abc");

        assertThat(result).isEmpty();
    }
}
