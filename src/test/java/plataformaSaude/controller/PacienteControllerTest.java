package plataformaSaude.controller;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import plataformaSaude.config.SecurityConfigTest;
import plataformaSaude.model.Paciente;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.AgendamentoRepository;
import plataformaSaude.service.PacienteService;
import plataformaSaude.service.UsuarioService;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PacienteController.class)
@AutoConfigureMockMvc
@Import(SecurityConfigTest.class)
@ActiveProfiles("test")
class PacienteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean PacienteService pacienteService;
    @MockBean UsuarioService usuarioService;
    @MockBean AgendamentoRepository agendamentoRepository;

    @Test
    void deveCriarPacienteSemAutenticacao() throws Exception {
        Paciente p = new Paciente();
        p.setNomeCompleto("Teste");
        p.setCpf("55544433322");
        p.setEmail("joao@teste.com");
        p.setSenha("abc123");

        Mockito.when(pacienteService.salvar(any())).thenReturn(p);

        mockMvc.perform(post("/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
        {
          "nomeCompleto": "Teste",
          "cpf":"55544433322",
          "email":"joao@teste.com",
          "senha":"abc123"
        }
    """)
        ).andExpect(status().isOk());
    }

    @Test
    void deveListarTodosComJwt() throws Exception {

        Usuario admin = new Usuario();
        admin.setEmail("admin@test.com");
        admin.setTipoUsuario("ADMIN");
        Mockito.when(usuarioService.buscarPorEmail("admin@test.com"))
                .thenReturn(admin);

        Mockito.when(pacienteService.listarTodos(any()))
                .thenReturn(new PageImpl<>(List.of(new Paciente()), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/usuarios")
                .with(jwt().jwt(jwt -> jwt.subject("admin@test.com")))
        ).andExpect(status().isOk());
    }

    @Test
    void deveBuscarPorIdComJwt() throws Exception {

        Usuario admin = new Usuario();
        admin.setEmail("admin@test.com");
        admin.setTipoUsuario("ADMIN");

        Mockito.when(usuarioService.buscarPorEmail("admin@test.com"))
                .thenReturn(admin);

        Mockito.when(pacienteService.buscarPorId(1L))
                .thenReturn(Optional.of(new Paciente()));

        mockMvc.perform(get("/usuarios/1")
                        .with(jwt().jwt(jwt -> jwt.subject("admin@test.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void deveDeletarComJwt() throws Exception {

        Usuario admin = new Usuario();
        admin.setEmail("admin@test.com");
        admin.setTipoUsuario("ADMIN");

        Paciente adminPaciente = new Paciente();
        adminPaciente.setEmail("admin@test.com");
        adminPaciente.setTipoUsuario("ADMIN");

        Mockito.when(usuarioService.buscarPorEmail("admin@test.com"))
                .thenReturn(admin);

        Mockito.when(pacienteService.buscarPorEmail("admin@test.com"))
                .thenReturn(Optional.of(adminPaciente));

        Mockito.when(pacienteService.buscarPorId(1L))
                .thenReturn(Optional.of(new Paciente()));

        mockMvc.perform(delete("/usuarios/1")
                        .with(jwt().jwt(jwt -> jwt.subject("admin@test.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarPerfilDoLogado() throws Exception {
        Paciente p = new Paciente();
        p.setEmail("user@test.com");

        Mockito.when(pacienteService.buscarPorEmail(eq("user@test.com")))
                .thenReturn(Optional.of(p));

        mockMvc.perform(get("/usuarios/perfil")
                .with(jwt().jwt(jwt -> jwt.subject("user@test.com")))
        ).andExpect(status().isOk());
    }
}
