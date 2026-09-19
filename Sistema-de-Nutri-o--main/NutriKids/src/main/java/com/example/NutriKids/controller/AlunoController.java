package com.example.NutriKids.controller;

import com.example.NutriKids.entity.Aluno;
import com.example.NutriKids.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos") //puxar a api do aluno
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
}