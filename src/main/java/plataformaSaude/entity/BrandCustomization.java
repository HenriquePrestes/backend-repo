package plataformaSaude.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data // Anotação do Lombok (cria getters, setters, toString, etc.)
public class BrandCustomization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clinicName;
    private String primaryColor;
    private String secondaryColor;

    // Não vamos salvar o logo no banco. Vamos salvar apenas o
    // nome do arquivo (ou caminho) e salvar o arquivo em uma pasta no servidor.
    // na prática vamos verificar como isso vai funcionar
    private String logoPath;
}