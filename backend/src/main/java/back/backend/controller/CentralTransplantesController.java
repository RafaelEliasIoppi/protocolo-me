package back.backend.controller;

import back.backend.model.CentralTransplantes;
import back.backend.service.CentralTransplantesService;
import back.backend.dto.CentralTransplantesDTO;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;


@RestController
@RequestMapping("/api/centrais-transplantes")
public class CentralTransplantesController {

    @Autowired
    private CentralTransplantesService centralService;

    // POST - Criar nova central
    @PostMapping
    public ResponseEntity<CentralTransplantesDTO> criarCentral(@Valid @RequestBody CentralTransplantesDTO dto) {
        CentralTransplantes novaCentral = centralService.criarCentralFromDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoFromEntity(novaCentral));
    }

    // GET - Listar todas as centrais
    @GetMapping
    public ResponseEntity<List<CentralTransplantesDTO>> listarTodas() {
        List<CentralTransplantes> centrais = centralService.listarTodas();
        List<CentralTransplantesDTO> dtos = centrais.stream()
                .map(this::dtoFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Buscar central por ID
    @GetMapping("/{id}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorId(@PathVariable Long id) {
        Optional<CentralTransplantes> central = centralService.buscarPorId(id);
        return central.map(c -> ResponseEntity.ok(dtoFromEntity(c)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // GET - Buscar por CNPJ
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorCnpj(@PathVariable String cnpj) {
        Optional<CentralTransplantes> central = centralService.buscarPorCnpj(cnpj);
        return central.map(c -> ResponseEntity.ok(dtoFromEntity(c)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // GET - Buscar por Nome
    @GetMapping("/nome/{nome}")
    public ResponseEntity<CentralTransplantesDTO> buscarPorNome(@PathVariable String nome) {
        Optional<CentralTransplantes> central = centralService.buscarPorNome(nome);
        return central.map(c -> ResponseEntity.ok(dtoFromEntity(c)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // GET - Listar por cidade
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorCidade(@PathVariable String cidade) {
        List<CentralTransplantes> centrais = centralService.listarPorCidade(cidade);
        List<CentralTransplantesDTO> dtos = centrais.stream()
                .map(this::dtoFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Listar por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorEstado(@PathVariable String estado) {
        List<CentralTransplantes> centrais = centralService.listarPorEstado(estado);
        List<CentralTransplantesDTO> dtos = centrais.stream()
                .map(this::dtoFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Listar por status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CentralTransplantesDTO>> listarPorStatus(@PathVariable String status) {
        CentralTransplantes.StatusCentral statusEnum = CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());
        List<CentralTransplantes> centrais = centralService.listarPorStatus(statusEnum);
        List<CentralTransplantesDTO> dtos = centrais.stream()
                .map(this::dtoFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
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
    public ResponseEntity<CentralTransplantesDTO> atualizarCentral(@PathVariable Long id,
                                                                   @Valid @RequestBody CentralTransplantesDTO dto) {
        CentralTransplantes centralAtualizada = centralService.atualizarCentralFromDTO(id, dto);
        return ResponseEntity.ok(dtoFromEntity(centralAtualizada));
    }

    // PATCH - Alterar status da central
    @PatchMapping("/{id}/status")
    public ResponseEntity<CentralTransplantesDTO> alterarStatus(@PathVariable Long id,
                                                                @RequestParam String status) {
        CentralTransplantes.StatusCentral novoStatus = CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());
        CentralTransplantes central = centralService.alterarStatus(id, novoStatus);
        return ResponseEntity.ok(dtoFromEntity(central));
    }

    // POST - Vincular hospital à central
    @PostMapping("/{centralId}/hospitais/{hospitalId}")
    public ResponseEntity<Map<String, String>> vincularHospital(@PathVariable Long centralId,
                                                                @PathVariable Long hospitalId) {
        centralService.vincularHospital(centralId, hospitalId);
        return ResponseEntity.ok(Map.of("mensagem", "Hospital vinculado com sucesso"));
    }

    // DELETE - Remover hospital da central
    @DeleteMapping("/{centralId}/hospitais/{hospitalId}")
    public ResponseEntity<Map<String, String>> removerHospital(@PathVariable Long centralId,
                                                               @PathVariable Long hospitalId) {
        centralService.removerHospital(centralId, hospitalId);
        return ResponseEntity.ok(Map.of("mensagem", "Hospital removido com sucesso"));
    }

    // DELETE - Deletar central
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCentral(@PathVariable Long id) {
        centralService.deletarCentral(id);
        return ResponseEntity.noContent().build();
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
}
