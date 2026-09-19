package com.example.NutriKids;

import com.example.NutriKids.controller.AuthController;
import com.example.NutriKids.dto.LoginRequestDTO;
import com.example.NutriKids.dto.LoginResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Test
    @DisplayName("Deve autenticar com sucesso usando credenciais corretas")
    void deveAutenticarComSucesso() {
        LoginRequestDTO request = new LoginRequestDTO("admin", "admin123");
        ResponseEntity<LoginResponseDTO> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isAutenticado());
        assertEquals("admin", response.getBody().getUsername());
        assertNotNull(response.getBody().getNome());
        assertFalse(response.getBody().getNome().trim().isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com senha incorreta")
    void deveRejeitarSenhaIncorreta() {
        LoginRequestDTO request = new LoginRequestDTO("admin", "senhaErrada");
        ResponseEntity<LoginResponseDTO> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isAutenticado());
    }

    @Test
    @DisplayName("Deve rejeitar usuário inexistente")
    void deveRejeitarUsuarioInexistente() {
        LoginRequestDTO request = new LoginRequestDTO("usuarioFantasma", "123");
        ResponseEntity<LoginResponseDTO> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isAutenticado());
    }
}
