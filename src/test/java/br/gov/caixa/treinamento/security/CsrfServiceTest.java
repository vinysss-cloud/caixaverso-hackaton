package br.gov.caixa.treinamento.security;

import jakarta.ws.rs.core.NewCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CsrfService - Testes unitários")
class CsrfServiceTest {

    @Test
    @DisplayName("Deve gerar token aleatório com tamanho adequado")
    void devGerarTokenAleatorio() {
        CsrfService service = new CsrfService();
        
        String token1 = service.gerarToken();
        String token2 = service.gerarToken();
        
        assertThat(token1).isNotEmpty();
        assertThat(token2).isNotEmpty();
        assertThat(token1).isNotEqualTo(token2);
        assertThat(token1.length()).isGreaterThan(20);
    }

    @Test
    @DisplayName("Deve retornar token novo quando token atual é nulo")
    void deveGerarTokenQuandoAtualNulo() {
        CsrfService service = new CsrfService();
        
        String token = service.obterOuGerarToken(null);
        
        assertThat(token).isNotEmpty();
        assertThat(token.length()).isGreaterThan(20);
    }

    @Test
    @DisplayName("Deve retornar token novo quando token atual está em branco")
    void deveGerarTokenQuandoAtualEmBranco() {
        CsrfService service = new CsrfService();
        
        String token = service.obterOuGerarToken("   ");
        
        assertThat(token).isNotEmpty();
        assertThat(token.length()).isGreaterThan(20);
    }

    @Test
    @DisplayName("Deve retornar token novo quando token atual é muito curto")
    void deveGerarTokenQuandoAtualMuitoCurto() {
        CsrfService service = new CsrfService();
        
        String token = service.obterOuGerarToken("abc123");
        
        assertThat(token).isNotEmpty();
        assertThat(token.length()).isGreaterThan(20);
    }

    @Test
    @DisplayName("Deve retornar token atual quando válido")
    void deveRetornarTokenAtualValido() {
        CsrfService service = new CsrfService();
        String tokenValido = service.gerarToken();
        
        String token = service.obterOuGerarToken(tokenValido);
        
        assertThat(token).isEqualTo(tokenValido);
    }

    @Test
    @DisplayName("Deve validar tokens iguais")
    void deveValidarTokensIguais() {
        CsrfService service = new CsrfService();
        String token = service.gerarToken();
        
        boolean valido = service.tokenValido(token, token);
        
        assertThat(valido).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar tokens diferentes")
    void deveRejeitarTokensDiferentes() {
        CsrfService service = new CsrfService();
        String token1 = service.gerarToken();
        String token2 = service.gerarToken();
        
        boolean valido = service.tokenValido(token1, token2);
        
        assertThat(valido).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar quando token formulário é nulo")
    void deveRejeitarTokenFormularioNulo() {
        CsrfService service = new CsrfService();
        String token = service.gerarToken();
        
        boolean valido = service.tokenValido(null, token);
        
        assertThat(valido).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar quando token cookie é nulo")
    void deveRejeitarTokenCookieNulo() {
        CsrfService service = new CsrfService();
        String token = service.gerarToken();
        
        boolean valido = service.tokenValido(token, null);
        
        assertThat(valido).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar quando token formulário é em branco")
    void deveRejeitarTokenFormularioEmBranco() {
        CsrfService service = new CsrfService();
        String token = service.gerarToken();
        
        boolean valido = service.tokenValido("   ", token);
        
        assertThat(valido).isFalse();
    }

    @Test
    @DisplayName("Deve criar cookie com configurações adequadas")
    void deveCriarCookieComConfiguracoes() {
        CsrfService service = new CsrfService();
        String token = service.gerarToken();
        
        NewCookie cookie = service.criarCookie(token);
        
        assertThat(cookie.getName()).isEqualTo(CsrfService.COOKIE_CSRF);
        assertThat(cookie.getValue()).isEqualTo(token);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isGreaterThan(0);
    }
}

