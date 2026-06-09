package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plataformaSaude.dto.BloqueioHorarioDTO;
import plataformaSaude.model.BloqueioHorario;
import plataformaSaude.model.Medico;
import plataformaSaude.repository.BloqueioHorarioRepository;
import plataformaSaude.repository.MedicoRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BloqueioHorarioServiceTest {

    @Mock
    BloqueioHorarioRepository bloqueioRepository;

    @Mock
    MedicoRepository medicoRepository;

    @InjectMocks
    BloqueioHorarioService bloqueioHorarioService;

    @Test
    void criarBloqueio_DeveCriarComSucesso() {

        Long medicoId = 5L;
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(2);

        BloqueioHorarioDTO dto = new BloqueioHorarioDTO();
        dto.setMedicoId(medicoId);
        dto.setDataInicio(inicio);
        dto.setDataFim(fim);
        dto.setMotivo("Férias <script>");

        Medico medico = new Medico();
        medico.setId(medicoId);

        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));
        when(bloqueioRepository.save(any(BloqueioHorario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BloqueioHorario result = bloqueioHorarioService.criarBloqueio(dto);

        assertThat(result.getMedico()).isEqualTo(medico);
        assertThat(result.getDataInicio()).isEqualTo(inicio);
        assertThat(result.getDataFim()).isEqualTo(fim);
        assertThat(result.getMotivo()).doesNotContain("<script>"); // sanitização aplicada
        verify(bloqueioRepository).save(any());
    }
    @Test
    void criarBloqueio_DeveLancarException_QuandoDatasInvalidas() {

        Long medicoId = 5L;
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio; // igual → inválido

        BloqueioHorarioDTO dto = new BloqueioHorarioDTO();
        dto.setMedicoId(medicoId);
        dto.setDataInicio(inicio);
        dto.setDataFim(fim);

        // act + assert
        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> bloqueioHorarioService.criarBloqueio(dto)
        );
    }
    @Test
    void criarBloqueio_DeveLancarException_QuandoMedicoNaoExiste() {

        Long medicoId = 999L;

        BloqueioHorarioDTO dto = new BloqueioHorarioDTO();
        dto.setMedicoId(medicoId);
        dto.setDataInicio(LocalDateTime.now().plusDays(1));
        dto.setDataFim(LocalDateTime.now().plusDays(2));

        when(medicoRepository.findById(medicoId)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> bloqueioHorarioService.criarBloqueio(dto)
        );
    }
    @Test
    void listarTodos_DeveChamarFindAll() {
        Pageable pageable = mock(Pageable.class);
        Page<BloqueioHorario> page = mock(Page.class);

        when(bloqueioRepository.findAll(pageable)).thenReturn(page);

        Page<BloqueioHorario> result = bloqueioHorarioService.listarTodos(pageable);

        assertThat(result).isEqualTo(page);
        verify(bloqueioRepository).findAll(pageable);
    }
    @Test
    void buscarPorId_DeveRetornarOptional() {
        Long id = 15L;
        Optional<BloqueioHorario> expected = Optional.of(new BloqueioHorario());

        when(bloqueioRepository.findById(id)).thenReturn(expected);

        Optional<BloqueioHorario> result = bloqueioHorarioService.buscarPorId(id);

        assertThat(result).isEqualTo(expected);
        verify(bloqueioRepository).findById(id);
    }
    @Test
    void buscarPorMedico_DeveRetornarLista_QuandoMedicoExiste() {
        Long medicoId = 1L;

        when(medicoRepository.existsById(medicoId)).thenReturn(true);

        bloqueioHorarioService.buscarPorMedico(medicoId);

        verify(bloqueioRepository).findByMedicoId(medicoId);
    }
    @Test
    void buscarPorMedico_DeveLancarException_QuandoMedicoNaoExiste() {
        Long medicoId = 99L;

        when(medicoRepository.existsById(medicoId)).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> bloqueioHorarioService.buscarPorMedico(medicoId)
        );
    }
    @Test
    void atualizarBloqueio_DeveAtualizarComSucesso_QuandoNaoHaErros() {

        Long id = 10L;
        Long medicoId = 2L;
        LocalDateTime inicio = LocalDateTime.now().plusDays(2);
        LocalDateTime fim = LocalDateTime.now().plusDays(3);

        BloqueioHorarioDTO dto = new BloqueioHorarioDTO();
        dto.setMedicoId(medicoId);
        dto.setDataInicio(inicio);
        dto.setDataFim(fim);
        dto.setMotivo("<b>teste</b>");

        BloqueioHorario bloqueioOriginal = new BloqueioHorario();
        bloqueioOriginal.setId(id);

        Medico medico = new Medico();
        medico.setId(medicoId);

        when(bloqueioRepository.findById(id)).thenReturn(Optional.of(bloqueioOriginal));
        when(medicoRepository.findById(medicoId)).thenReturn(Optional.of(medico));

        when(bloqueioRepository.save(any(BloqueioHorario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BloqueioHorario updated = bloqueioHorarioService.atualizarBloqueio(id, dto);

        assertThat(updated.getMedico()).isEqualTo(medico);
        assertThat(updated.getDataInicio()).isEqualTo(inicio);
        assertThat(updated.getDataFim()).isEqualTo(fim);
        assertThat(updated.getMotivo()).doesNotContain("<b>");
    }
    @Test
    void atualizarBloqueio_DeveLancarException_QuandoIdNaoExiste() {

        when(bloqueioRepository.findById(any())).thenReturn(Optional.empty());

        BloqueioHorarioDTO dto = new BloqueioHorarioDTO();
        dto.setMedicoId(1L);
        dto.setDataInicio(LocalDateTime.now().plusDays(1));
        dto.setDataFim(LocalDateTime.now().plusDays(2));

        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> bloqueioHorarioService.atualizarBloqueio(50L, dto)
        );
    }
    @Test
    void deletarBloqueio_DeveDeletar_QuandoExiste() {

        Long id = 20L;

        when(bloqueioRepository.existsById(id)).thenReturn(true);

        bloqueioHorarioService.deletarBloqueio(id);

        verify(bloqueioRepository).deleteById(id);
    }
    @Test
    void deletarBloqueio_DeveLancarException_QuandoNaoExiste() {

        Long id = 20L;

        when(bloqueioRepository.existsById(id)).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> bloqueioHorarioService.deletarBloqueio(id)
        );
    }
}
