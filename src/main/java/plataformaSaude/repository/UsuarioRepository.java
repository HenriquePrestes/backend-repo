package plataformaSaude.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import plataformaSaude.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    List<Usuario> findAllByEmail(@Param("email") String email);
    
    Optional<Usuario> findFirstByEmailOrderByIdAsc(String email);
    
    // Keep the original method for backward compatibility, but now it will return the first result only
    default Usuario findByEmail(String email) {
        return findFirstByEmailOrderByIdAsc(email).orElse(null);
    }

    Usuario findByCpf(String cpf);

    Optional<Usuario> findByResetPasswordToken(String token);
}
