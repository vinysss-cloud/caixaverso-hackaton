package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.service.AtualizacaoService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/atualizacao")
public class AtualizacaoResource {

    @Inject
    Template atualizacao;

    @Inject
    AtualizacaoService atualizacaoService;

    @Inject
    br.gov.caixa.treinamento.service.GamificacaoService gamificacaoService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object atualizacao(@CookieParam("usuarioLogado") String usuarioLogado) {
        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        try {
            gamificacaoService.registrarVisualizacaoAtualizacao(usuarioLogado);
        } catch (Exception e) {
            // ignore
        }

        return atualizacao.data("modulo", atualizacaoService.buscarModuloAtualizacao());
    }
}

