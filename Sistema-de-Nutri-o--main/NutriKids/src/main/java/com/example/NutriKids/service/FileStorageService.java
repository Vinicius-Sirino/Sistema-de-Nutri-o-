package com.example.NutriKids.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;
    private final Path uploadDirFotos;
    private final Path uploadDirFotosAlunos;

    public FileStorageService() {
        // Pasta onde os laudos médicos serão salvos no servidor
        this.uploadDir = Paths.get("uploads", "cartas_medicas").toAbsolutePath().normalize();
        // Pasta onde as fotos de perfil dos usuários serão salvas no servidor
        this.uploadDirFotos = Paths.get("uploads", "fotos_perfil").toAbsolutePath().normalize();
        // Pasta onde as fotos dos alunos serão salvas no servidor
        this.uploadDirFotosAlunos = Paths.get("uploads", "fotos_alunos").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            Files.createDirectories(this.uploadDirFotos);
            Files.createDirectories(this.uploadDirFotosAlunos);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar os diretórios de armazenamento.", e);
        }
    }

    /**
     * Salva o arquivo no disco e retorna o nome gerado exclusivo.
     */
    public String armazenarArquivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo enviado está vazio.");
        }

        String nomeOriginal = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Validação de tamanho máximo (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("O arquivo excede o limite máximo permitido de 5MB.");
        }

        // Validação básica de extensão segura
        String nomeMinusculo = nomeOriginal.toLowerCase();
        if (!nomeMinusculo.endsWith(".pdf") && !nomeMinusculo.endsWith(".png") &&
            !nomeMinusculo.endsWith(".jpg") && !nomeMinusculo.endsWith(".jpeg")) {
            throw new IllegalArgumentException("Formato inválido. Apenas PDF, PNG e JPG são permitidos.");
        }

        // Gera nome único para evitar sobrescrever arquivos com nomes iguais
        String nomeSalvo = UUID.randomUUID() + "_" + nomeOriginal.replaceAll("[^a-zA-Z0-9.\\-_]", "_");

        try {
            Path destino = this.uploadDir.resolve(nomeSalvo).normalize();
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return nomeSalvo;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar o arquivo: " + nomeOriginal, e);
        }
    }

    /**
     * Carrega o arquivo do disco como Resource para visualização ou download.
     */
    public Resource carregarArquivo(String nomeArquivo) {
        try {
            Path caminhoArquivo = this.uploadDir.resolve(nomeArquivo).normalize();
            Resource resource = new UrlResource(caminhoArquivo.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Arquivo não encontrado ou ilegível: " + nomeArquivo);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao resolver o caminho do arquivo: " + nomeArquivo, e);
        }
    }

    /**
     * Exclui o arquivo físico do disco.
     */
    public boolean deletarArquivo(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            return false;
        }
        try {
            Path caminhoArquivo = this.uploadDir.resolve(nomeArquivo).normalize();
            return Files.deleteIfExists(caminhoArquivo);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Salva a foto de perfil do usuário no disco em uploads/fotos_perfil
     */
    public String armazenarFotoPerfil(String username, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A foto enviada está vazia.");
        }

        String nomeOriginal = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("A foto excede o limite máximo permitido de 5MB.");
        }

        String ext = "";
        int dotIdx = nomeOriginal.lastIndexOf('.');
        if (dotIdx != -1) {
            ext = nomeOriginal.substring(dotIdx).toLowerCase();
        }
        if (!ext.equals(".png") && !ext.equals(".jpg") && !ext.equals(".jpeg") && !ext.equals(".webp")) {
            throw new IllegalArgumentException("Formato inválido. Apenas PNG, JPG, JPEG e WEBP são permitidos.");
        }

        // Remove fotos anteriores desse usuário para não acumular arquivos
        deletarFotoPerfilDoUsuario(username);

        String nomeSalvo = "avatar_" + username.replaceAll("[^a-zA-Z0-9.\\-_]", "_") + ext;

        try {
            Path destino = this.uploadDirFotos.resolve(nomeSalvo).normalize();
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return nomeSalvo;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar a foto de perfil do usuário: " + username, e);
        }
    }

    /**
     * Carrega a foto de perfil do usuário para visualização.
     */
    public Resource carregarFotoPerfilDoUsuario(String username) {
        try {
            String prefixo = "avatar_" + username.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.uploadDirFotos, prefixo + ".*")) {
                for (Path entry : stream) {
                    Resource resource = new UrlResource(entry.toUri());
                    if (resource.exists() && resource.isReadable()) {
                        return resource;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar foto de perfil para o usuário: " + username, e);
        }
        return null;
    }

    /**
     * Remove a foto de perfil do usuário do disco.
     */
    public void deletarFotoPerfilDoUsuario(String username) {
        try {
            String prefixo = "avatar_" + username.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.uploadDirFotos, prefixo + ".*")) {
                for (Path entry : stream) {
                    Files.deleteIfExists(entry);
                }
            }
        } catch (IOException ignored) {}
    }

    /**
     * Salva a foto do aluno no disco em uploads/fotos_alunos
     */
    public String armazenarFotoAluno(Long alunoId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A foto enviada está vazia.");
        }

        String nomeOriginal = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("A foto excede o limite máximo permitido de 5MB.");
        }

        String ext = "";
        int dotIdx = nomeOriginal.lastIndexOf('.');
        if (dotIdx != -1) {
            ext = nomeOriginal.substring(dotIdx).toLowerCase();
        }
        if (!ext.equals(".png") && !ext.equals(".jpg") && !ext.equals(".jpeg") && !ext.equals(".webp")) {
            throw new IllegalArgumentException("Formato inválido. Apenas PNG, JPG, JPEG e WEBP são permitidos.");
        }

        String nomeSalvo = "aluno_" + alunoId + "_" + UUID.randomUUID() + ext;

        try {
            Path destino = this.uploadDirFotosAlunos.resolve(nomeSalvo).normalize();
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return nomeSalvo;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar a foto do aluno ID: " + alunoId, e);
        }
    }

    /**
     * Carrega a foto do aluno do disco como Resource.
     */
    public Resource carregarFotoAluno(String nomeArquivo) {
        try {
            Path caminhoArquivo = this.uploadDirFotosAlunos.resolve(nomeArquivo).normalize();
            Resource resource = new UrlResource(caminhoArquivo.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Foto do aluno não encontrada ou ilegível: " + nomeArquivo);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao resolver o caminho da foto do aluno: " + nomeArquivo, e);
        }
    }

    /**
     * Remove a foto do aluno do disco.
     */
    public boolean deletarFotoAluno(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            return false;
        }
        try {
            Path caminhoArquivo = this.uploadDirFotosAlunos.resolve(nomeArquivo).normalize();
            return Files.deleteIfExists(caminhoArquivo);
        } catch (IOException e) {
            return false;
        }
    }
}
