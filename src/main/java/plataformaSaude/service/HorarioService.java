package plataformaSaude.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import plataformaSaude.model.Agendamento;
import plataformaSaude.model.BloqueioHorario;
import plataformaSaude.model.Horario;
import plataformaSaude.model.Medico;
import plataformaSaude.repository.AgendamentoRepository;
import plataformaSaude.repository.BloqueioHorarioRepository;
import plataformaSaude.repository.HorarioRepository;
import plataformaSaude.repository.MedicoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class HorarioService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private BloqueioHorarioRepository bloqueioHorarioRepository;

    /**
     * Encontra todos os slots de horário disponíveis para um médico em uma data específica.
     */
    // *** NOVO MÉTODO PARA PAGINAÇÃO ***
    /**
     * Lista todos os horários (janelas de trabalho) com paginação.
     * Usado pelo Admin para gerenciar os horários.
     */
    public Page<Horario> listarTodos(Pageable pageable) {
        return horarioRepository.findAll(pageable);
    }

    public List<LocalTime> encontrarSlotsDisponiveis(Long medicoId, LocalDate data) {

        // 1. Buscar o médico e sua duração de consulta
        Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);
        if (medicoOpt.isEmpty()) {
            throw new RuntimeException("Médico não encontrado");
        }
        int duracaoConsulta = medicoOpt.get().getDuracaoConsultaMinutos();
        if (duracaoConsulta <= 0) {
            return new ArrayList<>(); // Não é possível agendar
        }

        // 2. Buscar as janelas de trabalho do médico para aquele dia da semana
        List<Horario> janelasTrabalho = horarioRepository.findByMedicoIdAndDiaSemana(medicoId, data.getDayOfWeek());
        if (janelasTrabalho.isEmpty()) {
            return new ArrayList<>(); // Médico não trabalha neste dia
        }

        // 3. Buscar "ocupações" (agendamentos e bloqueios) para o dia inteiro
        LocalDateTime inicioDoDia = data.atStartOfDay(); // 2025-11-01T00:00:00
        LocalDateTime fimDoDia = data.plusDays(1).atStartOfDay(); // 2025-11-02T00:00:00

        List<Agendamento> agendamentos = agendamentoRepository
                .findActiveAppointmentsByMedicoAndDateRange(medicoId, inicioDoDia, fimDoDia);

        List<BloqueioHorario> bloqueios = bloqueioHorarioRepository
                .findBlocksByMedicoAndDateRange(medicoId, inicioDoDia, fimDoDia);

        // 4. O Algoritmo: Gerar slots e filtrar
        List<LocalTime> slotsDisponiveis = new ArrayList<>();

        for (Horario janela : janelasTrabalho) {
            LocalTime slotInicio = janela.getHoraInicio();
            LocalTime janelaFim = janela.getHoraFim();

            // Loop: continua gerando slots até o fim da janela de trabalho
            while (true) {
                LocalTime slotFim = slotInicio.plusMinutes(duracaoConsulta);

                // Se o slot terminar DEPOIS do fim da janela, pare.
                if (slotFim.isAfter(janelaFim)) {
                    break;
                }

                // 5. Verificar Conflitos
                boolean temConflito = false;

                // Conflito com Agendamentos?
                for (Agendamento ag : agendamentos) {
                    if (haSobreposicao(slotInicio, slotFim, ag.getDataConsultaInicio().toLocalTime(), ag.getDataConsultaFim().toLocalTime())) {
                        temConflito = true;
                        break;
                    }
                }

                if (temConflito) {
                    slotInicio = slotInicio.plusMinutes(duracaoConsulta); // Pula para o próximo
                    continue; // Volta ao início do while
                }

                // Conflito com Bloqueios?
                for (BloqueioHorario bl : bloqueios) {
                    if (haSobreposicao(slotInicio, slotFim, bl.getDataInicio().toLocalTime(), bl.getDataFim().toLocalTime())) {
                        temConflito = true;
                        break;
                    }
                }

                if (!temConflito) {
                    slotsDisponiveis.add(slotInicio); // Horário livre!
                }

                // Prepara para a próxima iteração
                slotInicio = slotInicio.plusMinutes(duracaoConsulta);
            }
        }

        return slotsDisponiveis;
    }

    /**
     * Lógica de verificação de sobreposição de intervalos.
     * (InícioA < FimB) E (FimA > InícioB)
     */
    private boolean haSobreposicao(LocalTime inicioA, LocalTime fimA, LocalTime inicioB, LocalTime fimB) {
        return inicioA.isBefore(fimB) && fimA.isAfter(inicioB);
    }
}