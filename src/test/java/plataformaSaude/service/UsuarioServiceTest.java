package plataformaSaude.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UsuarioService usuarioService;

    Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@teste.com");
        usuario.setCpf("12345678900");
        usuario.setSenha("senhaOriginal");
    }

    @Test
    void deveSalvarUsuarioComSenhaCriptografada() {
        when(passwordEncoder.encode("senhaOriginal")).thenReturn("senhaCriptografada");

        usuarioService.salvarUsuario(usuario);

        assertThat(usuario.getSenha()).isEqualTo("senhaCriptografada");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void deveGerarHashDaSenha() {
        when(passwordEncoder.encode("123")).thenReturn("hash123");

        String hash = usuarioService.hashSenha("123");

        assertThat(hash).isEqualTo("hash123");
    }

    @Test
    void deveValidarSenhaCorretamente() {
        when(passwordEncoder.matches("senhaDigitada", "senhaOriginal")).thenReturn(true);

        boolean valida = usuarioService.validarSenha(usuario, "senhaDigitada");

        assertThat(valida).isTrue();
    }

    @Test
    void deveBuscarPorEmail() {
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(usuario);

        Usuario resultado = usuarioService.buscarPorEmail("teste@teste.com");

        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    void deveBuscarPorCpf() {
        when(usuarioRepository.findByCpf("12345678900")).thenReturn(usuario);

        Usuario resultado = usuarioService.buscarPorCpf("12345678900");

        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    void deveGerarTokenResetSenhaQuandoEmailExiste() {
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(usuario);

        Optional<Usuario> result = usuarioService.gerarTokenResetSenha("teste@teste.com");

        assertThat(result).isPresent();
        assertThat(result.get().getResetPasswordToken()).isNotNull();
        assertThat(result.get().getResetPasswordTokenExpiryDate()).isEqualTo(LocalDate.now().plusDays(1));
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void naoDeveGerarTokenResetSenhaQuandoEmailNaoExiste() {
        when(usuarioRepository.findByEmail("naoExiste@teste.com")).thenReturn(null);

        Optional<Usuario> result = usuarioService.gerarTokenResetSenha("naoExiste@teste.com");

        assertThat(result).isEmpty();
    }

    @Test
    void deveRedefinirSenhaQuandoTokenValido() {
        usuario.setResetPasswordToken("abc");
        usuario.setResetPasswordTokenExpiryDate(LocalDate.now().plusDays(1));

        when(passwordEncoder.encode("nova")).thenReturn("novaHash");
        when(usuarioRepository.findByResetPasswordToken("abc")).thenReturn(Optional.of(usuario));

        boolean resultado = usuarioService.redefinirSenha("abc", "nova");

        assertThat(resultado).isTrue();
        assertThat(usuario.getSenha()).isEqualTo("novaHash");
        assertThat(usuario.getResetPasswordToken()).isNull();
        assertThat(usuario.getResetPasswordTokenExpiryDate()).isNull();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void naoDeveRedefinirSenhaQuandoTokenExpirado() {
        usuario.setResetPasswordToken("expired");
        usuario.setResetPasswordTokenExpiryDate(LocalDate.now().minusDays(1));

        when(usuarioRepository.findByResetPasswordToken("expired")).thenReturn(Optional.of(usuario));

        boolean resultado = usuarioService.redefinirSenha("expired", "nova");

        assertThat(resultado).isFalse();
        verify(usuarioRepository, never()).save(any());
    }
}
