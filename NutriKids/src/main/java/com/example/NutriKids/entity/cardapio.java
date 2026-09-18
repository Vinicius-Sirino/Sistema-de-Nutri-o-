package com.example.NutriKids.entity;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_cardapios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class cardapio {



 @Id
 @GeneratedValue
 private long id;

    @Column(nullable = false)
    private String diaDaSemana;

    @Column(nullable = false)
    private  String PratoPrincipal; //Lasanha , Strogonoff de Carne

    private  String guarnicao;  //arroz, macarrão


    private String sobremesa;  //maça, bolinho





}
