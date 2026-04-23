package back.backend.controller;

import back.backend.dto.HospitalDTO;
import back.backend.model.Hospital;
import back.backend.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hospitais")
@CrossOrigin(origins = "*")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    // POST - Criar novo hospital
    @PostMapping
    public ResponseEntity<HospitalDTO> criarHospital(@RequestBody Hospital hospital) {
        try {
            Hospital novoHospital = hospitalService.criarHospital(hospital);
            return ResponseEntity.status(HttpStatus.CREATED).body(HospitalDTO.fromEntity(novoHospital));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // GET - Listar todos os hospitais
    @GetMapping
    public ResponseEntity<List<HospitalDTO>> listarTodos() {
        List<Hospital> hospitais = hospitalService.listarTodos();
        return ResponseEntity.ok(hospitais.stream().map(HospitalDTO::fromEntity).collect(Collectors.toList()));
    }

    // GET - Buscar hospital por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Hospital> hospital = hospitalService.buscarPorId(id);
        return hospital.map(value -> ResponseEntity.ok(HospitalDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET - Buscar por CNPJ
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<?> buscarPorCnpj(@PathVariable String cnpj) {
        Optional<Hospital> hospital = hospitalService.buscarPorCnpj(cnpj);
        return hospital.map(value -> ResponseEntity.ok(HospitalDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET - Listar por status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<HospitalDTO>> listarPorStatus(@PathVariable String status) {
        try {
            Hospital.StatusHospital statusEnum = Hospital.StatusHospital.valueOf(status.toUpperCase());
            List<Hospital> hospitais = hospitalService.listarPorStatus(statusEnum);
            return ResponseEntity.ok(hospitais.stream().map(HospitalDTO::fromEntity).collect(Collectors.toList()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET - Listar por cidade
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<HospitalDTO>> listarPorCidade(@PathVariable String cidade) {
        List<Hospital> hospitais = hospitalService.listarPorCidade(cidade);
        return ResponseEntity.ok(hospitais.stream().map(HospitalDTO::fromEntity).collect(Collectors.toList()));
    }

    // GET - Listar por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<HospitalDTO>> listarPorEstado(@PathVariable String estado) {
        List<Hospital> hospitais = hospitalService.listarPorEstado(estado);
        return ResponseEntity.ok(hospitais.stream().map(HospitalDTO::fromEntity).collect(Collectors.toList()));
    }

    // PUT - Atualizar hospital
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarHospital(@PathVariable Long id, @RequestBody Hospital hospital) {
        try {
            Hospital hospitalAtualizado = hospitalService.atualizarHospital(id, hospital);
            return ResponseEntity.ok(HospitalDTO.fromEntity(hospitalAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PATCH - Alterar status (para equipe médica)
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            Hospital.StatusHospital novoStatus = Hospital.StatusHospital.valueOf(status.toUpperCase());
            Hospital hospital = hospitalService.alterarStatus(id, novoStatus);
            return ResponseEntity.ok(HospitalDTO.fromEntity(hospital));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Deletar hospital
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarHospital(@PathVariable Long id) {
        try {
            hospitalService.deletarHospital(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
