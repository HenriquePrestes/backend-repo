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
import plataformaSaude.model.BloqueioHorario;
import plataformaSaude.service.BloqueioHorarioService;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BloqueioHorarioController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
class BloqueioHorarioControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BloqueioHorarioService service;

    @Test
    void deveCriarBloqueio() throws Exception {

        BloqueioHorario bloqueioMock = new BloqueioHorario();
        Mockito.when(service.criarBloqueio(any())).thenReturn(bloqueioMock);

        mockMvc.perform(post("/api/bloqueios")
                .with(csrf())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {
              "medicoId": 1,
              "dataInicio": "2030-01-01T10:00:00",
              "dataFim": "2030-01-01T11:00:00",
              "motivo": "Descanso"
            }
            """)
        ).andExpect(status().isCreated());
    }
    @Test
    void deveRetornar400QuandoPayloadInvalido() throws Exception {

        mockMvc.perform(post("/api/bloqueios")
                .with(csrf())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            { "motivo": "Descanso" }
            """)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void deveListarTodos() throws Exception {
        Mockito.when(service.listarTodos(any()))
                .thenReturn(new PageImpl<>(List.of(new BloqueioHorario()), PageRequest.of(0,10),1));

        mockMvc.perform(get("/api/bloqueios"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarPorId() throws Exception {
        Mockito.when(service.buscarPorId(1L))
                .thenReturn(Optional.of(new BloqueioHorario()));

        mockMvc.perform(get("/api/bloqueios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveDeletarBloqueio() throws Exception {
        mockMvc.perform(delete("/api/bloqueios/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
