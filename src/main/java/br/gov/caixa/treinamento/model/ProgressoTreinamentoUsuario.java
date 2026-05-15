package br.gov.caixa.treinamento.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "progresso_treinamento_usuario")
public class ProgressoTreinamentoUsuario extends PanacheEntity {

    @ManyToOne
    public Usuario usuario;

    public String codigoTreinamento;

    public String tituloTreinamento;

    public Integer etapaAtual = 0;

    public Integer totalEtapas = 0;

    public Integer progressoPercentual = 0;

    public Boolean concluido = false;

    public Boolean desafioDesbloqueado = false;

    public Boolean desafioRespondido = false;

    public LocalDateTime dataInicio;

    public LocalDateTime dataConclusao;
}