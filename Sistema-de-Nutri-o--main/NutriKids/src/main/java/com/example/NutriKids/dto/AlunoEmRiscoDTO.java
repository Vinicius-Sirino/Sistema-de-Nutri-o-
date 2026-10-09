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
    
    // Item do cardápio que causa o conflito (ex: "Bolo de Chocolate", "Strogonoff com creme de leite")
    private String itemConflitante;

    // Sugestão de substituição segura para o aluno (ex: "Substituir bolo por maçã ou fruta fresca")
    private String substituicaoSugerida;

    // Dados da Carta / Laudo Médico
    private boolean temCartaMedica;
    private String cartaMedicaNome;

    // Foto do Aluno para identificação visual imediata
    private String fotoUrl;
}
