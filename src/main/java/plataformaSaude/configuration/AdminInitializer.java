package plataformaSaude.configuration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.UsuarioRepository;
import plataformaSaude.service.UsuarioService;

@Component
public class AdminInitializer {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:medfastsaude@gmail.com}")
    private String adminEmail;

    @Value("${app.admin.senha:admin123}")
    private String adminSenha;

    public AdminInitializer(UsuarioRepository usuarioRepository, UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void criarAdminRoot() {
        if (usuarioRepository.findByEmail(adminEmail) == null) {
            Usuario admin = new Usuario();
            admin.setEmail(adminEmail);
            admin.setCpf("00000000000");
            admin.setNomeCompleto("Administrador ROOT");
            admin.setSenha(passwordEncoder.encode(adminSenha));
            admin.setTipoUsuario("ADMIN");

            usuarioService.salvar(admin);

            System.out.println("ADMIN ROOT criado com sucesso: " + adminEmail);
        } else {
            System.out.println("ADMIN ROOT já existe: " + adminEmail);
        }
    }
}
