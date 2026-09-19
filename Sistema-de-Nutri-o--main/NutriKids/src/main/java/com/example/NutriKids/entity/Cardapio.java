package com.example.NutriKids.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_cardapios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String diaDaSemana;

    @Column(nullable = false)
    private String pratoPrincipal; // Lasanha, Strogonoff de Carne

    private String guarnicao;      // arroz, macarrão

    private String sobremesa;      // maçã, bolinho
}
