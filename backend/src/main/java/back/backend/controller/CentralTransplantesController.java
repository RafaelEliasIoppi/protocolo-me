package back.backend.controller;

import back.backend.model.CentralTransplantes;
import back.backend.service.CentralTransplantesService;
import back.backend.dto.CentralTransplantesDTO;
import org.springframework.validation.BindingResult;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/centrais-transplantes")
public class CentralTransplantesController {

    @Autowired
    private CentralTransplantesService centralService;

    // POST - Criar nova central
    @PostMapping
    public ResponseEntity<?> criarCentral(@Valid @RequestBody CentralTransplantesDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("erro", result.getAllErrors().get(0).getDefaultMessage()));
        }
        try {
            CentralTransplantes novaCentral = centralService.criarCentralFromDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoFromEntity(novaCentral));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // GET - Listar todas as centrais
    @GetMapping
    public ResponseEntity<?> listarTodas() {
        List<CentralTransplantes> centrais = centralService.listarTodas();
        List<CentralTransplantesDTO> dtos = centrais.stream().map(this::dtoFromEntity).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Buscar central por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<CentralTransplantes> central = centralService.buscarPorId(id);
        return central.<ResponseEntity<?>>map(c -> ResponseEntity.ok(dtoFromEntity(c)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Central não encontrada")));
    }

    // GET - Buscar por CNPJ
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<?> buscarPorCnpj(@PathVariable String cnpj) {
        Optional<CentralTransplantes> central = centralService.buscarPorCnpj(cnpj);
        return central.<ResponseEntity<?>>map(c -> ResponseEntity.ok(dtoFromEntity(c)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Central não encontrada")));
    }

    // GET - Buscar por Nome
    @GetMapping("/nome/{nome}")
    public ResponseEntity<?> buscarPorNome(@PathVariable String nome) {
        Optional<CentralTransplantes> central = centralService.buscarPorNome(nome);
        return central.<ResponseEntity<?>>map(c -> ResponseEntity.ok(dtoFromEntity(c)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Central não encontrada")));
    }

    // GET - Listar por cidade
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<?> listarPorCidade(@PathVariable String cidade) {
        List<CentralTransplantes> centrais = centralService.listarPorCidade(cidade);
        List<CentralTransplantesDTO> dtos = centrais.stream().map(this::dtoFromEntity).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Listar por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> listarPorEstado(@PathVariable String estado) {
        List<CentralTransplantes> centrais = centralService.listarPorEstado(estado);
        List<CentralTransplantesDTO> dtos = centrais.stream().map(this::dtoFromEntity).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Listar por status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> listarPorStatus(@PathVariable String status) {
        try {
            CentralTransplantes.StatusCentral statusEnum = CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());
            List<CentralTransplantes> centrais = centralService.listarPorStatus(statusEnum);
            List<CentralTransplantesDTO> dtos = centrais.stream().map(this::dtoFromEntity).collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Status inválido"));
        }
    }

    // GET - Estatísticas de doadores/receptores e órgãos/tecidos (Painel da Central)
    @GetMapping("/estatisticas/doadores-receptores")
    public ResponseEntity<CentralTransplantesService.EstatisticasCentralDoacaoTransplante> obterEstatisticasDoadoresReceptores() {
        CentralTransplantesService.EstatisticasCentralDoacaoTransplante estatisticas =
                centralService.obterEstatisticasDoacaoTransplante();
        return ResponseEntity.ok(estatisticas);
    }

    // PUT - Atualizar central
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCentral(@PathVariable Long id, @Valid @RequestBody CentralTransplantesDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("erro", result.getAllErrors().get(0).getDefaultMessage()));
        }
        try {
            CentralTransplantes centralAtualizada = centralService.atualizarCentralFromDTO(id, dto);
            return ResponseEntity.ok(dtoFromEntity(centralAtualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // PATCH - Alterar status da central
    @PatchMapping("/{id}/status")
    public ResponseEntity<CentralTransplantes> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            CentralTransplantes.StatusCentral novoStatus = CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());
            CentralTransplantes central = centralService.alterarStatus(id, novoStatus);
            return ResponseEntity.ok(central);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST - Vincular hospital à central
    @PostMapping("/{centralId}/hospitais/{hospitalId}")
    public ResponseEntity<?> vincularHospital(
            @PathVariable Long centralId,
            @PathVariable Long hospitalId) {
        try {
            centralService.vincularHospital(centralId, hospitalId);
            return ResponseEntity.ok(Map.of("mensagem", "Hospital vinculado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Central ou hospital não encontrado"));
        }
    }

    // DELETE - Remover hospital da central
    @DeleteMapping("/{centralId}/hospitais/{hospitalId}")
    public ResponseEntity<?> removerHospital(
            @PathVariable Long centralId,
            @PathVariable Long hospitalId) {
        try {
            centralService.removerHospital(centralId, hospitalId);
            return ResponseEntity.ok(Map.of("mensagem", "Hospital removido com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Central ou hospital não encontrado"));
        }
    }
    // Conversão Entity -> DTO
    private CentralTransplantesDTO dtoFromEntity(CentralTransplantes c) {
        CentralTransplantesDTO dto = new CentralTransplantesDTO();
        dto.setId(c.getId());
        dto.setNome(c.getNome());
        dto.setCnpj(c.getCnpj());
        dto.setEndereco(c.getEndereco());
        dto.setCidade(c.getCidade());
        dto.setEstado(c.getEstado());
        dto.setTelefone(c.getTelefone());
        dto.setTelefonePlantao(c.getTelefonePlantao());
        dto.setEmail(c.getEmail());
        dto.setEmailPlantao(c.getEmailPlantao());
        dto.setCoordenador(c.getCoordenador());
        dto.setTelefoneCoordenador(c.getTelefoneCoordenador());
        dto.setCapacidadeProcessamento(c.getCapacidadeProcessamento());
        dto.setEspecialidadesOrgaos(c.getEspecialidadesOrgaos());
        return dto;
    }

    // DELETE - Deletar central
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCentral(@PathVariable Long id) {
        try {
            centralService.deletarCentral(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
