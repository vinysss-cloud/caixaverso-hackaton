package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.model.ResultadoDesafio;
import br.gov.caixa.treinamento.security.UsuarioLogadoService;
import br.gov.caixa.treinamento.service.DesafioService;
import br.gov.caixa.treinamento.service.GamificacaoService;
import br.gov.caixa.treinamento.service.TreinamentoService;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/desafio")
public class DesafioResource {

    @Inject
    Template desafio;

    @Inject
    DesafioService desafioService;

    @Inject
    TreinamentoService treinamentoService;

    @Inject
    GamificacaoService gamificacaoService;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object desafio(@CookieParam("CAIXAVERSO_SESSION") String tokenSessao) {
        String usuarioLogado = usuarioLogadoService.matriculaOuNulo(tokenSessao);

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        boolean desbloqueado = treinamentoService.desafioEstaDesbloqueado(
                usuarioLogado,
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        return desafio
                .data("desafio", desafioService.buscarDesafioAberturaConta())
                .data("resultado", null)
                .data("desbloqueado", desbloqueado)
                .data("treinamentoTitulo", TreinamentoService.TITULO_TRILHA_CONTA_FACIL);
    }

    @POST
    @Path("/responder")
    @Produces(MediaType.TEXT_HTML)
    public Object responder(@CookieParam("CAIXAVERSO_SESSION") String tokenSessao,
                            @FormParam("resposta") String resposta) {
        String usuarioLogado = usuarioLogadoService.matriculaOuNulo(tokenSessao);

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        boolean desbloqueado = treinamentoService.desafioEstaDesbloqueado(
                usuarioLogado,
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        if (!desbloqueado) {
            return desafio
                    .data("desafio", desafioService.buscarDesafioAberturaConta())
                    .data("resultado", null)
                    .data("desbloqueado", false)
                    .data("treinamentoTitulo", TreinamentoService.TITULO_TRILHA_CONTA_FACIL);
        }

        ResultadoDesafio resultado = desafioService.validarRespostaAberturaConta(resposta);

        gamificacaoService.registrarDesafioRespondido(
                usuarioLogado,
                resultado.acertou,
                resultado.pontuacao
        );

        return desafio
                .data("desafio", desafioService.buscarDesafioAberturaConta())
                .data("resultado", resultado)
                .data("desbloqueado", true)
                .data("treinamentoTitulo", TreinamentoService.TITULO_TRILHA_CONTA_FACIL);
    }
}