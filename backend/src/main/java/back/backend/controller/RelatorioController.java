package back.backend.controller;

import back.backend.dto.RelatorioBibliotecaDTO;
import back.backend.service.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final PacienteService pacienteService;

    @GetMapping
    public ResponseEntity<List<RelatorioBibliotecaDTO>> listarTodos() {
        return ResponseEntity.ok(pacienteService.listarRelatoriosGerados());
    }
}
