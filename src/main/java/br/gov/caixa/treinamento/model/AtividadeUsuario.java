package br.gov.caixa.treinamento.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "atividades_usuario")
public class AtividadeUsuario extends PanacheEntity {

    @ManyToOne
    public Usuario usuario;

    public String tipoAtividade;

    public String descricao;

    public Integer pontosGanhos;

    public LocalDateTime dataHora;
}

