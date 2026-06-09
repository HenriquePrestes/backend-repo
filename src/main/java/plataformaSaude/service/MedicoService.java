package plataformaSaude.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plataformaSaude.dto.HorarioDTO;
import plataformaSaude.dto.MedicoDTO;
import plataformaSaude.dto.MedicoListaDTO;
import plataformaSaude.model.Especialidade;
import plataformaSaude.model.Horario;
import plataformaSaude.model.Medico;
import plataformaSaude.repository.EspecialidadeRepository;
import plataformaSaude.repository.MedicoRepository;
import plataformaSaude.repository.HorarioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plataformaSaude.specification.MedicoSpecification;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final HorarioRepository horarioRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MedicoService(MedicoRepository medicoRepository,
                         HorarioRepository horarioRepository,
                         EspecialidadeRepository especialidadeRepository,
                         PasswordEncoder passwordEncoder) {
        this.medicoRepository = medicoRepository;
        this.horarioRepository = horarioRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //MÉTODO DE PAGINAÇÃO
    public Page<Medico> listarTodos(
            Pageable pageable,
            String nome,
            String crm,
            String especialidade,
            String termoBusca
    ) {
        // Passa o novo termoBusca para a Specification
        Specification<Medico> spec = MedicoSpecification.withFilter(nome, crm, especialidade, termoBusca);

        // A busca continua usando findAll(spec, pageable)
        return medicoRepository.findAll(spec, pageable);
    }

    // MÉTODO DE PAGINAÇÃO COM DTO (para evitar problemas de serialização)
    public Page<MedicoListaDTO> listarTodosDTO(
            Pageable pageable,
            String nome,
            String crm,
            String especialidade,
            String termoBusca
    ) {
        // Usa a mesma lógica de busca
        Page<Medico> medicosPage = listarTodos(pageable, nome, crm, especialidade, termoBusca);
        
        // Converte para DTOs
        return medicosPage.map(this::convertToListaDTO);
    }

    // MÉTODO SEM PAGINAÇÃO (para compatibilidade)
    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    // MÉTODO PARA LISTAGEM SIMPLIFICADA (sem referências circulares)
    public List<MedicoListaDTO> listarTodosDTO() {
        List<Medico> medicos = medicoRepository.findAll();
        return medicos.stream()
                .map(this::convertToListaDTO)
                .collect(Collectors.toList());
    }

    private MedicoListaDTO convertToListaDTO(Medico medico) {
        MedicoListaDTO dto = new MedicoListaDTO();
        dto.setId(medico.getId());
        dto.setNomeCompleto(medico.getNomeCompleto());
        dto.setCpf(medico.getCpf());
        dto.setEmail(medico.getEmail());
        dto.setCelular(medico.getCelular());
        dto.setDataNascimento(medico.getDataNascimento());
        dto.setDataCadastro(medico.getDataCadastro());
        
        // Dados de endereço
        dto.setCep(medico.getCep());
        dto.setRua(medico.getRua());
        dto.setNumero(medico.getNumero());
        dto.setComplemento(medico.getComplemento());
        dto.setCidade(medico.getCidade());
        dto.setEstado(medico.getEstado());
        
        // Dados profissionais
        dto.setCrm(medico.getCrm());
        if (medico.getEspecialidade() != null) {
            dto.setEspecialidade(medico.getEspecialidade().getNome());
            dto.setEspecialidadeId(medico.getEspecialidade().getId());
        }
        dto.setFoto(medico.getFoto());
        dto.setDuracaoConsultaMinutos(medico.getDuracaoConsultaMinutos());

        // Converte horários
        if (medico.getHorariosTrabalho() != null) {
            List<MedicoListaDTO.HorarioDTO> horariosDTO = medico.getHorariosTrabalho().stream()
                    .map(horario -> new MedicoListaDTO.HorarioDTO(
                            horario.getId(),
                            horario.getDescricao(),
                            horario.getDiaSemana(),
                            horario.getHoraInicio(),
                            horario.getHoraFim()
                    ))
                    .collect(Collectors.toList());
            dto.setHorariosTrabalho(horariosDTO);
        }

        return dto;
    }

    public Optional<Medico> buscarPorId(Long id) {
        return medicoRepository.findById(id);
    }
    @Transactional // Garante que tudo (Médico e Horários) seja salvo junto
    public Medico criarMedico(MedicoDTO dto) {
        Medico medico = new Medico();

        // Copia dados do Usuario + Medico
        medico.setNomeCompleto(dto.getNomeCompleto());
        medico.setCpf(dto.getCpf());
        medico.setEmail(dto.getEmail());
        medico.setSenha(passwordEncoder.encode(dto.getSenha())); // Codifica a senha
        medico.setCelular(dto.getCelular());
        medico.setDataNascimento(dto.getDataNascimento());
        medico.setTipoUsuario("MEDICO"); // Define o tipo de usuário
        
        // Dados de endereço
        medico.setCep(dto.getCep());
        medico.setRua(dto.getRua());
        medico.setNumero(dto.getNumero());
        medico.setComplemento(dto.getComplemento());
        medico.setCidade(dto.getCidade());
        medico.setEstado(dto.getEstado());
        
        // Dados profissionais
        medico.setCrm(dto.getCrm());
        
        // Busca a especialidade pelo ID
        if (dto.getEspecialidadeId() != null) {
            Especialidade especialidade = especialidadeRepository.findById(dto.getEspecialidadeId())
                    .orElseThrow(() -> new RuntimeException("Especialidade não encontrada com ID: " + dto.getEspecialidadeId()));
            medico.setEspecialidade(especialidade);
        }
        
        medico.setFoto(dto.getFoto());
        medico.setDuracaoConsultaMinutos(dto.getDuracaoConsultaMinutos());

        // Converte os HorarioDTOs em Entidades Horario
        List<Horario> horarios = new ArrayList<>();
        if (dto.getHorariosTrabalho() != null) {
            for (HorarioDTO horarioDTO : dto.getHorariosTrabalho()) {
                Horario horario = new Horario();
                horario.setDescricao(horarioDTO.getDescricao());
                horario.setDiaSemana(horarioDTO.getDiaSemana());
                horario.setHoraInicio(horarioDTO.getHoraInicio());
                horario.setHoraFim(horarioDTO.getHoraFim());
                horario.setMedico(medico); // +++ LIGAÇÃO IMPORTANTE +++
                horarios.add(horario);
            }
        }
        medico.setHorariosTrabalho(horarios);

        // Salva o médico. O CascadeType.ALL salvará os horários juntos.
        return medicoRepository.save(medico);
    }

    @Transactional
    public Medico atualizarMedico(Long id, MedicoDTO dto) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado com ID: " + id));

        // Atualiza dados apenas se não forem nulos
        if (dto.getNomeCompleto() != null) {
            medico.setNomeCompleto(dto.getNomeCompleto());
        }
        if (dto.getCpf() != null) {
            medico.setCpf(dto.getCpf());
        }
        if (dto.getEmail() != null) {
            medico.setEmail(dto.getEmail());
        }
        if (dto.getCelular() != null) {
            medico.setCelular(dto.getCelular());
        }
        if (dto.getDataNascimento() != null) {
            medico.setDataNascimento(dto.getDataNascimento());
        }
        
        // Atualiza dados de endereço apenas se não forem nulos
        if (dto.getCep() != null) {
            medico.setCep(dto.getCep());
        }
        if (dto.getRua() != null) {
            medico.setRua(dto.getRua());
        }
        if (dto.getNumero() != null) {
            medico.setNumero(dto.getNumero());
        }
        if (dto.getComplemento() != null) {
            medico.setComplemento(dto.getComplemento());
        }
        if (dto.getCidade() != null) {
            medico.setCidade(dto.getCidade());
        }
        if (dto.getEstado() != null) {
            medico.setEstado(dto.getEstado());
        }
        
        // Atualiza dados profissionais apenas se não forem nulos
        if (dto.getCrm() != null) {
            medico.setCrm(dto.getCrm());
        }
        
        // Busca a especialidade pelo ID
        if (dto.getEspecialidadeId() != null) {
            Especialidade especialidade = especialidadeRepository.findById(dto.getEspecialidadeId())
                    .orElseThrow(() -> new RuntimeException("Especialidade não encontrada com ID: " + dto.getEspecialidadeId()));
            medico.setEspecialidade(especialidade);
        }
        
        if (dto.getFoto() != null) {
            medico.setFoto(dto.getFoto());
        }
        if (dto.getDuracaoConsultaMinutos() > 0) {
            medico.setDuracaoConsultaMinutos(dto.getDuracaoConsultaMinutos());
        }

        // Lógica de atualização de horários:
        // A forma mais fácil é remover os antigos e adicionar os novos.
        // (Graças ao orphanRemoval=true no @OneToMany do Medico)
        medico.getHorariosTrabalho().clear();

        if (dto.getHorariosTrabalho() != null) {
            for (HorarioDTO horarioDTO : dto.getHorariosTrabalho()) {
                Horario horario = new Horario();
                horario.setDescricao(horarioDTO.getDescricao());
                horario.setDiaSemana(horarioDTO.getDiaSemana());
                horario.setHoraInicio(horarioDTO.getHoraInicio());
                horario.setHoraFim(horarioDTO.getHoraFim());
                horario.setMedico(medico); // Liga o novo horário ao médico
                medico.getHorariosTrabalho().add(horario);
            }
        }

        return medicoRepository.save(medico);
    }

    public void deletarMedico(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new RuntimeException("Médico não encontrado com ID: " + id);
        }
        medicoRepository.deleteById(id);
    }

    public List<Horario> buscarHorariosPorMedico(Long medicoId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado com ID: " + medicoId));
        return medico.getHorariosTrabalho();
    }
}