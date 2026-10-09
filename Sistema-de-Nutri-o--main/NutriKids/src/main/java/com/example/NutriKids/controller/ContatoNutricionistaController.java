package com.example.NutriKids.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/contato-nutricionista")
@Slf4j
public class ContatoNutricionistaController {

    @Data
    public static class ContatoDTO {
        private String destino;
        private String nome;
        private String emailRemetente;
        private String assunto;
        private String mensagem;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> enviarMensagem(@RequestBody ContatoDTO contato) {
        log.info("=================================================");
        log.info("[FALE COM A NUTRI] Nova mensagem recebida!");
        log.info("Destinatário (Nutricionista): {}", contato.getDestino());
        log.info("Remetente: {} ({})", contato.getNome(), contato.getEmailRemetente());
        log.info("Assunto: {}", contato.getAssunto());
        log.info("Mensagem: {}", contato.getMensagem());
        log.info("=================================================");

        return ResponseEntity.ok(Map.of(
                "status", "sucesso",
                "mensagem", "Mensagem enviada com sucesso para a Nutricionista (" + contato.getDestino() + ")!"
        ));
    }
}
