// src/main/java/plataformaSaude/service/AuthService.java
package plataformaSaude.service;

public interface AuthService {
    /**
     * Valida o código de 6 dígitos enviado ao usuário e gera um token JWT curto
     * usado para autorizar a redefinição de senha.
     * Lança RuntimeException em caso de falha.
     */
    String verifyCodeAndGenerateToken(String email, String code);
}