package plataformaSaude.controller;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import plataformaSaude.dto.*;
import plataformaSaude.model.Medico;
import plataformaSaude.model.Paciente;
import plataformaSaude.model.Usuario;
import plataformaSaude.model.RefreshToken;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;
import plataformaSaude.service.UsuarioService;
import plataformaSaude.service.RefreshTokenService;
import plataformaSaude.service.EmailService;
import plataformaSaude.service.MfaService;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AutenticacaoController {

    private final UsuarioService usuarioService;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final JwtEncoder jwtEncoder;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final MfaService mfaService;

    @Value("${app.frontend.url:http://localhost:3000/reset-senha}")
    private String appFrontendUrl;

    public AutenticacaoController(
            UsuarioService usuarioService,
            MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository,
            JwtEncoder jwtEncoder,
            RefreshTokenService refreshTokenService,
            EmailService emailService,
            MfaService mfaService
    ) {
        this.usuarioService = usuarioService;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
        this.mfaService = mfaService;
    }

    @PostMapping("/register/admin")
    public ResponseEntity<Usuario> registrarAdmin(@RequestBody Usuario admin) {

        if (usuarioService.buscarPorEmail(admin.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        admin.setTipoUsuario("ADMIN");
        admin.setSenha(usuarioService.hashSenha(admin.getSenha()));

        usuarioService.salvarUsuario(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }


    @PostMapping("/register")
    public ResponseEntity<?> registrarPaciente(@Valid @RequestBody PacienteRegistroDTO dto) {
        if (usuarioService.buscarPorEmail(dto.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O email informado já está em uso.");
        }
        if (usuarioService.buscarPorCpf(dto.getCpf()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro: O CPF informado já está cadastrado.");
        }

        Paciente novoPaciente = new Paciente();
        novoPaciente.setEmail(dto.getEmail());
        novoPaciente.setCpf(dto.getCpf());

        novoPaciente.setNomeCompleto(dto.getNomeCompleto());

        novoPaciente.setSenha(dto.getSenha());
        novoPaciente.setTipoUsuario("PACIENTE");

        try {
            usuarioService.salvarUsuario(novoPaciente);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    @PostMapping("/register/medico")
    public ResponseEntity<Medico> registrarMedico(@RequestBody Medico medico) {
        medico.setTipoUsuario("MEDICO");
        usuarioService.salvarUsuario(medico);
        return ResponseEntity.status(HttpStatus.CREATED).body(medico);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());
        if (usuario == null || !usuarioService.validarSenha(usuario, request.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }

        String role = "ROLE_" + usuario.getTipoUsuario();

        String mfaSecret = usuario.getMfaSecret();
        if (mfaSecret != null && !mfaSecret.isBlank()) {
            String code = request.getMfaCode();
            if (code == null || code.isBlank() || !mfaService.verifyCode(mfaSecret, code)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código MFA inválido ou ausente.");
            }
        }

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("MedFast")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(usuario.getEmail())
                .claim("id", usuario.getId() == null ? 1L : usuario.getId())
                .claim("nome", usuario.getNomeCompleto())
                .claim("scope", role)
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        RefreshToken refreshToken = refreshTokenService.criarRefreshToken(usuario.getEmail());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        Optional<RefreshToken> validToken = refreshTokenService.validarRefreshToken(refreshToken);

        if (validToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token inválido ou expirado."));
        }

        String username = validToken.get().getUsername();
        Instant now = Instant.now();

        Usuario usuario = usuarioService.buscarPorEmail(username);
        String role = usuario.getAuthorities().iterator().next().getAuthority();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("MedFast")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(username)
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNomeCompleto())
                .claim("scope", role)
                .build();

        String novoAccessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(Map.of(
                "accessToken", novoAccessToken,
                "refreshToken", refreshToken
        ));
    }

    @PostMapping("/enable-mfa")
    public ResponseEntity<?> enableMfa(@RequestParam String email) {
        var usuario = usuarioService.buscarPorEmail(email);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado.");
        }

        String secret = mfaService.generateSecret();
        usuario.setMfaSecret(secret);
        usuarioService.salvarUsuario(usuario);

        try {
            String qrCodeUrl = mfaService.getQrCodeURL(secret, email, "MedFast");
            return ResponseEntity.ok(Map.of(
                    "secret", secret,
                    "qrCodeUrl", qrCodeUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao gerar QR Code");
        }
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> solicitarResetSenha(@RequestBody PasswordResetRequest request) {
        String email = request.getEmail();
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("O e-mail é obrigatório.");
        }

        var usuarioOpt = usuarioService.gerarTokenResetSenha(email);
        if (usuarioOpt.isPresent()) {
            String token = usuarioOpt.get().getResetPasswordToken();
            String link = appFrontendUrl + "?token=" + token;
            emailService.enviarEmail(
                    email,
                    "Redefinição de senha - MedFast",
                    "Clique no link abaixo para redefinir sua senha:\n\n" + link + "\n\nEste link expira em 24 horas."
            );
        }

        return ResponseEntity.ok("Caso o e-mail esteja cadastrado, você receberá um link em instantes.");
    }

    @PostMapping("/reset-senha")
    public ResponseEntity<String> redefinirSenha(@RequestBody PasswordResetConfirm request) {
        String token = request.getToken();
        String novaSenha = request.getNovaSenha();

        if (token == null || token.isEmpty() || novaSenha == null || novaSenha.isEmpty()) {
            return ResponseEntity.badRequest().body("Token e nova senha são obrigatórios.");
        }

        if (usuarioService.redefinirSenha(token, novaSenha)) {
            return ResponseEntity.ok("Nova senha criada com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }
    }
}