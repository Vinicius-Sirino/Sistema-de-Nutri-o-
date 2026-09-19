package com.example.NutriKids.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tb_cardapios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Data específica do calendário (ex: 2026-09-21)
    @JsonFormat(pattern = "yyyy-MM-dd") //calendario
    private LocalDate data;

    @Column(nullable = false)
    private String diaDaSemana; // Segunda-feira, Terça-feira...

    @Column(nullable = false)
    private String pratoPrincipal; // Lasanha, Strogonoff de Carne

    private String guarnicao;      // arroz, macarrão

    private String sobremesa;      // maçã, bolinho

    private String alergenos;      // Ex: "lactose, glúten, amendoim"

    private String pratoAlternativo; // Ex: "Frango grelhado com salada de frutas (sem lactose e glúten)"
}
