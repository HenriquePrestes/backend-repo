package plataformaSaude.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import plataformaSaude.dto.DashboardDTO;
import plataformaSaude.service.DashboardService;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DashboardService dashboardService;

    @Test
    void deveRetornarDashboardComSucesso() throws Exception {
        DashboardDTO mockResponse = new DashboardDTO();
        // se dentro do DashboardDTO tiver fields obrigatórios, preencha aqui

        when(dashboardService.getMetrics()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/dashboard/metrics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
