package back.backend.mapper;

import back.backend.dto.ProtocoloMEDTO;
import back.backend.model.Doacao;
import back.backend.model.OrgaoDoado;
import back.backend.model.ProtocoloME;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProtocoloMapperTest {

    @Autowired
    private ProtocoloMapper protocoloMapper;

    @Test
    void deveMapearOrgaosDoadosNoDTODoProtocolo() {
        ProtocoloME protocolo = new ProtocoloME();
        protocolo.setId(10L);
        protocolo.setNumeroProtocolo("PROTO-001");

        Doacao doacao = new Doacao();
        doacao.setProtocoloME(protocolo);

        OrgaoDoado orgao = new OrgaoDoado();
        orgao.setId(55L);
        orgao.setDoacao(doacao);
        orgao.setTipo(OrgaoDoado.TipoOrgao.RIM);
        orgao.setLado(OrgaoDoado.LadoOrgao.DIREITO);

        doacao.setOrgaos(Set.of(orgao));
        protocolo.setDoacao(doacao);

        ProtocoloMEDTO dto = protocoloMapper.toDTO(protocolo);

        assertNotNull(dto.getOrgaosDoados());
        assertEquals(1, dto.getOrgaosDoados().size());
        assertEquals(55L, dto.getOrgaosDoados().get(0).getId());
        assertEquals(10L, dto.getOrgaosDoados().get(0).getProtocoloId());
        assertEquals("PROTO-001", dto.getOrgaosDoados().get(0).getNumeroProtocolo());
        assertEquals("RIM", dto.getOrgaosDoados().get(0).getNomeOrgao());
    }
}
