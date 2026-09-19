package com.example.NutriKids.controller;

import com.example.NutriKids.entity.Cardapio;
import com.example.NutriKids.repository.CardapioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cardapios")
@RequiredArgsConstructor
public class CardapioController {

    private final CardapioRepository cardapioRepository;

    // Listar todos os cardápios cadastrados
    @GetMapping
    public List<Cardapio> listarTodos() {
        return cardapioRepository.findAll();
    }

    // Buscar cardápio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cardapio> buscarPorId(@PathVariable Long id) {
        return cardapioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar cardápio pelo dia da semana (ex: /api/cardapios/dia/segunda-feira)
    @GetMapping("/dia/{diaDaSemana}")
    public ResponseEntity<Cardapio> buscarPorDia(@PathVariable String diaDaSemana) {
        return cardapioRepository.findByDiaDaSemanaIgnoreCase(diaDaSemana)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Cadastrar novo cardápio
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cardapio salvar(@RequestBody Cardapio cardapio) {
        return cardapioRepository.save(cardapio);
    }

    // Atualizar cardápio existente
    @PutMapping("/{id}")
    public ResponseEntity<Cardapio> atualizar(@PathVariable Long id, @RequestBody Cardapio cardapioAtualizado) {
        return cardapioRepository.findById(id)
                .map(cardapioExistente -> {
                    cardapioExistente.setDiaDaSemana(cardapioAtualizado.getDiaDaSemana());
                    cardapioExistente.setPratoPrincipal(cardapioAtualizado.getPratoPrincipal());
                    cardapioExistente.setGuarnicao(cardapioAtualizado.getGuarnicao());
                    cardapioExistente.setSobremesa(cardapioAtualizado.getSobremesa());
                    return ResponseEntity.ok(cardapioRepository.save(cardapioExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Deletar cardápio por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (cardapioRepository.existsById(id)) {
            cardapioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
