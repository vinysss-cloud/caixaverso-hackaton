package br.gov.caixa.treinamento.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "badges_usuario")
public class BadgeUsuario extends PanacheEntity {

    @ManyToOne
    public Usuario usuario;

    public String nome;

    public String descricao;

    public String icone;

    public LocalDateTime dataConquista;
}

