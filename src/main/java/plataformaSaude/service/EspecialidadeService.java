package plataformaSaude.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plataformaSaude.dto.EspecialidadeDTO;
import plataformaSaude.model.Especialidade;
import plataformaSaude.repository.EspecialidadeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;

    @Autowired
    public EspecialidadeService(EspecialidadeRepository especialidadeRepository) {
        this.especialidadeRepository = especialidadeRepository;
    }

    public List<EspecialidadeDTO> listarTodas() {
        return especialidadeRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EspecialidadeDTO buscarPorId(Long id) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada com ID: " + id));
        return convertToDTO(especialidade);
    }

    @Transactional
    public EspecialidadeDTO criar(EspecialidadeDTO dto) {
        if (especialidadeRepository.existsByNome(dto.nome())) {
            throw new RuntimeException("Já existe uma especialidade com o nome: " + dto.nome());
        }

        Especialidade especialidade = new Especialidade(dto.nome());
        Especialidade savedEspecialidade = especialidadeRepository.save(especialidade);
        return convertToDTO(savedEspecialidade);
    }

    @Transactional
    public EspecialidadeDTO atualizar(Long id, EspecialidadeDTO dto) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada com ID: " + id));

        // Verificar se o nome já existe em outra especialidade
        if (especialidadeRepository.existsByNome(dto.nome()) && 
            !especialidade.getNome().equals(dto.nome())) {
            throw new RuntimeException("Já existe uma especialidade com o nome: " + dto.nome());
        }

        especialidade.setNome(dto.nome());
        Especialidade updatedEspecialidade = especialidadeRepository.save(especialidade);
        return convertToDTO(updatedEspecialidade);
    }

    @Transactional
    public void deletar(Long id) {
        if (!especialidadeRepository.existsById(id)) {
            throw new RuntimeException("Especialidade não encontrada com ID: " + id);
        }
        especialidadeRepository.deleteById(id);
    }

    private EspecialidadeDTO convertToDTO(Especialidade especialidade) {
        return new EspecialidadeDTO(
                especialidade.getId(),
                especialidade.getNome()
        );
    }

    private Especialidade convertToEntity(EspecialidadeDTO dto) {
        Especialidade especialidade = new Especialidade();
        especialidade.setId(dto.id());
        especialidade.setNome(dto.nome());
        return especialidade;
    }
}