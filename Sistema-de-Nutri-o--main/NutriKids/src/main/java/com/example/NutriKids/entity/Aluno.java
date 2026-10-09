package com.example.NutriKids.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tb_alunos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String turma;


    private String restricao;

    // Metadados do anexo da Carta / Laudo Médico
    private String cartaMedicaNome;     // Nome original do arquivo (ex: "laudo_alergia.pdf")
    private String cartaMedicaTipo;     // Content-Type (ex: "application/pdf", "image/jpeg")
    private String cartaMedicaArquivo;  // Nome do arquivo gerado no servidor

    // Foto do Aluno para segurança alimentar e identificação visual imediata
    private String fotoUrl;             // Endpoint para renderizar a imagem (ex: "/api/alunos/1/foto")
    private String fotoArquivo;         // Nome do arquivo gerado no disco
}