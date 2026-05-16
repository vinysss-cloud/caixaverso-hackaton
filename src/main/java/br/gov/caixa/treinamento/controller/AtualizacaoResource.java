package br.gov.caixa.treinamento.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

/**
 * A tela de Nova Funcionalidade foi removida do MVP.
 *
 * Mantemos este recurso apenas para evitar erro caso alguém acesse
 * diretamente uma URL antiga, redirecionando para a home.
 */
@Path("/atualizacao")
public class AtualizacaoResource {

    @GET
    public Response atualizacaoRemovida() {
        return Response.seeOther(URI.create("/home")).build();
    }
}
