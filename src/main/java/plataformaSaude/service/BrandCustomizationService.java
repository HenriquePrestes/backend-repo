package plataformaSaude.service;

import plataformaSaude.entity.BrandCustomization;
import plataformaSaude.repository.BrandCustomizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class BrandCustomizationService {

    @Autowired
    private BrandCustomizationRepository repository;

    // Vamos injetar o caminho da pasta de uploads do nosso application.properties
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Busca a personalização atual. Se não existir, retorna uma nova.
     */
    public BrandCustomization getCustomization() {
        // Busca a primeira configuração salva. Se não houver, cria uma vazia.
        return repository.findFirstByOrderByIdAsc().orElse(new BrandCustomization());
    }

    /**
     * Salva ou atualiza a personalização.
     */
    public BrandCustomization saveCustomization(String clinicName, String primaryColor, String secondaryColor, MultipartFile logoFile) throws IOException {

        // Pega a personalização existente ou cria uma nova
        BrandCustomization customization = repository.findFirstByOrderByIdAsc().orElse(new BrandCustomization());

        customization.setClinicName(clinicName);
        customization.setPrimaryColor(primaryColor);
        customization.setSecondaryColor(secondaryColor);

        // Lógica para salvar o logo
        if (logoFile != null && !logoFile.isEmpty()) {
            // Garante que o diretório de uploads exista
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gera um nome de arquivo único para evitar conflitos
            String originalFilename = logoFile.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Salva o arquivo no sistema de arquivos
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(logoFile.getInputStream(), filePath);

            // Salva apenas o *nome* do arquivo no banco
            customization.setLogoPath(uniqueFilename);
        }

        return repository.save(customization);
    }
}