package br.gov.caixa.treinamento.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "resultados_desafio_usuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_resultado_usuario_desafio", columnNames = {"usuario_id", "desafio_id"})
})
public class ResultadoDesafioUsuario extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    public Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    public DesafioAssistivo desafio;

    @Column(nullable = false)
    public Integer totalSituacoes = 0;

    @Column(nullable = false)
    public Integer acertos = 0;

    @Column(nullable = false)
    public Integer percentual = 0;

    @Column(nullable = false)
    public Integer pontuacao = 0;

    @Column(nullable = false)
    public Boolean aprovado = false;

    @Column(nullable = false, length = 30)
    public String status = "CONCLUIDO";

    @Column(length = 500)
    public String respostaResumo;

    public LocalDateTime dataInicio;

    public LocalDateTime dataConclusao = LocalDateTime.now();
}
