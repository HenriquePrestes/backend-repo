package plataformaSaude.controller;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import plataformaSaude.config.SecurityConfigTest;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
@Import(SecurityConfigTest.class)
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtEncoder jwtEncoder;

    @Test
    void deveRetornarHomeComSucesso() throws Exception {

        OidcUser oidcUser = Mockito.mock(OidcUser.class);
        when(oidcUser.getSubject()).thenReturn("123");
        when(oidcUser.getEmail()).thenReturn("teste@teste.com");
        when(oidcUser.getFullName()).thenReturn("Usuário Teste");
    }

        @Test
    void deveRetornarRootComSucesso() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
