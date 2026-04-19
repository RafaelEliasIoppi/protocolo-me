package back.backend.service;

import back.backend.model.OrgaoDoado;
import back.backend.model.ProtocoloME;
import back.backend.repository.OrgaoDoadoRepository;
import back.backend.repository.ProtocoloMERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EstatisticasTransplantesService {

    @Autowired
    private ProtocoloMERepository protocoloMERepository;

    @Autowired
    private OrgaoDoadoRepository orgaoDoadoRepository;

    /**
     * Obtém estatísticas gerais de transplantes com filtro por ano
     */
    public EstatisticasGeradasTransplante obterEstatisticasGerais(Integer ano) {
        List<ProtocoloME> protocolos = protocoloMERepository.findAll();
        List<OrgaoDoado> orgaos = orgaoDoadoRepository.findAll();

        // Filtrar por ano se informado
        if (ano != null) {
            protocolos = protocolos.stream()
                    .filter(p -> p.getDataCriacao() != null && p.getDataCriacao().getYear() == ano)
                    .collect(Collectors.toList());
            
            orgaos = orgaos.stream()
                    .filter(o -> o.getDataCriacao() != null && o.getDataCriacao().getYear() == ano)
                    .collect(Collectors.toList());
        }

        long totalDoadores = protocolos.size();
        long doadoresComImplantacao = orgaos.stream()
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.IMPLANTADO)
                .map(o -> o.getProtocoloME().getId())
                .distinct()
                .count();

        long totalOrgaosDisponiveis = orgaos.size();
        long orgaosImplantados = orgaos.stream()
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.IMPLANTADO)
                .count();
        long orgaosDescartados = orgaos.stream()
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.DESCARTADO)
                .count();

        long receptoresUnicos = orgaos.stream()
                .filter(o -> o.getPacienteReceptor() != null && !o.getPacienteReceptor().isEmpty())
                .map(OrgaoDoado::getCpfReceptor)
                .distinct()
                .count();

        double taxaImplantacao = totalOrgaosDisponiveis > 0 
                ? (double) orgaosImplantados / totalOrgaosDisponiveis * 100 
                : 0;

        return new EstatisticasGeradasTransplante(
                totalDoadores,
                doadoresComImplantacao,
                totalOrgaosDisponiveis,
                orgaosImplantados,
                orgaosDescartados,
                receptoresUnicos,
                taxaImplantacao
        );
    }

    /**
     * Obtém estatísticas por paciente doador
     */
    public List<PacienteDoacaoInfo> obterEstatisticasPorPaciente(Integer ano) {
        List<ProtocoloME> protocolos = protocoloMERepository.findAll();

        // Filtrar por ano se informado
        if (ano != null) {
            protocolos = protocolos.stream()
                    .filter(p -> p.getDataCriacao() != null && p.getDataCriacao().getYear() == ano)
                    .collect(Collectors.toList());
        }

        Map<Long, PacienteDoacaoInfo> pacientesMap = new HashMap<>();

        for (ProtocoloME protocolo : protocolos) {
            Long pacienteId = protocolo.getPaciente().getId();
            
            PacienteDoacaoInfo info = pacientesMap.getOrDefault(
                    pacienteId,
                    new PacienteDoacaoInfo(
                            pacienteId,
                            protocolo.getPaciente().getNome(),
                            protocolo.getPaciente().getCpf(),
                            protocolo.getDataCriacao()
                    )
            );

            // Obter órgãos deste protocolo
            List<OrgaoDoado> orgaos = protocolo.getOrgaosDoados();
            if (orgaos != null) {
                for (OrgaoDoado orgao : orgaos) {
                    if (orgao.getStatus() == OrgaoDoado.StatusOrgaoDoado.IMPLANTADO) {
                        info.addOrgaoImplantado(
                                orgao.getNomeOrgao(),
                                orgao.getPacienteReceptor(),
                                orgao.getHospitalReceptor(),
                                orgao.getCpfReceptor(),
                                orgao.getDataImplantacao()
                        );
                    } else if (orgao.getStatus() == OrgaoDoado.StatusOrgaoDoado.DESCARTADO) {
                        info.addOrgaoDescartado(
                                orgao.getNomeOrgao(),
                                orgao.getMotivoDescarte(),
                                orgao.getDataDescarte()
                        );
                    }
                }
            }

            pacientesMap.put(pacienteId, info);
        }

        return new ArrayList<>(pacientesMap.values());
    }

    /**
     * Obtém anos disponíveis para filtro
     */
    public List<Integer> obterAnosaDisponiveis() {
        List<ProtocoloME> protocolos = protocoloMERepository.findAll();
        return protocolos.stream()
                .filter(p -> p.getDataCriacao() != null)
                .map(p -> p.getDataCriacao().getYear())
                .distinct()
                .sorted((a, b) -> b.compareTo(a))
                .collect(Collectors.toList());
    }

    /**
     * Classe para estatísticas gerais
     */
    public static class EstatisticasGeradasTransplante {
        private long totalDoadores;
        private long doadoresComImplantacao;
        private long totalOrgaosDisponiveis;
        private long orgaosImplantados;
        private long orgaosDescartados;
        private long receptoresUnicos;
        private double taxaImplantacao;

        public EstatisticasGeradasTransplante(
                long totalDoadores,
                long doadoresComImplantacao,
                long totalOrgaosDisponiveis,
                long orgaosImplantados,
                long orgaosDescartados,
                long receptoresUnicos,
                double taxaImplantacao
        ) {
            this.totalDoadores = totalDoadores;
            this.doadoresComImplantacao = doadoresComImplantacao;
            this.totalOrgaosDisponiveis = totalOrgaosDisponiveis;
            this.orgaosImplantados = orgaosImplantados;
            this.orgaosDescartados = orgaosDescartados;
            this.receptoresUnicos = receptoresUnicos;
            this.taxaImplantacao = taxaImplantacao;
        }

        public long getTotalDoadores() { return totalDoadores; }
        public long getDoadoresComImplantacao() { return doadoresComImplantacao; }
        public long getTotalOrgaosDisponiveis() { return totalOrgaosDisponiveis; }
        public long getOrgaosImplantados() { return orgaosImplantados; }
        public long getOrgaosDescartados() { return orgaosDescartados; }
        public long getReceptoresUnicos() { return receptoresUnicos; }
        public double getTaxaImplantacao() { return taxaImplantacao; }
    }

    /**
     * Classe para informações por paciente
     */
    public static class PacienteDoacaoInfo {
        private Long pacienteId;
        private String nomePaciente;
        private String cpfPaciente;
        private LocalDateTime dataDoacao;
        private List<OrgaoImplantadoInfo> orgaosImplantados = new ArrayList<>();
        private List<OrgaoDescartadoInfo> orgaosDescartados = new ArrayList<>();

        public PacienteDoacaoInfo(
                Long pacienteId,
                String nomePaciente,
                String cpfPaciente,
                LocalDateTime dataDoacao
        ) {
            this.pacienteId = pacienteId;
            this.nomePaciente = nomePaciente;
            this.cpfPaciente = cpfPaciente;
            this.dataDoacao = dataDoacao;
        }

        public void addOrgaoImplantado(
                String nomeOrgao,
                String nomeReceptor,
                String hospitalReceptor,
                String cpfReceptor,
                LocalDateTime dataImplantacao
        ) {
            orgaosImplantados.add(new OrgaoImplantadoInfo(
                    nomeOrgao, nomeReceptor, hospitalReceptor, cpfReceptor, dataImplantacao
            ));
        }

        public void addOrgaoDescartado(
                String nomeOrgao,
                String motivo,
                LocalDateTime dataDescarte
        ) {
            orgaosDescartados.add(new OrgaoDescartadoInfo(
                    nomeOrgao, motivo, dataDescarte
            ));
        }

        public Long getPacienteId() { return pacienteId; }
        public String getNomePaciente() { return nomePaciente; }
        public String getCpfPaciente() { return cpfPaciente; }
        public LocalDateTime getDataDoacao() { return dataDoacao; }
        public List<OrgaoImplantadoInfo> getOrgaosImplantados() { return orgaosImplantados; }
        public List<OrgaoDescartadoInfo> getOrgaosDescartados() { return orgaosDescartados; }
        
        public int getTotalOrgaos() {
            return orgaosImplantados.size() + orgaosDescartados.size();
        }
    }

    /**
     * Classe para órgão implantado
     */
    public static class OrgaoImplantadoInfo {
        private String nomeOrgao;
        private String nomeReceptor;
        private String hospitalReceptor;
        private String cpfReceptor;
        private LocalDateTime dataImplantacao;

        public OrgaoImplantadoInfo(
                String nomeOrgao,
                String nomeReceptor,
                String hospitalReceptor,
                String cpfReceptor,
                LocalDateTime dataImplantacao
        ) {
            this.nomeOrgao = nomeOrgao;
            this.nomeReceptor = nomeReceptor;
            this.hospitalReceptor = hospitalReceptor;
            this.cpfReceptor = cpfReceptor;
            this.dataImplantacao = dataImplantacao;
        }

        public String getNomeOrgao() { return nomeOrgao; }
        public String getNomeReceptor() { return nomeReceptor; }
        public String getHospitalReceptor() { return hospitalReceptor; }
        public String getCpfReceptor() { return cpfReceptor; }
        public LocalDateTime getDataImplantacao() { return dataImplantacao; }
    }

    /**
     * Classe para órgão descartado
     */
    public static class OrgaoDescartadoInfo {
        private String nomeOrgao;
        private String motivo;
        private LocalDateTime dataDescarte;

        public OrgaoDescartadoInfo(String nomeOrgao, String motivo, LocalDateTime dataDescarte) {
            this.nomeOrgao = nomeOrgao;
            this.motivo = motivo;
            this.dataDescarte = dataDescarte;
        }

        public String getNomeOrgao() { return nomeOrgao; }
        public String getMotivo() { return motivo; }
        public LocalDateTime getDataDescarte() { return dataDescarte; }
    }
}
