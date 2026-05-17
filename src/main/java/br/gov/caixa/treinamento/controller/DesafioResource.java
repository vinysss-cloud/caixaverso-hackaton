package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.logging.AuditLogService;
import br.gov.caixa.treinamento.model.ResultadoDesafio;
import br.gov.caixa.treinamento.model.ResultadoDesafioUsuario;
import br.gov.caixa.treinamento.security.CsrfService;
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
import java.util.Optional;

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

    @Inject
    CsrfService csrfService;

    @Inject
    AuditLogService auditLogService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response desafio(@CookieParam("CAIXAVERSO_SESSION") String tokenSessao,
                            @CookieParam(CsrfService.COOKIE_CSRF) String csrfCookie) {
        String usuarioLogado = usuarioLogadoService.matriculaOuNulo(tokenSessao);

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        boolean desbloqueado = treinamentoService.desafioEstaDesbloqueado(
                usuarioLogado,
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        boolean desafioRespondido = desafioService.desafioContaFacilJaRespondido(usuarioLogado);
        Optional<ResultadoDesafioUsuario> resultadoSalvo = desafioService.buscarResultadoContaFacil(usuarioLogado);

        String csrfToken = csrfService.obterOuGerarToken(csrfCookie);

        return renderizarDesafio(desbloqueado, desafioRespondido, resultadoSalvo.orElse(null), null, csrfToken, null);
    }

    /**
     * Sobrecarga mantida para testes unitários antigos que chamavam o método diretamente
     * antes da inclusão dos campos acertos/percentual/totalSituacoes no formulário dinâmico.
     */
    public Object responder(String tokenSessao, String csrfCookie, String csrfToken, String resposta) {
        return responder(tokenSessao, csrfCookie, csrfToken, resposta, null, null, null);
    }

    @POST
    @Path("/responder")
    @Produces(MediaType.TEXT_HTML)
    public Object responder(@CookieParam("CAIXAVERSO_SESSION") String tokenSessao,
                            @CookieParam(CsrfService.COOKIE_CSRF) String csrfCookie,
                            @FormParam("csrfToken") String csrfToken,
                            @FormParam("resposta") String resposta,
                            @FormParam("acertos") Integer acertos,
                            @FormParam("percentual") Integer percentual,
                            @FormParam("totalSituacoes") Integer totalSituacoes) {
        String usuarioLogado = usuarioLogadoService.matriculaOuNulo(tokenSessao);

        if (!csrfService.tokenValido(csrfToken, csrfCookie)) {
            registrarCsrfInvalido(usuarioLogado);
            return Response.seeOther(URI.create("/desafio")).build();
        }

        if (usuarioLogado == null || usuarioLogado.isBlank()) {
            return Response.seeOther(URI.create("/login")).build();
        }

        boolean desbloqueado = treinamentoService.desafioEstaDesbloqueado(
                usuarioLogado,
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        if (!desbloqueado) {
            registrarDesafioBloqueado(usuarioLogado);
            return renderizarDesafio(false, false, null, null, csrfToken,
                    "Conclua primeiro o assistente guiado para liberar esta validação.");
        }

        if (desafioService.desafioContaFacilJaRespondido(usuarioLogado)) {
            registrarDesafioJaConcluido(usuarioLogado);
            Optional<ResultadoDesafioUsuario> resultadoSalvo = desafioService.buscarResultadoContaFacil(usuarioLogado);
            ResultadoDesafio resultado = new ResultadoDesafio(
                    resultadoSalvo.map(r -> Boolean.TRUE.equals(r.aprovado)).orElse(false),
                    0,
                    "Este desafio já foi concluído por este usuário. O botão foi bloqueado para evitar pontuação repetida e preservar o ranking."
            );
            return renderizarDesafio(true, true, resultadoSalvo.orElse(null), resultado, csrfToken, null);
        }

        ResultadoDesafio resultado = desafioService.registrarResultadoContaFacil(
                usuarioLogado,
                resposta,
                acertos,
                percentual,
                totalSituacoes
        );

        gamificacaoService.registrarDesafioRespondido(
                usuarioLogado,
                resultado.acertou,
                resultado.pontuacao
        );

        treinamentoService.marcarDesafioRespondido(
                usuarioLogado,
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        registrarDesafioConcluido(usuarioLogado, acertos, percentual);

        Optional<ResultadoDesafioUsuario> resultadoSalvo = desafioService.buscarResultadoContaFacil(usuarioLogado);

        return renderizarDesafio(true, true, resultadoSalvo.orElse(null), resultado, csrfToken, null);
    }

    private void registrarCsrfInvalido(String matricula) {
        if (auditLogService != null) {
            auditLogService.csrfInvalido("/desafio/responder", matricula);
        }
    }

    private void registrarDesafioBloqueado(String matricula) {
        if (auditLogService != null) {
            auditLogService.desafioBloqueado(matricula, DesafioService.CODIGO_DESAFIO_CONTA_FACIL);
        }
    }

    private void registrarDesafioJaConcluido(String matricula) {
        if (auditLogService != null) {
            auditLogService.desafioJaConcluido(matricula, DesafioService.CODIGO_DESAFIO_CONTA_FACIL);
        }
    }

    private void registrarDesafioConcluido(String matricula, Integer acertos, Integer percentual) {
        if (auditLogService != null) {
            auditLogService.desafioConcluido(matricula, DesafioService.CODIGO_DESAFIO_CONTA_FACIL, acertos, percentual);
        }
    }

    private Response renderizarDesafio(boolean desbloqueado,
                                       boolean desafioRespondido,
                                       ResultadoDesafioUsuario resultadoSalvo,
                                       ResultadoDesafio resultado,
                                       String csrfToken,
                                       String erro) {
        boolean podeExecutarDesafio = desbloqueado && !desafioRespondido;

        return Response.ok(desafio
                .data("desafio", desafioService.buscarDesafioAberturaConta())
                .data("resultado", resultado)
                .data("resultadoSalvo", resultadoSalvo)
                .data("desbloqueado", desbloqueado)
                .data("desafioRespondido", desafioRespondido)
                .data("podeExecutarDesafio", podeExecutarDesafio)
                .data("treinamentoTitulo", TreinamentoService.TITULO_TRILHA_CONTA_FACIL)
                .data("csrfToken", csrfToken)
                .data("erro", erro))
                .type(MediaType.TEXT_HTML)
                .cookie(csrfService.criarCookie(csrfToken))
                .header("Cache-Control", "no-store")
                .build();
    }
}
