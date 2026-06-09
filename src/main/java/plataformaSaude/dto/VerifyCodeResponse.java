// src/main/java/plataformaSaude/dto/VerifyCodeResponse.java
package plataformaSaude.dto;

public class VerifyCodeResponse {
    private String token;

    public VerifyCodeResponse() {}
    public VerifyCodeResponse(String token) { this.token = token; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}