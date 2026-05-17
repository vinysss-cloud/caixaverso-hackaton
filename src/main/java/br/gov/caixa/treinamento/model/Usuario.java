package br.gov.caixa.treinamento.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario extends PanacheEntity {

    @Column(nullable = false)
    public String nome;

    @Column(nullable = false, unique = true)
    public String matricula;

    @Column(nullable = false)
    public Integer idade;

    @ElementCollection(fetch = FetchType.EAGER)
    public List<String> preferenciasAcessibilidade = new ArrayList<>();

    @Column(nullable = false)
    public String senhaHash;

    @Column(length = 120)
    public String sessaoTokenHash;

    public LocalDateTime sessaoExpiraEm;

    // Gamification fields
    public Integer pontuacaoTotal = 0;

    public Integer nivel = 1;

    public Integer progressoPercentual = 0;

    public Integer trilhasConcluidas = 0;

    public Integer desafiosRespondidos = 0;
}

