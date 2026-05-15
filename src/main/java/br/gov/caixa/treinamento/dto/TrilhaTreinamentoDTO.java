package br.gov.caixa.treinamento.dto;

import java.util.List;

public class TrilhaTreinamentoDTO {

    public String codigo;
    public String titulo;
    public String descricao;
    public String publicoAlvo;
    public Integer xpConclusao;
    public List<EtapaTreinamentoDTO> etapas;

    public TrilhaTreinamentoDTO(String codigo, String titulo, String descricao, String publicoAlvo, Integer xpConclusao, List<EtapaTreinamentoDTO> etapas) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.publicoAlvo = publicoAlvo;
        this.xpConclusao = xpConclusao;
        this.etapas = etapas;
    }
}