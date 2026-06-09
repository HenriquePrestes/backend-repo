package plataformaSaude.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import plataformaSaude.Enum.StatusAgendamento;
import plataformaSaude.dto.DashboardDTO;
import plataformaSaude.repository.AgendamentoRepository;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;

@Service
public class DashboardService {

    @Autowired
    private PacienteRepository pacienteRepository; //

    @Autowired
    private MedicoRepository medicoRepository; //

    @Autowired
    private AgendamentoRepository agendamentoRepository; //

    /**
     * Coleta todas as métricas simples para o dashboard.
     */
    public DashboardDTO getMetrics() {

        // 1. Contar pacientes (usando método padrão do JpaRepository)
        long totalPacientes = pacienteRepository.count();

        // 2. Contar médicos (usando método padrão do JpaRepository)
        long totalMedicos = medicoRepository.count();

        // 3. Contar total de agendamentos (usando método padrão do JpaRepository)
        long totalAgendamentos = agendamentoRepository.count();

        // 4. Contar agendamentos pendentes (usando o novo método que criamos)
        //
        long agendamentosPendentes = agendamentoRepository.countByStatus(StatusAgendamento.AGENDADO);

        // 5. Montar e retornar o DTO
        // (Este DTO foi definido no Passo 1)
        return new DashboardDTO(
                totalPacientes,
                totalMedicos,
                totalAgendamentos,
                agendamentosPendentes
        );
    }
}