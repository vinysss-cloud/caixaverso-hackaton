package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.model.Desafio;
import br.gov.caixa.treinamento.model.ResultadoDesafio;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;

@ApplicationScoped
public class DesafioService {

    public Desafio buscarDesafioAberturaConta() {
        return new Desafio(
                "quiz-abertura-conta",
                "Quiz: Abertura de Conta Bancária",
                "Após concluir o treinamento guiado, responda: qual etapa deve ocorrer antes da confirmação final da abertura da conta?",
                Arrays.asList(
                        "Revisar os dados da proposta",
                        "Ignorar alertas de documentação",
                        "Confirmar a conta sem validação",
                        "Pular a identificação do tipo de conta"
                ),
                "Revisar os dados da proposta"
        );
    }

    public ResultadoDesafio validarRespostaAberturaConta(String resposta) {
        if (resposta == null || resposta.trim().isEmpty()) {
            return new ResultadoDesafio(
                    false,
                    0,
                    "Nenhuma resposta foi selecionada."
            );
        }

        Desafio desafio = buscarDesafioAberturaConta();
        boolean acertou = desafio.respostaCorreta.equalsIgnoreCase(resposta.trim());

        if (acertou) {
            return new ResultadoDesafio(
                    true,
                    100,
                    "Correto! Antes de confirmar a abertura da conta, é necessário revisar os dados da proposta."
            );
        }

        return new ResultadoDesafio(
                false,
                40,
                "Resposta incorreta. Revise o treinamento guiado sobre a etapa de conferência da proposta."
        );
    }
}