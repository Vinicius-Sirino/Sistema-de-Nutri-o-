package com.example.NutriKids.service;

import com.example.NutriKids.entity.Cardapio;
import com.example.NutriKids.repository.CardapioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardapioService {

    private final CardapioRepository cardapioRepository;

    public List<Cardapio> listarTodos() {
        return cardapioRepository.findAll();
    }

    public Optional<Cardapio> buscarPorId(Long id) {
        return cardapioRepository.findById(id);
    }

    public Optional<Cardapio> buscarPorDia(String diaDaSemana) {
        return cardapioRepository.findByDiaDaSemanaIgnoreCase(diaDaSemana);
    }

    public Optional<Cardapio> buscarPorData(LocalDate data) {
        return cardapioRepository.findByData(data);
    }

    public List<Cardapio> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return cardapioRepository.findByDataBetweenOrderByDataAsc(inicio, fim);
    }

    public Cardapio salvar(Cardapio cardapio) {
        preencherDiaDaSemanaSeNecessario(cardapio);
        return cardapioRepository.save(cardapio);
    }

    public Optional<Cardapio> atualizar(Long id, Cardapio cardapioAtualizado) {
        return cardapioRepository.findById(id).map(cardapioExistente -> {
            cardapioExistente.setData(cardapioAtualizado.getData());
            cardapioExistente.setDiaDaSemana(cardapioAtualizado.getDiaDaSemana());
            cardapioExistente.setPratoPrincipal(cardapioAtualizado.getPratoPrincipal());
            cardapioExistente.setGuarnicao(cardapioAtualizado.getGuarnicao());
            cardapioExistente.setSobremesa(cardapioAtualizado.getSobremesa());
            cardapioExistente.setAlergenos(cardapioAtualizado.getAlergenos());
            cardapioExistente.setPratoAlternativo(cardapioAtualizado.getPratoAlternativo());
            preencherDiaDaSemanaSeNecessario(cardapioExistente);
            return cardapioRepository.save(cardapioExistente);
        });
    }

    public boolean deletar(Long id) {
        if (cardapioRepository.existsById(id)) {
            cardapioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void preencherDiaDaSemanaSeNecessario(Cardapio cardapio) {
        if (cardapio.getData() != null && (cardapio.getDiaDaSemana() == null || cardapio.getDiaDaSemana().isBlank())) {
            cardapio.setDiaDaSemana(obterNomeDiaSemana(cardapio.getData()));
        }
    }

    public String obterNomeDiaSemana(LocalDate data) {
        switch (data.getDayOfWeek()) {
            case MONDAY: return "Segunda-feira";
            case TUESDAY: return "Terça-feira";
            case WEDNESDAY: return "Quarta-feira";
            case THURSDAY: return "Quinta-feira";
            case FRIDAY: return "Sexta-feira";
            case SATURDAY: return "Sábado";
            case SUNDAY: return "Domingo";
            default: return "";
        }
    }
}
