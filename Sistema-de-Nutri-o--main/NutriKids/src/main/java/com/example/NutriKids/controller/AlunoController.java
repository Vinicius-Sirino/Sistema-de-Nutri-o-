package com.example.NutriKids.controller;


import com.example.NutriKids.entity.Aluno;
import com.example.NutriKids.repository.AlunoRepository; //prstar atenção nos import do repository.
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    // Deixamos o nome apenas como "repository" para bater com o código de baixo
    private final AlunoRepository repository;

    @GetMapping
    public List<Aluno> listarTodos() {
        return repository.findAll();
    }

    @PostMapping
    public Aluno salvar(@RequestBody Aluno aluno) {
        // Usando o cast (Aluno) que funcionou na sua máquina anterior
        return (Aluno) repository.save(aluno);
    }

    @GetMapping("/restricoes")
    public List<Aluno> listarComRestricao() {
        return repository.findByRestricaoIsNotNull();
    }
}