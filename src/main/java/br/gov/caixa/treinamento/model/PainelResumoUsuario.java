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
@Table(name = "painel_resumo_usuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_painel_resumo_usuario", columnNames = "usuario_id")
})
public class PainelResumoUsuario extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    public Usuario usuario;

    @Column(nullable = false)
    public Integer pontuacaoTotal = 0;

    @Column(nullable = false)
    public Integer nivel = 1;

    @Column(nullable = false)
    public Integer trilhasConcluidas = 0;

    @Column(nullable = false)
    public Integer desafiosConcluidos = 0;

    @Column(nullable = false)
    public Integer badgesConquistadas = 0;

    @Column(nullable = false)
    public Integer posicaoRanking = 0;

    public LocalDateTime dataAtualizacao = LocalDateTime.now();
}
