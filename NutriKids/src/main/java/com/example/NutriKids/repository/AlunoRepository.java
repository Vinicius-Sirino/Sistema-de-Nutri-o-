package com.example.NutriKids.repository;

import com.example.NutriKids.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    // Buscar alunos por turma (ex: "1º Ano A", ignorando maiúsculas/minúsculas)
    List<Aluno> findByTurmaIgnoreCase(String turma);

    // Listar alunos que possuem alguma restrição alimentar cadastrada (não nula e não vazia)
    @Query("SELECT a FROM Aluno a WHERE a.restricao IS NOT NULL AND TRIM(a.restricao) <> ''")
    List<Aluno> findAlunosComRestricao();

    // Mantido por compatibilidade
    List<Aluno> findByRestricaoIsNotNull();
}
