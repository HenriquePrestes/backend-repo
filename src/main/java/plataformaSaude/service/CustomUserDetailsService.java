package plataformaSaude.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.PacienteRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // O "username" que recebemos é o email

        // 1. Tenta encontrar um médico com o email
        UserDetails usuario = medicoRepository.findByEmail(username).orElse(null);

        // 2. Se não encontrou um médico, tenta encontrar um paciente
        if (usuario == null) {
            usuario = pacienteRepository.findByEmail(username).orElse(null);
        }

        // 3. Se não encontrou em nenhum dos dois, lança a exceção
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + username);
        }

        return usuario; // Retorna o médico ou paciente encontrado
    }
}