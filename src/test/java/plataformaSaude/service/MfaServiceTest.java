package plataformaSaude.service;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MfaServiceTest {

    @Mock
    GoogleAuthenticator googleAuthenticator;

    @InjectMocks
    MfaService mfaService;

    @Test
    void generateSecret_DeveGerarSecret() {
        // arrange
        GoogleAuthenticatorKey keyMock = mock(GoogleAuthenticatorKey.class);
        when(keyMock.getKey()).thenReturn("SECRET123");
        when(googleAuthenticator.createCredentials()).thenReturn(keyMock);

        // act
        String secret = mfaService.generateSecret();

        // assert
        assertThat(secret).isEqualTo("SECRET123");
        verify(googleAuthenticator).createCredentials();
    }

    @Test
    void getQrCodeURL_DeveGerarURLCorreta() {
        String url = mfaService.getQrCodeURL("ABC", "email@test.com", "MinhaClinica");

        assertThat(url).contains("otpauth://totp/");
        assertThat(url).contains("ABC");
        assertThat(url).contains("email%40test.com"); // email encoded
    }

    @Test
    void verifyCode_DeveRetornarFalse_QuandoSecretVazio() {
        boolean result = mfaService.verifyCode("", "123456");
        assertThat(result).isFalse();
    }

    @Test
    void verifyCode_DeveRetornarFalse_QuandoCodigoInvalido() {
        boolean result = mfaService.verifyCode("SECRET", "abc"); // não é dígito 6
        assertThat(result).isFalse();
    }

    @Test
    void verifyCode_DeveChamarGoogleAuthenticator() {
        // arrange
        when(googleAuthenticator.authorize("SECRET", 123456)).thenReturn(true);

        // act
        boolean result = mfaService.verifyCode("SECRET", "123456");

        // assert
        assertThat(result).isTrue();
        verify(googleAuthenticator).authorize("SECRET", 123456);
    }
}
