package br.gov.caixa.treinamento.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.NewCookie;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class CsrfService {

    public static final String COOKIE_CSRF = "CAIXAVERSO_CSRF";
    private static final int BYTES_TOKEN = 32;
    private static final int TEMPO_TOKEN_SEGUNDOS = 7200;

    private final SecureRandom secureRandom = new SecureRandom();

    @ConfigProperty(name = "caixaverso.cookie.secure", defaultValue = "true")
    boolean cookieSecure;

    public String gerarToken() {
        byte[] bytes = new byte[BYTES_TOKEN];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String obterOuGerarToken(String tokenAtual) {
        if (tokenAtual == null || tokenAtual.isBlank() || tokenAtual.length() < 32) {
            return gerarToken();
        }
        return tokenAtual;
    }

    public boolean tokenValido(String tokenFormulario, String tokenCookie) {
        return tokenFormulario != null
                && tokenCookie != null
                && !tokenFormulario.isBlank()
                && tokenFormulario.equals(tokenCookie);
    }

    public NewCookie criarCookie(String token) {
        return new NewCookie.Builder(COOKIE_CSRF)
                .value(token)
                .path("/")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(NewCookie.SameSite.STRICT)
                .maxAge(TEMPO_TOKEN_SEGUNDOS)
                .build();
    }
}
