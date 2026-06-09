package plataformaSaude.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plataformaSaude.model.HistoricoBusca;
import plataformaSaude.model.Usuario;
import plataformaSaude.repository.HistoricoBuscaRepository;
import plataformaSaude.repository.UsuarioRepository; //

/* Serviço para gerenciar o registro de buscas. */
@Service
public class HistoricoBuscaService {

    @Autowired
    private HistoricoBuscaRepository historicoBuscaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; //

    /**
     * Salva um novo registro de busca no histórico.
     *
     * @param termoBuscado O que o usuário digitou.
     * @param tipoEntidade O tipo de registro buscado (ex: "MEDICO").
     * @param emailUsuario O email do usuário logado (pode ser nulo se anônimo).
     */
    @Transactional
    public void salvarBusca(String termoBuscado, String tipoEntidade, String emailUsuario) {
        if (termoBuscado == null || termoBuscado.isBlank()) {
            return; // Não salva buscas vazias
        }

        Usuario usuario = null;
        if (emailUsuario != null && !emailUsuario.isBlank()) {
            // Encontra o usuário para associar ao histórico
            usuario = usuarioRepository.findFirstByEmailOrderByIdAsc(emailUsuario).orElse(null); //
        }

        HistoricoBusca historico = new HistoricoBusca(termoBuscado, tipoEntidade, usuario);
        historicoBuscaRepository.save(historico);
    }

    public void registrarBusca() {

    }
}