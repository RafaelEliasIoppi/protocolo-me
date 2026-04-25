package back.backend.controller;

import back.backend.dto.EstatisticaProtocoloMEDTO;
import back.backend.service.EstatisticaProtocoloMEService;
import back.backend.service.EstatisticasTransplantesService;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estatisticas-transplantes")
@CrossOrigin(origins = "*")
public class EstatisticasTransplantesController {

    private final EstatisticasTransplantesService estatisticasService;
    private final EstatisticaProtocoloMEService estatisticaProtocoloMEService;

    public EstatisticasTransplantesController(
            EstatisticasTransplantesService estatisticasService,
            EstatisticaProtocoloMEService estatisticaProtocoloMEService) {
        this.estatisticasService = estatisticasService;
        this.estatisticaProtocoloMEService = estatisticaProtocoloMEService;
    }

    @GetMapping("/gerais")
    public ResponseEntity<EstatisticasTransplantesService.EstatisticasGeradasTransplante> obterEstatisticasGerais(
            @RequestParam(required = false) Integer ano) {

        return ResponseEntity.ok(estatisticasService.obterEstatisticasGerais(ano));
    }

    @GetMapping("/por-paciente")
    public ResponseEntity<List<EstatisticasTransplantesService.PacienteDoacaoInfo>> obterEstatisticasPorPaciente(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String nomePaciente,
            @RequestParam(required = false) String receptor) {

        return ResponseEntity.ok(estatisticasService.obterEstatisticasPorPaciente(ano, nomePaciente, receptor));
    }

    @GetMapping("/anos-disponiveis")
    public ResponseEntity<List<Integer>> obterAnosDisponiveis() {
        return ResponseEntity.ok(estatisticasService.obterAnosDisponiveis());
    }

    @GetMapping("/protocolo-me")
    public ResponseEntity<List<EstatisticaProtocoloMEDTO>> listarEstatisticasPorProtocolo(
            @RequestParam(defaultValue = "ANUAL") String periodicidade,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {

        return ResponseEntity.ok(
                estatisticaProtocoloMEService.listarPorPeriodicidade(periodicidade, ano, mes)
        );
    }

    @GetMapping("/protocolo-me/{protocoloId}")
    public ResponseEntity<EstatisticaProtocoloMEDTO> obterEstatisticaPorProtocolo(
            @PathVariable Long protocoloId) {

        return ResponseEntity.ok(
                estatisticaProtocoloMEService.obterPorProtocoloId(protocoloId)
        );
    }

    @PutMapping("/protocolo-me/{protocoloId}")
    public ResponseEntity<EstatisticaProtocoloMEDTO> salvarEstatisticaPorProtocolo(
            @PathVariable Long protocoloId,
            @Valid @RequestBody EstatisticaProtocoloMEDTO payload) {

        return ResponseEntity.ok(
                estatisticaProtocoloMEService.salvarOuAtualizar(protocoloId, payload)
        );
    }

    @GetMapping("/protocolo-me/auditoria")
    public ResponseEntity<List<EstatisticaProtocoloMEService.ProtocoloSemEstatisticaDTO>> listarProtocolosSemEstatistica(
            @RequestParam(required = false) Integer ano) {

        return ResponseEntity.ok(
                estatisticaProtocoloMEService.listarProtocolosSemEstatistica(ano)
        );
    }
}
