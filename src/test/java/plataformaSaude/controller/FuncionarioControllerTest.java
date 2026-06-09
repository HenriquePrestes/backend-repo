package plataformaSaude.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import plataformaSaude.config.SecurityConfigTest;
import plataformaSaude.model.Funcionario;
import plataformaSaude.repository.FuncionarioRepository;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FuncionarioController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
@TestPropertySource(properties = {
        "MAIL_HOST=disabled",
        "MAIL_PORT=25",
        "MAIL_USERNAME=dummy",
        "MAIL_PASSWORD=dummy"
})
class FuncionarioControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    FuncionarioRepository repository;

    @Test
    void deveCriarFuncionario() throws Exception {
        Funcionario f = new Funcionario();
        f.setId(1L);
        f.setNomeCompleto("Teste");

        when(repository.save(any())).thenReturn(f);

        String json = """
        {
          "nomeCompleto": "Teste"
        }
        """;

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarTodos() throws Exception {
        when(repository.findAll()).thenReturn(List.of(new Funcionario()));

        mockMvc.perform(get("/funcionarios"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarPorId() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(new Funcionario()));

        mockMvc.perform(get("/funcionarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveExcluir() throws Exception {
        doNothing().when(repository).deleteById(1L);

        mockMvc.perform(delete("/funcionarios/1"))
                .andExpect(status().isOk());
    }
}
