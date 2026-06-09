package plataformaSaude.controller;
import plataformaSaude.dto.AgendamentoDTO;
import plataformaSaude.dto.AgendamentoResponseDTO;
import plataformaSaude.model.Agendamento;
import plataformaSaude.Enum.StatusAgendamento;
import plataformaSaude.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    // CREATE - Criar novo agendamento
    @PostMapping
    public ResponseEntity<?> criarAgendamento(@Valid @RequestBody AgendamentoDTO agendamentoDTO) { // +++ DTO ATUALIZADO
        try {
            Agendamento agendamento = agendamentoService.criarAgendamento(agendamentoDTO);
            // Retornar o agendamento usando DTO para evitar problemas de serialização
            AgendamentoResponseDTO agendamentoResponseDTO = new AgendamentoResponseDTO(agendamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoResponseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // READ - Listar todos os agendamentos
    @GetMapping
    public ResponseEntity<Page<Agendamento>> listarTodos(Pageable pageable) {
        Page<Agendamento> agendamentos = agendamentoService.listarTodos(pageable);
        return ResponseEntity.ok(agendamentos);
    }

    // READ - Listar todos os agendamentos com relacionamentos (para admin)
    @GetMapping("/admin")
    public ResponseEntity<List<Agendamento>> listarTodosParaAdmin() {
        List<Agendamento> agendamentos = agendamentoService.listarTodosComRelacionamentos();
        return ResponseEntity.ok(agendamentos);
    }

    // READ - Buscar agendamento por ID
    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<Agendamento> agendamento = agendamentoService.buscarPorId(id);
        return agendamento.map(a -> ResponseEntity.ok(new AgendamentoResponseDTO(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    // READ - Listar agendamentos futuros
    @GetMapping("/futuros")
    public ResponseEntity<Page<Agendamento>> listarFuturos(Pageable pageable) {
        Page<Agendamento> agendamentos = agendamentoService.listarFuturos(pageable);
        return ResponseEntity.ok(agendamentos);
    }

    // UPDATE - Atualizar agendamento
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAgendamento(@PathVariable Long id,
                                                  @Valid @RequestBody AgendamentoDTO agendamentoDTO) { // +++ DTO ATUALIZADO
        try {
            Agendamento agendamento = agendamentoService.atualizarAgendamento(id, agendamentoDTO);
            AgendamentoResponseDTO agendamentoResponseDTO = new AgendamentoResponseDTO(agendamento);
            return ResponseEntity.ok(agendamentoResponseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UPDATE - Atualizar status do agendamento
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id,
                                             @RequestParam StatusAgendamento status) {
        try {
            Agendamento agendamento = agendamentoService.atualizarStatus(id, status);
            AgendamentoResponseDTO agendamentoDTO = new AgendamentoResponseDTO(agendamento);
            return ResponseEntity.ok(agendamentoDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE - Deletar agendamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        try {
            agendamentoService.deletarAgendamento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Buscas específicas (Refatoradas) ---

    // Agora busca por ID, não por nome - usando DTO para evitar problemas de serialização
    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<AgendamentoResponseDTO>> buscarPorPaciente(@PathVariable Long id) {
        List<Agendamento> agendamentos = agendamentoService.buscarPorPaciente(id);
        List<AgendamentoResponseDTO> agendamentosDTO = agendamentos.stream()
                .map(AgendamentoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(agendamentosDTO);
    }

    // Agora busca por ID, não por nome
    @GetMapping("/medico/{id}")
    public ResponseEntity<List<Agendamento>> buscarPorMedico(@PathVariable Long id) {
        List<Agendamento> agendamentos = agendamentoService.buscarPorMedico(id);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/especialidade/{especialidade}")
    public ResponseEntity<List<Agendamento>> buscarPorEspecialidade(@PathVariable String especialidade) {
        List<Agendamento> agendamentos = agendamentoService.buscarPorEspecialidade(especialidade);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Agendamento>> buscarPorStatus(@PathVariable StatusAgendamento status) {
        List<Agendamento> agendamentos = agendamentoService.buscarPorStatus(status);
        return ResponseEntity.ok(agendamentos);
    }
}