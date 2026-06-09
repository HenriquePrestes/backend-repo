package plataformaSaude.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import plataformaSaude.dto.AgendamentoDTO;
import plataformaSaude.Enum.StatusAgendamento;
import plataformaSaude.model.Agendamento;
import plataformaSaude.model.BloqueioHorario;
import plataformaSaude.model.Medico;
import plataformaSaude.model.Paciente;
import plataformaSaude.repository.AgendamentoRepository;
import plataformaSaude.repository.BloqueioHorarioRepository;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plataformaSaude.security.HtmlSanitizerUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // +++ NOVAS INJEÇÕES +++
    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private BloqueioHorarioRepository bloqueioHorarioRepository;

    // CREATE - Criar novo agendamento
    public Agendamento criarAgendamento(AgendamentoDTO agendamentoDTO) {

        // 0. Validar se a data não é do passado
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataConsulta = agendamentoDTO.getDataConsultaInicio();
        
        if (dataConsulta.isBefore(agora)) {
            throw new RuntimeException("Não é possível agendar consultas para o passado");
        }

        // 1. Buscar as entidades principais
        Paciente paciente = pacienteRepository.findById(agendamentoDTO.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Medico medico = medicoRepository.findById(agendamentoDTO.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        // 2. Calcular datas início e fim
        LocalDateTime inicio = agendamentoDTO.getDataConsultaInicio();
        int duracao = medico.getDuracaoConsultaMinutos();
        if (duracao <= 0) {
            throw new RuntimeException("Médico não possui duração de consulta configurada.");
        }
        LocalDateTime fim = inicio.plusMinutes(duracao);

        // 3. Validar conflito de horário (a nova lógica)
        if (existeConflitoHorario(null, medico.getId(), inicio, fim)) {
            throw new RuntimeException("Horário indisponível. Já existe um agendamento ou bloqueio neste período.");
        }

        // 4. Criar a entidade
        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setMedico(medico);
        agendamento.setDataConsultaInicio(inicio);
        agendamento.setDataConsultaFim(fim);
        //agendamento.setObservacoes(agendamentoDTO.getObservacoes());
        String observacoesLimpas = HtmlSanitizerUtil.sanitize(agendamentoDTO.getObservacoes());
        agendamento.setObservacoes(observacoesLimpas);
        agendamento.setStatus(StatusAgendamento.AGENDADO); // Define o padrão

        return agendamentoRepository.save(agendamento);
    }

    // READ - Listar todos os agendamentos
    public Page<Agendamento> listarTodos(Pageable pageable) {
        // Usa o método findAll(Pageable) do JpaRepository
        return agendamentoRepository.findAll(pageable);
    }

    // READ - Listar todos os agendamentos com relacionamentos carregados
    public List<Agendamento> listarTodosComRelacionamentos() {
        return agendamentoRepository.findAllWithRelationships();
    }

    // READ - Buscar agendamento por ID
    public Optional<Agendamento> buscarPorId(Long id) {
        return agendamentoRepository.findById(id);
    }

    // READ - Buscar agendamentos futuros
    public Page<Agendamento> listarFuturos(Pageable pageable) {
        // Usa o novo método paginado do Repository
        return agendamentoRepository.findAgendamentosFuturos(LocalDateTime.now(), pageable);
    }

    // UPDATE - Atualizar agendamento
    public Agendamento atualizarAgendamento(Long id, AgendamentoDTO agendamentoDTO) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));

        // 1. Buscar entidades
        Paciente paciente = pacienteRepository.findById(agendamentoDTO.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        Medico medico = medicoRepository.findById(agendamentoDTO.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        // 2. Calcular novas datas
        LocalDateTime inicio = agendamentoDTO.getDataConsultaInicio();
        int duracao = medico.getDuracaoConsultaMinutos();
        LocalDateTime fim = inicio.plusMinutes(duracao);

        // 3. Validar conflito (excluindo o ID do próprio agendamento)
        if (existeConflitoHorario(id, medico.getId(), inicio, fim)) {
            throw new RuntimeException("Horário indisponível. Já existe um agendamento ou bloqueio neste período.");
        }

        // 4. Atualizar a entidade
        agendamento.setPaciente(paciente);
        agendamento.setMedico(medico);
        agendamento.setDataConsultaInicio(inicio);
        agendamento.setDataConsultaFim(fim);
        //agendamento.setObservacoes(agendamentoDTO.getObservacoes());
        String observacoesLimpasAtt = HtmlSanitizerUtil.sanitize(agendamentoDTO.getObservacoes());
        agendamento.setObservacoes(observacoesLimpasAtt);
        if (agendamentoDTO.getStatus() != null) {
            agendamento.setStatus(agendamentoDTO.getStatus());
        }

        return agendamentoRepository.save(agendamento);
    }

    // DELETE - Deletar agendamento
    public void deletarAgendamento(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new RuntimeException("Agendamento não encontrado com ID: " + id);
        }
        agendamentoRepository.deleteById(id);
    }

    // UPDATE - Atualizar status do agendamento
    public Agendamento atualizarStatus(Long id, StatusAgendamento novoStatus) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));

        agendamento.setStatus(novoStatus);
        return agendamentoRepository.save(agendamento);
    }

    // --- Buscas (agora usando os métodos corretos do repo) ---

    public List<Agendamento> buscarPorPaciente(Long pacienteId) {
        return agendamentoRepository.findByPacienteId(pacienteId);
    }

    public List<Agendamento> buscarPorMedico(Long medicoId) {
        return agendamentoRepository.findByMedicoId(medicoId);
    }

    public List<Agendamento> buscarPorEspecialidade(String especialidade) {
        return agendamentoRepository.findByMedicoEspecialidade(especialidade);
    }

    public List<Agendamento> buscarPorStatus(StatusAgendamento status) {
        return agendamentoRepository.findByStatus(status);
    }

    // --- Métodos auxiliares ---

    /**
     * Nova lógica de conflito. Verifica SOBREPOSIÇÃO com
     * Agendamentos E Bloqueios.
     */
    private boolean existeConflitoHorario(Long idExcluir, Long medicoId, LocalDateTime inicio, LocalDateTime fim) {

        // 1. Verifica conflito com AGENDAMENTOS
        List<Agendamento> agendamentosConflitantes = agendamentoRepository.findOverlappingAppointments(
                medicoId, inicio, fim, (idExcluir != null ? idExcluir : -1L)
        );

        if (!agendamentosConflitantes.isEmpty()) {
            return true; // Conflito com outro agendamento
        }

        // 2. Verifica conflito com BLOQUEIOS (Férias, Almoço...)
        // (Precisamos adicionar a query no BloqueioHorarioRepository)
        List<BloqueioHorario> bloqueiosConflitantes = bloqueioHorarioRepository.findOverlappingBlocks(medicoId, inicio, fim);

        return !bloqueiosConflitantes.isEmpty(); // Conflito com bloqueio
    }
}