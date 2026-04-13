package back.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import back.model.Paciente;
import back.service.PacienteService;


@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<Paciente> cadastrar(@RequestBody Paciente paciente) {
        Paciente novo = pacienteService.salvar(paciente);
        return ResponseEntity.ok(novo);
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> listar() {
        return ResponseEntity.ok(pacienteService.listarTodos());
    }

    @PutMapping("/{id}/protocolo")
    public ResponseEntity<Paciente> atualizarProtocolo(@PathVariable Long id, @RequestBody String status) {
        Paciente atualizado = pacienteService.atualizarProtocolo(id, status);
        return ResponseEntity.ok(atualizado);
    }
}
