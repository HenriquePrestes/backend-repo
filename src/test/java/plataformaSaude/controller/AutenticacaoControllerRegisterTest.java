package plataformaSaude.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import plataformaSaude.config.SecurityConfigTest;
import plataformaSaude.model.Paciente;
import plataformaSaude.service.UsuarioService;
import plataformaSaude.service.EmailService;
import plataformaSaude.service.RefreshTokenService;
import plataformaSaude.service.MfaService;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;


@WebMvcTest(controllers = AutenticacaoController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@TestPropertySource(properties = {
        "MAIL_HOST=disabled",
        "MAIL_PORT=25",
        "MAIL_USERNAME=dummy",
        "MAIL_PASSWORD=dummy"
})
class AutenticacaoControllerRegisterTest {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    UsuarioService usuarioService;
    @MockBean
    EmailService emailService;
    @MockBean
    RefreshTokenService refreshTokenService;
    @MockBean
    MfaService mfaService;
    @MockBean
    MedicoRepository medicoRepository;
    @MockBean
    PacienteRepository pacienteRepository;

    @MockBean
    JwtEncoder jwtEncoder;


    @Test
    void deveRegistrarPacienteComSucesso() throws Exception {
        when(usuarioService.buscarPorEmail("teste@teste.com")).thenReturn(null);
        when(usuarioService.buscarPorCpf("11122233345")).thenReturn(null);

        // se salvarUsuario for void:
        doNothing().when(usuarioService).salvarUsuario(any(Paciente.class));

        String json = """
                {
                  "email": "teste@teste.com",
                  "cpf": "11122233345",
                  "nomeCompleto": "Paciente Teste",
                  "senha": "12345678"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }
}