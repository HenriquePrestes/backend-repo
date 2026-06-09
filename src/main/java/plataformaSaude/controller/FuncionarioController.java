package plataformaSaude.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.model.Funcionario;
import plataformaSaude.repository.FuncionarioRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository repository;

    public FuncionarioController(FuncionarioRepository repository) {
        this.repository = repository;
    }
  
    @PostMapping
    public Funcionario criar(@RequestBody Funcionario funcionario) {
        return repository.save(funcionario);
    }
   
    @GetMapping
    public List<Funcionario> listarTodos() {
        return repository.findAll();
    }
 
    @GetMapping("/{id}")
    public Optional<Funcionario> buscar(@PathVariable Long id) {
        return repository.findById(id);
    }

    @PutMapping("/{id}")
    public Funcionario atualizar(@PathVariable Long id, @RequestBody Funcionario novosDados) {
        return repository.findById(id)
                .map(f -> {
                    f.setNomeCompleto(novosDados.getNomeCompleto());
                    f.setEmail(novosDados.getEmail());
                    f.setCpf(novosDados.getCpf());
                    f.setDataNascimento(novosDados.getDataNascimento());
                    f.setCelular(novosDados.getCelular());
                    f.setCelular(novosDados.getCelular());
                    f.setCep(novosDados.getCep());
                    f.setRua(novosDados.getRua());
                    f.setCidade(novosDados.getCidade());
                    f.setEstado(novosDados.getEstado());
                    f.setCargo(novosDados.getCargo());
                    f.setDepartamento(novosDados.getDepartamento());
                    f.setFoto(novosDados.getFoto());
                    
                    if (novosDados.getSenha() != null && !novosDados.getSenha().trim().isEmpty()) {
                        f.setSenha(novosDados.getSenha());
                    }
                    return repository.save(f);
                })
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
