package back.backend.service;

import back.backend.model.AnexoDocumento;
import back.backend.repository.AnexoDocumentoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnexoDocumentoService {

    private final AnexoDocumentoRepository anexoRepository;

    public AnexoDocumentoService(AnexoDocumentoRepository anexoRepository) {
        this.anexoRepository = anexoRepository;
    }

    @Value("${file.upload.dir:uploads/anexos}")
    private String uploadDir;

    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "jpg", "jpeg", "png", "gif", "bmp",
            "txt", "csv", "zip", "rar"
    );

    private static final long TAMANHO_MAXIMO = 20 * 1024 * 1024;

    // ---------------- UPLOAD ----------------

    public AnexoDocumento uploadAnexoExame(Long exameId, MultipartFile file,
                                            String descricao, String uploadPor) throws IOException {
        return upload(file, "EXAME", exameId, null, descricao, uploadPor);
    }

    public AnexoDocumento uploadAnexoEntrevista(Long protocoloId, MultipartFile file,
                                                 String descricao, String uploadPor) throws IOException {
        return upload(file, "ENTREVISTA", null, protocoloId, descricao, uploadPor);
    }

    private AnexoDocumento upload(MultipartFile file, String tipo,
                                  Long exameId, Long protocoloId,
                                  String descricao, String uploadPor) throws IOException {

        validar(file);

        Path dir = Paths.get(uploadDir, tipo.toLowerCase()).normalize();
        Files.createDirectories(dir);

        String original = file.getOriginalFilename();
        String ext = getExtensao(original);

        String nomeUnico = UUID.randomUUID() + "." + ext;

        Path destino = dir.resolve(nomeUnico).normalize();
        Files.write(destino, file.getBytes());

        AnexoDocumento anexo = new AnexoDocumento();
        anexo.setNomeArquivo(original);
        anexo.setCaminhoArquivo(destino.toString());
        anexo.setTipoMime(file.getContentType());
        anexo.setTamanhoBytes(file.getSize());
        anexo.setTipoAnexo(tipo);
        anexo.setExameMEId(exameId);
        anexo.setProtocoloMEId(protocoloId);
        anexo.setDescricao(descricao);
        anexo.setUploadPor(uploadPor);

        return anexoRepository.save(anexo);
    }

    // ---------------- VALIDATION ----------------

    private void validar(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Arquivo inválido");
        }

        if (file.getSize() > TAMANHO_MAXIMO) {
            throw new RuntimeException("Arquivo maior que 20MB");
        }

        String ext = getExtensao(file.getOriginalFilename());

        if (!EXTENSOES_PERMITIDAS.contains(ext)) {
            throw new RuntimeException("Extensão não permitida: " + ext);
        }
    }

    private String getExtensao(String nome) {
        if (nome == null || !nome.contains(".")) {
            throw new RuntimeException("Arquivo sem extensão");
        }
        return nome.substring(nome.lastIndexOf(".") + 1).toLowerCase();
    }

    // ---------------- QUERY ----------------

    public List<AnexoDocumento> listarAnexosExame(Long exameId) {
        return anexoRepository.findByExameMEId(exameId);
    }

    public List<AnexoDocumento> listarAnexosEntrevista(Long protocoloId) {
        return anexoRepository.findByProtocoloMEId(protocoloId);
    }

    public AnexoDocumento obterPorId(Long id) {
        return anexoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anexo não encontrado"));
    }

    // ---------------- DOWNLOAD ----------------

    public byte[] downloadArquivo(Long id) throws IOException {

        AnexoDocumento anexo = obterPorId(id);

        Path file = Paths.get(anexo.getCaminhoArquivo()).normalize();

        if (!Files.exists(file)) {
            throw new RuntimeException("Arquivo não encontrado no disco");
        }

        return Files.readAllBytes(file);
    }

    // ---------------- DELETE ----------------

    public void deletarAnexo(Long id) throws IOException {

        AnexoDocumento anexo = obterPorId(id);

        Path file = Paths.get(anexo.getCaminhoArquivo()).normalize();

        if (Files.exists(file)) {
            Files.delete(file);
        }

        anexoRepository.delete(anexo);
    }

    public void deletarAnexosExame(Long exameId) throws IOException {

        List<AnexoDocumento> anexos = anexoRepository.findByExameMEId(exameId);

        for (AnexoDocumento a : anexos) {
            deletarAnexo(a.getId());
        }
    }

    // ---------------- MAINTENANCE ----------------

    public void limparArquivosOrfaos() throws IOException {

        List<AnexoDocumento> anexos = anexoRepository.findAll();

        Set<String> arquivosBanco = anexos.stream()
                .map(AnexoDocumento::getCaminhoArquivo)
                .collect(Collectors.toSet());

        try (var stream = Files.walk(Paths.get(uploadDir))) {

            stream.filter(Files::isRegularFile)
                    .forEach(file -> {

                        if (!arquivosBanco.contains(file.toString())) {
                            try {
                                Files.delete(file);
                            } catch (IOException e) {
                                System.err.println("Erro ao deletar arquivo órfão: " + file);
                            }
                        }
                    });
        }
    }
}