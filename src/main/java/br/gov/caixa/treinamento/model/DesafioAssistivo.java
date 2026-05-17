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
@Table(name = "desafios_assistivos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_desafio_codigo", columnNames = "codigo")
})
public class DesafioAssistivo extends PanacheEntity {

    @Column(nullable = false, unique = true, length = 80)
    public String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    public TrilhaAssistenteGuiado trilha;

    @Column(nullable = false, length = 180)
    public String titulo;

    @Column(length = 1000)
    public String descricao;

    @Column(nullable = false)
    public Integer quantidadeSituacoes = 0;

    @Column(nullable = false)
    public Integer pontuacaoMaxima = 100;

    @Column(nullable = false, length = 30)
    public String status = "LIBERADO";

    @Column(nullable = false)
    public Boolean permiteRefazer = false;

    public LocalDateTime dataCriacao = LocalDateTime.now();
}
