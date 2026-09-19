package com.example.NutriKids.repository;

import com.example.NutriKids.entity.Cardapio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardapioRepository extends JpaRepository<Cardapio, Long> {

    // Buscar cardápio específico pelo dia da semana (ex: "Segunda-feira")
    Optional<Cardapio> findByDiaDaSemanaIgnoreCase(String diaDaSemana);
}
