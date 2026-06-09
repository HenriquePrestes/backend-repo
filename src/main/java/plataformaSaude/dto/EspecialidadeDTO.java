package plataformaSaude.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecialidadeDTO(
        Long id,
        
        @NotBlank(message = "O nome da especialidade é obrigatório")
        @Size(min = 2, max = 100, message = "O nome da especialidade deve ter entre 2 e 100 caracteres")
        String nome
) {}