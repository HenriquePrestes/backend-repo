package plataformaSaude.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "medicos")
public class Medico extends Usuario {

    private String crm;
    
    @ManyToOne
    @JoinColumn(name = "especialidade_id")
    private Especialidade especialidade;
    
    private String foto;
    private int duracaoConsultaMinutos;

    @OneToMany(
            mappedBy = "medico",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<Horario> horariosTrabalho = new ArrayList<>();

    public Medico() {
        super();
    }

    public Medico(String nomeCompleto, String cpf, String email, String senha,
                  String celular, LocalDate dataNascimento, String crm, Especialidade especialidade,
                  String foto) {
        super(nomeCompleto, cpf, email, senha, celular, dataNascimento);
        this.crm = crm;
        this.especialidade = especialidade;
        this.foto = foto;
    }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }

    public Especialidade getEspecialidade() { return especialidade; }
    public void setEspecialidade(Especialidade especialidade) { this.especialidade = especialidade; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public int getDuracaoConsultaMinutos() {
        return duracaoConsultaMinutos;
    }

    public void setDuracaoConsultaMinutos(int duracaoConsultaMinutos) {
        this.duracaoConsultaMinutos = duracaoConsultaMinutos;
    }

    public List<Horario> getHorariosTrabalho() {
        return horariosTrabalho;
    }

    public void setHorariosTrabalho(List<Horario> horariosTrabalho) {
        this.horariosTrabalho = horariosTrabalho;
    }

    public void setId(long l) {
    }
}
