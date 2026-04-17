package back.backend.service;

import back.backend.model.AnexoDocumento;
import back.backend.repository.AnexoDocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnexoDocumentoService {

    @Autowired
    private AnexoDocumentoRepository anexoRepository;

    // Diretório onde os arquivos serão armazenados
    @Value("${file.upload.dir:uploads/anexos}")
    private String uploadDir;

    // Extensões permitidas
    private static final String[] EXTENSOES_PERMITIDAS = {
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", 
        "jpg", "jpeg", "png", "gif", "bmp",
        "txt", "csv", "zip", "rar"
    };

    // Tamanho máximo em bytes (20MB)
    private static final long TAMANHO_MAXIMO = 20 * 1024 * 1024;

    /**
     * Upload de arquivo para exame
     */
    public AnexoDocumento uploadAnexoExame(Long exameMEId, MultipartFile file, 
                                           String descricao, String uploadPor) throws IOException {
        return uploadArquivo(file, "EXAME", exameMEId, null, descricao, uploadPor);
    }

    /**
     * Upload de arquivo para entrevista familiar
     */
    public AnexoDocumento uploadAnexoEntrevista(Long protocoloMEId, MultipartFile file, 
                                               String descricao, String uploadPor) throws IOException {
        return uploadArquivo(file, "ENTREVISTA", null, protocoloMEId, descricao, uploadPor);
    }

    /**
     * Upload genérico de arquivo
     */
    private AnexoDocumento uploadArquivo(MultipartFile file, String tipoAnexo, 
                                        Long exameMEId, Long protocoloMEId, 
                                        String descricao, String uploadPor) throws IOException {
        // Validações
        validarArquivo(file);

        // Criar diretório se não existir
        Path uploadPath = Paths.get(uploadDir, tipoAnexo.toLowerCase());
        Files.createDirectories(uploadPath);

        // Gerar nome único para o arquivo
        String nomeOriginal = file.getOriginalFilename();
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf(".") + 1);
        String nomeUnico = UUID.randomUUID().toString() + "." + extensao;
        
        // Salvar arquivo no disco
        Path caminhoCompleto = uploadPath.resolve(nomeUnico);
        Files.write(caminhoCompleto, file.getBytes());

        // Criar registro no banco de dados
        AnexoDocumento anexo = new AnexoDocumento();
        anexo.setNomeArquivo(nomeOriginal);
        anexo.setCaminhoArquivo(caminhoCompleto.toString());
        anexo.setTipoMime(file.getContentType());
        anexo.setTamanhoBytes(file.getSize());
        anexo.setTipoAnexo(tipoAnexo);
        anexo.setExameMEId(exameMEId);
        anexo.setProtocoloMEId(protocoloMEId);
        anexo.setDescricao(descricao);
        anexo.setUploadPor(uploadPor);

        return anexoRepository.save(anexo);
    }

    /**
     * Validar arquivo
     */
    private void validarArquivo(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Arquivo vazio");
        }

        if (file.getSize() > TAMANHO_MAXIMO) {
            throw new IOException("Arquivo excede tamanho máximo de 20MB");
        }

        String nomeArquivo = file.getOriginalFilename();
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            throw new IOException("Arquivo sem extensão válida");
        }

        String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1).toLowerCase();
        boolean extensaoValida = false;
        for (String ext : EXTENSOES_PERMITIDAS) {
            if (ext.equals(extensao)) {
                extensaoValida = true;
                break;
            }
        }

        if (!extensaoValida) {
            throw new IOException("Tipo de arquivo não permitido: " + extensao);
        }
    }

    /**
     * Listar anexos de um exame
     */
    public List<AnexoDocumento> listarAnexosExame(Long exameMEId) {
        return anexoRepository.findByExameMEId(exameMEId);
    }

    /**
     * Listar anexos de entrevista (protocolo)
     */
    public List<AnexoDocumento> listarAnexosEntrevista(Long protocoloMEId) {
        return anexoRepository.findByProtocoloMEId(protocoloMEId);
    }

    /**
     * Obter anexo por ID
     */
    public Optional<AnexoDocumento> obterAnexoPorId(Long id) {
        return anexoRepository.findById(id);
    }

    /**
     * Download de arquivo
     */
    public byte[] downloadArquivo(Long anexoId) throws IOException {
        AnexoDocumento anexo = anexoRepository.findById(anexoId)
                .orElseThrow(() -> new RuntimeException("Anexo não encontrado"));

        Path caminhoArquivo = Paths.get(anexo.getCaminhoArquivo());
        if (!Files.exists(caminhoArquivo)) {
            throw new IOException("Arquivo não encontrado no servidor");
        }

        return Files.readAllBytes(caminhoArquivo);
    }

    /**
     * Deletar anexo
     */
    public void deletarAnexo(Long anexoId) throws IOException {
        AnexoDocumento anexo = anexoRepository.findById(anexoId)
                .orElseThrow(() -> new RuntimeException("Anexo não encontrado"));

        // Deletar arquivo do disco
        Path caminhoArquivo = Paths.get(anexo.getCaminhoArquivo());
        if (Files.exists(caminhoArquivo)) {
            Files.delete(caminhoArquivo);
        }

        // Deletar registro do banco
        anexoRepository.delete(anexo);
    }

    /**
     * Deletar todos os anexos de um exame
     */
    public void deletarAnexosExame(Long exameMEId) throws IOException {
        List<AnexoDocumento> anexos = anexoRepository.findByExameMEId(exameMEId);
        for (AnexoDocumento anexo : anexos) {
            deletarAnexo(anexo.getId());
        }
    }

    /**
     * Limpar diretório de upload (manutenção)
     */
    public void limparArquivosOrfaos() throws IOException {
        List<AnexoDocumento> anexos = anexoRepository.findAll();
        Path uploadPath = Paths.get(uploadDir);

        Files.walk(uploadPath)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    boolean existe = anexos.stream()
                            .anyMatch(a -> a.getCaminhoArquivo().equals(file.toString()));
                    if (!existe) {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
