package plataformaSaude.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import plataformaSaude.model.Paciente;
import plataformaSaude.repository.PacienteRepository;

import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    /* PAGINAÇÃO */
    public Page<Paciente> listarTodos(Pageable pageable) {
        return pacienteRepository.findAll(pageable);
    }

    // Métodos de CRUD que serão usados pelo Controller
    public Optional<Paciente> buscarPorEmail(String email) {
        return pacienteRepository.findByEmail(email);
    }

    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    public Paciente salvar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public void deletar(Long id) {
        pacienteRepository.deleteById(id);
    }
}