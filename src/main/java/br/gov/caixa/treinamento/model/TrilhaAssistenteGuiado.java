package br.gov.caixa.treinamento.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "trilhas_assistente_guiado", uniqueConstraints = {
        @UniqueConstraint(name = "uk_trilha_codigo", columnNames = "codigo")
})
public class TrilhaAssistenteGuiado extends PanacheEntity {

    @Column(nullable = false, unique = true, length = 80)
    public String codigo;

    @Column(nullable = false, length = 160)
    public String titulo;

    @Column(length = 1000)
    public String descricao;

    @Column(length = 500)
    public String publicoAlvo;

    @Column(nullable = false, length = 30)
    public String status = "DISPONIVEL";

    @Column(nullable = false)
    public Integer ordem = 0;

    @Column(nullable = false)
    public Boolean ativa = true;

    public LocalDateTime dataCriacao = LocalDateTime.now();
}
