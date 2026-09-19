package com.example.NutriKids.service;

import com.example.NutriKids.dto.LoginRequestDTO;
import com.example.NutriKids.dto.LoginResponseDTO;
import com.example.NutriKids.entity.Usuario;
import com.example.NutriKids.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public LoginResponseDTO autenticar(LoginRequestDTO request) {
        if (request == null || request.getUsername() == null || request.getSenha() == null) {
            return new LoginResponseDTO(false, "Usuário e senha são obrigatórios.", null, null, null);
        }

        String username = request.getUsername().trim();
        String senha = request.getSenha().trim();

        if (username.isEmpty() || senha.isEmpty()) {
            return new LoginResponseDTO(false, "Usuário e senha não podem estar em branco.", null, null, null);
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            return new LoginResponseDTO(false, "Credenciais inválidas. Verifique o usuário e a senha.", null, null, null);
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.getSenha().equals(senha)) {
            return new LoginResponseDTO(false, "Credenciais inválidas. Verifique o usuário e a senha.", null, null, null);
        }

        return new LoginResponseDTO(
                true,
                "Login realizado com sucesso!",
                usuario.getUsername(),
                usuario.getNome(),
                usuario.getCargo()
        );
    }
}
