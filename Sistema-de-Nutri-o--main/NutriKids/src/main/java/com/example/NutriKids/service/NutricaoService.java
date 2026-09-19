package com.example.NutriKids.service;

import com.example.NutriKids.dto.AlunoEmRiscoDTO;
import com.example.NutriKids.dto.RelatorioAlertaCardapioDTO;
import com.example.NutriKids.entity.Aluno;
import com.example.NutriKids.entity.Cardapio;
import com.example.NutriKids.repository.AlunoRepository;
import com.example.NutriKids.repository.CardapioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NutricaoService {

    private final CardapioRepository cardapioRepository;
    private final AlunoRepository alunoRepository;

    /**
     * Gera relatório de alerta por dia da semana (ex: "Segunda-feira").
     */
    public Optional<RelatorioAlertaCardapioDTO> gerarAlertaPorDia(String diaDaSemana, String turma) {
        Optional<Cardapio> cardapioOpt = cardapioRepository.findByDiaDaSemanaIgnoreCase(diaDaSemana);
        return cardapioOpt.map(cardapio -> processarAlerta(cardapio, turma));
    }

    /**
     * Gera relatório de alerta estritamente pela data do calendário (ex: 2026-09-21).
     * Não busca outro dia se a data informada não possuir cardápio cadastrado.
     */
    public Optional<RelatorioAlertaCardapioDTO> gerarAlertaPorData(LocalDate data, String turma) {
        Optional<Cardapio> cardapioOpt = cardapioRepository.findByData(data);
        return cardapioOpt.map(cardapio -> processarAlerta(cardapio, turma));
    }

    private RelatorioAlertaCardapioDTO processarAlerta(Cardapio cardapio, String turma) {
        List<Aluno> alunosComRestricao = alunoRepository.findAlunosComRestricao();

        if (turma != null && !turma.isBlank()) {
            alunosComRestricao = alunosComRestricao.stream()
                    .filter(a -> a.getTurma() != null && a.getTurma().equalsIgnoreCase(turma.trim()))
                    .collect(Collectors.toList());
        }

        List<AlunoEmRiscoDTO> alunosEmAlerta = new ArrayList<>();

        String alergenos = cardapio.getAlergenos() != null ? cardapio.getAlergenos().toLowerCase() : "";
        String pratoPrincipal = cardapio.getPratoPrincipal() != null ? cardapio.getPratoPrincipal().toLowerCase() : "";
        String guarnicao = cardapio.getGuarnicao() != null ? cardapio.getGuarnicao().toLowerCase() : "";
        String sobremesa = cardapio.getSobremesa() != null ? cardapio.getSobremesa().toLowerCase() : "";

        // Se o cardápio declara explicitamente "nenhum" e não há alérgenos, não gera falso alerta
        boolean cardapioSemAlergenos = alergenos.contains("nenhum") || alergenos.isBlank();

        for (Aluno aluno : alunosComRestricao) {
            String restricao = aluno.getRestricao() != null ? aluno.getRestricao().toLowerCase() : "";
            if (restricao.isBlank()) continue;

            // Se o cardápio não tem alérgenos, esse aluno não está em risco hoje
            if (cardapioSemAlergenos) {
                continue;
            }

            // Mapeamento de grupos de alergia e verificação de correspondência
            ConflitoAlimentar conflito = detectarConflito(restricao, alergenos, pratoPrincipal, guarnicao, sobremesa, cardapio);

            if (conflito.possuiConflito) {
                alunosEmAlerta.add(new AlunoEmRiscoDTO(
                        aluno.getId(),
                        aluno.getNome(),
                        aluno.getTurma(),
                        aluno.getRestricao(),
                        conflito.motivoAlerta,
                        conflito.itemConflitante,
                        conflito.substituicaoSugerida
                ));
            }
        }

        return new RelatorioAlertaCardapioDTO(
                cardapio.getData(),
                cardapio.getDiaDaSemana(),
                cardapio.getPratoPrincipal(),
                cardapio.getGuarnicao(),
                cardapio.getSobremesa(),
                cardapio.getAlergenos(),
                cardapio.getPratoAlternativo(),
                alunosEmAlerta.size(),
                alunosEmAlerta
        );
    }

    private static class ConflitoAlimentar {
        boolean possuiConflito;
        String motivoAlerta;
        String itemConflitante;
        String substituicaoSugerida;

        ConflitoAlimentar(boolean possuiConflito, String motivoAlerta, String itemConflitante, String substituicaoSugerida) {
            this.possuiConflito = possuiConflito;
            this.motivoAlerta = motivoAlerta;
            this.itemConflitante = itemConflitante;
            this.substituicaoSugerida = substituicaoSugerida;
        }
    }

    private ConflitoAlimentar detectarConflito(String restricao, String alergenos, String pratoPrincipal, 
                                              String guarnicao, String sobremesa, Cardapio cardapio) {
        
        // Se o cardápio tiver um prato alternativo já definido pela nutricionista, usamos de base
        String sugestaoPadrao = (cardapio.getPratoAlternativo() != null && !cardapio.getPratoAlternativo().isBlank())
                ? cardapio.getPratoAlternativo()
                : null;

        // 1. LACTOSE / LEITE / DERIVADOS
        if (restricao.contains("lactose") || restricao.contains("leite") || restricao.contains("queijo")) {
            if (alergenos.contains("lactose") || alergenos.contains("leite") || pratoPrincipal.contains("strogonoff") || pratoPrincipal.contains("lasanha") || sobremesa.contains("bolo") || sobremesa.contains("chocolate") || guarnicao.contains("queijo") || guarnicao.contains("purê")) {
                String item = identificarItem(pratoPrincipal, guarnicao, sobremesa, "strogonoff", "queijo", "bolo", "chocolate", "purê", "leite", "lactose");
                String troca = sugestaoPadrao != null ? sugestaoPadrao : sugerirTrocaLactose(item, sobremesa, cardapio);
                return new ConflitoAlimentar(true, "ALERTA DE LACTOSE: Cardápio contém leite ou derivados.", item, troca);
            }
        }

        // 2. GLÚTEN / CELÍACO / TRIGO
        if (restricao.contains("glúten") || restricao.contains("gluten") || restricao.contains("celíac") || restricao.contains("celiaco") || restricao.contains("trigo")) {
            if (alergenos.contains("glúten") || alergenos.contains("gluten") || alergenos.contains("trigo") || pratoPrincipal.contains("macarr") || pratoPrincipal.contains("lasanha") || pratoPrincipal.contains("empanado") || sobremesa.contains("bolo") || sobremesa.contains("torta") || guarnicao.contains("farofa") || guarnicao.contains("macarr")) {
                String item = identificarItem(pratoPrincipal, guarnicao, sobremesa, "macarr", "lasanha", "empanado", "bolo", "torta", "farofa", "trigo");
                String troca = sugestaoPadrao != null ? sugestaoPadrao : sugerirTrocaGluten(item, sobremesa, cardapio);
                return new ConflitoAlimentar(true, "ALERTA DE GLÚTEN: Cardápio contém farinha de trigo, massas ou empanados.", item, troca);
            }
        }

        // 3. AMENDOIM / NOZES / CASTANHAS
        if (restricao.contains("amendoim") || restricao.contains("castanha") || restricao.contains("nozes")) {
            if (alergenos.contains("amendoim") || alergenos.contains("castanha") || alergenos.contains("nozes") || sobremesa.contains("paçoca") || sobremesa.contains("amendoim") || sobremesa.contains("bolo")) {
                String item = identificarItem(pratoPrincipal, guarnicao, sobremesa, "amendoim", "castanha", "nozes", "paçoca", "bolo");
                String troca = sugestaoPadrao != null ? sugestaoPadrao : "Substituir por Fruta fresca (Maçã ou Banana) higienizada em bancada sem oleaginosas";
                return new ConflitoAlimentar(true, "ALERTA DE AMENDOIM/NOZES: Risco de anafilaxia com oleaginosas.", item, troca);
            }
        }

        // 4. PEIXE / FRUTOS DO MAR
        if (restricao.contains("peixe") || restricao.contains("frutos do mar") || restricao.contains("camarão") || restricao.contains("camarao")) {
            if (alergenos.contains("peixe") || alergenos.contains("frutos do mar") || pratoPrincipal.contains("peixe") || pratoPrincipal.contains("atum") || pratoPrincipal.contains("camar")) {
                String item = "Prato Principal: " + cardapio.getPratoPrincipal();
                String troca = sugestaoPadrao != null ? sugestaoPadrao : "Substituir filé de peixe por Filé de Frango Grelhado ou Omelete";
                return new ConflitoAlimentar(true, "ALERTA DE PEIXE: Cardápio contém peixes ou derivados marinhos.", item, troca);
            }
        }

        // 5. OVOS
        if (restricao.contains("ovo")) {
            if (alergenos.contains("ovo") || pratoPrincipal.contains("empanado") || sobremesa.contains("bolo") || sobremesa.contains("pudim") || guarnicao.contains("maionese")) {
                String item = identificarItem(pratoPrincipal, guarnicao, sobremesa, "empanado", "bolo", "pudim", "maionese", "ovo");
                String troca = sugestaoPadrao != null ? sugestaoPadrao : "Substituir por carne/frango grelhado e Fruta fresca sem ovos";
                return new ConflitoAlimentar(true, "ALERTA DE OVO: Cardápio contém ovos ou preparação empanada.", item, troca);
            }
        }

        // 6. VERIFICAÇÃO GENÉRICA DE PALAVRAS DA RESTRIÇÃO
        String[] palavras = restricao.split("[,;\\s/]+");
        for (String palavra : palavras) {
            String p = palavra.trim();
            if (p.length() > 3 && (alergenos.contains(p) || pratoPrincipal.contains(p) || guarnicao.contains(p) || sobremesa.contains(p))) {
                return new ConflitoAlimentar(true, 
                        "ALERTA ESPECÍFICO: Cardápio contém termo relacionado à restrição do aluno (" + p + ")",
                        "Item com: " + p,
                        sugestaoPadrao != null ? sugestaoPadrao : "Servir opção neutra: Frango grelhado, Arroz, Feijão e Fruta");
            }
        }

        return new ConflitoAlimentar(false, null, null, null);
    }

    private String identificarItem(String prato, String guarnicao, String sobremesa, String... termos) {
        for (String t : termos) {
            if (sobremesa.contains(t)) return "Sobremesa: " + sobremesa;
            if (prato.contains(t)) return "Prato Principal: " + prato;
            if (guarnicao.contains(t)) return "Guarnição: " + guarnicao;
        }
        return "Prato Principal ou Sobremesa";
    }

    private String sugerirTrocaLactose(String item, String sobremesa, Cardapio cardapio) {
        if (item.toLowerCase().contains("sobremesa") || item.toLowerCase().contains("bolo") || item.toLowerCase().contains("chocolate")) {
            return "Trocar " + cardapio.getSobremesa() + " por Fruta fresca da época (Maçã ou Banana) sem lactose";
        }
        return "Substituir molho/laticínio por versão sem lactose ou servir Frango Grelhado simples com azeite";
    }

    private String sugerirTrocaGluten(String item, String sobremesa, Cardapio cardapio) {
        if (item.toLowerCase().contains("sobremesa") || item.toLowerCase().contains("bolo")) {
            return "Trocar " + cardapio.getSobremesa() + " por Fruta fresca (Maçã, Laranja ou Banana)";
        }
        if (item.toLowerCase().contains("macarr") || item.toLowerCase().contains("lasanha")) {
            return "Substituir massa tradicional por Arroz e Feijão ou Macarrão sem glúten (de arroz/milho)";
        }
        return "Substituir preparação com farinha por Carne/Frango grelhado simples sem empanamento";
    }
}
