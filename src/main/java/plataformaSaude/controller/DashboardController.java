package plataformaSaude.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plataformaSaude.dto.DashboardDTO;
import plataformaSaude.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*") //
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Endpoint para buscar as métricas/estatísticas simples da plataforma.
     */
    @GetMapping("/metrics")
    public ResponseEntity<DashboardDTO> getMetrics() {
        // Chama o serviço criado no Passo 3
        DashboardDTO metrics = dashboardService.getMetrics();

        // Retorna o DTO criado no Passo 1
        return ResponseEntity.ok(metrics);
    }
}