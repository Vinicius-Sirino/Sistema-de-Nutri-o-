package com.example.NutriKids.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioAlertaCardapioDTO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;
    private String diaDaSemana;
    private String pratoPrincipal;
    private String guarnicao;
    private String sobremesa;
    private String alergenos;
    private String pratoAlternativo;
    private int totalAlunosEmAlerta;
    private List<AlunoEmRiscoDTO> alunosEmAlerta;
}
