package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.service.DashboardService;
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

@Path("/dashboard")
public class DashboardResource {

    @Inject
    Template dashboard;

    @Inject
    DashboardService dashboardService;

    @Inject
    br.gov.caixa.treinamento.service.GamificacaoService gamificacaoService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object dashboard(@CookieParam("usuarioLogado") String usuarioLogado) {
        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        var dados = dashboardService.buscarDados();
        var resumo = gamificacaoService.buscarResumoUsuario(usuarioLogado);

        return dashboard.data("dados", dados)
                        .data("resumo", resumo);
    }
}

