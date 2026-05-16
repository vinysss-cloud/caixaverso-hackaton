package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.dto.CadastroUsuarioDTO;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.security.AuthFilter;
import br.gov.caixa.treinamento.service.AuthService;
import br.gov.caixa.treinamento.service.GamificacaoService;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
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
    GamificacaoService gamificacaoService;

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public Response login() {
        return renderLogin(null, null);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response autenticar(@FormParam("matricula") String matricula,
                               @FormParam("senha") String senha) {

        if (matricula == null || matricula.isBlank() || senha == null || senha.isBlank()) {
            return renderLogin("Informe sua matrícula e senha para acessar a plataforma.", null);
        }

        if (!authService.existeMatricula(matricula)) {
            return renderLogin("Não encontramos cadastro para esta matrícula. Confira se digitou corretamente ou clique em Criar cadastro.", null);
        }

        Optional<Usuario> usuario = authService.autenticar(matricula, senha);

        if (usuario.isEmpty()) {
            return renderLogin("Senha incorreta. Verifique a senha cadastrada e tente novamente.", null);
        }

        String tokenSessao = authService.criarSessao(usuario.get());
        NewCookie cookie = criarCookieSessao(tokenSessao, 7200);

        try {
            gamificacaoService.registrarLogin(usuario.get().matricula);
        } catch (Exception e) {
            // Não impedir login em caso de erro na gamificação.
        }

        return Response.seeOther(URI.create("/home"))
                .cookie(cookie)
                .build();
    }

    @GET
    @Path("/cadastro")
    @Produces(MediaType.TEXT_HTML)
    public Response cadastro() {
        return Response.ok(cadastro.data("erro", null))
                .type(MediaType.TEXT_HTML)
                .header("Cache-Control", "no-store")
                .build();
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
            return renderLogin(null, "Cadastro realizado com sucesso. Agora faça login com sua matrícula e senha.");
        } catch (IllegalArgumentException e) {
            return Response.ok(cadastro.data("erro", e.getMessage()))
                    .type(MediaType.TEXT_HTML)
                    .header("Cache-Control", "no-store")
                    .build();
        }
    }

    @GET
    @Path("/logout")
    public Response logout(@CookieParam(AuthFilter.COOKIE_SESSAO) String tokenSessao) {
        authService.encerrarSessao(tokenSessao);

        return Response.seeOther(URI.create("/login"))
                .cookie(criarCookieSessao("", 0))
                .build();
    }

    private Response renderLogin(String erro, String sucesso) {
        return Response.ok(login.data("erro", erro).data("sucesso", sucesso))
                .type(MediaType.TEXT_HTML)
                .header("Cache-Control", "no-store")
                .build();
    }

    private NewCookie criarCookieSessao(String valor, int maxAge) {
        return new NewCookie.Builder(AuthFilter.COOKIE_SESSAO)
                .value(valor)
                .path("/")
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .maxAge(maxAge)
                .build();
    }
}
