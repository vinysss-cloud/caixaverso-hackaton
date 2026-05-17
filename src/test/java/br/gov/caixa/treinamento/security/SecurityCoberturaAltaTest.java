package br.gov.caixa.treinamento.security;

import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.service.AuthService;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Security - Cobertura alta")
class SecurityCoberturaAltaTest {

    @Mock AuthService authService;
    @Mock ContainerRequestContext requestContext;
    @Mock UriInfo uriInfo;

    @Test
    @DisplayName("AuthFilter deve liberar requisição OPTIONS")
    void authFilter_deveLiberarOptions() throws Exception {
        AuthFilter filter = authFilter();
        prepararRequestSemCookies("OPTIONS", "/dashboard");

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any(Response.class));
        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("AuthFilter deve liberar rotas públicas exatas")
    void authFilter_deveLiberarRotasPublicasExatas() throws Exception {
        AuthFilter filter = authFilter();
        prepararRequestSemCookies("GET", "login");

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any(Response.class));
        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("AuthFilter deve liberar rotas públicas por prefixo")
    void authFilter_deveLiberarRotasPublicasPorPrefixo() throws Exception {
        AuthFilter filter = authFilter();
        prepararRequestSemCookies("GET", "//css/app.css");

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any(Response.class));
        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("AuthFilter deve redirecionar rota protegida sem cookie")
    void authFilter_deveRedirecionarSemCookie() throws Exception {
        AuthFilter filter = authFilter();
        prepararRequest("GET", "/dashboard", Map.of());

        filter.filter(requestContext);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(requestContext).abortWith(captor.capture());
        Response response = captor.getValue();

        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation()).isEqualTo(URI.create("/login"));
        assertThat(response.getHeaderString("Cache-Control")).isEqualTo("no-store");
        assertThat(response.getCookies().get(AuthFilter.COOKIE_SESSAO).getMaxAge()).isZero();
    }

    @Test
    @DisplayName("AuthFilter deve redirecionar rota protegida com cookie inválido")
    void authFilter_deveRedirecionarCookieInvalido() throws Exception {
        AuthFilter filter = authFilter();
        prepararRequest("GET", "/dashboard", Map.of(AuthFilter.COOKIE_SESSAO, new Cookie(AuthFilter.COOKIE_SESSAO, "token")));
        when(authService.buscarUsuarioPorTokenSessao("token")).thenReturn(Optional.empty());

        filter.filter(requestContext);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(requestContext).abortWith(captor.capture());
        assertThat(captor.getValue().getLocation()).isEqualTo(URI.create("/login"));
    }

    @Test
    @DisplayName("AuthFilter deve liberar rota protegida com cookie válido")
    void authFilter_deveLiberarCookieValido() throws Exception {
        AuthFilter filter = authFilter();
        Usuario usuario = new Usuario();
        usuario.matricula = "c123456";
        prepararRequest("GET", "/dashboard", Map.of(AuthFilter.COOKIE_SESSAO, new Cookie(AuthFilter.COOKIE_SESSAO, "token")));
        when(authService.buscarUsuarioPorTokenSessao("token")).thenReturn(Optional.of(usuario));

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any(Response.class));
        verify(authService).buscarUsuarioPorTokenSessao("token");
    }

    @Test
    @DisplayName("UsuarioLogadoService deve buscar usuário por token")
    void usuarioLogadoService_deveBuscarPorToken() {
        UsuarioLogadoService service = new UsuarioLogadoService();
        service.authService = authService;
        Usuario usuario = new Usuario();
        usuario.matricula = "c123456";
        when(authService.buscarUsuarioPorTokenSessao("token")).thenReturn(Optional.of(usuario));

        assertThat(service.buscarPorToken("token")).contains(usuario);
        assertThat(service.matriculaOuNulo("token")).isEqualTo("c123456");
    }

    @Test
    @DisplayName("UsuarioLogadoService deve retornar null quando token não tem usuário")
    void usuarioLogadoService_deveRetornarNullSemUsuario() {
        UsuarioLogadoService service = new UsuarioLogadoService();
        service.authService = authService;
        when(authService.buscarUsuarioPorTokenSessao("token")).thenReturn(Optional.empty());

        assertThat(service.buscarPorToken("token")).isEmpty();
        assertThat(service.matriculaOuNulo("token")).isNull();
    }

    @Test
    @DisplayName("LoginRateLimitService deve permitir login sem tentativas")
    void loginRateLimit_devePermitirSemTentativas() {
        LoginRateLimitService service = new LoginRateLimitService();
        assertThat(service.estaBloqueado("c123456", "127.0.0.1")).isFalse();
    }

    @Test
    @DisplayName("LoginRateLimitService deve registrar falha")
    void loginRateLimit_deveRegistrarFalha() {
        LoginRateLimitService service = new LoginRateLimitService();
        service.registrarFalha("c123456", "127.0.0.1");
        assertThat(service.estaBloqueado("c123456", "127.0.0.1")).isFalse();
    }

    @Test
    @DisplayName("LoginRateLimitService deve bloquear apos 5 tentativas")
    void loginRateLimit_deveBloquearApos5Tentativas() {
        LoginRateLimitService service = new LoginRateLimitService();
        for (int i = 0; i < 5; i++) {
            service.registrarFalha("c123456", "127.0.0.1");
        }
        assertThat(service.estaBloqueado("c123456", "127.0.0.1")).isTrue();
    }

    @Test
    @DisplayName("LoginRateLimitService deve limpar apos sucesso")
    void loginRateLimit_deveLimparAposSucesso() {
        LoginRateLimitService service = new LoginRateLimitService();
        service.registrarFalha("c123456", "127.0.0.1");
        service.registrarSucesso("c123456", "127.0.0.1");
        assertThat(service.estaBloqueado("c123456", "127.0.0.1")).isFalse();
    }

    @Test
    @DisplayName("CsrfService deve gerar token aleatorio")
    void csrfService_deveGerarTokenAleatorio() {
        CsrfService service = new CsrfService();
        String token1 = service.gerarToken();
        String token2 = service.gerarToken();
        assertThat(token1).isNotEmpty();
        assertThat(token2).isNotEmpty();
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("CsrfService deve validar tokens iguais")
    void csrfService_deveValidarTokensIguais() {
        CsrfService service = new CsrfService();
        String token = service.gerarToken();
        assertThat(service.tokenValido(token, token)).isTrue();
    }

    @Test
    @DisplayName("CsrfService deve rejeitar tokens diferentes")
    void csrfService_deveRejeitarTokensDiferentes() {
        CsrfService service = new CsrfService();
        String token1 = service.gerarToken();
        String token2 = service.gerarToken();
        assertThat(service.tokenValido(token1, token2)).isFalse();
    }

    @Test
    @DisplayName("CsrfService deve retornar token novo quando nulo")
    void csrfService_deveGerarTokenQuandoNulo() {
        CsrfService service = new CsrfService();
        String token = service.obterOuGerarToken(null);
        assertThat(token).isNotEmpty().hasSizeGreaterThan(20);
    }

    @Test
    @DisplayName("CsrfService deve retornar token novo quando em branco")
    void csrfService_deveGerarTokenQuandoEmBranco() {
        CsrfService service = new CsrfService();
        String token = service.obterOuGerarToken("   ");
        assertThat(token).isNotEmpty().hasSizeGreaterThan(20);
    }

    @Test
    @DisplayName("CsrfService deve retornar token novo quando muito curto")
    void csrfService_deveGerarTokenQuandoMuitoCurto() {
        CsrfService service = new CsrfService();
        String token = service.obterOuGerarToken("abc");
        assertThat(token).isNotEmpty().hasSizeGreaterThan(20);
    }

    @Test
    @DisplayName("CsrfService deve retornar token valido")
    void csrfService_deveRetornarTokenValido() {
        CsrfService service = new CsrfService();
        String tokenValido = service.gerarToken();
        String token = service.obterOuGerarToken(tokenValido);
        assertThat(token).isEqualTo(tokenValido);
    }

    @Test
    @DisplayName("CsrfService deve criar cookie httpOnly")
    void csrfService_deveCriarCookieHttpOnly() {
        CsrfService service = new CsrfService();
        String token = service.gerarToken();
        var cookie = service.criarCookie(token);
        assertThat(cookie.getName()).isEqualTo(CsrfService.COOKIE_CSRF);
        assertThat(cookie.getValue()).isEqualTo(token);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
    }

    private AuthFilter authFilter() {
        AuthFilter filter = new AuthFilter();
        filter.authService = authService;
        return filter;
    }

    private void prepararRequestSemCookies(String metodo, String path) {
        prepararRequestBase(metodo, path);
    }

    private void prepararRequest(String metodo, String path, Map<String, Cookie> cookies) {
        prepararRequestBase(metodo, path);
        when(requestContext.getCookies()).thenReturn(cookies);
    }

    private void prepararRequestBase(String metodo, String path) {
        when(requestContext.getMethod()).thenReturn(metodo);
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPath()).thenReturn(path);
    }
}
