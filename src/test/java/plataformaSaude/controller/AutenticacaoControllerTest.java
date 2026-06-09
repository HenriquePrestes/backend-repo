package plataformaSaude.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import plataformaSaude.config.SecurityConfigTest;
import plataformaSaude.model.RefreshToken;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import plataformaSaude.service.*;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(SecurityConfigTest.class)
@WebMvcTest(AutenticacaoController.class)
@AutoConfigureMockMvc
class AutenticacaoControllerTest {

    @MockBean private UsuarioService usuarioService;
    @MockBean private JwtEncoder jwtEncoder;
    @MockBean private RefreshTokenService refreshTokenService;
    @MockBean private MfaService mfaService;
    @MockBean private MedicoRepository medicoRepository;
    @MockBean private PacienteRepository pacienteRepository;
    @MockBean private EmailService emailService;

    @Autowired
    private MockMvc mockMvc;

    // helper para role temporária
    private void mockDefaultRole(Usuario usuario) {
        when(usuario.getAuthorities())
                .thenReturn((java.util.Collection) java.util.List.of(
                        new SimpleGrantedAuthority("ROLE_USER")
                ));
    }

    @Test
    void deveFazerLoginComSucesso() throws Exception {

        Usuario usuarioMock = spy(new Usuario());
        usuarioMock.setId(1L);
        usuarioMock.setEmail("login@test.com");
        usuarioMock.setNomeCompleto("User Login Test");
        usuarioMock.setSenha("hashqualquer");

        mockDefaultRole(usuarioMock);

        when(usuarioService.buscarPorEmail("login@test.com"))
                .thenReturn(usuarioMock);

        // <-- aqui está o ajuste final
        when(usuarioService.validarSenha(any(Usuario.class), any(String.class)))
                .thenReturn(true);

        Jwt jwtMock = new Jwt(
                "fakeToken",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg","HS256"),
                Map.of(
                        "sub","login@test.com",
                        "roles","ROLE_USER"
                )
        );

        when(jwtEncoder.encode(any())).thenReturn(jwtMock);

        RefreshToken refresh = new RefreshToken(
                "mockRefreshToken",
                "login@test.com",
                Instant.now().plusSeconds(3600)
        );

        when(refreshTokenService.criarRefreshToken("login@test.com"))
                .thenReturn(refresh);


        String json = """
                {
                    "email": "login@test.com",
                    "senha": "123456"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar401QuandoCredenciaisInvalidas() throws Exception {

        when(usuarioService.buscarPorEmail("naoexiste@test.com"))
                .thenReturn(null);

        String json = """
                {
                    "email": "naoexiste@test.com",
                    "senha": "123"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isUnauthorized());
    }
}
