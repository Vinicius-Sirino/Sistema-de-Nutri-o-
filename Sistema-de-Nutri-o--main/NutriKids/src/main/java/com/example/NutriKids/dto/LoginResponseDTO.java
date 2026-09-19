package com.example.NutriKids.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private boolean autenticado;
    private String mensagem;
    private String username;
    private String nome;
    private String cargo;
}
