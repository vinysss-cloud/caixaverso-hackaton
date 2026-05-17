package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.model.Desafio;
import br.gov.caixa.treinamento.model.ResultadoDesafio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DesafioService - Testes unitários")
class DesafioServiceTest {

    private DesafioService desafioService;

    @BeforeEach
    void setup() {
        desafioService = new DesafioService();
    }

    // ==================== TESTES DE BUSCAR DESAFIO ====================

    @Test
    @DisplayName("Deve retornar desafio de abertura de conta")
    void testBuscarDesafioAberturaConta() {
        Desafio desafio = desafioService.buscarDesafioAberturaConta();

        assertThat(desafio)
                .isNotNull()
                .extracting("id", "titulo")
                .containsExactly("quiz-abertura-conta", "Validação: Conta Fácil - Jornada Assistiva PcD");
    }

    @Test
    @DisplayName("Deve ter resposta correta no desafio")
    void testDesafioTemRespostaCorreta() {
        Desafio desafio = desafioService.buscarDesafioAberturaConta();

        assertThat(desafio.respostaCorreta)
                .isNotBlank()
                .isEqualTo("Revisar os dados da proposta");
    }

    @Test
    @DisplayName("Deve ter 4 alternativas no desafio")
    void testDesafioTem4Alternativas() {
        Desafio desafio = desafioService.buscarDesafioAberturaConta();

        assertThat(desafio.alternativas)
                .hasSize(4)
                .contains(
                        "Revisar os dados da proposta",
                        "Ignorar alertas de documentação",
                        "Confirmar a conta sem validação",
                        "Pular a identificação do tipo de conta"
                );
    }

    @Test
    @DisplayName("Deve conter descrição do desafio")
    void testDesafioTemDescricao() {
        Desafio desafio = desafioService.buscarDesafioAberturaConta();

        assertThat(desafio.descricao)
                .isNotBlank()
                .containsIgnoringCase("fluxo piloto de abertura de conta");
    }

    // ==================== TESTES DE VALIDAÇÃO - RESPOSTA CORRETA ====================

    @Test
    @DisplayName("Deve retornar sucesso quando resposta está correta")
    void testValidarRespostaCorreta() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Revisar os dados da proposta");

        assertThat(resultado)
                .extracting("acertou", "pontuacao")
                .containsExactly(true, 100);
    }

    @Test
    @DisplayName("Deve retornar mensagem de acerto")
    void testMensagemAcerto() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Revisar os dados da proposta");

        assertThat(resultado.mensagemFeedback)
                .isNotBlank()
                .contains("Correto");
    }

    @Test
    @DisplayName("Deve validar resposta correta com espaços em branco")
    void testRespostaCorretaComEspacos() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("  Revisar os dados da proposta  ");

        assertThat(resultado.acertou).isTrue();
    }

    @Test
    @DisplayName("Deve validar resposta correta independente de maiúsculas")
    void testRespostaCorretaMaiuscula() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("REVISAR OS DADOS DA PROPOSTA");

        assertThat(resultado.acertou).isTrue();
    }

    @Test
    @DisplayName("Deve validar resposta correta com case misto")
    void testRespostaCorretaCaseMisto() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("rEvIsAr Os DaDoS dA pRoPoStA");

        assertThat(resultado.acertou).isTrue();
    }

    // ==================== TESTES DE VALIDAÇÃO - RESPOSTA INCORRETA ====================

    @Test
    @DisplayName("Deve retornar erro quando resposta está incorreta")
    void testValidarRespostaIncorreta() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Ignorar alertas de documentação");

        assertThat(resultado)
                .extracting("acertou", "pontuacao")
                .containsExactly(false, 40);
    }

    @Test
    @DisplayName("Deve retornar mensagem de erro")
    void testMensagemErro() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Ignorar alertas de documentação");

        assertThat(resultado.mensagemFeedback)
                .isNotBlank()
                .contains("incorreta");
    }

    @Test
    @DisplayName("Deve dar pontos parciais para resposta incorreta")
    void testPontosParciais() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Confirmar a conta sem validação");

        assertThat(resultado.pontuacao).isEqualTo(40);
    }

    @Test
    @DisplayName("Deve retornar indicação de revisão para resposta incorreta")
    void testIndicacaoRevisao() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Pular a identificação do tipo de conta");

        assertThat(resultado.mensagemFeedback).contains("Revise");
    }

    // ==================== TESTES DE VALIDAÇÃO - RESPOSTA NULA/VAZIA ====================

    @Test
    @DisplayName("Deve retornar erro quando resposta é nula")
    void testValidarRespostaNula() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta(null);

        assertThat(resultado)
                .extracting("acertou", "pontuacao")
                .containsExactly(false, 0);
    }

    @Test
    @DisplayName("Deve retornar mensagem adequada para resposta nula")
    void testMensagemRespostaNula() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta(null);

        assertThat(resultado.mensagemFeedback)
                .isNotBlank()
                .contains("Nenhuma resposta");
    }

    @Test
    @DisplayName("Deve retornar erro quando resposta está vazia")
    void testValidarRespostaVazia() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("");

        assertThat(resultado)
                .extracting("acertou", "pontuacao")
                .containsExactly(false, 0);
    }

    @Test
    @DisplayName("Deve retornar erro quando resposta é só espaços")
    void testValidarRespostaSoEspacos() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("   ");

        assertThat(resultado.acertou).isFalse();
    }

    // ==================== TESTES DE ALTERNATIVAS ====================

    @Test
    @DisplayName("Deve validar alternativa 1")
    void testValidarAlternativa1() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Revisar os dados da proposta");
        assertThat(resultado.acertou).isTrue();
    }

    @Test
    @DisplayName("Deve validar alternativa 2")
    void testValidarAlternativa2() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Ignorar alertas de documentação");
        assertThat(resultado.acertou).isFalse();
    }

    @Test
    @DisplayName("Deve validar alternativa 3")
    void testValidarAlternativa3() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Confirmar a conta sem validação");
        assertThat(resultado.acertou).isFalse();
    }

    @Test
    @DisplayName("Deve validar alternativa 4")
    void testValidarAlternativa4() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Pular a identificação do tipo de conta");
        assertThat(resultado.acertou).isFalse();
    }

    // ==================== TESTES DE RESULTADO ====================

    @Test
    @DisplayName("Resultado com acerto não deve ser negativo")
    void testResultadoAcertoNaoNegativo() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Revisar os dados da proposta");

        assertThat(resultado.pontuacao).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Resultado com erro não deve ser negativo")
    void testResultadoErroNaoNegativo() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("resposta errada");

        assertThat(resultado.pontuacao).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Pontos de acerto deve ser maior que pontos de erro")
    void testPontosAcertoMaiorErro() {
        ResultadoDesafio acerto = desafioService.validarRespostaAberturaConta("Revisar os dados da proposta");
        ResultadoDesafio erro = desafioService.validarRespostaAberturaConta("Ignorar alertas de documentação");

        assertThat(acerto.pontuacao).isGreaterThan(erro.pontuacao);
    }

    @Test
    @DisplayName("Deve retornar objeto ResultadoDesafio válido")
    void testResultadoValido() {
        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta("Revisar os dados da proposta");

        assertThat(resultado)
                .isNotNull()
                .extracting("mensagemFeedback").isNotNull();
    }
}

