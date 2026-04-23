package back.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private String mensagem;
    private int codigo;
    private Map<String, String> detalhes;

    public ErrorResponseDTO(String mensagem, int codigo) {
        this.mensagem = mensagem;
        this.codigo = codigo;
    }
}