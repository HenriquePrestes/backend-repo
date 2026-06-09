package plataformaSaude.model;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "mfa_secret")
    private String mfaSecret;
    @NotBlank
    @Column(nullable = false)
    private String nomeCompleto;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String cpf;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;
    @NotBlank
    @Column(length = 100, nullable = false)
    private String senha;
    private String celular;
    private String cep;
    private String rua;
    private String cidade;
    private String estado;
    private String numero;
    private String complemento;
    private LocalDate dataNascimento;
    private LocalDate dataCadastro;
    private String resetPasswordToken;
    private LocalDate resetPasswordTokenExpiryDate;
    private String tipoUsuario;

    /**
     * Preferência do usuário para receber ou não notificações (ex: email).
     * true = SIM, false = NÃO, null = Não definido (padrão)
     */
    @Column(name = "notificacoes")
    private Boolean notificacoes;

    @Column(name = "tema_preferido")
    private String temaPreferido;

    public Usuario() {
        this.dataCadastro = LocalDate.now();
        this.tipoUsuario = determinarTipoUsuario();
    }

    public Usuario(String nomeCompleto, String cpf, String email, String senha, String celular, LocalDate dataNascimento) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.celular = celular;
        this.dataNascimento = dataNascimento;
        this.dataCadastro = LocalDate.now();
        this.tipoUsuario = determinarTipoUsuario();
    }

    private String determinarTipoUsuario() {
        if (this instanceof Medico) {
            return "MEDICO";
        } else if (this instanceof Paciente) {
            return "PACIENTE";
        }
        return "USER";
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + this.tipoUsuario;
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public LocalDate getResetPasswordTokenExpiryDate() {
        return resetPasswordTokenExpiryDate;
    }

    public void setResetPasswordTokenExpiryDate(LocalDate resetPasswordTokenExpiryDate) {
        this.resetPasswordTokenExpiryDate = resetPasswordTokenExpiryDate;
    }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public LocalDate getDataCadastro() { return dataCadastro; }

    public Long getId() {
        return id;
    }

    public Boolean getNotificacoes() {
        return notificacoes;
    }

    public String getTemaPreferido() {
        return this.temaPreferido;
    }

    public void setNotificacoes(Boolean notificacoes) {
        this.notificacoes = notificacoes;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    public void setTemaPreferido(String temaPreferido) {
        this.temaPreferido = temaPreferido;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.tipoUsuario = role;
    }

    public boolean isPresent() {
        return false;
    }
}
