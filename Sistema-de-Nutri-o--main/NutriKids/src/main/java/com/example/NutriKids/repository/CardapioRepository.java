package com.example.NutriKids.repository;

import com.example.NutriKids.entity.Cardapio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardapioRepository extends JpaRepository<Cardapio, Long> {

    // Buscar cardápio por data específica (ex: 2026-09-21)
    Optional<Cardapio> findByData(LocalDate data);

    // Buscar cardápios de um período/semana (ex: de 2026-09-21 até 2026-09-25)
    List<Cardapio> findByDataBetweenOrderByDataAsc(LocalDate inicio, LocalDate fim);

    // Buscar cardápio específico pelo dia da semana (ex: "Segunda-feira")
    Optional<Cardapio> findByDiaDaSemanaIgnoreCase(String diaDaSemana);
}
