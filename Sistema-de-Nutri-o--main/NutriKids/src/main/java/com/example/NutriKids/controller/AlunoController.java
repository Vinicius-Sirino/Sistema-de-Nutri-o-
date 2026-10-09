package com.example.NutriKids.controller;

import com.example.NutriKids.entity.Aluno;
import com.example.NutriKids.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    // Listar todos os alunos cadastrados
    @GetMapping
    public List<Aluno> listarTodos() {
        return alunoService.listarTodos();
    }

    // Buscar aluno por ID
    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        return alunoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar alunos por turma (ex: /api/alunos/turma/1A)
    @GetMapping("/turma/{turma}")
    public List<Aluno> buscarPorTurma(@PathVariable String turma) {
        return alunoService.buscarPorTurma(turma);
    }

    // Listar alunos que possuem restrições alimentares cadastradas
    @GetMapping("/restricoes")
    public List<Aluno> listarComRestricao() {
        return alunoService.listarComRestricao();
    }

    // Cadastrar um novo aluno
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Aluno salvar(@RequestBody Aluno aluno) {
        return alunoService.salvar(aluno);
    }

    // Atualizar dados de um aluno existente
    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizar(@PathVariable Long id, @RequestBody Aluno alunoAtualizado) {
        return alunoService.atualizar(id, alunoAtualizado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Excluir aluno por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (alunoService.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // =========================================================================
    // ENDPOINTS DE CARTA MÉDICA / LAUDO MÉDICO
    // =========================================================================

    /**
     * Upload e vinculação de Carta/Laudo Médico a um aluno.
     * POST /api/alunos/{id}/carta-medica
     */
    @PostMapping("/{id}/carta-medica")
    public ResponseEntity<Aluno> uploadCartaMedica(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return alunoService.salvarCartaMedica(id, arquivo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Visualização ou Download da Carta Médica do aluno.
     * Abre diretamente em nova aba do navegador para PDFs e Imagens.
     * GET /api/alunos/{id}/carta-medica
     */
    @GetMapping("/{id}/carta-medica")
    public ResponseEntity<Resource> visualizarCartaMedica(@PathVariable Long id) {
        return alunoService.buscarPorId(id)
                .filter(aluno -> aluno.getCartaMedicaArquivo() != null)
                .map(aluno -> {
                    Resource resource = alunoService.carregarCartaMedica(aluno.getCartaMedicaArquivo());
                    String contentType = aluno.getCartaMedicaTipo();
                    if (contentType == null || contentType.isBlank()) {
                        contentType = "application/octet-stream";
                    }

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + aluno.getCartaMedicaNome() + "\"")
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Exclui a Carta Médica vinculada ao aluno.
     * DELETE /api/alunos/{id}/carta-medica
     */
    @DeleteMapping("/{id}/carta-medica")
    public ResponseEntity<Aluno> removerCartaMedica(@PathVariable Long id) {
        return alunoService.removerCartaMedica(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================================
    // ENDPOINTS DE FOTO DO ALUNO
    // =========================================================================

    /**
     * Upload e vinculação da foto do aluno.
     * POST /api/alunos/{id}/foto
     */
    @PostMapping("/{id}/foto")
    public ResponseEntity<Aluno> uploadFotoAluno(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile foto) {

        if (foto == null || foto.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return alunoService.salvarFoto(id, foto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Visualização inline da foto do aluno.
     * GET /api/alunos/{id}/foto
     */
    @GetMapping("/{id}/foto")
    public ResponseEntity<Resource> visualizarFotoAluno(@PathVariable Long id) {
        return alunoService.buscarPorId(id)
                .filter(aluno -> aluno.getFotoArquivo() != null)
                .map(aluno -> {
                    Resource resource = alunoService.carregarFoto(aluno.getFotoArquivo());
                    String contentType = "image/jpeg";
                    try {
                        contentType = Files.probeContentType(Paths.get(resource.getURI()));
                    } catch (IOException ignored) {}

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg"))
                            .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Exclui a foto vinculada ao aluno.
     * DELETE /api/alunos/{id}/foto
     */
    @DeleteMapping("/{id}/foto")
    public ResponseEntity<Aluno> removerFotoAluno(@PathVariable Long id) {
        return alunoService.removerFoto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}