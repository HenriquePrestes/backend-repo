package plataformaSaude.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import plataformaSaude.entity.BrandCustomization;
import plataformaSaude.repository.BrandCustomizationRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandCustomizationServiceTest {

    @Mock
    BrandCustomizationRepository repository;

    @InjectMocks
    BrandCustomizationService service;

    @Test
    void getCustomization_DeveRetornarNovaQuandoNaoExiste() {

        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        BrandCustomization result = service.getCustomization();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
    }
    @Test
    void getCustomization_DeveRetornarExistenteQuandoExiste() {

        BrandCustomization existing = new BrandCustomization();
        existing.setId(20L);

        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(existing));

        BrandCustomization result = service.getCustomization();

        assertThat(result).isSameAs(existing);
    }
    @Test
    void saveCustomization_DeveSalvarSemArquivo() throws Exception {

        BrandCustomization existing = new BrandCustomization();
        existing.setId(30L);

        // quando buscar, retorna existente
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(existing));

        // quando salvar, retorna o que foi salvo
        when(repository.save(any(BrandCustomization.class))).thenAnswer(inv -> inv.getArgument(0));

        BrandCustomization result = service.saveCustomization(
                "Clinica ABC", "#ffffff", "#000000", null
        );

        assertThat(result.getClinicName()).isEqualTo("Clinica ABC");
        assertThat(result.getPrimaryColor()).isEqualTo("#ffffff");
        assertThat(result.getSecondaryColor()).isEqualTo("#000000");
        verify(repository).save(any(BrandCustomization.class));
    }
    @Test
    void saveCustomization_DeveSalvarComArquivo() throws Exception {
        //diretório temporário
        Path tempDir = Files.createTempDirectory("upload-test-");

        // injeta o uploadDir direto via reflexão
        java.lang.reflect.Field field = BrandCustomizationService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(service, tempDir.toString());

        // mocks
        BrandCustomization existing = new BrandCustomization();
        existing.setId(30L);

        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(existing));
        when(repository.save(any(BrandCustomization.class))).thenAnswer(inv -> inv.getArgument(0));

        // simula um MultipartFile fake
        MultipartFile fakeFile = mock(MultipartFile.class);
        when(fakeFile.isEmpty()).thenReturn(false);
        when(fakeFile.getOriginalFilename()).thenReturn("logo.png");
        when(fakeFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("fakecontent".getBytes()));

        BrandCustomization result = service.saveCustomization(
                "Clinica XYZ", "#111111", "#222222", fakeFile
        );

        assertThat(result.getLogoPath()).isNotNull();
        assertThat(result.getLogoPath()).contains(".png"); // extensão preservada

        verify(repository).save(any(BrandCustomization.class));

        Path savedFile = tempDir.resolve(result.getLogoPath());
        assertThat(Files.exists(savedFile)).isTrue();
    }
}
