package com.example.NutriKids.controller;

import com.example.NutriKids.dto.RelatorioAlertaCardapioDTO;
import com.example.NutriKids.service.NutricaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/nutricao")
@RequiredArgsConstructor
public class NutricaoController {

    private final NutricaoService nutricaoService;

    /**
     * Endpoint para consultar alertas de restrições alimentares por data ou dia da semana.
     * Exemplos de uso:
     * GET /api/nutricao/alertas?data=2026-09-21
     * GET /api/nutricao/alertas?data=2026-09-21&turma=1A
     * GET /api/nutricao/alertas?diaDaSemana=Segunda-feira&turma=1A
     */
    @GetMapping("/alertas")
    public ResponseEntity<RelatorioAlertaCardapioDTO> consultarAlertas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) String diaDaSemana,
            @RequestParam(required = false) String turma) {

        if (data != null) {
            return nutricaoService.gerarAlertaPorData(data, turma)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        if (diaDaSemana != null && !diaDaSemana.isBlank()) {
            return nutricaoService.gerarAlertaPorDia(diaDaSemana, turma)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        return ResponseEntity.badRequest().build();
    }
}
