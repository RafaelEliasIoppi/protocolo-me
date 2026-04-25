package back.backend.controller;

import back.backend.dto.EstatisticaProtocoloMEDTO;
import back.backend.service.EstatisticaProtocoloMEService;
import back.backend.service.EstatisticasTransplantesService;

import jakarta.validation.Valid;
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

    // 🔹 Estatísticas gerais por ano
    @GetMapping("/gerais")
    public ResponseEntity<EstatisticasTransplantesService.EstatisticasGeradasTransplante> obterEstatisticasGerais(
            @RequestParam(required = false) Integer ano) {

        return ResponseEntity.ok(estatisticasService.obterEstatisticasGerais(ano));
    }

    // 🔹 Estatísticas por paciente (por ano)
    @GetMapping("/por-paciente")
    public ResponseEntity<List<EstatisticasTransplantesService.PacienteDoacaoInfo>> obterPorAno(
            @RequestParam(required = false) Integer ano) {

        return ResponseEntity.ok(estatisticasService.obterEstatisticasPorPaciente(ano));
    }

    // 🔹 Buscar por nome do paciente
    @GetMapping("/por-paciente/nome")
    public ResponseEntity<List<EstatisticasTransplantesService.PacienteDoacaoInfo>> obterPorNomePaciente(
            @RequestParam String nomePaciente) {

        return ResponseEntity.ok(estatisticasService.buscarPorNomePaciente(nomePaciente));
    }

    // 🔹 Buscar por nome do receptor
    @GetMapping("/por-receptor")
    public ResponseEntity<List<EstatisticasTransplantesService.PacienteDoacaoInfo>> obterPorNomeReceptor(
            @RequestParam String nomeReceptor) {

        return ResponseEntity.ok(estatisticasService.buscarPorNomeReceptor(nomeReceptor));
    }

    // 🔹 Anos disponíveis
    @GetMapping("/anos-disponiveis")
    public ResponseEntity<List<Integer>> obterAnosDisponiveis() {
        return ResponseEntity.ok(estatisticasService.obterAnosDisponiveis());
    }

    // 🔹 Estatísticas do protocolo ME (com filtro por periodicidade/ano/mês)
    @GetMapping("/protocolo-me")
    public ResponseEntity<List<EstatisticaProtocoloMEDTO>> listarEstatisticasPorProtocolo(
            @RequestParam(defaultValue = "ANUAL") String periodicidade,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {

        return ResponseEntity.ok(
                estatisticaProtocoloMEService.listarPorPeriodicidade(periodicidade, ano, mes)
        );
    }

    // 🔹 Obter estatística de um protocolo específico
    @GetMapping("/protocolo-me/{protocoloId}")
    public ResponseEntity<EstatisticaProtocoloMEDTO> obterEstatisticaPorProtocolo(
            @PathVariable Long protocoloId) {

        return ResponseEntity.ok(
                estatisticaProtocoloMEService.obterPorProtocoloId(protocoloId)
        );
    }

    // 🔹 Salvar/atualizar estatística de um protocolo
    @PutMapping("/protocolo-me/{protocoloId}")
    public ResponseEntity<EstatisticaProtocoloMEDTO> salvarEstatisticaPorProtocolo(
            @PathVariable Long protocoloId,
            @Valid @RequestBody EstatisticaProtocoloMEDTO payload) {

        return ResponseEntity.ok(
                estatisticaProtocoloMEService.salvarOuAtualizar(protocoloId, payload)
        );
    }

    // 🔹 Auditoria: protocolos sem estatística
    @GetMapping("/protocolo-me/auditoria")
    public ResponseEntity<List<EstatisticaProtocoloMEService.ProtocoloSemEstatisticaDTO>> listarProtocolosSemEstatistica(
            @RequestParam(required = false) Integer ano) {

        return ResponseEntity.ok(
                estatisticaProtocoloMEService.listarProtocolosSemEstatistica(ano)
        );
    }
}
