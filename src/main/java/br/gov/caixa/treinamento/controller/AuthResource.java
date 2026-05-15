package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.dto.CadastroUsuarioDTO;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.service.AuthService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/")
public class AuthResource {

    @Inject
    Template login;

    @Inject
    Template cadastro;

    @Inject
    AuthService authService;

    @Inject
    br.gov.caixa.treinamento.service.GamificacaoService gamificacaoService;

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance login() {
        return login.data("erro", null)
                    .data("sucesso", null);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response autenticar(@FormParam("matricula") String matricula,
                               @FormParam("senha") String senha) {

        Optional<Usuario> usuario = authService.autenticar(matricula, senha);

        if (usuario.isEmpty()) {
            return Response.ok(login.data("erro", "Matrícula ou senha inválidos.")
                                    .data("sucesso", null))
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        NewCookie cookie = new NewCookie.Builder("usuarioLogado")
                .value(usuario.get().matricula)
                .path("/")
                .httpOnly(true)
                .maxAge(3600)
                .build();

        // registrar atividade de login e pontos
        try {
            gamificacaoService.registrarLogin(usuario.get().matricula);
        } catch (Exception e) {
            // não impedir login em caso de erro no gamification
        }

        return Response.seeOther(URI.create("/home"))
                .cookie(cookie)
                .build();
    }

    @GET
    @Path("/cadastro")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance cadastro() {
        return cadastro.data("erro", null);
    }

    @POST
    @Path("/cadastro")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response cadastrar(@FormParam("nome") String nome,
                              @FormParam("matricula") String matricula,
                              @FormParam("idade") Integer idade,
                              @FormParam("deficiencias") List<String> deficiencias,
                              @FormParam("senha") String senha,
                              @FormParam("repetirSenha") String repetirSenha) {

        CadastroUsuarioDTO dto = new CadastroUsuarioDTO();
        dto.nome = nome;
        dto.matricula = matricula;
        dto.idade = idade;
        dto.deficiencias = deficiencias;
        dto.senha = senha;
        dto.repetirSenha = repetirSenha;

        try {
            authService.cadastrarUsuario(dto);
            return Response.ok(login.data("erro", null)
                                    .data("sucesso", "Cadastro realizado com sucesso. Faça login para continuar."))
                    .type(MediaType.TEXT_HTML)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.ok(cadastro.data("erro", e.getMessage()))
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @GET
    @Path("/logout")
    public Response logout() {
        NewCookie cookie = new NewCookie.Builder("usuarioLogado")
                .value("")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();

        return Response.seeOther(URI.create("/login"))
                .cookie(cookie)
                .build();
    }
}

