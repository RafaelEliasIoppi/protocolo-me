package back.backend.controller;

import back.backend.dto.OrgaoDoadoDTO;
import back.backend.dto.ProtocoloMEDTO;
import back.backend.model.ProtocoloME;
import back.backend.service.ProtocoloMEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/protocolos-me")
@CrossOrigin(origins = "*")
public class ProtocoloMEController {

    @Autowired
    private ProtocoloMEService protocoloService;

    // POST - Criar novo protocolo de ME
    @PostMapping
    public ResponseEntity<?> criarProtocolo(@RequestBody Map<String, Object> payload) {
        try {
            Object pacienteIdObj = payload.get("pacienteId");
            if (pacienteIdObj == null && payload.get("paciente") instanceof Map) {
                Object nestedId = ((Map<?, ?>) payload.get("paciente")).get("id");
                pacienteIdObj = nestedId;
            }

            if (pacienteIdObj == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("mensagem", "pacienteId é obrigatório"));
            }

            Long pacienteId = Long.valueOf(String.valueOf(pacienteIdObj));
            String diagnosticoBasico = payload.get("diagnosticoBasico") != null
                    ? String.valueOf(payload.get("diagnosticoBasico"))
                    : null;

                String numeroProtocolo = payload.get("numeroProtocolo") != null
                    ? String.valueOf(payload.get("numeroProtocolo"))
                    : null;

                ProtocoloME novoProtocolo = protocoloService.criarProtocoloPorPacienteId(pacienteId, diagnosticoBasico, numeroProtocolo);
                return ResponseEntity.status(HttpStatus.CREATED).body(dtoFromEntity(novoProtocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensagem", e.getMessage()));
        }
    }

    // GET - Listar todos os protocolos
    @GetMapping
    public ResponseEntity<List<ProtocoloMEDTO>> listarTodos() {
        List<ProtocoloME> protocolos = protocoloService.listarTodos();
        List<ProtocoloMEDTO> dtos = protocolos.stream()
            .map(ProtocoloMEDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Buscar protocolo por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<ProtocoloME> protocolo = protocoloService.buscarPorId(id);
        return protocolo.map(value -> ResponseEntity.ok(dtoFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET - Buscar por número do protocolo
    @GetMapping("/numero/{numeroProtocolo}")
    public ResponseEntity<?> buscarPorNumero(@PathVariable String numeroProtocolo) {
        Optional<ProtocoloME> protocolo = protocoloService.buscarPorNumeroProtocolo(numeroProtocolo);
        return protocolo.map(value -> ResponseEntity.ok(dtoFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET - Listar protocolos de uma central
    @GetMapping("/central/{centralId}")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorCentral(@PathVariable Long centralId) {
        try {
            List<ProtocoloME> protocolos = protocoloService.listarPorCentral(centralId);
            return ResponseEntity.ok(protocolos.stream().map(ProtocoloMEDTO::fromEntity).collect(Collectors.toList()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar por status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorStatus(@PathVariable String status) {
        try {
            ProtocoloME.StatusProtocoloME statusEnum = ProtocoloME.StatusProtocoloME.valueOf(status.toUpperCase());
            List<ProtocoloME> protocolos = protocoloService.listarPorStatus(statusEnum);
            return ResponseEntity.ok(protocolos.stream().map(ProtocoloMEDTO::fromEntity).collect(Collectors.toList()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET - Listar por central e status
    @GetMapping("/central/{centralId}/status/{status}")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorCentralEStatus(
            @PathVariable Long centralId,
            @PathVariable String status) {
        try {
            ProtocoloME.StatusProtocoloME statusEnum = ProtocoloME.StatusProtocoloME.valueOf(status.toUpperCase());
            List<ProtocoloME> protocolos = protocoloService.listarPorCentralEStatus(centralId, statusEnum);
            return ResponseEntity.ok(protocolos.stream().map(ProtocoloMEDTO::fromEntity).collect(Collectors.toList()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar por período
    @GetMapping("/periodo")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        List<ProtocoloME> protocolos = protocoloService.listarPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(protocolos.stream().map(ProtocoloMEDTO::fromEntity).collect(Collectors.toList()));
    }

    // GET - Listar por hospital origem
    @GetMapping("/hospital/{hospitalOrigem}")
    public ResponseEntity<List<ProtocoloMEDTO>> listarPorHospitalOrigem(@PathVariable String hospitalOrigem) {
        List<ProtocoloME> protocolos = protocoloService.listarPorHospitalOrigem(hospitalOrigem);
        return ResponseEntity.ok(protocolos.stream().map(ProtocoloMEDTO::fromEntity).collect(Collectors.toList()));
    }

    // PUT - Atualizar protocolo
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProtocolo(@PathVariable Long id, @RequestBody ProtocoloME protocolo) {
        try {
            ProtocoloME protocoloAtualizado = protocoloService.atualizarProtocolo(id, protocolo);
            return ResponseEntity.ok(dtoFromEntity(protocoloAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PATCH - Atualizar conclusao editavel do relatorio final do protocolo
    @PatchMapping("/{id}/relatorio-final")
    public ResponseEntity<?> atualizarRelatorioFinal(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        try {
            String texto = payload.get("textoRelatorio");
            String atualizadoPor = payload.get("atualizadoPor");
            ProtocoloME protocolo = protocoloService.atualizarRelatorioFinal(id, texto, atualizadoPor);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensagem", e.getMessage()));
        }
    }

    // POST - Registrar teste clínico 1
    @PostMapping("/{id}/teste-clinico-1")
    public ResponseEntity<?> registrarTesteClinico1(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.registrarTesteClinico1(id);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar teste clínico 2
    @PostMapping("/{id}/teste-clinico-2")
    public ResponseEntity<?> registrarTesteClinico2(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.registrarTesteClinico2(id);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar testes complementares
    @PostMapping("/{id}/testes-complementares")
    public ResponseEntity<?> registrarTestesComplementares(
            @PathVariable Long id,
            @RequestParam String testesComplementares) {
        try {
            ProtocoloME protocolo = protocoloService.registrarTestesComplementares(id, testesComplementares);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar notificação da família
    @PostMapping("/{id}/notificar-familia")
    public ResponseEntity<?> registrarNotificacaoFamilia(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.registrarNotificacaoFamilia(id);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Autorizar autópsia
    @PostMapping("/{id}/autorizar-autopsia")
    public ResponseEntity<?> autorizarAutopsia(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.autorizarAutopsia(id);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Registrar preservação de órgãos
    @PostMapping("/{id}/preservacao-orgaos")
    public ResponseEntity<?> registrarPreservacaoOrgaos(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.registrarPreservacaoOrgaos(id);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Confirmar morte cerebral
    @PostMapping("/{id}/confirmar-morte-cerebral")
    public ResponseEntity<?> confirmarMorteCerebral(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.confirmarMorteCerebral(id);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PATCH - Alterar status do protocolo
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            ProtocoloME.StatusProtocoloME novoStatus = ProtocoloME.StatusProtocoloME.valueOf(status.toUpperCase());
            ProtocoloME protocolo = protocoloService.alterarStatus(id, novoStatus);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Atualizar status automaticamente após inserção de exame
    @PostMapping("/{id}/atualizar-status")
    public ResponseEntity<?> atualizarStatusAutomatico(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.atualizarStatusAutomatico(id);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Marcar para entrevista familiar
    @PostMapping("/{id}/marcar-entrevista")
    public ResponseEntity<?> marcarParaEntrevista(@PathVariable Long id) {
        try {
            ProtocoloME protocolo = protocoloService.marcarParaEntrevista(id);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // POST - Registrar resultado da entrevista familiar
    @PostMapping("/{id}/resultado-entrevista")
    public ResponseEntity<?> registrarResultadoEntrevista(
            @PathVariable Long id,
            @RequestParam boolean autorizouDoacao,
            @RequestParam(required = false) String observacoes) {
        try {
            ProtocoloME protocolo = protocoloService.registrarResultadoEntrevista(id, autorizouDoacao, observacoes);
            return ResponseEntity.ok(dtoFromEntity(protocolo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Deletar protocolo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProtocolo(@PathVariable Long id) {
        try {
            protocoloService.deletarProtocolo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private ProtocoloMEDTO dtoFromEntity(ProtocoloME entity) {
        return ProtocoloMEDTO.fromEntity(entity);
    }
}
