package back.backend.service;

import back.backend.model.EstatisticaProtocoloME;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Service
public class ExportacaoSNTService {

    public ByteArrayInputStream gerarCSV(List<EstatisticaProtocoloME> lista) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        // 🔥 Cabeçalho (IMPORTANTE pro SNT)
        writer.println("ofNac,rgctDoador,nomeDoador,hospitalNotif,dataOf,municipio,sexoDoad,aboDoad,rimD,rimE,coracao,figado");

        for (EstatisticaProtocoloME e : lista) {
            writer.println(
                    safe(e.getOfNac()) + "," +
                    safe(e.getRgctDoador()) + "," +
                    safe(e.getNomeDoador()) + "," +
                    safe(e.getHospitalNotif()) + "," +
                    safe(e.getDataOf()) + "," +
                    safe(e.getMunicipio()) + "," +
                    safe(e.getSexoDoad()) + "," +
                    safe(e.getAboDoad()) + "," +
                    safe(e.getRimD()) + "," +
                    safe(e.getRimE()) + "," +
                    safe(e.getCoracao()) + "," +
                    safe(e.getFigado())
            );
        }

        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String safe(String value) {
        return value == null ? "" : value.replace(",", " ");
    }
}
