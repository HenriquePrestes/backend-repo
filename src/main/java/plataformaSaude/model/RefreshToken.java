package plataformaSaude.model;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Instant expiracao;

    public RefreshToken() {}

    public RefreshToken(String mockRefreshToken, String mail) {}

    public RefreshToken(String token, String username, Instant expiracao) {
        this.token = token;
        this.username = username;
        this.expiracao = expiracao;
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Instant getExpiracao() { return expiracao; }
    public void setExpiracao(Instant expiracao) { this.expiracao = expiracao; }
}
