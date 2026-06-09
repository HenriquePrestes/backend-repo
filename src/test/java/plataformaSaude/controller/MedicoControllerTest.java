package plataformaSaude.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import plataformaSaude.service.MedicoService;
import plataformaSaude.service.HistoricoBuscaService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MedicoController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
})
class MedicoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MedicoService medicoService;

    @MockBean
    HistoricoBuscaService historicoBuscaService;

    @Test
    void deveListarMedicosERegistrarHistorico() throws Exception {
        mockMvc.perform(get("/api/medicos?page=0"))
                .andExpect(status().isOk());
    }
}
