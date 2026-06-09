package plataformaSaude.service;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class MfaService {

    private final GoogleAuthenticator gAuth;

    public MfaService(GoogleAuthenticator gAuth) {
        this.gAuth = gAuth;
    }

    /* Gera um secret único para o usuário*/
    public String generateSecret() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public String getQrCodeURL(String secret, String email, String issuer) {
        try {
            String encodedIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

            return "otpauth://totp/" + encodedIssuer + ":" + encodedEmail +
                    "?secret=" + secret +
                    "&issuer=" + encodedIssuer +
                    "&algorithm=SHA1&digits=6&period=30";
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar URL do QR Code", e);
        }
    }

    /* Verifica se o código TOTP informado é válido.*/
    public boolean verifyCode(String secret, String code) {
        if (secret == null || secret.isBlank()) return false;
        if (code == null || code.isBlank()) return false;
        if (!code.matches("\\d{6}")) return false;

        int intCode = Integer.parseInt(code);
        return gAuth.authorize(secret, intCode);
    }
}
