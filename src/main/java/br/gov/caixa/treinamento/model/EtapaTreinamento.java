package br.gov.caixa.treinamento.model;

/**
 * Representa uma etapa no treinamento guiado.
 */
public class EtapaTreinamento {
    public String id;
    public String titulo;
    public String instrucao;
    public String dicaAcessibilidade;
    public int ordem;

    public EtapaTreinamento() {}

    public EtapaTreinamento(String id, String titulo, String instrucao, String dicaAcessibilidade, int ordem) {
        this.id = id;
        this.titulo = titulo;
        this.instrucao = instrucao;
        this.dicaAcessibilidade = dicaAcessibilidade;
        this.ordem = ordem;
    }
}

