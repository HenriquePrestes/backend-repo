package plataformaSaude.controller;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.model.Paciente;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import plataformaSaude.dto.PacientePerfilDTO;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.AgendamentoRepository;
import plataformaSaude.service.PacienteService;
import plataformaSaude.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/usuarios")
public class PacienteController {

    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;
    private final AgendamentoRepository agendamentoRepository;

    public PacienteController(PacienteService pacienteService,
                              UsuarioService usuarioService,
                              AgendamentoRepository agendamentoRepository) {
        this.pacienteService = pacienteService;
        this.usuarioService = usuarioService;
        this.agendamentoRepository = agendamentoRepository;

}

    @PostMapping
    public Paciente criar(@RequestBody Paciente paciente) {
        return pacienteService.salvar(paciente);
    }

    @GetMapping
    public ResponseEntity<Page<Paciente>> listarTodos(Pageable pageable,
                                                      Authentication authentication) {

        String email = authentication.getName();
        Usuario usuarioLogado = usuarioService.buscarPorEmail(email);
        if (usuarioLogado == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (!usuarioLogado.getTipoUsuario().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o administrador pode ver todos os pacientes!");
        }

        Page<Paciente> pacientes = pacienteService.listarTodos(pageable);
        return ResponseEntity.ok(pacientes);
    }

    //Busca por ID
    @GetMapping("/{id}")
    public Paciente buscar(@PathVariable Long id, Authentication auth) {

        String email = auth.getName();

        Usuario usuarioLogado = usuarioService.buscarPorEmail(email);
        if (usuarioLogado == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Paciente paciente = pacienteService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // ADMIN pode visualizar qualquer paciente
        if (usuarioLogado.getTipoUsuario().equals("ADMIN")) {
            return paciente;
        }

        // PACIENTE só pode ver ele como paciente
        if (usuarioLogado.getTipoUsuario().equals("PACIENTE")) {
            if (!paciente.getEmail().equals(email)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Você não está autorizado a visualizar outros perfis!");
            }
            return paciente;
        }

        // MÉDICO só pode ver se houver agendamento entre ele e o paciente
        if (usuarioLogado.getTipoUsuario().equals("MEDICO")) {
            boolean temRelacionamento = agendamentoRepository
                    .existsByMedicoIdAndPacienteId(usuarioLogado.getId(), paciente.getId());

            if (!temRelacionamento) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Médico só pode visualizar pacientes que possuem consulta com ele!");
            }

            return paciente;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }


    /* Endpoint para o PACIENTE LOGADO buscar o PRÓPRIO perfil.*/
    @GetMapping("/perfil")
    public Paciente buscarPerfil(Authentication authentication) {
        String email = authentication.getName();
        // Usando o Service
        return pacienteService.buscarPorEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paciente não encontrado"
                ));
    }

    /* Endpoint para o PACIENTE LOGADO atualizar o PRÓPRIO perfil.*/
    @PutMapping("/perfil")
    public Paciente atualizarPerfil(
            @RequestBody PacientePerfilDTO dto,
            Authentication authentication
    ) {
        String email = authentication.getName();

        // Service para buscar
        Paciente paciente = pacienteService.buscarPorEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paciente não encontrado"
                ));

        if (dto.getNomeCompleto() != null) {
            paciente.setNomeCompleto(dto.getNomeCompleto());
        }
        if (dto.getCpf() != null) {
            paciente.setCpf(dto.getCpf());
        }
        if (dto.getEmail() != null) {
            paciente.setEmail(dto.getEmail());
        }
        if (dto.getCelular() != null) {
            paciente.setCelular(dto.getCelular());
        }
        if (dto.getProfissao() != null) {
            paciente.setProfissao(dto.getProfissao());
        }
        if (dto.getDataNascimento() != null) {
            paciente.setDataNascimento(dto.getDataNascimento());
        }
        if (dto.getCep() != null) {
            paciente.setCep(dto.getCep());
        }
        if (dto.getCidade() != null) {
            paciente.setCidade(dto.getCidade());
        }
        if (dto.getRua() != null) {
            paciente.setRua(dto.getRua());
        }
        if (dto.getNumero() != null) {
            paciente.setNumero(dto.getNumero());
        }
        if (dto.getComplemento() != null) {
            paciente.setComplemento(dto.getComplemento());
        }
        if (dto.getEstado() != null) {
            paciente.setEstado(dto.getEstado());
        }
        if (dto.getNotificacoes() != null) {
            paciente.setNotificacoes(dto.getNotificacoes());
        }
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            paciente.setSenha(usuarioService.hashSenha(dto.getSenha()));
        }
        if (dto.getTemaPreferido() != null && !dto.getTemaPreferido().isEmpty()) {
            paciente.setTemaPreferido(dto.getTemaPreferido());
        }

        return pacienteService.salvar(paciente);
    }

    // Excluir
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id, Authentication auth) {

        String emailLogado = auth.getName();
        Paciente logado = pacienteService.buscarPorEmail(emailLogado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Paciente paciente = pacienteService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // ADMIN pode excluir qualquer paciente
        if (logado.getTipoUsuario().equals("ADMIN")) {
            pacienteService.deletar(id);
            return;
        }

        // PACIENTE só apaga a própria conta
        if (logado.getTipoUsuario().equals("PACIENTE")) {
            if (!paciente.getEmail().equals(emailLogado)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Paciente não tem permissão para excluir outros pacientes");
            }
            pacienteService.deletar(id);
            return;
        }

        // qualquer outra role é proibida
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}