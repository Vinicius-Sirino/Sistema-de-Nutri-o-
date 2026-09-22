package com.example.NutriKids.controller;

import com.example.NutriKids.dto.RelatorioAlertaCardapioDTO;
import com.example.NutriKids.entity.Cardapio;
import com.example.NutriKids.service.CardapioService;
import com.example.NutriKids.service.NutricaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cardapios")
@RequiredArgsConstructor
public class CardapioController {

    private final CardapioService cardapioService;
    private final NutricaoService nutricaoService;

    // Listar todos os cardápios cadastrados
    @GetMapping
    public List<Cardapio> listarTodos() {
        return cardapioService.listarTodos();
    }

    // Buscar cardápio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cardapio> buscarPorId(@PathVariable Long id) {
        return cardapioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar cardápio pelo dia da semana (ex: /api/cardapios/dia/segunda-feira)
    @GetMapping("/dia/{diaDaSemana}")
    public ResponseEntity<Cardapio> buscarPorDia(@PathVariable String diaDaSemana) {
        return cardapioService.buscarPorDia(diaDaSemana)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar cardápio por data específica do calendário (ex: /api/cardapios/data/2026-09-21)
    @GetMapping("/data/{data}")
    public ResponseEntity<Cardapio> buscarPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return cardapioService.buscarPorData(data)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar cardápios de um período/semana (ex: /api/cardapios/periodo?inicio=2026-09-21&fim=2026-09-25)
    @GetMapping("/periodo")
    public List<Cardapio> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return cardapioService.buscarPorPeriodo(inicio, fim);
    }

    // Relatório de alertas nutricionais para o cardápio do dia
    @GetMapping("/dia/{diaDaSemana}/alertas")
    public ResponseEntity<RelatorioAlertaCardapioDTO> buscarAlertasDoDia(
            @PathVariable String diaDaSemana,
            @RequestParam(required = false) String turma) {
        return nutricaoService.gerarAlertaPorDia(diaDaSemana, turma)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Cadastrar novo cardápio (com data e/ou dia da semana)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cardapio salvar(@RequestBody Cardapio cardapio) {
        return cardapioService.salvar(cardapio);
    }

    // Atualizar cardápio existente
    @PutMapping("/{id}")
    public ResponseEntity<Cardapio> atualizar(@PathVariable Long id, @RequestBody Cardapio cardapioAtualizado) {
        return cardapioService.atualizar(id, cardapioAtualizado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Deletar cardápio por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (cardapioService.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
