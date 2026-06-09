package plataformaSaude.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Funcionario extends Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cargo;
    private String departamento;
    private String foto;

    public Funcionario() {
        super();
    }

    public Funcionario(String nomeCompleto, String cpf, String email, String senha,
                      String celular, LocalDate dataNascimento, String cargo, String departamento, 
                      String foto) {
        super(nomeCompleto, cpf, email, senha, celular, dataNascimento);
        this.cargo = cargo;
        this.departamento = departamento;
        this.foto = foto != null ? foto : null;
    }

    public Long getId() {
        return id;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}

