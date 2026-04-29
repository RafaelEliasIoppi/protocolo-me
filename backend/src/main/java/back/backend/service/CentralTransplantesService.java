package back.backend.service;

import back.backend.dto.CentralTransplantesDTO;
import back.backend.dto.CentralTransplantesRequestDTO;
import back.backend.exception.ConflitoNegocioException;
import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.model.*;
import back.backend.repository.*;
import back.backend.mapper.CentralTransplantesMapper;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CentralTransplantesService {

    private final CentralTransplantesRepository centralRepository;
    private final HospitalRepository hospitalRepository;
    private final ProtocoloMERepository protocoloMERepository;
    private final PacienteRepository pacienteRepository;
    private final CentralTransplantesMapper mapper;

    public CentralTransplantesService(
            CentralTransplantesRepository centralRepository,
            HospitalRepository hospitalRepository,
            ProtocoloMERepository protocoloMERepository,
            PacienteRepository pacienteRepository,
            CentralTransplantesMapper mapper) {

        this.centralRepository = centralRepository;
        this.hospitalRepository = hospitalRepository;
        this.protocoloMERepository = protocoloMERepository;
        this.pacienteRepository = pacienteRepository;
        this.mapper = mapper;
    }

    // =========================
    // DTO
    // =========================

    public CentralTransplantesDTO criarCentralFromDTO(CentralTransplantesRequestDTO dto) {
        CentralTransplantes central = mapDto(dto, new CentralTransplantes());
        validarDuplicidade(central, null);
        return mapper.toDTO(centralRepository.save(central));
    }

    public CentralTransplantesDTO atualizarCentralFromDTO(Long id, CentralTransplantesRequestDTO dto) {

        CentralTransplantes central = getCentral(id);

        mapDto(dto, central);
        validarDuplicidade(central, id);

        return mapper.toDTO(centralRepository.save(central));
    }

    private CentralTransplantes mapDto(CentralTransplantesRequestDTO dto, CentralTransplantes c) {

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

        return c;
    }

    // =========================
    // CRUD
    // =========================

    public List<CentralTransplantesDTO> listarTodas() {
        return centralRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    public CentralTransplantesDTO buscarPorCnpjOuFalhar(String cnpj) {
        return mapper.toDTO(
                centralRepository.findByCnpj(cnpj.trim())
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Central não encontrada"))
        );
    }

    public CentralTransplantesDTO buscarPorNomeOuFalhar(String nome) {
        return mapper.toDTO(
                centralRepository.findByNomeIgnoreCase(nome.trim())
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Central não encontrada"))
        );
    }

    public List<CentralTransplantesDTO> listarPorCidade(String cidade) {
        return centralRepository.findByCidadeIgnoreCase(cidade.trim())
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<CentralTransplantesDTO> listarPorEstado(String estado) {
        return centralRepository.findByEstadoIgnoreCase(estado.trim())
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<CentralTransplantesDTO> listarPorStatus(String status) {
        return centralRepository.findByStatusOperacional(parseStatus(status))
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public CentralTransplantesDTO buscarPorIdOuFalhar(Long id) {
        return mapper.toDTO(getCentral(id));
    }

    public void deletarCentral(Long id) {
        if (!centralRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Central não encontrada");
        }
        centralRepository.deleteById(id);
    }

    // =========================
    // BUSINESS
    // =========================

    public CentralTransplantesDTO alterarStatus(Long id, String status) {
        CentralTransplantes c = getCentral(id);
        c.setStatusOperacional(parseStatus(status));
        return mapper.toDTO(centralRepository.save(c));
    }

    public CentralTransplantesDTO vincularHospital(Long centralId, Long hospitalId) {

        CentralTransplantes central = getCentral(centralId);
        Hospital hospital = getHospital(hospitalId);

        List<Hospital> lista = getOrDefaultList(central.getHospitaisParceados());

        if (!lista.contains(hospital)) {
            lista.add(hospital);
        }

        central.setHospitaisParceados(lista);
        return mapper.toDTO(centralRepository.save(central));
    }

    public CentralTransplantesDTO removerHospital(Long centralId, Long hospitalId) {

        CentralTransplantes central = getCentral(centralId);
        Hospital hospital = getHospital(hospitalId);

        List<Hospital> lista = getOrDefaultList(central.getHospitaisParceados());
        lista.remove(hospital);

        central.setHospitaisParceados(lista);
        return mapper.toDTO(centralRepository.save(central));
    }

    // =========================
    // 🔥 ESTATÍSTICAS REAIS
    // =========================

    public EstatisticasCentralDoacaoTransplante obterEstatisticasDoacaoTransplante() {

        List<ProtocoloME> protocolos = protocoloMERepository.findAll();

        EstatisticasCentralDoacaoTransplante dto = new EstatisticasCentralDoacaoTransplante();

        dto.setTotalProtocolos(protocolos.size());

        long emAvaliacao = 0;
        long autorizados = 0;
        long recusas = 0;

        Map<String, Long> orgaosCount = new HashMap<>();

        for (ProtocoloME p : protocolos) {

            switch (p.getStatus()) {
                case NOTIFICADO, EM_PROCESSO -> emAvaliacao++;
                case DOACAO_AUTORIZADA -> autorizados++;
                case FAMILIA_RECUSOU -> recusas++;
            }

            // 🔥 CONTAR ÓRGÃOS
            Optional.ofNullable(p.getDoacao())
                    .map(Doacao::getOrgaos)
                    .ifPresent(orgaos -> {
                        for (OrgaoDoado orgao : orgaos) {
                            if (orgao.getTipo() == null) continue;
                            String nome = orgao.getTipo().name();
                            orgaosCount.put(nome, orgaosCount.getOrDefault(nome, 0L) + 1);
                        }
                    });
        }

        dto.setDoadoresEmAvaliacao(emAvaliacao);
        dto.setDoadoresAutorizados(autorizados);
        dto.setRecusasFamiliares(recusas);

        dto.setReceptoresAptos(
                pacienteRepository.countByStatus(Paciente.StatusPaciente.APTO_TRANSPLANTE)
        );

        dto.setReceptoresNaoAptos(
                pacienteRepository.countByStatus(Paciente.StatusPaciente.NAO_APTO)
        );

        // 🔥 montar lista de órgãos
        List<ItemOrgaoTecidoEstatistica> listaOrgaos = orgaosCount.entrySet()
                .stream()
                .map(e -> new ItemOrgaoTecidoEstatistica(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ItemOrgaoTecidoEstatistica::getTotal).reversed())
                .toList();

        dto.setOrgaosTecidos(listaOrgaos);

        return dto;
    }

    // =========================
    // HELPERS
    // =========================

    private CentralTransplantes getCentral(Long id) {
        return centralRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Central não encontrada"));
    }

    private Hospital getHospital(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado"));
    }

    private List<Hospital> getOrDefaultList(List<Hospital> list) {
        return list != null ? list : new ArrayList<>();
    }

    private CentralTransplantes.StatusCentral parseStatus(String status) {
        try {
            return CentralTransplantes.StatusCentral.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new ConflitoNegocioException("Status inválido: " + status);
        }
    }

    private void validarDuplicidade(CentralTransplantes central, Long idAtual) {

        centralRepository.findByNome(central.getNome())
                .filter(c -> !c.getId().equals(idAtual))
                .ifPresent(c -> { throw new ConflitoNegocioException("Nome já cadastrado"); });

        centralRepository.findByCnpj(central.getCnpj())
                .filter(c -> !c.getId().equals(idAtual))
                .ifPresent(c -> { throw new ConflitoNegocioException("CNPJ já cadastrado"); });
    }

    // =========================
    // DTO INTERNO
    // =========================

    public static class EstatisticasCentralDoacaoTransplante {

        private long totalProtocolos;
        private long doadoresEmAvaliacao;
        private long doadoresAutorizados;
        private long recusasFamiliares;
        private long receptoresAptos;
        private long receptoresNaoAptos;
        private List<ItemOrgaoTecidoEstatistica> orgaosTecidos;

        // getters/setters...
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

        public ItemOrgaoTecidoEstatistica(String nome, long total) {
            this.nome = nome;
            this.total = total;
        }

        public String getNome() { return nome; }
        public long getTotal() { return total; }
    }
}
