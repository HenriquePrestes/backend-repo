package plataformaSaude.dto;

// DTO para encapsular as métricas do dashboard
public class DashboardDTO {

    private long totalPacientes;
    private long totalMedicos;
    private long totalAgendamentos;
    private long agendamentosPendentes; // Ex: Status "AGENDADO"

    // Construtor
    public DashboardDTO(long totalPacientes, long totalMedicos, long totalAgendamentos, long agendamentosPendentes) {
        this.totalPacientes = totalPacientes;
        this.totalMedicos = totalMedicos;
        this.totalAgendamentos = totalAgendamentos;
        this.agendamentosPendentes = agendamentosPendentes;
    }

    public DashboardDTO() {
        
    }

    // Getters
    public long getTotalPacientes() { return totalPacientes; }
    public long getTotalMedicos() { return totalMedicos; }
    public long getTotalAgendamentos() { return totalAgendamentos; }
    public long getAgendamentosPendentes() { return agendamentosPendentes; }

    // Setters (opcionais, mas boa prática)
    public void setTotalPacientes(long totalPacientes) { this.totalPacientes = totalPacientes; }
    public void setTotalMedicos(long totalMedicos) { this.totalMedicos = totalMedicos; }
    public void setTotalAgendamentos(long totalAgendamentos) { this.totalAgendamentos = totalAgendamentos; }
    public void setAgendamentosPendentes(long agendamentosPendentes) { this.agendamentosPendentes = agendamentosPendentes; }
}