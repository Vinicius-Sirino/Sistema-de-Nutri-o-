package com.example.NutriKids.service;

import com.example.NutriKids.entity.Aluno;
import com.example.NutriKids.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    public Optional<Aluno> buscarPorId(Long id) {
        return alunoRepository.findById(id);
    }

    public List<Aluno> buscarPorTurma(String turma) {
        return alunoRepository.findByTurmaIgnoreCase(turma);
    }

    public List<Aluno> listarComRestricao() {
        return alunoRepository.findAlunosComRestricao();
    }

    public Aluno salvar(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    public Optional<Aluno> atualizar(Long id, Aluno alunoAtualizado) {
        return alunoRepository.findById(id).map(alunoExistente -> {
            alunoExistente.setNome(alunoAtualizado.getNome());
            alunoExistente.setTurma(alunoAtualizado.getTurma());
            alunoExistente.setRestricao(alunoAtualizado.getRestricao());
            return alunoRepository.save(alunoExistente);
        });
    }

    public boolean deletar(Long id) {
        if (alunoRepository.existsById(id)) {
            alunoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
