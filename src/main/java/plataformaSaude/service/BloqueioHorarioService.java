package plataformaSaude.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import plataformaSaude.dto.BloqueioHorarioDTO;
import plataformaSaude.model.BloqueioHorario;
import plataformaSaude.model.Medico;
import plataformaSaude.repository.BloqueioHorarioRepository;
import plataformaSaude.repository.MedicoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plataformaSaude.security.HtmlSanitizerUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class BloqueioHorarioService {

    @Autowired
    private BloqueioHorarioRepository bloqueioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Valida se a data de fim é após a data de início.
     */
    private void validarDatas(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio.isAfter(fim) || inicio.isEqual(fim)) {
            throw new RuntimeException("A data/hora de fim deve ser após a data/hora de início.");
        }
    }

    public BloqueioHorario criarBloqueio(BloqueioHorarioDTO dto) {
        validarDatas(dto.getDataInicio(), dto.getDataFim());

        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        BloqueioHorario bloqueio = new BloqueioHorario();
        bloqueio.setMedico(medico);
        bloqueio.setDataInicio(dto.getDataInicio());
        bloqueio.setDataFim(dto.getDataFim());
        //bloqueio.setMotivo(dto.getMotivo());
        String motivoLimpo = HtmlSanitizerUtil.sanitize(dto.getMotivo());
        bloqueio.setMotivo(motivoLimpo); // Salva o dado limpo

        return bloqueioRepository.save(bloqueio);
    }

    public Page<BloqueioHorario> listarTodos(Pageable pageable) {
        // Usa o método findAll(Pageable) do JpaRepository
        return bloqueioRepository.findAll(pageable);
    }

    public Optional<BloqueioHorario> buscarPorId(Long id) {
        return bloqueioRepository.findById(id);
    }

    /**
     * Lista todos os bloqueios de um médico específico.
     */
    public List<BloqueioHorario> buscarPorMedico(Long medicoId) {
        if (!medicoRepository.existsById(medicoId)) {
            throw new RuntimeException("Médico não encontrado");
        }
        // Precisamos adicionar este método no repositório
        return bloqueioRepository.findByMedicoId(medicoId);
    }

    public BloqueioHorario atualizarBloqueio(Long id, BloqueioHorarioDTO dto) {
        BloqueioHorario bloqueio = bloqueioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bloqueio não encontrado"));

        validarDatas(dto.getDataInicio(), dto.getDataFim());

        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        bloqueio.setMedico(medico);
        bloqueio.setDataInicio(dto.getDataInicio());
        bloqueio.setDataFim(dto.getDataFim());
        //bloqueio.setMotivo(dto.getMotivo());
        String motivoLimpoAtt = HtmlSanitizerUtil.sanitize(dto.getMotivo());
        bloqueio.setMotivo(motivoLimpoAtt); // Salva o dado limpo

        return bloqueioRepository.save(bloqueio);
    }

    public void deletarBloqueio(Long id) {
        if (!bloqueioRepository.existsById(id)) {
            throw new RuntimeException("Bloqueio não encontrado");
        }
        bloqueioRepository.deleteById(id);
    }
}