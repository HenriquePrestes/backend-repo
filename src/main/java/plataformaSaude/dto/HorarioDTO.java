// src/main/java/plataformaSaude/dto/HorarioDTO.java
package plataformaSaude.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO para o Admin enviar os dados de uma janela de trabalho.
 */
public class HorarioDTO {

    private String descricao; // ex: "Turno da Manhã"
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    // Getters e Setters
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DayOfWeek diaSemana) { this.diaSemana = diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
}