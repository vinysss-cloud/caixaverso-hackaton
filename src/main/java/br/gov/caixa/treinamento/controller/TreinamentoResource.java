package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.model.ProgressoTreinamentoUsuario;
import br.gov.caixa.treinamento.service.GamificacaoService;
import br.gov.caixa.treinamento.service.TreinamentoService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
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

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Object treinamento(@CookieParam("usuarioLogado") String usuarioLogado) {
        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        var trilha = treinamentoService.buscarTrilhaAberturaConta();
        ProgressoTreinamentoUsuario progresso =
                treinamentoService.iniciarOuBuscarProgresso(usuarioLogado, TreinamentoService.CODIGO_ABERTURA_CONTA);

        gamificacaoService.registrarInicioTreinamento(usuarioLogado);

        return treinamento
                .data("trilha", trilha)
                .data("progresso", progresso);
    }

    @POST
    @Path("/etapa-concluida")
    @Produces(MediaType.APPLICATION_JSON)
    public Response concluirEtapa(@CookieParam("usuarioLogado") String usuarioLogado) {
        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        ProgressoTreinamentoUsuario progresso =
                treinamentoService.concluirProximaEtapa(usuarioLogado, TreinamentoService.CODIGO_ABERTURA_CONTA);

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
}