package br.gov.caixa.treinamento.model;

/**
 * Resultado simples de um desafio.
 */
public class ResultadoDesafio {
    public boolean acertou;
    public int pontuacao;
    public String mensagemFeedback;

    public ResultadoDesafio() {}

    public ResultadoDesafio(boolean acertou, int pontuacao, String mensagemFeedback) {
        this.acertou = acertou;
        this.pontuacao = pontuacao;
        this.mensagemFeedback = mensagemFeedback;
    }
}

