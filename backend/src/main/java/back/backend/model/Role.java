package back.backend.model;

import java.util.Arrays;
import java.util.List;

public enum Role {

    ADMIN(
        "Administrador",
        Arrays.asList(
            "GERENCIAR_USUARIOS",
            "GERENCIAR_HOSPITAIS",
            "GERENCIAR_CENTRAIS"
        )
    ),

    COORDENADOR_TRANSPLANTES(
        "Coordenador de Transplantes",
        Arrays.asList(
            "GERENCIAR_PROTOCOLOS",
            "VISUALIZAR_DISPONIBILIDADE",
            "ATUALIZAR_STATUS"
        )
    ),

    MEDICO(
        "Médico",
        Arrays.asList(
            "VISUALIZAR_PROTOCOLOS",
            "ATUALIZAR_PROTOCOLO",
            "REGISTRAR_OBSERVACOES"
        )
    ),

    ENFERMEIRO(
        "Enfermeiro",
        Arrays.asList(
            "VISUALIZAR_PROTOCOLOS",
            "REGISTRAR_OBSERVACOES"
        )
    ),

    CENTRAL_TRANSPLANTES(
        "Central de Transplantes",
        Arrays.asList(
            "GERENCIAR_PROTOCOLOS_ME",
            "REGISTRAR_DOADORES",
            "ATUALIZAR_PROTOCOLO_ME"
        )
    );

    private final String descricao;
    private final List<String> permissoes;

    Role(String descricao, List<String> permissoes) {
        this.descricao = descricao;
        this.permissoes = permissoes;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<String> getPermissoes() {
        return permissoes;
    }

    public boolean temPermissao(String permissao) {
        return permissoes.contains(permissao);
    }
}
