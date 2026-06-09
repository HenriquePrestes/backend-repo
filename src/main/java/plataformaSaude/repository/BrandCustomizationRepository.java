package plataformaSaude.repository;

import plataformaSaude.entity.BrandCustomization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandCustomizationRepository extends JpaRepository<BrandCustomization, Long> {

    // Método para buscar a "primeira" configuração.
    // Como só teremos uma, isso funciona.
    Optional<BrandCustomization> findFirstByOrderByIdAsc();
}
