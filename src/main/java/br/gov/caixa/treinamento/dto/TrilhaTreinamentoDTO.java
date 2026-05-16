package br.gov.caixa.treinamento.dto;

import java.util.List;

public class TrilhaTreinamentoDTO {

    public String codigo;
    public String titulo;
    public String descricao;
    public String publicoAlvo;
    public Integer xpConclusao;
    public Integer duracao;
    public List<EtapaTreinamentoDTO> etapas;

    public TrilhaTreinamentoDTO(String codigo, String titulo, String descricao, String publicoAlvo, Integer xpConclusao, List<EtapaTreinamentoDTO> etapas) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.publicoAlvo = publicoAlvo;
        this.xpConclusao = xpConclusao;
        this.etapas = etapas;
        this.duracao = calcularDuracaoPadrao(etapas);
    }

    public TrilhaTreinamentoDTO(String codigo, String titulo, String descricao, String publicoAlvo, Integer xpConclusao, Integer duracao, List<EtapaTreinamentoDTO> etapas) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.publicoAlvo = publicoAlvo;
        this.xpConclusao = xpConclusao;
        this.duracao = duracao;
        this.etapas = etapas;
    }

    private Integer calcularDuracaoPadrao(List<EtapaTreinamentoDTO> etapas) {
        if (etapas == null || etapas.isEmpty()) {
            return 0;
        }
        return etapas.size() * 5;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getPublicoAlvo() {
        return publicoAlvo;
    }

    public Integer getXpConclusao() {
        return xpConclusao;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public List<EtapaTreinamentoDTO> getEtapas() {
        return etapas;
    }
}
