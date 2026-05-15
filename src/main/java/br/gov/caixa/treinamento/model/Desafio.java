package br.gov.caixa.treinamento.model;

import java.util.List;

/**
 * Representa um desafio com alternativas.
 */
public class Desafio {
    public String id;
    public String titulo;
    public String descricao;
    public List<String> alternativas;
    public String respostaCorreta;

    public Desafio() {}

    public Desafio(String id, String titulo, String descricao, List<String> alternativas, String respostaCorreta) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.alternativas = alternativas;
        this.respostaCorreta = respostaCorreta;
    }
}

