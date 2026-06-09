package plataformaSaude.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.dto.BloqueioHorarioDTO;
import plataformaSaude.model.BloqueioHorario;
import plataformaSaude.service.BloqueioHorarioService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/bloqueios")
@CrossOrigin(origins = "*")
public class BloqueioHorarioController {

    @Autowired
    private BloqueioHorarioService bloqueioService;

    // CREATE - Criar novo bloqueio
    @PostMapping
    public ResponseEntity<?> criarBloqueio(@Valid @RequestBody BloqueioHorarioDTO dto) {
        try {
            BloqueioHorario bloqueio = bloqueioService.criarBloqueio(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(bloqueio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // READ - Listar todos os bloqueios
    @GetMapping
    public ResponseEntity<Page<BloqueioHorario>> listarTodos(Pageable pageable) {
        Page<BloqueioHorario> bloqueios = bloqueioService.listarTodos(pageable);
        return ResponseEntity.ok(bloqueios);
    }

    // READ - Buscar bloqueio por ID
    @GetMapping("/{id}")
    public ResponseEntity<BloqueioHorario> buscarPorId(@PathVariable Long id) {
        return bloqueioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ - Buscar todos os bloqueios de um médico
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<?> buscarPorMedico(@PathVariable Long medicoId) {
        try {
            List<BloqueioHorario> bloqueios = bloqueioService.buscarPorMedico(medicoId);
            return ResponseEntity.ok(bloqueios);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // UPDATE - Atualizar bloqueio
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarBloqueio(@PathVariable Long id,
                                               @Valid @RequestBody BloqueioHorarioDTO dto) {
        try {
            BloqueioHorario bloqueio = bloqueioService.atualizarBloqueio(id, dto);
            return ResponseEntity.ok(bloqueio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE - Deletar bloqueio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarBloqueio(@PathVariable Long id) {
        try {
            bloqueioService.deletarBloqueio(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}