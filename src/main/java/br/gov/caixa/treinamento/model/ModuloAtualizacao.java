package br.gov.caixa.treinamento.model;

import java.util.List;

/**
 * Representa um módulo de atualização / nova funcionalidade.
 */
public class ModuloAtualizacao {
    public String id;
    public String nome;
    public String resumo;
    public List<String> passos;

    public ModuloAtualizacao() {}

    public ModuloAtualizacao(String id, String nome, String resumo, List<String> passos) {
        this.id = id;
        this.nome = nome;
        this.resumo = resumo;
        this.passos = passos;
    }
}

