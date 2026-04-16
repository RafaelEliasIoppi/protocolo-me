package back.backend.service;

import back.backend.model.CentralTransplantes;
import back.backend.model.Hospital;
import back.backend.repository.CentralTransplantesRepository;
import back.backend.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CentralTransplantesService {

    @Autowired
    private CentralTransplantesRepository centralRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    // Criar Central de Transplantes
    public CentralTransplantes criarCentral(CentralTransplantes central) {
        if (centralRepository.findByCnpj(central.getCnpj()).isPresent()) {
            throw new RuntimeException("Central de Transplantes com CNPJ " + central.getCnpj() + " já existe");
        }
        return centralRepository.save(central);
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

        // Verificar duplicidade de CNPJ em outra central
        if (centralAtualizada.getCnpj() != null) {
            centralRepository.findByCnpj(centralAtualizada.getCnpj())
                .ifPresent(existente -> {
                    if (!existente.getId().equals(id)) {
                        throw new RuntimeException("CNPJ já está cadastrado em outra central");
                    }
                });
        }

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

        return centralRepository.save(central);
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
}
