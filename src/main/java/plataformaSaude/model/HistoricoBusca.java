package plataformaSaude.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/* Entidade para registrar o histórico de buscas feitas pelos usuários.*/
@Entity
@Table(name = "historico_buscas")
public class HistoricoBusca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Termo que o usuário digitou
    @Column(nullable = false)
    private String termoBuscado;

    // Opcional: Para qual tipo de entidade foi a busca (ex: "MEDICO", "AGENDAMENTO")
    private String tipoEntidade;

    // O usuário que realizou a busca (se estava logado)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id") // Linka com a tabela Usuario
    private Usuario usuario; // Assume que você tem a entidade Usuario.java

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataBusca;

    // Construtores
    public HistoricoBusca() {
        this.dataBusca = LocalDateTime.now();
    }

    public HistoricoBusca(String termoBuscado, String tipoEntidade, Usuario usuario) {
        this(); // Chama o construtor padrão para setar a dataBusca
        this.termoBuscado = termoBuscado;
        this.tipoEntidade = tipoEntidade;
        this.usuario = usuario;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTermoBuscado() { return termoBuscado; }
    public void setTermoBuscado(String termoBuscado) { this.termoBuscado = termoBuscado; }

    public String getTipoEntidade() { return tipoEntidade; }
    public void setTipoEntidade(String tipoEntidade) { this.tipoEntidade = tipoEntidade; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getDataBusca() { return dataBusca; }
    public void setDataBusca(LocalDateTime dataBusca) { this.dataBusca = dataBusca; }
}