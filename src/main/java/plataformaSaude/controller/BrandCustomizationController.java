package plataformaSaude.controller;

import plataformaSaude.entity.BrandCustomization;
import plataformaSaude.service.BrandCustomizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/customization")
@CrossOrigin(origins = "*") // Permite chamadas de qualquer frontend (bom para dev)
public class BrandCustomizationController {

    @Autowired
    private BrandCustomizationService service;

    /**
     * Endpoint para BUSCAR a personalização atual.
     * O frontend chama este endpoint quando a página carrega.
     */
    @GetMapping
    public ResponseEntity<BrandCustomization> getCustomization() {
        BrandCustomization customization = service.getCustomization();
        return ResponseEntity.ok(customization);
    }

    /**
     * Endpoint para SALVAR a personalização.
     * O frontend envia os dados como 'multipart/form-data'.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BrandCustomization> saveCustomization(
            @RequestParam("clinicName") String clinicName,
            @RequestParam("primaryColor") String primaryColor,
            @RequestParam("secondaryColor") String secondaryColor,
            @RequestParam(value = "logo", required = false) MultipartFile logoFile) {

        try {
            BrandCustomization saved = service.saveCustomization(
                    clinicName,
                    primaryColor,
                    secondaryColor,
                    logoFile
            );
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            // Em um app real, trate a exceção de forma mais robusta
            return ResponseEntity.status(500).body(null);
        }
    }
}