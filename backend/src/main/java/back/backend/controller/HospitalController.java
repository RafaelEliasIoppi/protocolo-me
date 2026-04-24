package back.backend.controller;

import back.backend.dto.ErrorResponseDTO;
import back.backend.dto.HospitalDTO;
import back.backend.model.Hospital;
import back.backend.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitais")
@CrossOrigin(origins = "*")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    // POST
    @PostMapping
    public ResponseEntity<?> criarHospital(@RequestBody Hospital hospital) {
        try {
            Hospital novo = hospitalService.criarHospital(hospital);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(HospitalDTO.fromEntity(novo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO(e.getMessage(), 400));
        }
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<HospitalDTO>> listarTodos() {
        return ResponseEntity.ok(
                hospitalService.listarTodos()
                        .stream()
                        .map(HospitalDTO::fromEntity)
                        .toList()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return hospitalService.buscarPorId(id)
                .map(h -> ResponseEntity.ok(HospitalDTO.fromEntity(h)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponseDTO("Hospital não encontrado", 404)));
    }

    // GET BY CNPJ
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<?> buscarPorCnpj(@PathVariable String cnpj) {
        return hospitalService.buscarPorCnpj(cnpj)
                .map(h -> ResponseEntity.ok(HospitalDTO.fromEntity(h)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponseDTO("Hospital não encontrado", 404)));
    }

    // GET BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<?> listarPorStatus(@PathVariable String status) {
        try {
            Hospital.StatusHospital statusEnum =
                    Hospital.StatusHospital.valueOf(status.toUpperCase());

            return ResponseEntity.ok(
                    hospitalService.listarPorStatus(statusEnum)
                            .stream()
                            .map(HospitalDTO::fromEntity)
                            .toList()
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO("Status inválido", 400));
        }
    }

    // GET BY CIDADE
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<HospitalDTO>> listarPorCidade(@PathVariable String cidade) {
        return ResponseEntity.ok(
                hospitalService.listarPorCidade(cidade)
                        .stream()
                        .map(HospitalDTO::fromEntity)
                        .toList()
        );
    }

    // GET BY ESTADO
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<HospitalDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(
                hospitalService.listarPorEstado(estado)
                        .stream()
                        .map(HospitalDTO::fromEntity)
                        .toList()
        );
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarHospital(@PathVariable Long id, @RequestBody Hospital hospital) {
        try {
            return ResponseEntity.ok(
                    HospitalDTO.fromEntity(
                            hospitalService.atualizarHospital(id, hospital)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(e.getMessage(), 404));
        }
    }

    // PATCH STATUS
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            Hospital.StatusHospital novoStatus =
                    Hospital.StatusHospital.valueOf(status.toUpperCase());

            return ResponseEntity.ok(
                    HospitalDTO.fromEntity(
                            hospitalService.alterarStatus(id, novoStatus)
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO("Status inválido", 400));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(e.getMessage(), 404));
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarHospital(@PathVariable Long id) {
        try {
            hospitalService.deletarHospital(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(e.getMessage(), 404));
        }
    }
}