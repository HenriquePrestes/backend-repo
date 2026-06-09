package plataformaSaude.service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.UsuarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // O Cache deve ser LIMPO quando um novo usuário é salvo (senha, secret MFA, etc.)
    @Transactional
    @CacheEvict(value = "usuarios", key = "#usuario.getEmail()") // Limpa o cache para este e-mail
    public void salvarUsuario(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
    }

    public String hashSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    public boolean validarSenha(Usuario usuario, String senhaDigitada) {
        return passwordEncoder.matches(senhaDigitada, usuario.getSenha());
    }

    // O Cache deve ser USADO quando um usuário é buscado por e-mail (chave de login)
    @Cacheable(value = "usuarios", key = "#email")
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findFirstByEmailOrderByIdAsc(email).orElse(null);
    }

    public Usuario buscarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf);
    }

    /* Gera token redefinição de senha válido por 24h. */
    @Transactional
    // O Cache deve ser LIMPO ao gerar o token, pois o objeto Usuario foi alterado
    @CacheEvict(value = "usuarios", key = "#email")
    public Optional<Usuario> gerarTokenResetSenha(String email) {
        Usuario usuario = usuarioRepository.findFirstByEmailOrderByIdAsc(email).orElse(null);
        if (usuario != null) {
            String token = UUID.randomUUID().toString();
            usuario.setResetPasswordToken(token);
            usuario.setResetPasswordTokenExpiryDate(LocalDate.now().plusDays(1));
            usuarioRepository.save(usuario);
            return Optional.of(usuario);
        }
        return Optional.empty();
    }

    /** Redefine a senha se o token for válido.  */
    @Transactional
    @CacheEvict(value = "usuarios", allEntries = true)
    public boolean redefinirSenha(String token, String novaSenha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByResetPasswordToken(token);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            boolean tokenValido = usuario.getResetPasswordTokenExpiryDate() != null &&
                    usuario.getResetPasswordTokenExpiryDate().isAfter(LocalDate.now());

            if (tokenValido) {
                usuario.setSenha(passwordEncoder.encode(novaSenha));
                usuario.setResetPasswordToken(null);
                usuario.setResetPasswordTokenExpiryDate(null);
                usuarioRepository.save(usuario);
                return true;
            }
        }
        return false;
    }

    /** Valida se o código/token de reset de senha é válido para o email. */
    @Transactional(readOnly = true)
    public boolean validarCodigoResetSenha(String email, String code) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getResetPasswordToken() != null) {
            boolean tokenValido = usuario.getResetPasswordToken().equals(code) &&
                    usuario.getResetPasswordTokenExpiryDate() != null &&
                    usuario.getResetPasswordTokenExpiryDate().isAfter(LocalDate.now());
            return tokenValido;
        }
        return false;
    }

    @Transactional
    @CacheEvict(value = "usuarios", key = "#usuario.getEmail()")
    public Usuario salvar(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }
}