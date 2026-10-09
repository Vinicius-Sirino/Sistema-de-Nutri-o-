package com.example.NutriKids.controller;

import com.example.NutriKids.entity.Usuario;
import com.example.NutriKids.repository.UsuarioRepository;
import com.example.NutriKids.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final FileStorageService fileStorageService;

    /**
     * Upload da foto de perfil do usuário.
     * Salva o arquivo no disco (uploads/fotos_perfil) e grava o link na tabela tb_usuarios (PostgreSQL).
     */
    @PostMapping("/{username}/foto")
    public ResponseEntity<?> uploadFotoPerfil(
            @PathVariable String username,
            @RequestParam("foto") MultipartFile foto) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();
        fileStorageService.armazenarFotoPerfil(username, foto);

        // Define a URL acessível da foto e persiste no banco de dados
        String fotoUrl = "/api/usuarios/" + username + "/foto";
        usuario.setFotoUrl(fotoUrl);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return ResponseEntity.ok(usuarioSalvo);
    }

    /**
     * Retorna a imagem da foto de perfil diretamente para o navegador.
     */
    @GetMapping("/{username}/foto")
    public ResponseEntity<Resource> obterFotoPerfil(@PathVariable String username) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = fileStorageService.carregarFotoPerfilDoUsuario(username);
        if (resource == null || !resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = "image/jpeg";
        try {
            contentType = Files.probeContentType(Paths.get(resource.getURI()));
        } catch (IOException ignored) {}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg"))
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .body(resource);
    }
}
