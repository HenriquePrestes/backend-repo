package plataformaSaude.specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import plataformaSaude.model.Medico;
import java.util.ArrayList;
import java.util.List;

public class MedicoSpecification {

    public static Specification<Medico> withFilter(String nome, String crm, String especialidade, String termoBusca) {
        return (Root<Medico> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por nome específico
            if (nome != null && !nome.isBlank()) {
                String nomeLike = "%" + nome.toLowerCase() + "%";
                predicates.add(builder.like(builder.lower(root.get("nomeCompleto")), nomeLike));
            }

            // Filtro por CRM específico
            if (crm != null && !crm.isBlank()) {
                String crmLike = "%" + crm.toLowerCase() + "%";
                predicates.add(builder.like(builder.lower(root.get("crm")), crmLike));
            }

            // Filtro por especialidade específica
            if (especialidade != null && !especialidade.isBlank()) {
                String especialidadeLike = "%" + especialidade.toLowerCase() + "%";
                predicates.add(builder.like(builder.lower(root.join("especialidade").get("nome")), especialidadeLike));
            }

            // Busca geral (termoBusca) que procura em todos os campos
            if (termoBusca != null && !termoBusca.isBlank()) {
                String termoLike = "%" + termoBusca.toLowerCase() + "%";
                // Combina Nome, CRM e Especialidade em um Predicate OR
                Predicate fullTextPredicate = builder.or(
                        builder.like(builder.lower(root.get("nomeCompleto")), termoLike),
                        builder.like(builder.lower(root.join("especialidade").get("nome")), termoLike),
                        builder.like(builder.lower(root.get("crm")), termoLike)
                );

                predicates.add(fullTextPredicate);
            }

            // Combina todos os filtros usando AND.
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
