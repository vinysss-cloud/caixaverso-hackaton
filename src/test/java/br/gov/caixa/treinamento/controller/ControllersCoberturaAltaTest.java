package br.gov.caixa.treinamento.controller;

import br.gov.caixa.treinamento.dto.CadastroUsuarioDTO;
import br.gov.caixa.treinamento.model.Desafio;
import br.gov.caixa.treinamento.model.ProgressoTreinamentoUsuario;
import br.gov.caixa.treinamento.model.ResultadoDesafio;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.security.AuthFilter;
import br.gov.caixa.treinamento.security.UsuarioLogadoService;
import br.gov.caixa.treinamento.service.AuthService;
import br.gov.caixa.treinamento.service.DashboardService;
import br.gov.caixa.treinamento.service.DesafioService;
import br.gov.caixa.treinamento.service.GamificacaoService;
import br.gov.caixa.treinamento.service.TreinamentoService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Controllers - Cobertura alta")
class ControllersCoberturaAltaTest {

    @Mock Template login;
    @Mock Template cadastro;
    @Mock Template home;
    @Mock Template dashboard;
    @Mock Template treinamento;
    @Mock Template desafioTemplate;

    @Mock TemplateInstance loginInstance;
    @Mock TemplateInstance cadastroInstance;
    @Mock TemplateInstance homeInstance;
    @Mock TemplateInstance dashboardInstance;
    @Mock TemplateInstance treinamentoInstance;
    @Mock TemplateInstance desafioInstance;

    @Mock AuthService authService;
    @Mock GamificacaoService gamificacaoService;
    @Mock DashboardService dashboardService;
    @Mock TreinamentoService treinamentoService;
    @Mock DesafioService desafioService;
    @Mock UsuarioLogadoService usuarioLogadoService;

    @Test
    @DisplayName("AtualizacaoResource deve redirecionar URL antiga para home")
    void atualizacao_deveRedirecionarParaHome() {
        Response response = new AtualizacaoResource().atualizacaoRemovida();

        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation()).isEqualTo(URI.create("/home"));
    }

    @Test
    @DisplayName("AuthResource GET login deve renderizar template sem erro")
    void auth_getLogin_deveRenderizarTemplate() {
        AuthResource resource = authResource();
        prepararLoginTemplate();

        Response response = resource.login();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getMediaType().toString()).isEqualTo("text/html");
        assertThat(response.getHeaderString("Cache-Control")).isEqualTo("no-store");
        verify(login).data("erro", null);
        verify(loginInstance).data("sucesso", null);
    }

    @Test
    @DisplayName("AuthResource POST login deve validar campos obrigatórios")
    void auth_autenticar_deveValidarCamposObrigatorios() {
        AuthResource resource = authResource();
        prepararLoginTemplate();

        Response response = resource.autenticar(" ", "");

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        verify(login).data(eq("erro"), contains("matrícula"));
        verifyNoInteractions(authService, gamificacaoService);
    }

    @Test
    @DisplayName("AuthResource POST login deve informar matrícula inexistente")
    void auth_autenticar_deveInformarMatriculaInexistente() {
        AuthResource resource = authResource();
        prepararLoginTemplate();
        when(authService.existeMatricula("c123456")).thenReturn(false);

        Response response = resource.autenticar("c123456", "Senha@123");

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        verify(login).data(eq("erro"), contains("Não encontramos cadastro"));
        verify(authService, never()).autenticar(anyString(), anyString());
    }

    @Test
    @DisplayName("AuthResource POST login deve informar senha incorreta")
    void auth_autenticar_deveInformarSenhaIncorreta() {
        AuthResource resource = authResource();
        prepararLoginTemplate();
        when(authService.existeMatricula("c123456")).thenReturn(true);
        when(authService.autenticar("c123456", "errada")).thenReturn(Optional.empty());

        Response response = resource.autenticar("c123456", "errada");

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        verify(login).data(eq("erro"), contains("Senha incorreta"));
    }

    @Test
    @DisplayName("AuthResource POST login válido deve criar cookie e redirecionar para home")
    void auth_autenticar_validoDeveRedirecionarComCookie() {
        AuthResource resource = authResource();
        Usuario usuario = usuarioExemplo();
        when(authService.existeMatricula("c123456")).thenReturn(true);
        when(authService.autenticar("c123456", "Senha@123")).thenReturn(Optional.of(usuario));
        when(authService.criarSessao(usuario)).thenReturn("token-gerado");

        Response response = resource.autenticar("c123456", "Senha@123");

        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation()).isEqualTo(URI.create("/home"));
        NewCookie cookie = response.getCookies().get(AuthFilter.COOKIE_SESSAO);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo("token-gerado");
        assertThat(cookie.isHttpOnly()).isTrue();
        verify(gamificacaoService).registrarLogin("c123456");
    }

    @Test
    @DisplayName("AuthResource POST login válido não deve falhar se gamificação lançar exceção")
    void auth_autenticar_deveIgnorarErroGamificacao() {
        AuthResource resource = authResource();
        Usuario usuario = usuarioExemplo();
        when(authService.existeMatricula("c123456")).thenReturn(true);
        when(authService.autenticar("c123456", "Senha@123")).thenReturn(Optional.of(usuario));
        when(authService.criarSessao(usuario)).thenReturn("token-gerado");
        doThrow(new RuntimeException("erro gamificação"))
                .when(gamificacaoService).registrarLogin("c123456");

        Response response = resource.autenticar("c123456", "Senha@123");

        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation()).isEqualTo(URI.create("/home"));
    }

    @Test
    @DisplayName("AuthResource GET cadastro deve renderizar template")
    void auth_getCadastro_deveRenderizarTemplate() {
        AuthResource resource = authResource();
        when(cadastro.data("erro", null)).thenReturn(cadastroInstance);

        Response response = resource.cadastro();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getMediaType().toString()).isEqualTo("text/html");
        assertThat(response.getHeaderString("Cache-Control")).isEqualTo("no-store");
    }

    @Test
    @DisplayName("AuthResource POST cadastro válido deve cadastrar e voltar ao login com sucesso")
    void auth_postCadastro_valido() {
        AuthResource resource = authResource();
        prepararLoginTemplate();

        Response response = resource.cadastrar(
                "João Silva",
                "c123456",
                30,
                List.of("visual"),
                "Senha@123",
                "Senha@123"
        );

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        verify(authService).cadastrarUsuario(argThat(dto ->
                "João Silva".equals(dto.nome)
                        && "c123456".equals(dto.matricula)
                        && Integer.valueOf(30).equals(dto.idade)
                        && dto.deficiencias.contains("visual")
                        && "Senha@123".equals(dto.senha)
                        && "Senha@123".equals(dto.repetirSenha)
        ));
        verify(loginInstance).data(eq("sucesso"), contains("Cadastro realizado"));
    }

    @Test
    @DisplayName("AuthResource POST cadastro inválido deve voltar ao cadastro com mensagem")
    void auth_postCadastro_invalido() {
        AuthResource resource = authResource();
        doThrow(new IllegalArgumentException("Matrícula já cadastrada"))
                .when(authService).cadastrarUsuario(any(CadastroUsuarioDTO.class));
        when(cadastro.data(eq("erro"), nullable(Object.class))).thenReturn(cadastroInstance);

        Response response = resource.cadastrar("João", "c123", 30, List.of(), "123", "456");

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getHeaderString("Cache-Control")).isEqualTo("no-store");
        verify(cadastro).data("erro", "Matrícula já cadastrada");
    }

    @Test
    @DisplayName("AuthResource logout deve encerrar sessão, limpar cookie e redirecionar")
    void auth_logout_deveEncerrarSessao() {
        AuthResource resource = authResource();

        Response response = resource.logout("token");

        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation()).isEqualTo(URI.create("/login"));
        assertThat(response.getCookies().get(AuthFilter.COOKIE_SESSAO).getMaxAge()).isZero();
        verify(authService).encerrarSessao("token");
    }

    @Test
    @DisplayName("HomeResource index deve redirecionar para login")
    void home_index_deveRedirecionarLogin() {
        HomeResource resource = homeResource();

        Response response = resource.index();

        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation()).isEqualTo(URI.create("/login"));
    }

    @Test
    @DisplayName("HomeResource home sem usuário deve redirecionar para login")
    void home_semUsuario_deveRedirecionar() {
        HomeResource resource = homeResource();
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn(null);

        Object result = resource.home("token");

        assertThat(result).isInstanceOf(Response.class);
        assertThat(((Response) result).getLocation()).isEqualTo(URI.create("/login"));
    }

    @Test
    @DisplayName("HomeResource home com usuário deve renderizar template")
    void home_comUsuario_deveRenderizarTemplate() {
        HomeResource resource = homeResource();
        Map<String, Object> resumo = Map.of("pontuacaoTotal", 100);
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn("c123456");
        when(gamificacaoService.buscarResumoUsuario("c123456")).thenReturn(resumo);
        when(home.data(eq("titulo"), nullable(Object.class))).thenReturn(homeInstance);
        when(homeInstance.data(eq("usuarioLogado"), nullable(Object.class))).thenReturn(homeInstance);
        when(homeInstance.data(eq("resumo"), nullable(Object.class))).thenReturn(homeInstance);

        Object result = resource.home("token");

        assertThat(result).isSameAs(homeInstance);
        verify(home).data("titulo", "CaixaVerso Treinamento");
        verify(homeInstance).data("usuarioLogado", "c123456");
        verify(homeInstance).data("resumo", resumo);
    }

    @Test
    @DisplayName("DashboardResource sem usuário deve redirecionar para login")
    void dashboard_semUsuario_deveRedirecionar() {
        DashboardResource resource = dashboardResource();
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn(" ");

        Object result = resource.dashboard("token");

        assertThat(result).isInstanceOf(Response.class);
        assertThat(((Response) result).getLocation()).isEqualTo(URI.create("/login"));
    }

    @Test
    @DisplayName("DashboardResource com usuário deve carregar resumo e dados")
    void dashboard_comUsuario_deveRenderizarTemplate() {
        DashboardResource resource = dashboardResource();
        Map<String, Object> resumo = Map.of("nivel", 2);
        Map<String, Object> dados = Map.of("ranking", List.of());
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn("c123456");
        when(gamificacaoService.buscarResumoUsuario("c123456")).thenReturn(resumo);
        when(dashboardService.buscarDados("c123456", resumo)).thenReturn(dados);
        when(dashboard.data(eq("usuarioLogado"), nullable(Object.class))).thenReturn(dashboardInstance);
        when(dashboardInstance.data(eq("dados"), nullable(Object.class))).thenReturn(dashboardInstance);
        when(dashboardInstance.data(eq("resumo"), nullable(Object.class))).thenReturn(dashboardInstance);

        Object result = resource.dashboard("token");

        assertThat(result).isSameAs(dashboardInstance);
        verify(dashboard).data("usuarioLogado", "c123456");
        verify(dashboardInstance).data("dados", dados);
        verify(dashboardInstance).data("resumo", resumo);
    }

    @Test
    @DisplayName("TreinamentoResource sem usuário deve redirecionar para login")
    void treinamento_semUsuario_deveRedirecionar() {
        TreinamentoResource resource = treinamentoResource();
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn(null);

        Object result = resource.treinamento("token");

        assertThat(result).isInstanceOf(Response.class);
        assertThat(((Response) result).getLocation()).isEqualTo(URI.create("/login"));
    }

    @Test
    @DisplayName("TreinamentoResource com usuário deve iniciar ou buscar progresso")
    void treinamento_comUsuario_deveRenderizarTemplate() {
        TreinamentoResource resource = treinamentoResource();
        ProgressoTreinamentoUsuario progresso = new ProgressoTreinamentoUsuario();
        Object trilha = new Object();
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn("c123456");
        when(treinamentoService.buscarTrilhaAberturaConta()).thenReturn(null);
        when(treinamentoService.iniciarOuBuscarProgresso("c123456", TreinamentoService.CODIGO_ABERTURA_CONTA)).thenReturn(progresso);
        when(treinamento.data(eq("trilha"), nullable(Object.class))).thenReturn(treinamentoInstance);
        when(treinamentoInstance.data(eq("progresso"), nullable(Object.class))).thenReturn(treinamentoInstance);

        Object result = resource.treinamento("token");

        assertThat(result).isSameAs(treinamentoInstance);
        verify(treinamentoService).iniciarOuBuscarProgresso("c123456", TreinamentoService.CODIGO_ABERTURA_CONTA);
    }

    @Test
    @DisplayName("TreinamentoResource concluir etapa sem usuário deve retornar unauthorized")
    void treinamento_concluirEtapa_semUsuario() {
        TreinamentoResource resource = treinamentoResource();
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn(null);

        Response response = resource.concluirEtapa("token");

        assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
        verifyNoInteractions(treinamentoService, gamificacaoService);
    }

    @Test
    @DisplayName("TreinamentoResource concluir etapa deve retornar JSON de progresso")
    void treinamento_concluirEtapa_comUsuario() {
        TreinamentoResource resource = treinamentoResource();
        ProgressoTreinamentoUsuario progresso = new ProgressoTreinamentoUsuario();
        progresso.etapaAtual = 2;
        progresso.totalEtapas = 5;
        progresso.progressoPercentual = 40;
        progresso.concluido = false;
        progresso.desafioDesbloqueado = false;

        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn("c123456");
        when(treinamentoService.concluirProximaEtapa("c123456", TreinamentoService.CODIGO_ABERTURA_CONTA)).thenReturn(progresso);

        Response response = resource.concluirEtapa("token");

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getEntity();

        assertThat(body).containsEntry("sucesso", true)
                .containsEntry("etapaAtual", 2)
                .containsEntry("totalEtapas", 5)
                .containsEntry("progressoPercentual", 40);
        verify(gamificacaoService).registrarEtapaConcluida("c123456");
    }

    @Test
    @DisplayName("TreinamentoResource refazer sem usuário deve redirecionar para login")
    void treinamento_refazer_semUsuario() {
        TreinamentoResource resource = treinamentoResource();
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn(" ");

        Response response = resource.refazerTrilha("token");

        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation()).isEqualTo(URI.create("/login"));
    }

    @Test
    @DisplayName("TreinamentoResource refazer deve zerar gamificação e reiniciar trilha")
    void treinamento_refazer_comUsuario() {
        TreinamentoResource resource = treinamentoResource();
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn("c123456");

        Response response = resource.refazerTrilha("token");

        assertThat(response.getStatus()).isEqualTo(Response.Status.SEE_OTHER.getStatusCode());
        assertThat(response.getLocation()).isEqualTo(URI.create("/treinamento"));
        verify(gamificacaoService).zerarGamificacaoDoUsuario("c123456");
        verify(treinamentoService).reiniciarTrilha("c123456", TreinamentoService.CODIGO_ABERTURA_CONTA);
    }

    @Test
    @DisplayName("DesafioResource GET sem usuário deve redirecionar")
    void desafio_get_semUsuario() {
        DesafioResource resource = desafioResource();
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn(null);

        Object result = resource.desafio("token");

        assertThat(result).isInstanceOf(Response.class);
        assertThat(((Response) result).getLocation()).isEqualTo(URI.create("/login"));
    }

    @Test
    @DisplayName("DesafioResource GET com usuário deve renderizar desafio")
    void desafio_get_comUsuario() {
        DesafioResource resource = desafioResource();
        Desafio desafio = new Desafio("d1", "Título", "Descrição", List.of("A", "B"), "A");
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn("c123456");
        when(treinamentoService.desafioEstaDesbloqueado("c123456", TreinamentoService.CODIGO_ABERTURA_CONTA)).thenReturn(true);
        when(desafioService.buscarDesafioAberturaConta()).thenReturn(desafio);
        prepararDesafioTemplate();

        Object result = resource.desafio("token");

        assertThat(result).isSameAs(desafioInstance);
        verify(desafioTemplate).data("desafio", desafio);
        verify(desafioInstance).data("desbloqueado", true);
    }

    @Test
    @DisplayName("DesafioResource POST sem usuário deve redirecionar")
    void desafio_post_semUsuario() {
        DesafioResource resource = desafioResource();
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn(null);

        Object result = resource.responder("token", "A");

        assertThat(result).isInstanceOf(Response.class);
        assertThat(((Response) result).getLocation()).isEqualTo(URI.create("/login"));
    }

    @Test
    @DisplayName("DesafioResource POST bloqueado deve renderizar sem registrar resposta")
    void desafio_post_bloqueado() {
        DesafioResource resource = desafioResource();
        Desafio desafio = new Desafio("d1", "Título", "Descrição", List.of("A", "B"), "A");
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn("c123456");
        when(treinamentoService.desafioEstaDesbloqueado("c123456", TreinamentoService.CODIGO_ABERTURA_CONTA)).thenReturn(false);
        when(desafioService.buscarDesafioAberturaConta()).thenReturn(desafio);
        prepararDesafioTemplate();

        Object result = resource.responder("token", "A");

        assertThat(result).isSameAs(desafioInstance);
        verify(desafioInstance).data("desbloqueado", false);
        verify(gamificacaoService, never()).registrarDesafioRespondido(anyString(), anyBoolean(), anyInt());
    }

    @Test
    @DisplayName("DesafioResource POST desbloqueado deve validar e registrar resposta")
    void desafio_post_desbloqueado() {
        DesafioResource resource = desafioResource();
        ResultadoDesafio resultado = new ResultadoDesafio(true, 100, "Parabéns");
        Desafio desafio = new Desafio("d1", "Título", "Descrição", List.of("A", "B"), "A");
        when(usuarioLogadoService.matriculaOuNulo("token")).thenReturn("c123456");
        when(treinamentoService.desafioEstaDesbloqueado("c123456", TreinamentoService.CODIGO_ABERTURA_CONTA)).thenReturn(true);
        when(desafioService.validarRespostaAberturaConta("A")).thenReturn(resultado);
        when(desafioService.buscarDesafioAberturaConta()).thenReturn(desafio);
        prepararDesafioTemplate();

        Object result = resource.responder("token", "A");

        assertThat(result).isSameAs(desafioInstance);
        verify(gamificacaoService).registrarDesafioRespondido("c123456", true, 100);
        verify(desafioInstance).data("resultado", resultado);
        verify(desafioInstance).data("desbloqueado", true);
    }

    private AuthResource authResource() {
        AuthResource resource = new AuthResource();
        resource.login = login;
        resource.cadastro = cadastro;
        resource.authService = authService;
        resource.gamificacaoService = gamificacaoService;
        return resource;
    }

    private HomeResource homeResource() {
        HomeResource resource = new HomeResource();
        resource.home = home;
        resource.gamificacaoService = gamificacaoService;
        resource.usuarioLogadoService = usuarioLogadoService;
        return resource;
    }

    private DashboardResource dashboardResource() {
        DashboardResource resource = new DashboardResource();
        resource.dashboard = dashboard;
        resource.dashboardService = dashboardService;
        resource.gamificacaoService = gamificacaoService;
        resource.usuarioLogadoService = usuarioLogadoService;
        return resource;
    }

    private TreinamentoResource treinamentoResource() {
        TreinamentoResource resource = new TreinamentoResource();
        resource.treinamento = treinamento;
        resource.treinamentoService = treinamentoService;
        resource.gamificacaoService = gamificacaoService;
        resource.usuarioLogadoService = usuarioLogadoService;
        return resource;
    }

    private DesafioResource desafioResource() {
        DesafioResource resource = new DesafioResource();
        resource.desafio = desafioTemplate;
        resource.desafioService = desafioService;
        resource.treinamentoService = treinamentoService;
        resource.gamificacaoService = gamificacaoService;
        resource.usuarioLogadoService = usuarioLogadoService;
        return resource;
    }

    private void prepararLoginTemplate() {
        when(login.data(eq("erro"), any())).thenReturn(loginInstance);
        when(loginInstance.data(eq("sucesso"), any())).thenReturn(loginInstance);
    }

    private void prepararDesafioTemplate() {
        when(desafioTemplate.data(eq("desafio"), nullable(Object.class))).thenReturn(desafioInstance);
        when(desafioInstance.data(eq("resultado"), nullable(Object.class))).thenReturn(desafioInstance);
        when(desafioInstance.data(eq("desbloqueado"), nullable(Object.class))).thenReturn(desafioInstance);
        when(desafioInstance.data(eq("treinamentoTitulo"), nullable(Object.class))).thenReturn(desafioInstance);
    }

    private Usuario usuarioExemplo() {
        Usuario usuario = new Usuario();
        usuario.nome = "João Silva";
        usuario.matricula = "c123456";
        usuario.idade = 30;
        usuario.senhaHash = "hash";
        return usuario;
    }
}
