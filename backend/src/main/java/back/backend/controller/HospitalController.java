package back.backend.controller;

import back.backend.dto.HospitalDTO;
import back.backend.dto.HospitalRequestDTO;
import back.backend.mapper.HospitalRequestMapper;
import back.backend.model.Hospital;
import back.backend.service.HospitalService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitais")
@CrossOrigin(origins = "*")
public class HospitalController {

    private final HospitalService hospitalService;
    private final HospitalRequestMapper hospitalRequestMapper;

    public HospitalController(HospitalService hospitalService, HospitalRequestMapper hospitalRequestMapper) {
        this.hospitalService = hospitalService;
        this.hospitalRequestMapper = hospitalRequestMapper;
    }

    // POST
    @PostMapping
    public ResponseEntity<HospitalDTO> criarHospital(@Valid @RequestBody HospitalRequestDTO request) {
        Hospital hospital = hospitalRequestMapper.toEntity(request);
        HospitalDTO novo = hospitalService.criarHospital(hospital);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(novo);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<HospitalDTO>> listarTodos() {
        return ResponseEntity.ok(hospitalService.listarTodos());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<HospitalDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.buscarPorIdOuFalhar(id));
    }

    // GET BY CNPJ
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<HospitalDTO> buscarPorCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(hospitalService.buscarPorCnpjOuFalhar(cnpj));
    }

    // GET BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<List<HospitalDTO>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(hospitalService.listarPorStatus(status));
    }

    // GET BY CIDADE
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<HospitalDTO>> listarPorCidade(@PathVariable String cidade) {
        return ResponseEntity.ok(hospitalService.listarPorCidade(cidade));
    }

    // GET BY ESTADO
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<HospitalDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(hospitalService.listarPorEstado(estado));
    }

    // PUT
    @PutMapping("/{id}")
        public ResponseEntity<HospitalDTO> atualizarHospital(@PathVariable Long id, @Valid @RequestBody HospitalRequestDTO request) {
            Hospital hospital = hospitalRequestMapper.toEntity(request);
            return ResponseEntity.ok(hospitalService.atualizarHospital(id, hospital));
    }

    // PATCH STATUS
    @PatchMapping("/{id}/status")
    public ResponseEntity<HospitalDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(hospitalService.alterarStatus(id, status));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarHospital(@PathVariable Long id) {
        hospitalService.deletarHospital(id);
        return ResponseEntity.noContent().build();
    }
}