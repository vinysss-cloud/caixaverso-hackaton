package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.dto.CadastroUsuarioDTO;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.security.AuthFilter;
import br.gov.caixa.treinamento.security.CsrfService;
import br.gov.caixa.treinamento.security.LoginRateLimitService;
import br.gov.caixa.treinamento.service.AuthService;
import br.gov.caixa.treinamento.service.GamificacaoService;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

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

    @Inject
    CsrfService csrfService;

    @Inject
    LoginRateLimitService loginRateLimitService;

    @ConfigProperty(name = "caixaverso.cookie.secure", defaultValue = "true")
    boolean cookieSecure;

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
                               @FormParam("senha") String senha,
                               @FormParam("csrfToken") String csrfToken,
                               @CookieParam(CsrfService.COOKIE_CSRF) String csrfCookie,
                               @HeaderParam("X-Forwarded-For") String forwardedFor,
                               @HeaderParam("X-Real-IP") String realIp) {

        if (!csrfService.tokenValido(csrfToken, csrfCookie)) {
            return renderLogin("Formulário expirado por segurança. Recarregue a página e tente novamente.", null);
        }

        String origem = origemRequisicao(forwardedFor, realIp);

        if (loginRateLimitService.estaBloqueado(matricula, origem)) {
            return renderLogin("Muitas tentativas de acesso. Aguarde alguns minutos antes de tentar novamente.", null);
        }

        if (matricula == null || matricula.isBlank() || senha == null || senha.isBlank()) {
            loginRateLimitService.registrarFalha(matricula, origem);
            return renderLogin("Informe sua matrícula e senha para acessar a plataforma.", null);
        }

        if (!authService.existeMatricula(matricula)) {
            loginRateLimitService.registrarFalha(matricula, origem);
            return renderLogin("Não encontramos cadastro para esta matrícula. Confira se digitou corretamente ou clique em Criar cadastro.", null);
        }

        Optional<Usuario> usuario = authService.autenticar(matricula, senha);

        if (usuario.isEmpty()) {
            loginRateLimitService.registrarFalha(matricula, origem);
            return renderLogin("Senha incorreta. Verifique a senha cadastrada e tente novamente.", null);
        }

        loginRateLimitService.registrarSucesso(matricula, origem);

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
        return renderCadastro(null);
    }

    @POST
    @Path("/cadastro")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response cadastrar(@FormParam("nome") String nome,
                              @FormParam("matricula") String matricula,
                              @FormParam("idade") Integer idade,
                              @FormParam("preferenciasAcessibilidade") java.util.List<String> preferenciasAcessibilidade,
                              @FormParam("senha") String senha,
                              @FormParam("repetirSenha") String repetirSenha,
                              @FormParam("csrfToken") String csrfToken,
                              @CookieParam(CsrfService.COOKIE_CSRF) String csrfCookie) {

        if (!csrfService.tokenValido(csrfToken, csrfCookie)) {
            return renderCadastro("Formulário expirado por segurança. Recarregue a página e tente novamente.");
        }

        CadastroUsuarioDTO dto = new CadastroUsuarioDTO();
        dto.nome = nome;
        dto.matricula = matricula;
        dto.idade = idade;
        dto.preferenciasAcessibilidade = preferenciasAcessibilidade;
        dto.senha = senha;
        dto.repetirSenha = repetirSenha;

        try {
            authService.cadastrarUsuario(dto);
            return renderLogin(null, "Cadastro realizado com sucesso. Agora faça login com sua matrícula e senha.");
        } catch (IllegalArgumentException e) {
            return renderCadastro(e.getMessage());
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
        String csrfToken = csrfService.gerarToken();
        return Response.ok(login.data("erro", erro).data("sucesso", sucesso).data("csrfToken", csrfToken))
                .type(MediaType.TEXT_HTML)
                .cookie(csrfService.criarCookie(csrfToken))
                .header("Cache-Control", "no-store")
                .build();
    }

    private Response renderCadastro(String erro) {
        String csrfToken = csrfService.gerarToken();
        return Response.ok(cadastro.data("erro", erro).data("csrfToken", csrfToken))
                .type(MediaType.TEXT_HTML)
                .cookie(csrfService.criarCookie(csrfToken))
                .header("Cache-Control", "no-store")
                .build();
    }

    private String origemRequisicao(String forwardedFor, String realIp) {
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return "local";
    }

    private NewCookie criarCookieSessao(String valor, int maxAge) {
        return new NewCookie.Builder(AuthFilter.COOKIE_SESSAO)
                .value(valor)
                .path("/")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(NewCookie.SameSite.STRICT)
                .maxAge(maxAge)
                .build();
    }
}
