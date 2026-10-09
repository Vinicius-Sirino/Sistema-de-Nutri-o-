package com.example.NutriKids.service;

import com.example.NutriKids.entity.Aluno;
import com.example.NutriKids.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final FileStorageService fileStorageService;

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
            
            // Preserva dados da carta se não forem passados na edição geral
            if (alunoAtualizado.getCartaMedicaArquivo() != null) {
                alunoExistente.setCartaMedicaNome(alunoAtualizado.getCartaMedicaNome());
                alunoExistente.setCartaMedicaTipo(alunoAtualizado.getCartaMedicaTipo());
                alunoExistente.setCartaMedicaArquivo(alunoAtualizado.getCartaMedicaArquivo());
            }

            // Preserva dados da foto se não forem passados na edição geral
            if (alunoAtualizado.getFotoArquivo() != null) {
                alunoExistente.setFotoArquivo(alunoAtualizado.getFotoArquivo());
                alunoExistente.setFotoUrl(alunoAtualizado.getFotoUrl());
            }

            return alunoRepository.save(alunoExistente);
        });
    }

    public boolean deletar(Long id) {
        return alunoRepository.findById(id).map(aluno -> {
            if (aluno.getCartaMedicaArquivo() != null) {
                fileStorageService.deletarArquivo(aluno.getCartaMedicaArquivo());
            }
            if (aluno.getFotoArquivo() != null) {
                fileStorageService.deletarFotoAluno(aluno.getFotoArquivo());
            }
            alunoRepository.delete(aluno);
            return true;
        }).orElse(false);
    }

    /**
     * Salva o anexo do laudo/carta médica e vincula ao aluno.
     */
    public Optional<Aluno> salvarCartaMedica(Long alunoId, MultipartFile file) {
        return alunoRepository.findById(alunoId).map(aluno -> {
            if (aluno.getCartaMedicaArquivo() != null) {
                fileStorageService.deletarArquivo(aluno.getCartaMedicaArquivo());
            }

            String nomeSalvo = fileStorageService.armazenarArquivo(file);
            aluno.setCartaMedicaArquivo(nomeSalvo);
            aluno.setCartaMedicaNome(file.getOriginalFilename());
            aluno.setCartaMedicaTipo(file.getContentType());

            return alunoRepository.save(aluno);
        });
    }

    /**
     * Remove o laudo/carta médica vinculado ao aluno.
     */
    public Optional<Aluno> removerCartaMedica(Long alunoId) {
        return alunoRepository.findById(alunoId).map(aluno -> {
            if (aluno.getCartaMedicaArquivo() != null) {
                fileStorageService.deletarArquivo(aluno.getCartaMedicaArquivo());
                aluno.setCartaMedicaArquivo(null);
                aluno.setCartaMedicaNome(null);
                aluno.setCartaMedicaTipo(null);
            }
            return alunoRepository.save(aluno);
        });
    }

    /**
     * Carrega o recurso para download ou visualização inline no navegador.
     */
    public Resource carregarCartaMedica(String nomeArquivo) {
        return fileStorageService.carregarArquivo(nomeArquivo);
    }

    /**
     * Salva a foto do aluno e vincula ao cadastro.
     */
    public Optional<Aluno> salvarFoto(Long alunoId, MultipartFile file) {
        return alunoRepository.findById(alunoId).map(aluno -> {
            if (aluno.getFotoArquivo() != null) {
                fileStorageService.deletarFotoAluno(aluno.getFotoArquivo());
            }

            String nomeSalvo = fileStorageService.armazenarFotoAluno(alunoId, file);
            aluno.setFotoArquivo(nomeSalvo);
            aluno.setFotoUrl("/api/alunos/" + alunoId + "/foto");

            return alunoRepository.save(aluno);
        });
    }

    /**
     * Remove a foto vinculada ao aluno.
     */
    public Optional<Aluno> removerFoto(Long alunoId) {
        return alunoRepository.findById(alunoId).map(aluno -> {
            if (aluno.getFotoArquivo() != null) {
                fileStorageService.deletarFotoAluno(aluno.getFotoArquivo());
                aluno.setFotoArquivo(null);
                aluno.setFotoUrl(null);
            }
            return alunoRepository.save(aluno);
        });
    }

    /**
     * Carrega a foto do aluno do disco como Resource.
     */
    public Resource carregarFoto(String nomeArquivo) {
        return fileStorageService.carregarFotoAluno(nomeArquivo);
    }
}
