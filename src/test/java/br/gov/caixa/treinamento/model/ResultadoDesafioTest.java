package br.gov.caixa.treinamento.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ResultadoDesafio - Testes unitários")
class ResultadoDesafioTest {

    private ResultadoDesafio resultado;

    @BeforeEach
    void setup() {
        resultado = new ResultadoDesafio(true, 100, "Resposta correta!");
    }

    // ==================== TESTES DE CONSTRUÇÃO ====================

    @Test
    @DisplayName("Deve criar resultado com construtor padrão")
    void testCriarComConstrutorPadrao() {
        ResultadoDesafio novo = new ResultadoDesafio();
        assertThat(novo).isNotNull();
    }

    @Test
    @DisplayName("Deve criar resultado com construtor parametrizado")
    void testCriarComParametros() {
        assertThat(resultado)
                .extracting("acertou", "pontuacao", "mensagemFeedback")
                .containsExactly(true, 100, "Resposta correta!");
    }

    @Test
    @DisplayName("Deve criar resultado com acerto")
    void testCriarResultadoAcerto() {
        ResultadoDesafio acerto = new ResultadoDesafio(true, 100, "Parabéns!");

        assertThat(acerto.acertou).isTrue();
        assertThat(acerto.pontuacao).isEqualTo(100);
        assertThat(acerto.mensagemFeedback).contains("Parabéns");
    }

    @Test
    @DisplayName("Deve criar resultado com erro")
    void testCriarResultadoErro() {
        ResultadoDesafio erro = new ResultadoDesafio(false, 40, "Resposta incorreta");

        assertThat(erro.acertou).isFalse();
        assertThat(erro.pontuacao).isGreaterThan(0);
        assertThat(erro.mensagemFeedback).contains("incorreta");
    }

    // ==================== TESTES DE CAMPOS ====================

    @Test
    @DisplayName("Resultado deve ter campo acertou")
    void testResultadoTemAcertou() {
        assertThat(resultado.acertou).isTrue();
    }

    @Test
    @DisplayName("Resultado deve ter campo pontuação")
    void testResultadoTemPontuacao() {
        assertThat(resultado.pontuacao).isEqualTo(100);
    }

    @Test
    @DisplayName("Resultado deve ter campo mensagem de feedback")
    void testResultadoTemMensagem() {
        assertThat(resultado.mensagemFeedback).isEqualTo("Resposta correta!");
    }

    // ==================== TESTES DE PONTUAÇÃO ====================

    @Test
    @DisplayName("Pontuação de acerto deve ser 100")
    void testPontuacaoAcerto() {
        assertThat(resultado.pontuacao).isEqualTo(100);
    }

    @Test
    @DisplayName("Pontuação de erro deve ser menor que acerto")
    void testPontuacaoErroMenorQueAcerto() {
        ResultadoDesafio erro = new ResultadoDesafio(false, 40, "Errado");

        assertThat(erro.pontuacao).isLessThan(resultado.pontuacao);
    }

    @Test
    @DisplayName("Pontuação deve ser não-negativa")
    void testPontuacaoNaoNegativa() {
        assertThat(resultado.pontuacao).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Pode ter pontuação zero para resposta em branco")
    void testPontuacaoZero() {
        ResultadoDesafio vazio = new ResultadoDesafio(false, 0, "Nenhuma resposta");

        assertThat(vazio.pontuacao).isZero();
        assertThat(vazio.acertou).isFalse();
    }

    // ==================== TESTES DE FEEDBACK ====================

    @Test
    @DisplayName("Mensagem de feedback não deve ser nula")
    void testMensagemNaoNula() {
        assertThat(resultado.mensagemFeedback).isNotNull();
    }

    @Test
    @DisplayName("Mensagem de feedback deve ser não-vazia")
    void testMensagemNaoVazia() {
        assertThat(resultado.mensagemFeedback).isNotBlank();
    }

    @Test
    @DisplayName("Deve ter mensagens diferentes para acerto e erro")
    void testMensagensDistintas() {
        ResultadoDesafio erro = new ResultadoDesafio(false, 40, "Resposta incorreta");

        assertThat(resultado.mensagemFeedback).isNotEqualTo(erro.mensagemFeedback);
    }

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("Deve permitir campos nulos")
    void testCamposNulos() {
        ResultadoDesafio nulo = new ResultadoDesafio(false, 0, null);

        assertThat(nulo.mensagemFeedback).isNull();
    }

    @Test
    @DisplayName("Deve permitir modificação de campos")
    void testModificarCampos() {
        resultado.acertou = false;
        resultado.pontuacao = 50;
        resultado.mensagemFeedback = "Revisada";

        assertThat(resultado.acertou).isFalse();
        assertThat(resultado.pontuacao).isEqualTo(50);
        assertThat(resultado.mensagemFeedback).isEqualTo("Revisada");
    }

    @Test
    @DisplayName("Múltiplos resultados devem ser independentes")
    void testResultadosIndependentes() {
        ResultadoDesafio resultado1 = new ResultadoDesafio(true, 100, "Certo");
        ResultadoDesafio resultado2 = new ResultadoDesafio(false, 40, "Errado");

        resultado1.pontuacao = 50;

        assertThat(resultado2.pontuacao).isEqualTo(40);
    }
}

