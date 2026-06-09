package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plataformaSaude.model.HistoricoBusca;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.HistoricoBuscaRepository;
import plataformaSaude.repository.UsuarioRepository;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricoBuscaServiceTest {

    @Mock
    HistoricoBuscaRepository historicoRepo;

    @Mock
    UsuarioRepository usuarioRepo;

    @InjectMocks
    HistoricoBuscaService service;


    @Test
    void deveSalvarBuscaComUsuario() {
        // arrange
        Usuario usuario = new Usuario();
        when(usuarioRepo.findByEmail("teste@a.com")).thenReturn(usuario);

        // act
        service.salvarBusca("joao", "MEDICO", "teste@a.com");

        // assert
        verify(usuarioRepo, times(1)).findByEmail("teste@a.com");
        verify(historicoRepo, times(1)).save(any(HistoricoBusca.class));
    }

    @Test
    void deveSalvarBuscaSemUsuario() {
        // act
        service.salvarBusca("joao", "MEDICO", null);

        // assert
        verify(historicoRepo, times(1)).save(any(HistoricoBusca.class));
        verify(usuarioRepo, times(0)).findByEmail(anyString());
    }

    @Test
    void naoDeveSalvarBuscaVazia() {
        // act
        service.salvarBusca("", "MEDICO", "x@y.com");

        // assert
        verify(historicoRepo, times(0)).save(any());
        verify(usuarioRepo, times(0)).findByEmail(anyString());
    }
}
