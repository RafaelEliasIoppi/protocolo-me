package back.backend.controller;

import back.backend.dto.EstatisticaProtocoloMEDTO;
import back.backend.service.EstatisticaProtocoloMEService;
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

    @Autowired
    private EstatisticaProtocoloMEService estatisticaProtocoloMEService;

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

    /**
     * GET - Listar estatísticas preenchidas por protocolo com opção mensal/anual
     */
    @GetMapping("/protocolo-me")
    public ResponseEntity<List<EstatisticaProtocoloMEDTO>> listarEstatisticasPorProtocolo(
            @RequestParam(required = false, defaultValue = "ANUAL") String periodicidade,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {
        List<EstatisticaProtocoloMEDTO> lista = estatisticaProtocoloMEService.listarPorPeriodicidade(periodicidade, ano, mes);
        return ResponseEntity.ok(lista);
    }

    /**
     * GET - Obter estatística de um protocolo específico
     */
    @GetMapping("/protocolo-me/{protocoloId}")
    public ResponseEntity<EstatisticaProtocoloMEDTO> obterEstatisticaPorProtocolo(
            @PathVariable Long protocoloId) {
        return ResponseEntity.ok(estatisticaProtocoloMEService.obterPorProtocoloId(protocoloId));
    }

    /**
     * PUT - Salvar/editar estatística de um protocolo (preenchimento da central)
     */
    @PutMapping("/protocolo-me/{protocoloId}")
    public ResponseEntity<EstatisticaProtocoloMEDTO> salvarEstatisticaPorProtocolo(
            @PathVariable Long protocoloId,
            @RequestBody EstatisticaProtocoloMEDTO payload) {
        return ResponseEntity.ok(estatisticaProtocoloMEService.salvarOuAtualizar(protocoloId, payload));
    }

    /**
     * GET - Auditoria de protocolos sem estatística preenchida
     */
    @GetMapping("/protocolo-me/auditoria")
    public ResponseEntity<List<EstatisticaProtocoloMEService.ProtocoloSemEstatisticaDTO>> listarProtocolosSemEstatistica(
            @RequestParam(required = false) Integer ano) {
        return ResponseEntity.ok(estatisticaProtocoloMEService.listarProtocolosSemEstatistica(ano));
    }
}
