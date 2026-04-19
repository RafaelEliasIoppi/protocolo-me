package back.backend.controller;

import back.backend.service.EstatisticasTransplantesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estatisticas-transplantes")
@CrossOrigin(origins = "*")
public class EstatisticasTransplantesController {

    @Autowired
    private EstatisticasTransplantesService estatisticasService;

    /**
     * GET - Obter estatísticas gerais com filtro por ano (opcional)
     */
    @GetMapping("/gerais")
    public ResponseEntity<EstatisticasTransplantesService.EstatisticasGeradasTransplante> obterEstatisticasGerais(
            @RequestParam(required = false) Integer ano) {
        EstatisticasTransplantesService.EstatisticasGeradasTransplante stats = 
                estatisticasService.obterEstatisticasGerais(ano);
        return ResponseEntity.ok(stats);
    }

    /**
     * GET - Obter estatísticas por paciente com filtro por ano (opcional)
     */
    @GetMapping("/por-paciente")
    public ResponseEntity<List<EstatisticasTransplantesService.PacienteDoacaoInfo>> obterEstatisticasPorPaciente(
            @RequestParam(required = false) Integer ano) {
        List<EstatisticasTransplantesService.PacienteDoacaoInfo> stats = 
                estatisticasService.obterEstatisticasPorPaciente(ano);
        return ResponseEntity.ok(stats);
    }

    /**
     * GET - Obter anos disponíveis para filtro
     */
    @GetMapping("/anos-disponiveis")
    public ResponseEntity<List<Integer>> obterAnosDisponiveis() {
        List<Integer> anos = estatisticasService.obterAnosaDisponiveis();
        return ResponseEntity.ok(anos);
    }
}
