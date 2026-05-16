package br.gov.caixa.treinamento.security;

import br.gov.caixa.treinamento.service.AuthService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    public static final String COOKIE_SESSAO = "CAIXAVERSO_SESSION";

    private static final Set<String> ROTAS_PUBLICAS_EXATAS = Set.of(
            "",
            "/",
            "/login",
            "/cadastro",
            "/favicon.ico"
    );

    private static final Set<String> ROTAS_PUBLICAS_PREFIXO = Set.of(
            "/css/",
            "/js/",
            "/img/",
            "/assets/",
            "/q/"
    );

    @Inject
    AuthService authService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = normalizarPath(requestContext.getUriInfo().getPath());

        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }

        if (rotaPublica(path)) {
            return;
        }

        Cookie cookie = requestContext.getCookies().get(COOKIE_SESSAO);

        if (cookie == null || authService.buscarUsuarioPorTokenSessao(cookie.getValue()).isEmpty()) {
            NewCookie limparCookie = new NewCookie.Builder(COOKIE_SESSAO)
                    .value("")
                    .path("/")
                    .httpOnly(true)
                    .sameSite(NewCookie.SameSite.STRICT)
                    .maxAge(0)
                    .build();

            requestContext.abortWith(
                    Response.seeOther(URI.create("/login"))
                            .cookie(limparCookie)
                            .header(HttpHeaders.CACHE_CONTROL, "no-store")
                            .build()
            );
        }
    }

    private String normalizarPath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }

        String normalizado = path.trim();

        if (!normalizado.startsWith("/")) {
            normalizado = "/" + normalizado;
        }

        while (normalizado.contains("//")) {
            normalizado = normalizado.replace("//", "/");
        }

        return normalizado;
    }

    private boolean rotaPublica(String path) {
        if (ROTAS_PUBLICAS_EXATAS.contains(path)) {
            return true;
        }

        return ROTAS_PUBLICAS_PREFIXO.stream().anyMatch(path::startsWith);
    }
}
