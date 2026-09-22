package com.example.NutriKids.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlunoEmRiscoDTO {
    private Long id;
    private String nome;
    private String turma;
    private String restricao;
    private String motivoAlerta;
    
    // Novo: item do cardápio que causa o conflito (ex: "Bolo de Chocolate", "Strogonoff com creme de leite")
    private String itemConflitante;

    // Novo: sugestão de substituição segura para o aluno (ex: "Substituir bolo por maçã ou fruta fresca")
    private String substituicaoSugerida;
}
