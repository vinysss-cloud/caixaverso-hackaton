package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.model.ProgressoTreinamentoUsuario;
import br.gov.caixa.treinamento.security.CsrfService;
import br.gov.caixa.treinamento.security.UsuarioLogadoService;
import br.gov.caixa.treinamento.service.GamificacaoService;
import br.gov.caixa.treinamento.service.TreinamentoService;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.Map;

@Path("/treinamento")
public class TreinamentoResource {

    @Inject
    Template treinamento;

    @Inject
    TreinamentoService treinamentoService;

    @Inject
    GamificacaoService gamificacaoService;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    CsrfService csrfService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response treinamento(@CookieParam("CAIXAVERSO_SESSION") String tokenSessao,
                                @CookieParam(CsrfService.COOKIE_CSRF) String csrfCookie) {
        String usuarioLogado = usuarioLogadoService.matriculaOuNulo(tokenSessao);

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        var trilha = treinamentoService.buscarTrilhaAberturaConta();

        ProgressoTreinamentoUsuario progresso =
                treinamentoService.iniciarOuBuscarProgresso(
                        usuarioLogado,
                        TreinamentoService.CODIGO_ABERTURA_CONTA
                );

        String csrfToken = csrfService.obterOuGerarToken(csrfCookie);

        return Response.ok(treinamento
                .data("trilha", trilha)
                .data("progresso", progresso)
                .data("csrfToken", csrfToken))
                .type(MediaType.TEXT_HTML)
                .cookie(csrfService.criarCookie(csrfToken))
                .header("Cache-Control", "no-store")
                .build();
    }

    @POST
    @Path("/etapa-concluida")
    @Produces(MediaType.APPLICATION_JSON)
    public Response concluirEtapa(@CookieParam("CAIXAVERSO_SESSION") String tokenSessao,
                                  @CookieParam(CsrfService.COOKIE_CSRF) String csrfCookie,
                                  @HeaderParam("X-CSRF-Token") String csrfToken) {
        if (!csrfService.tokenValido(csrfToken, csrfCookie)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("sucesso", false, "mensagem", "Token CSRF inválido ou ausente."))
                    .build();
        }

        String usuarioLogado = usuarioLogadoService.matriculaOuNulo(tokenSessao);

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        ProgressoTreinamentoUsuario progresso =
                treinamentoService.concluirProximaEtapa(
                        usuarioLogado,
                        TreinamentoService.CODIGO_ABERTURA_CONTA
                );

        gamificacaoService.registrarEtapaConcluida(usuarioLogado);

        return Response.ok(Map.of(
                "sucesso", true,
                "etapaAtual", progresso.etapaAtual,
                "totalEtapas", progresso.totalEtapas,
                "progressoPercentual", progresso.progressoPercentual,
                "concluido", progresso.concluido,
                "desafioDesbloqueado", progresso.desafioDesbloqueado
        )).build();
    }

    @POST
    @Path("/refazer")
    public Response refazerTrilha(@CookieParam("CAIXAVERSO_SESSION") String tokenSessao,
                                  @CookieParam(CsrfService.COOKIE_CSRF) String csrfCookie,
                                  @FormParam("csrfToken") String csrfToken) {
        if (!csrfService.tokenValido(csrfToken, csrfCookie)) {
            return Response.seeOther(URI.create("/treinamento")).build();
        }

        String usuarioLogado = usuarioLogadoService.matriculaOuNulo(tokenSessao);

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        gamificacaoService.zerarGamificacaoDoUsuario(usuarioLogado);

        treinamentoService.reiniciarTrilha(
                usuarioLogado,
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        return Response.seeOther(URI.create("/treinamento")).build();
    }
}
