package back.backend.service;

import back.backend.model.Doacao;
import back.backend.model.OrgaoDoado;
import back.backend.model.ProtocoloME;
import back.backend.repository.ProtocoloMERepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EstatisticasTransplantesService {

    private final ProtocoloMERepository protocoloMERepository;

    public EstatisticasTransplantesService(ProtocoloMERepository protocoloMERepository) {
        this.protocoloMERepository = protocoloMERepository;
    }

    public EstatisticasGeradasTransplante obterEstatisticasGerais(Integer ano) {
        List<ProtocoloME> protocolos = filtrarPorAno(ano);

        long totalOrgaos = protocolos.stream()
                .map(ProtocoloME::getDoacao)
                .filter(Objects::nonNull)
                .map(Doacao::getOrgaos)
                .filter(Objects::nonNull)
                .mapToLong(List::size)
                .sum();

        long implantados = protocolos.stream()
                .map(ProtocoloME::getDoacao)
                .filter(Objects::nonNull)
                .map(Doacao::getOrgaos)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.IMPLANTADO)
                .count();

        long descartados = protocolos.stream()
                .map(ProtocoloME::getDoacao)
                .filter(Objects::nonNull)
                .map(Doacao::getOrgaos)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.DESCARTADO)
                .count();

        Set<String> receptoresUnicos = protocolos.stream()
                .map(ProtocoloME::getDoacao)
                .filter(Objects::nonNull)
                .map(Doacao::getOrgaos)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(OrgaoDoado::getCpfReceptor)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        long totalDoadores = protocolos.stream()
                .map(ProtocoloME::getPaciente)
                .filter(Objects::nonNull)
                .map(p -> p.getId() != null ? p.getId().toString() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        double taxaImplantacao = totalOrgaos == 0 ? 0.0 : (implantados * 100.0) / totalOrgaos;

        return new EstatisticasGeradasTransplante(
                (int) totalOrgaos,
                (int) implantados,
                (int) descartados,
                receptoresUnicos.size(),
                (int) totalDoadores,
                taxaImplantacao
        );
    }

    public List<PacienteDoacaoInfo> obterEstatisticasPorPaciente(Integer ano) {
        return filtrarPorAno(ano).stream()
                .filter(p -> p.getPaciente() != null)
                .map(this::toPacienteDoacaoInfo)
                .sorted(Comparator.comparing(PacienteDoacaoInfo::getDataDoacao, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public List<PacienteDoacaoInfo> buscarPorNomePaciente(String nomePaciente) {
        String filtro = normalizar(nomePaciente);
        return obterEstatisticasPorPaciente(null).stream()
                .filter(i -> normalizar(i.getNomePaciente()).contains(filtro))
                .collect(Collectors.toList());
    }

    public List<PacienteDoacaoInfo> buscarPorNomeReceptor(String nomeReceptor) {
        String filtro = normalizar(nomeReceptor);
        return obterEstatisticasPorPaciente(null).stream()
                .filter(i -> i.getOrgaosImplantados().stream()
                        .anyMatch(o -> normalizar(o.getNomeReceptor()).contains(filtro)))
                .collect(Collectors.toList());
    }

    public List<Integer> obterAnosDisponiveis() {
        return protocoloMERepository.findAll().stream()
                .map(ProtocoloME::getDataNotificacao)
                .filter(Objects::nonNull)
                .map(LocalDateTime::getYear)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    private List<ProtocoloME> filtrarPorAno(Integer ano) {
        return protocoloMERepository.findAll().stream()
                .filter(p -> ano == null || (p.getDataNotificacao() != null && p.getDataNotificacao().getYear() == ano))
                .collect(Collectors.toList());
    }

    private PacienteDoacaoInfo toPacienteDoacaoInfo(ProtocoloME protocolo) {
        PacienteDoacaoInfo info = new PacienteDoacaoInfo();
        info.setPacienteId(protocolo.getPaciente().getId());
        info.setNomePaciente(protocolo.getPaciente().getNome());
        info.setCpfPaciente(protocolo.getPaciente().getCpf());
        info.setDataDoacao(protocolo.getDataNotificacao());

        List<OrgaoPacienteInfo> implantados = new ArrayList<>();
        List<OrgaoPacienteInfo> descartados = new ArrayList<>();

        if (protocolo.getDoacao() != null && protocolo.getDoacao().getOrgaos() != null) {
            for (OrgaoDoado orgao : protocolo.getDoacao().getOrgaos()) {
                OrgaoPacienteInfo detalhe = new OrgaoPacienteInfo();
                detalhe.setNomeOrgao(orgao.getNomeOrgao());
                detalhe.setNomeReceptor(orgao.getPacienteReceptor());
                detalhe.setPacienteReceptor(orgao.getPacienteReceptor());
                detalhe.setCpfReceptor(orgao.getCpfReceptor());
                detalhe.setHospitalReceptor(orgao.getHospitalReceptor());
                detalhe.setDataImplantacao(orgao.getDataImplantacao());
                detalhe.setDataDescarte(orgao.getDataDescarte());
                detalhe.setMotivo(orgao.getMotivoDescarte());

                if (orgao.getStatus() == OrgaoDoado.StatusOrgaoDoado.IMPLANTADO) {
                    implantados.add(detalhe);
                } else if (orgao.getStatus() == OrgaoDoado.StatusOrgaoDoado.DESCARTADO) {
                    descartados.add(detalhe);
                }
            }
        }

        info.setOrgaosImplantados(implantados);
        info.setOrgaosDescartados(descartados);
        info.setTotalOrgaos(implantados.size() + descartados.size());
        return info;
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.trim().toLowerCase(Locale.ROOT);
    }

    public static class EstatisticasGeradasTransplante {
        private final int totalOrgaosDisponiveis;
        private final int orgaosImplantados;
        private final int orgaosDescartados;
        private final int receptoresUnicos;
        private final int totalDoadores;
        private final double taxaImplantacao;

        public EstatisticasGeradasTransplante(int totalOrgaosDisponiveis,
                                              int orgaosImplantados,
                                              int orgaosDescartados,
                                              int receptoresUnicos,
                                              int totalDoadores,
                                              double taxaImplantacao) {
            this.totalOrgaosDisponiveis = totalOrgaosDisponiveis;
            this.orgaosImplantados = orgaosImplantados;
            this.orgaosDescartados = orgaosDescartados;
            this.receptoresUnicos = receptoresUnicos;
            this.totalDoadores = totalDoadores;
            this.taxaImplantacao = taxaImplantacao;
        }

        public int getTotalOrgaosDisponiveis() { return totalOrgaosDisponiveis; }
        public int getOrgaosImplantados() { return orgaosImplantados; }
        public int getOrgaosDescartados() { return orgaosDescartados; }
        public int getReceptoresUnicos() { return receptoresUnicos; }
        public int getTotalDoadores() { return totalDoadores; }
        public double getTaxaImplantacao() { return taxaImplantacao; }
    }

    public static class PacienteDoacaoInfo {
        private Long pacienteId;
        private String nomePaciente;
        private String cpfPaciente;
        private LocalDateTime dataDoacao;
        private int totalOrgaos;
        private List<OrgaoPacienteInfo> orgaosImplantados = new ArrayList<>();
        private List<OrgaoPacienteInfo> orgaosDescartados = new ArrayList<>();

        public Long getPacienteId() { return pacienteId; }
        public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
        public String getNomePaciente() { return nomePaciente; }
        public void setNomePaciente(String nomePaciente) { this.nomePaciente = nomePaciente; }
        public String getCpfPaciente() { return cpfPaciente; }
        public void setCpfPaciente(String cpfPaciente) { this.cpfPaciente = cpfPaciente; }
        public LocalDateTime getDataDoacao() { return dataDoacao; }
        public void setDataDoacao(LocalDateTime dataDoacao) { this.dataDoacao = dataDoacao; }
        public int getTotalOrgaos() { return totalOrgaos; }
        public void setTotalOrgaos(int totalOrgaos) { this.totalOrgaos = totalOrgaos; }
        public List<OrgaoPacienteInfo> getOrgaosImplantados() { return orgaosImplantados; }
        public void setOrgaosImplantados(List<OrgaoPacienteInfo> orgaosImplantados) { this.orgaosImplantados = orgaosImplantados; }
        public List<OrgaoPacienteInfo> getOrgaosDescartados() { return orgaosDescartados; }
        public void setOrgaosDescartados(List<OrgaoPacienteInfo> orgaosDescartados) { this.orgaosDescartados = orgaosDescartados; }
    }

    public static class OrgaoPacienteInfo {
        private String nomeOrgao;
        private String nomeReceptor;
        private String pacienteReceptor;
        private String cpfReceptor;
        private String hospitalReceptor;
        private String motivo;
        private LocalDateTime dataImplantacao;
        private LocalDateTime dataDescarte;

        public String getNomeOrgao() { return nomeOrgao; }
        public void setNomeOrgao(String nomeOrgao) { this.nomeOrgao = nomeOrgao; }
        public String getNomeReceptor() { return nomeReceptor; }
        public void setNomeReceptor(String nomeReceptor) { this.nomeReceptor = nomeReceptor; }
        public String getPacienteReceptor() { return pacienteReceptor; }
        public void setPacienteReceptor(String pacienteReceptor) { this.pacienteReceptor = pacienteReceptor; }
        public String getCpfReceptor() { return cpfReceptor; }
        public void setCpfReceptor(String cpfReceptor) { this.cpfReceptor = cpfReceptor; }
        public String getHospitalReceptor() { return hospitalReceptor; }
        public void setHospitalReceptor(String hospitalReceptor) { this.hospitalReceptor = hospitalReceptor; }
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
        public LocalDateTime getDataImplantacao() { return dataImplantacao; }
        public void setDataImplantacao(LocalDateTime dataImplantacao) { this.dataImplantacao = dataImplantacao; }
        public LocalDateTime getDataDescarte() { return dataDescarte; }
        public void setDataDescarte(LocalDateTime dataDescarte) { this.dataDescarte = dataDescarte; }
    }
}
