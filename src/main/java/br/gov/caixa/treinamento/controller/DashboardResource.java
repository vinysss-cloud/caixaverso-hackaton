package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.security.UsuarioLogadoService;
import br.gov.caixa.treinamento.service.DashboardService;
import br.gov.caixa.treinamento.service.GamificacaoService;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.Map;

@Path("/dashboard")
public class DashboardResource {

    @Inject
    Template dashboard;

    @Inject
    DashboardService dashboardService;

    @Inject
    GamificacaoService gamificacaoService;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object dashboard(@CookieParam("CAIXAVERSO_SESSION") String tokenSessao) {
        String usuarioLogado = usuarioLogadoService.matriculaOuNulo(tokenSessao);

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        Map<String, Object> resumo = gamificacaoService.buscarResumoUsuario(usuarioLogado);
        Map<String, Object> dados = dashboardService.buscarDados(usuarioLogado, resumo);

        return dashboard
                .data("usuarioLogado", usuarioLogado)
                .data("dados", dados)
                .data("resumo", resumo);
    }
}
