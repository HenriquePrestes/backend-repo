package plataformaSaude.controller;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.dto.MedicoDTO;
import plataformaSaude.dto.MedicoListaDTO;
import plataformaSaude.model.Medico;
import plataformaSaude.model.Horario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.security.core.Authentication;
import plataformaSaude.service.MedicoService;
import plataformaSaude.service.HistoricoBuscaService;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "*")
public class MedicoController {

    private final MedicoService medicoService;
    private final HistoricoBuscaService historicoBuscaService;

    @Autowired
    public MedicoController(
            MedicoService medicoService,
            HistoricoBuscaService historicoBuscaService
    ) {
        this.medicoService = medicoService;
        this.historicoBuscaService = historicoBuscaService;
    }

    /*** PAGINAÇÃO e FILTROS DINÂMICOS
     * Ex: GET /api/medicos?page=0&nome=joao&especialidade=Cardiologia
     */
    @GetMapping
    public ResponseEntity<Page<MedicoListaDTO>> listarTodos(
            Pageable pageable,
            Authentication authentication,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String crm,
            @RequestParam(required = false) String especialidade,
            @RequestParam(required = false) String termoBusca
    ) {

        // Executa a busca (Pode incluir filtros específicos ou termoBusca)
        Page<MedicoListaDTO> medicos = medicoService.listarTodosDTO(pageable, nome, crm, especialidade, termoBusca);

        String emailUsuario = (authentication != null) ? authentication.getName() : "ANONIMO";

        String termoBuscado = String.format(
                "termo:%s, nome:%s, crm:%s, esp:%s",
                termoBusca != null ? termoBusca : "-",
                nome != null ? nome : "-",
                crm != null ? crm : "-",
                especialidade != null ? especialidade : "-"
        );

        historicoBuscaService.salvarBusca(
                termoBuscado,
                "MEDICO",
                emailUsuario
        );

        return ResponseEntity.ok(medicos);
    }
    // Listar todos os médicos sem paginação
    @GetMapping("/all")
    public ResponseEntity<List<MedicoListaDTO>> listarTodosSemPaginacao() {
        List<MedicoListaDTO> medicos = medicoService.listarTodosDTO();
        return ResponseEntity.ok(medicos);
    }

    // Criar médico
    @PostMapping
    public ResponseEntity<?> criarMedico(@Valid @RequestBody MedicoDTO medicoDTO) {
        try {
            Medico medico = medicoService.criarMedico(medicoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(medico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Medico> buscarPorId(@PathVariable Long id) {
        return medicoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar horários de um médico específico
    @GetMapping("/{id}/horarios")
    public ResponseEntity<List<Horario>> buscarHorariosPorMedico(@PathVariable Long id) {
        try {
            List<Horario> horarios = medicoService.buscarHorariosPorMedico(id);
            return ResponseEntity.ok(horarios);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarMedico(@PathVariable Long id,
                                             @Valid @RequestBody MedicoDTO medicoDTO) {
        try {
            Medico medico = medicoService.atualizarMedico(id, medicoDTO);
            return ResponseEntity.ok(medico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMedico(@PathVariable Long id) {
        try {
            medicoService.deletarMedico(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
