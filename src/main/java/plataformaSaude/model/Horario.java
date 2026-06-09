package plataformaSaude.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalTime;
import java.time.DayOfWeek; // IMPORTAR

@Entity
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Descrição opcional, como "Turno da Manhã"
    private String descricao;

    // --- MUDANÇA AQUI ---
    // Usar Enum é mais robusto que String
    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana;

    private LocalTime horaInicio;

    private LocalTime horaFim;

    // --- NOVO RELACIONAMENTO ---
    // Ligação para qual médico este horário pertence
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    @JsonBackReference
    private Medico medico;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    // --- GETTER/SETTER ATUALIZADO ---
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DayOfWeek diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }
}