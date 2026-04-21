import back.backend.dto.CentralTransplantesDTO;
package back.backend.service;

import back.backend.model.CentralTransplantes;
import back.backend.model.Hospital;
import back.backend.model.Paciente;
import back.backend.model.ProtocoloME;
import back.backend.repository.CentralTransplantesRepository;
import back.backend.repository.HospitalRepository;
import back.backend.repository.PacienteRepository;
import back.backend.repository.ProtocoloMERepository;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CentralTransplantesService {

    // Criar a partir do DTO
    public CentralTransplantes criarCentralFromDTO(CentralTransplantesDTO dto) {
        CentralTransplantes central = new CentralTransplantes();
        preencherEntityComDTO(central, dto);
        return criarCentral(central);
    }

    // Atualizar a partir do DTO
    public CentralTransplantes atualizarCentralFromDTO(Long id, CentralTransplantesDTO dto) {
        CentralTransplantes central = buscarPorId(id).orElseThrow(() -> new RuntimeException("Central não encontrada"));
        preencherEntityComDTO(central, dto);
        return centralRepository.save(central);
    }

    private void preencherEntityComDTO(CentralTransplantes central, CentralTransplantesDTO dto) {
        central.setNome(dto.getNome());
        central.setCnpj(dto.getCnpj());
        central.setEndereco(dto.getEndereco());
        central.setCidade(dto.getCidade());
        central.setEstado(dto.getEstado());
        central.setTelefone(dto.getTelefone());
        central.setTelefonePlantao(dto.getTelefonePlantao());
        central.setEmail(dto.getEmail());
        central.setEmailPlantao(dto.getEmailPlantao());
        central.setCoordenador(dto.getCoordenador());
        central.setTelefoneCoordenador(dto.getTelefoneCoordenador());
        central.setCapacidadeProcessamento(dto.getCapacidadeProcessamento());
        central.setEspecialidadesOrgaos(dto.getEspecialidadesOrgaos());
    }

    private static final List<String> ITENS_ORGAOS_TECIDOS = Arrays.asList(
            "Coração",
            "Pulmão",
            "Fígado",
            "Rim",
            "Pâncreas",
            "Intestino",
            "Córnea",
            "Pele",
            "Ossos",
            "Tendões",
            "Válvulas Cardíacas",
            "Vasos Sanguíneos",
            "Medula Óssea"
    );

    private static final Map<String, String> ALIAS_ORGAOS_TECIDOS = new LinkedHashMap<>();

    static {
        ALIAS_ORGAOS_TECIDOS.put("coracao", "Coração");
        ALIAS_ORGAOS_TECIDOS.put("pulmao", "Pulmão");
        ALIAS_ORGAOS_TECIDOS.put("figado", "Fígado");
        ALIAS_ORGAOS_TECIDOS.put("rim", "Rim");
        ALIAS_ORGAOS_TECIDOS.put("rins", "Rim");
        ALIAS_ORGAOS_TECIDOS.put("pancreas", "Pâncreas");
        ALIAS_ORGAOS_TECIDOS.put("intestino", "Intestino");
        ALIAS_ORGAOS_TECIDOS.put("cornea", "Córnea");
        ALIAS_ORGAOS_TECIDOS.put("corneas", "Córnea");
        ALIAS_ORGAOS_TECIDOS.put("pele", "Pele");
        ALIAS_ORGAOS_TECIDOS.put("osso", "Ossos");
        ALIAS_ORGAOS_TECIDOS.put("ossos", "Ossos");
        ALIAS_ORGAOS_TECIDOS.put("tendao", "Tendões");
        ALIAS_ORGAOS_TECIDOS.put("tendoes", "Tendões");
        ALIAS_ORGAOS_TECIDOS.put("valvula cardiaca", "Válvulas Cardíacas");
        ALIAS_ORGAOS_TECIDOS.put("valvulas cardiacas", "Válvulas Cardíacas");
        ALIAS_ORGAOS_TECIDOS.put("vaso sanguineo", "Vasos Sanguíneos");
        ALIAS_ORGAOS_TECIDOS.put("vasos sanguineos", "Vasos Sanguíneos");
        ALIAS_ORGAOS_TECIDOS.put("medula ossea", "Medula Óssea");
    }

    private final CentralTransplantesRepository centralRepository;
    private final HospitalRepository hospitalRepository;
    private final ProtocoloMERepository protocoloMERepository;
    private final PacienteRepository pacienteRepository;

    public CentralTransplantesService(CentralTransplantesRepository centralRepository,
                                      HospitalRepository hospitalRepository,
                                      ProtocoloMERepository protocoloMERepository,
                                      PacienteRepository pacienteRepository) {
        this.centralRepository = centralRepository;
        this.hospitalRepository = hospitalRepository;
        this.protocoloMERepository = protocoloMERepository;
        this.pacienteRepository = pacienteRepository;
    }

    // Criar Central de Transplantes
    public CentralTransplantes criarCentral(CentralTransplantes central) {
        validarCentralDuplicada(central, null);
        try {
            return centralRepository.save(central);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(mensagemViolacaoUnicidade(central), e);
        }
    }

    // Listar todas as centrais
    public List<CentralTransplantes> listarTodas() {
        return centralRepository.findAll();
    }

    // Buscar por ID
    public Optional<CentralTransplantes> buscarPorId(Long id) {
        return centralRepository.findById(id);
    }

    // Buscar por CNPJ
    public Optional<CentralTransplantes> buscarPorCnpj(String cnpj) {
        return centralRepository.findByCnpj(cnpj);
    }

    // Buscar por Nome
    public Optional<CentralTransplantes> buscarPorNome(String nome) {
        return centralRepository.findByNome(nome);
    }

    // Listar por cidade
    public List<CentralTransplantes> listarPorCidade(String cidade) {
        return centralRepository.findByCidade(cidade);
    }

    // Listar por estado
    public List<CentralTransplantes> listarPorEstado(String estado) {
        return centralRepository.findByEstado(estado);
    }

    // Listar por status
    public List<CentralTransplantes> listarPorStatus(CentralTransplantes.StatusCentral status) {
        return centralRepository.findByStatusOperacional(status);
    }

    // Atualizar Central
    public CentralTransplantes atualizarCentral(Long id, CentralTransplantes centralAtualizada) {
        CentralTransplantes central = centralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Central não encontrada com ID: " + id));

        validarCentralDuplicada(centralAtualizada, id);

        central.setNome(centralAtualizada.getNome());
        central.setEndereco(centralAtualizada.getEndereco());
        central.setCidade(centralAtualizada.getCidade());
        central.setEstado(centralAtualizada.getEstado());
        central.setTelefone(centralAtualizada.getTelefone());
        central.setTelefonePlantao(centralAtualizada.getTelefonePlantao());
        central.setEmail(centralAtualizada.getEmail());
        central.setEmailPlantao(centralAtualizada.getEmailPlantao());
        central.setCoordenador(centralAtualizada.getCoordenador());
        central.setTelefoneCoordenador(centralAtualizada.getTelefoneCoordenador());
        central.setCapacidadeProcessamento(centralAtualizada.getCapacidadeProcessamento());
        central.setEspecialidadesOrgaos(centralAtualizada.getEspecialidadesOrgaos());

        try {
            return centralRepository.save(central);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(mensagemViolacaoUnicidade(centralAtualizada), e);
        }
    }

    // Alterar Status da Central
    public CentralTransplantes alterarStatus(Long id, CentralTransplantes.StatusCentral novoStatus) {
        CentralTransplantes central = centralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Central não encontrada com ID: " + id));

        central.setStatusOperacional(novoStatus);
        return centralRepository.save(central);
    }

    // Vincular Hospital à Central
    public CentralTransplantes vincularHospital(Long centralId, Long hospitalId) {
        CentralTransplantes central = centralRepository.findById(centralId)
                .orElseThrow(() -> new RuntimeException("Central não encontrada"));
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital não encontrado"));

        if (central.getHospitaisParceados() != null && !central.getHospitaisParceados().contains(hospital)) {
            central.getHospitaisParceados().add(hospital);
        } else if (central.getHospitaisParceados() == null) {
            List<Hospital> hospitais = new java.util.ArrayList<>();
            hospitais.add(hospital);
            central.setHospitaisParceados(hospitais);
        }

        return centralRepository.save(central);
    }

    // Remover Hospital da Central
    public CentralTransplantes removerHospital(Long centralId, Long hospitalId) {
        CentralTransplantes central = centralRepository.findById(centralId)
                .orElseThrow(() -> new RuntimeException("Central não encontrada"));
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital não encontrado"));

        if (central.getHospitaisParceados() != null) {
            central.getHospitaisParceados().remove(hospital);
        }

        return centralRepository.save(central);
    }

    // Deletar Central
    public void deletarCentral(Long id) {
        if (!centralRepository.existsById(id)) {
            throw new RuntimeException("Central não encontrada com ID: " + id);
        }
        centralRepository.deleteById(id);
    }

    private void validarCentralDuplicada(CentralTransplantes central, Long idIgnorado) {
        String nomeNormalizado = normalizarTexto(central.getNome());
        String cnpjNormalizado = normalizarTexto(central.getCnpj());

        if (nomeNormalizado == null || nomeNormalizado.isEmpty()) {
            throw new RuntimeException("Nome da central é obrigatório");
        }

        if (cnpjNormalizado == null || cnpjNormalizado.isEmpty()) {
            throw new RuntimeException("CNPJ da central é obrigatório");
        }

        boolean nomeDuplicado = centralRepository.findAll().stream().anyMatch(existing ->
                existing.getId() != null
                        && !existing.getId().equals(idIgnorado)
                        && normalizarTexto(existing.getNome()) != null
                        && normalizarTexto(existing.getNome()).equalsIgnoreCase(nomeNormalizado)
        );

        if (nomeDuplicado) {
            throw new RuntimeException("Já existe uma central de transplantes cadastrada com esse nome");
        }

        boolean cnpjDuplicado = centralRepository.findAll().stream().anyMatch(existing ->
                existing.getId() != null
                        && !existing.getId().equals(idIgnorado)
                        && normalizarTexto(existing.getCnpj()) != null
                        && normalizarTexto(existing.getCnpj()).equalsIgnoreCase(cnpjNormalizado)
        );

        if (cnpjDuplicado) {
            throw new RuntimeException("Já existe uma central de transplantes cadastrada com esse CNPJ");
        }
    }

    private String normalizarTexto(String valor) {
        return valor == null ? null : valor.trim();
    }

    private String mensagemViolacaoUnicidade(CentralTransplantes central) {
        String nome = normalizarTexto(central.getNome());
        String cnpj = normalizarTexto(central.getCnpj());
        if (nome != null && centralRepository.findByNome(nome).isPresent()) {
            return "Já existe uma central de transplantes cadastrada com esse nome";
        }
        if (cnpj != null && centralRepository.findByCnpj(cnpj).isPresent()) {
            return "Já existe uma central de transplantes cadastrada com esse CNPJ";
        }
        return "Não foi possível salvar a central por violação de unicidade";
    }

    public EstatisticasCentralDoacaoTransplante obterEstatisticasDoacaoTransplante() {
        List<ProtocoloME> protocolos = protocoloMERepository.findAll();

        long totalProtocolos = protocolos.size();
        long doadoresEmAvaliacao = protocolos.stream()
                .filter(this::isDoadorEmAvaliacao)
                .count();
        long doadoresAutorizados = protocolos.stream()
                .filter(this::isDoadorAutorizado)
                .count();
        long recusasFamiliares = protocolos.stream()
                .filter(p -> p.getStatus() == ProtocoloME.StatusProtocoloME.FAMILIA_RECUSOU)
                .count();
        long protocolosContraindicados = protocolos.stream()
                .filter(p -> p.getStatus() == ProtocoloME.StatusProtocoloME.CONTRAINDICADO)
                .count();
        long protocolosFinalizados = protocolos.stream()
                .filter(p -> p.getStatus() == ProtocoloME.StatusProtocoloME.FINALIZADO)
                .count();

        long receptoresAptos = pacienteRepository.countByStatus(Paciente.StatusPaciente.APTO_TRANSPLANTE);
        long receptoresNaoAptos = pacienteRepository.countByStatus(Paciente.StatusPaciente.NAO_APTO);

        Map<String, Long> contagemPorItem = new LinkedHashMap<>();
        for (String item : ITENS_ORGAOS_TECIDOS) {
            contagemPorItem.put(item, 0L);
        }

        for (ProtocoloME protocolo : protocolos) {
            Set<String> itensNoProtocolo = extrairItensOrgaosTecidos(protocolo.getOrgaosDisponiveis());
            for (String item : itensNoProtocolo) {
                contagemPorItem.computeIfPresent(item, (k, v) -> v + 1L);
            }
        }

        List<ItemOrgaoTecidoEstatistica> distribuicao = contagemPorItem.entrySet().stream()
                .map(entry -> new ItemOrgaoTecidoEstatistica(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        EstatisticasCentralDoacaoTransplante resultado = new EstatisticasCentralDoacaoTransplante();
        resultado.setTotalProtocolos(totalProtocolos);
        resultado.setDoadoresEmAvaliacao(doadoresEmAvaliacao);
        resultado.setDoadoresAutorizados(doadoresAutorizados);
        resultado.setRecusasFamiliares(recusasFamiliares);
        resultado.setProtocolosContraindicados(protocolosContraindicados);
        resultado.setProtocolosFinalizados(protocolosFinalizados);
        resultado.setReceptoresAptos(receptoresAptos);
        resultado.setReceptoresNaoAptos(receptoresNaoAptos);
        resultado.setOrgaosTecidos(distribuicao);
        return resultado;
    }

    private boolean isDoadorEmAvaliacao(ProtocoloME protocolo) {
        if (protocolo == null || protocolo.getStatus() == null) {
            return false;
        }

        return protocolo.getStatus() == ProtocoloME.StatusProtocoloME.NOTIFICADO
                || protocolo.getStatus() == ProtocoloME.StatusProtocoloME.EM_PROCESSO
                || protocolo.getStatus() == ProtocoloME.StatusProtocoloME.MORTE_CEREBRAL_CONFIRMADA
                || protocolo.getStatus() == ProtocoloME.StatusProtocoloME.ENTREVISTA_FAMILIAR;
    }

    private boolean isDoadorAutorizado(ProtocoloME protocolo) {
        if (protocolo == null) {
            return false;
        }
        return protocolo.getStatus() == ProtocoloME.StatusProtocoloME.DOACAO_AUTORIZADA
                || Boolean.TRUE.equals(protocolo.getAutopsiaAutorizada());
    }

    private Set<String> extrairItensOrgaosTecidos(String campoOrgaosDisponiveis) {
        if (campoOrgaosDisponiveis == null || campoOrgaosDisponiveis.trim().isEmpty()) {
            return java.util.Collections.emptySet();
        }

        String textoNormalizado = campoOrgaosDisponiveis
                .replace("/", ",")
                .replace(";", ",")
                .replace("|", ",")
                .replace(" e ", ",");

        Set<String> itens = Arrays.stream(textoNormalizado.split(","))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .map(this::normalizarToken)
                .map(ALIAS_ORGAOS_TECIDOS::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        return itens;
    }

    private String normalizarToken(String token) {
        String semAcentos = Normalizer.normalize(token, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.toLowerCase(Locale.ROOT).trim();
    }

    public static class EstatisticasCentralDoacaoTransplante {
        private long totalProtocolos;
        private long doadoresEmAvaliacao;
        private long doadoresAutorizados;
        private long recusasFamiliares;
        private long protocolosContraindicados;
        private long protocolosFinalizados;
        private long receptoresAptos;
        private long receptoresNaoAptos;
        private List<ItemOrgaoTecidoEstatistica> orgaosTecidos = new ArrayList<>();

        public long getTotalProtocolos() {
            return totalProtocolos;
        }

        public void setTotalProtocolos(long totalProtocolos) {
            this.totalProtocolos = totalProtocolos;
        }

        public long getDoadoresEmAvaliacao() {
            return doadoresEmAvaliacao;
        }

        public void setDoadoresEmAvaliacao(long doadoresEmAvaliacao) {
            this.doadoresEmAvaliacao = doadoresEmAvaliacao;
        }

        public long getDoadoresAutorizados() {
            return doadoresAutorizados;
        }

        public void setDoadoresAutorizados(long doadoresAutorizados) {
            this.doadoresAutorizados = doadoresAutorizados;
        }

        public long getRecusasFamiliares() {
            return recusasFamiliares;
        }

        public void setRecusasFamiliares(long recusasFamiliares) {
            this.recusasFamiliares = recusasFamiliares;
        }

        public long getProtocolosContraindicados() {
            return protocolosContraindicados;
        }

        public void setProtocolosContraindicados(long protocolosContraindicados) {
            this.protocolosContraindicados = protocolosContraindicados;
        }

        public long getProtocolosFinalizados() {
            return protocolosFinalizados;
        }

        public void setProtocolosFinalizados(long protocolosFinalizados) {
            this.protocolosFinalizados = protocolosFinalizados;
        }

        public long getReceptoresAptos() {
            return receptoresAptos;
        }

        public void setReceptoresAptos(long receptoresAptos) {
            this.receptoresAptos = receptoresAptos;
        }

        public long getReceptoresNaoAptos() {
            return receptoresNaoAptos;
        }

        public void setReceptoresNaoAptos(long receptoresNaoAptos) {
            this.receptoresNaoAptos = receptoresNaoAptos;
        }

        public List<ItemOrgaoTecidoEstatistica> getOrgaosTecidos() {
            return orgaosTecidos;
        }

        public void setOrgaosTecidos(List<ItemOrgaoTecidoEstatistica> orgaosTecidos) {
            this.orgaosTecidos = orgaosTecidos;
        }
    }

    public static class ItemOrgaoTecidoEstatistica {
        private String nome;
        private long total;

        public ItemOrgaoTecidoEstatistica() {
        }

        public ItemOrgaoTecidoEstatistica(String nome, long total) {
            this.nome = nome;
            this.total = total;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }
}
