// src/main/java/plataformaSaude/model/Agendamento.java
package plataformaSaude.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

// +++ IMPORTAÇÕES ADICIONADAS +++
import plataformaSaude.Enum.StatusAgendamento; // Importando o Enum do pacote correto
import plataformaSaude.model.Medico;
import plataformaSaude.model.Paciente;


@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- CAMPO REMOVIDO ---
    // @NotBlank(message = "Nome do paciente é obrigatório")
    // @Column(nullable = false)
    // private String nomePaciente;

    // --- CAMPO REMOVIDO ---
    // @NotBlank(message = "Email é obrigatório")
    // @Email(message = "Email deve ser válido")
    // @Column(nullable = false)
    // private String email;

    // --- CAMPO REMOVIDO ---
    // @NotBlank(message = "Telefone é obrigatório")
    // @Column(nullable = false)
    // private String telefone;

    // +++ NOVO RELACIONAMENTO +++
    @NotNull(message = "Paciente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // --- CAMPO REMOVIDO (será obtido via médico) ---
    // @NotBlank(message = "Especialidade é obrigatória")
    // @Column(nullable = false)
    // private String especialidade;

    // --- CAMPO REMOVIDO ---
    // @NotBlank(message = "Nome do médico é obrigatório")
    // @Column(nullable = false)
    // private String nomeMedico;

    // +++ NOVO RELACIONAMENTO +++
    @NotNull(message = "Médico é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    // --- CAMPO RENOMEADO ---
    @NotNull(message = "Data de início da consulta é obrigatória")
    @Future(message = "Data da consulta deve ser no futuro")
    @Column(nullable = false)
    private LocalDateTime dataConsultaInicio; // Era 'dataConsulta'

    // +++ NOVO CAMPO +++
    @NotNull(message = "Data de fim da consulta é obrigatória")
    @Column(nullable = false)
    private LocalDateTime dataConsultaFim;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    @Column(updatable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    // Construtores
    public Agendamento() {}

    // --- CONSTRUTOR ATUALIZADO (opcional, mas recomendado) ---
    public Agendamento(Paciente paciente, Medico medico, LocalDateTime dataConsultaInicio, LocalDateTime dataConsultaFim) {
        this.paciente = paciente;
        this.medico = medico;
        this.dataConsultaInicio = dataConsultaInicio;
        this.dataConsultaFim = dataConsultaFim;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // --- GETTERS/SETTERS REMOVIDOS (nomePaciente, email, telefone, especialidade, nomeMedico) ---

    // +++ NOVOS GETTERS/SETTERS +++
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    // --- GETTER/SETTER RENOMEADO ---
    public LocalDateTime getDataConsultaInicio() { return dataConsultaInicio; }
    public void setDataConsultaInicio(LocalDateTime dataConsultaInicio) { this.dataConsultaInicio = dataConsultaInicio; }

    // +++ NOVO GETTER/SETTER +++
    public LocalDateTime getDataConsultaFim() { return dataConsultaFim; }
    public void setDataConsultaFim(LocalDateTime dataConsultaFim) { this.dataConsultaFim = dataConsultaFim; }


    // --- GETTERS/SETTERS EXISTENTES (observacoes, status, etc) ---
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    // Métodos do ciclo de vida do JPA
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now(); // Também é bom setar na criação
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}