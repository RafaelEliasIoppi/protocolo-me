package back.backend.service;

import back.backend.dto.CentralTransplantesDTO;
import back.backend.model.CentralTransplantes;
import back.backend.model.Hospital;
import back.backend.model.Paciente;
import back.backend.model.ProtocoloME;
import back.backend.repository.CentralTransplantesRepository;
import back.backend.repository.HospitalRepository;
import back.backend.repository.PacienteRepository;
import back.backend.repository.ProtocoloMERepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CentralTransplantesService {

    private final CentralTransplantesRepository centralRepository;
    private final HospitalRepository hospitalRepository;
    private final ProtocoloMERepository protocoloMERepository;
    private final PacienteRepository pacienteRepository;

    public CentralTransplantesService(
            CentralTransplantesRepository centralRepository,
            HospitalRepository hospitalRepository,
            ProtocoloMERepository protocoloMERepository,
            PacienteRepository pacienteRepository) {

        this.centralRepository = centralRepository;
        this.hospitalRepository = hospitalRepository;
        this.protocoloMERepository = protocoloMERepository;
        this.pacienteRepository = pacienteRepository;
    }

    // ---------------- DTO BRIDGE ----------------

    public CentralTransplantes criarCentralFromDTO(CentralTransplantesDTO dto) {
        CentralTransplantes central = mapDto(dto);
        return criarCentral(central);
    }

    public CentralTransplantes atualizarCentralFromDTO(Long id, CentralTransplantesDTO dto) {
        CentralTransplantes central = centralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Central não encontrada"));

        mapDto(dto, central);

        return centralRepository.save(central);
    }

    private CentralTransplantes mapDto(CentralTransplantesDTO dto) {
        CentralTransplantes c = new CentralTransplantes();
        mapDto(dto, c);
        return c;
    }

    private void mapDto(CentralTransplantesDTO dto, CentralTransplantes c) {
        c.setNome(dto.getNome());
        c.setCnpj(dto.getCnpj());
        c.setEndereco(dto.getEndereco());
        c.setCidade(dto.getCidade());
        c.setEstado(dto.getEstado());
        c.setTelefone(dto.getTelefone());
        c.setTelefonePlantao(dto.getTelefonePlantao());
        c.setEmail(dto.getEmail());
        c.setEmailPlantao(dto.getEmailPlantao());
        c.setCoordenador(dto.getCoordenador());
        c.setTelefoneCoordenador(dto.getTelefoneCoordenador());
        c.setCapacidadeProcessamento(dto.getCapacidadeProcessamento());
        c.setEspecialidadesOrgaos(dto.getEspecialidadesOrgaos());
    }

    // ---------------- CRUD ----------------

    public CentralTransplantes criarCentral(CentralTransplantes central) {
        validarDuplicidade(central);
        return centralRepository.save(central);
    }

    public List<CentralTransplantes> listarTodas() {
        return centralRepository.findAll();
    }

    public Optional<CentralTransplantes> buscarPorId(Long id) {
        return centralRepository.findById(id);
    }

    public void deletarCentral(Long id) {
        if (!centralRepository.existsById(id)) {
            throw new RuntimeException("Central não encontrada");
        }
        centralRepository.deleteById(id);
    }

    // ---------------- BUSINESS ----------------

    public CentralTransplantes alterarStatus(Long id, CentralTransplantes.StatusCentral status) {
        CentralTransplantes c = getCentral(id);
        c.setStatusOperacional(status);
        return centralRepository.save(c);
    }

    public CentralTransplantes vincularHospital(Long centralId, Long hospitalId) {

        CentralTransplantes central = getCentral(centralId);
        Hospital hospital = getHospital(hospitalId);

        if (central.getHospitaisParceados() == null) {
            central.setHospitaisParceados(new ArrayList<>());
        }

        if (!central.getHospitaisParceados().contains(hospital)) {
            central.getHospitaisParceados().add(hospital);
        }

        return centralRepository.save(central);
    }

    public CentralTransplantes removerHospital(Long centralId, Long hospitalId) {

        CentralTransplantes central = getCentral(centralId);
        Hospital hospital = getHospital(hospitalId);

        if (central.getHospitaisParceados() != null) {
            central.getHospitaisParceados().remove(hospital);
        }

        return centralRepository.save(central);
    }

    // ---------------- HELPERS ----------------

    private CentralTransplantes getCentral(Long id) {
        return centralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Central não encontrada"));
    }

    private Hospital getHospital(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital não encontrado"));
    }

    private void validarDuplicidade(CentralTransplantes central) {

        if (centralRepository.existsByNome(central.getNome())) {
            throw new RuntimeException("Nome já cadastrado");
        }

        if (centralRepository.existsByCnpj(central.getCnpj())) {
            throw new RuntimeException("CNPJ já cadastrado");
        }
    }

    // ---------------- STATISTICS ----------------

    public EstatisticasCentralDoacaoTransplante obterEstatisticasDoacaoTransplante() {

        List<ProtocoloME> protocolos = protocoloMERepository.findAll();

        EstatisticasCentralDoacaoTransplante dto = new EstatisticasCentralDoacaoTransplante();

        dto.setTotalProtocolos(protocolos.size());

        dto.setDoadoresEmAvaliacao(
                protocolos.stream().filter(this::emAvaliacao).count()
        );

        dto.setDoadoresAutorizados(
                protocolos.stream().filter(this::autorizado).count()
        );

        dto.setRecusasFamiliares(
                protocolos.stream()
                        .filter(p -> p.getStatus() == ProtocoloME.StatusProtocoloME.FAMILIA_RECUSOU)
                        .count()
        );

        dto.setReceptoresAptos(
                pacienteRepository.countByStatus(Paciente.StatusPaciente.APTO_TRANSPLANTE)
        );

        dto.setReceptoresNaoAptos(
                pacienteRepository.countByStatus(Paciente.StatusPaciente.NAO_APTO)
        );

        dto.setOrgaosTecidos(new ArrayList<>());

        return dto;
    }

    private boolean emAvaliacao(ProtocoloME p) {
        return p.getStatus() == ProtocoloME.StatusProtocoloME.NOTIFICADO
                || p.getStatus() == ProtocoloME.StatusProtocoloME.EM_PROCESSO;
    }

    private boolean autorizado(ProtocoloME p) {
        return p.getStatus() == ProtocoloME.StatusProtocoloME.DOACAO_AUTORIZADA;
    }

    // ---------------- DTO INTERNO ----------------

    public static class EstatisticasCentralDoacaoTransplante {

        private long totalProtocolos;
        private long doadoresEmAvaliacao;
        private long doadoresAutorizados;
        private long recusasFamiliares;
        private long receptoresAptos;
        private long receptoresNaoAptos;
        private List<ItemOrgaoTecidoEstatistica> orgaosTecidos;

        public long getTotalProtocolos() { return totalProtocolos; }
        public void setTotalProtocolos(long totalProtocolos) { this.totalProtocolos = totalProtocolos; }

        public long getDoadoresEmAvaliacao() { return doadoresEmAvaliacao; }
        public void setDoadoresEmAvaliacao(long doadoresEmAvaliacao) { this.doadoresEmAvaliacao = doadoresEmAvaliacao; }

        public long getDoadoresAutorizados() { return doadoresAutorizados; }
        public void setDoadoresAutorizados(long doadoresAutorizados) { this.doadoresAutorizados = doadoresAutorizados; }

        public long getRecusasFamiliares() { return recusasFamiliares; }
        public void setRecusasFamiliares(long recusasFamiliares) { this.recusasFamiliares = recusasFamiliares; }

        public long getReceptoresAptos() { return receptoresAptos; }
        public void setReceptoresAptos(long receptoresAptos) { this.receptoresAptos = receptoresAptos; }

        public long getReceptoresNaoAptos() { return receptoresNaoAptos; }
        public void setReceptoresNaoAptos(long receptoresNaoAptos) { this.receptoresNaoAptos = receptoresNaoAptos; }

        public List<ItemOrgaoTecidoEstatistica> getOrgaosTecidos() { return orgaosTecidos; }
        public void setOrgaosTecidos(List<ItemOrgaoTecidoEstatistica> orgaosTecidos) { this.orgaosTecidos = orgaosTecidos; }
    }

    public static class ItemOrgaoTecidoEstatistica {

        private String nome;
        private long total;

        public ItemOrgaoTecidoEstatistica() {}

        public ItemOrgaoTecidoEstatistica(String nome, long total) {
            this.nome = nome;
            this.total = total;
        }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
    }
}