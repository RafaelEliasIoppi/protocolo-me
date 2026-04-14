package back.backend.model;

import java.util.Arrays;
import java.util.List;

public enum Role {
    ADMIN("Administrador", Arrays.asList("gerenciar_usuarios", "gerenciar_hospitais", "gerenciar_centrais")),
    COORDENADOR_TRANSPLANTES("Coordenador de Transplantes", Arrays.asList("gerenciar_protocolos", "visualizar_disponibilidade", "atualizar_status")),
    MEDICO("Médico", Arrays.asList("visualizar_protocolos", "atualizar_protocolo", "registrar_observacoes")),
    ENFERMEIRO("Enfermeiro", Arrays.asList("visualizar_protocolos", "registrar_observacoes")),
    CENTRAL_TRANSPLANTES("Central de Transplantes", Arrays.asList("gerenciar_protocolos_me", "registrar_doadores", "atualizar_protocolo_me"));

    private String descricao;
    private List<String> permissoes;

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
