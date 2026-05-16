package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.security.UsuarioLogadoService;
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

@Path("/")
public class HomeResource {

    @Inject
    Template home;

    @Inject
    br.gov.caixa.treinamento.service.GamificacaoService gamificacaoService;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @GET
    public Response index() {
        return Response.seeOther(URI.create("/login")).build();
    }

    @GET
    @Path("/home")
    @Produces(MediaType.TEXT_HTML)
    public Object home(@CookieParam("CAIXAVERSO_SESSION") String tokenSessao) {
        String usuarioLogado = usuarioLogadoService.matriculaOuNulo(tokenSessao);

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        var resumo = gamificacaoService.buscarResumoUsuario(usuarioLogado);

        return home.data("titulo", "CaixaVerso Treinamento")
                   .data("usuarioLogado", usuarioLogado)
                   .data("resumo", resumo);
    }
}

