package br.gov.caixa.treinamento.dto;

public class EtapaTreinamentoDTO {

    public int ordem;
    public String titulo;
    public String instrucao;
    public String dica;
    public String campoSimulado;
    public String acaoEsperada;

    public EtapaTreinamentoDTO(int ordem, String titulo, String instrucao, String dica, String campoSimulado, String acaoEsperada) {
        this.ordem = ordem;
        this.titulo = titulo;
        this.instrucao = instrucao;
        this.dica = dica;
        this.campoSimulado = campoSimulado;
        this.acaoEsperada = acaoEsperada;
    }
}