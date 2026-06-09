package plataformaSaude.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.dto.EspecialidadeDTO;
import plataformaSaude.service.EspecialidadeService;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin(origins = "*")
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

    @Autowired
    public EspecialidadeController(EspecialidadeService especialidadeService) {
        this.especialidadeService = especialidadeService;
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadeDTO>> listarTodas() {
        try {
            List<EspecialidadeDTO> especialidades = especialidadeService.listarTodas();
            return ResponseEntity.ok(especialidades);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeDTO> buscarPorId(@PathVariable Long id) {
        try {
            EspecialidadeDTO especialidade = especialidadeService.buscarPorId(id);
            return ResponseEntity.ok(especialidade);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody EspecialidadeDTO dto) {
        try {
            EspecialidadeDTO novaEspecialidade = especialidadeService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaEspecialidade);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody EspecialidadeDTO dto) {
        try {
            EspecialidadeDTO especialidadeAtualizada = especialidadeService.atualizar(id, dto);
            return ResponseEntity.ok(especialidadeAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            especialidadeService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}