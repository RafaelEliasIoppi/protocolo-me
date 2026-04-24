package back.backend.controller;

import back.backend.dto.ErrorResponseDTO;
import back.backend.dto.HospitalDTO;
import back.backend.dto.HospitalRequestDTO;
import back.backend.mapper.HospitalMapper;
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
    private final HospitalMapper hospitalMapper;
    private final HospitalRequestMapper hospitalRequestMapper;

    public HospitalController(HospitalService hospitalService, HospitalMapper hospitalMapper, HospitalRequestMapper hospitalRequestMapper) {
        this.hospitalService = hospitalService;
        this.hospitalMapper = hospitalMapper;
        this.hospitalRequestMapper = hospitalRequestMapper;
    }

    // POST
    @PostMapping
    public ResponseEntity<?> criarHospital(@Valid @RequestBody HospitalRequestDTO request) {
        Hospital hospital = hospitalRequestMapper.toEntity(request);
        Hospital novo = hospitalService.criarHospital(hospital);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hospitalMapper.toDTO(novo));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<HospitalDTO>> listarTodos() {
        return ResponseEntity.ok(
                hospitalService.listarTodos()
                        .stream()
                    .map(hospitalMapper::toDTO)
                        .toList()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalMapper.toDTO(hospitalService.buscarPorIdOuFalhar(id)));
    }

    // GET BY CNPJ
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<?> buscarPorCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(hospitalMapper.toDTO(hospitalService.buscarPorCnpjOuFalhar(cnpj)));
    }

    // GET BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<?> listarPorStatus(@PathVariable String status) {
        Hospital.StatusHospital statusEnum = Hospital.StatusHospital.valueOf(status.toUpperCase());

        return ResponseEntity.ok(
            hospitalService.listarPorStatus(statusEnum)
                .stream()
                .map(hospitalMapper::toDTO)
                .toList()
        );
    }

    // GET BY CIDADE
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<HospitalDTO>> listarPorCidade(@PathVariable String cidade) {
        return ResponseEntity.ok(
                hospitalService.listarPorCidade(cidade)
                        .stream()
                    .map(hospitalMapper::toDTO)
                        .toList()
        );
    }

    // GET BY ESTADO
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<HospitalDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(
                hospitalService.listarPorEstado(estado)
                        .stream()
                    .map(hospitalMapper::toDTO)
                        .toList()
        );
    }

    // PUT
    @PutMapping("/{id}")
        public ResponseEntity<?> atualizarHospital(@PathVariable Long id, @Valid @RequestBody HospitalRequestDTO request) {
            Hospital hospital = hospitalRequestMapper.toEntity(request);
            return ResponseEntity.ok(
                hospitalMapper.toDTO(
                    hospitalService.atualizarHospital(id, hospital)
                )
            );
    }

    // PATCH STATUS
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Hospital.StatusHospital novoStatus = Hospital.StatusHospital.valueOf(status.toUpperCase());

        return ResponseEntity.ok(
            hospitalMapper.toDTO(
                hospitalService.alterarStatus(id, novoStatus)
            )
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarHospital(@PathVariable Long id) {
        hospitalService.deletarHospital(id);
        return ResponseEntity.noContent().build();
    }
}