package plataformaSaude.security;
import org.owasp.html.PolicyFactory;
import org.owasp.html.HtmlPolicyBuilder;
/**
 * Classe utilitária para limpar (sanitizar) inputs de texto do usuário,
 * prevenindo ataques de XSS.
 * Utiliza a biblioteca OWASP Java HTML Sanitizer.
 */
public class HtmlSanitizerUtil {
    /**
     * Define a política de sanitização.
     * Criamos uma política que não permite NENHUMA tag (REJECT_ALL).
     * Isso é o equivalente ao 'Sanitizers.NONE' que estava falhando.
     */
    private static final PolicyFactory POLICY = new HtmlPolicyBuilder().toFactory();
    /**
     * Limpa uma string de entrada, removendo todas as tags HTML perigosas.
     *
     * @param untrustedInput A string vinda do usuário (ex: DTO)
     * @return A string segura (limpa)
     */
    public static String sanitize(String untrustedInput) {
        if (untrustedInput == null) {
            return null;
        }
        // Aplica a política de sanitização
        return POLICY.sanitize(untrustedInput);
    }
}