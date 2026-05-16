package br.gov.caixa.treinamento.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DashboardService - Testes unitários")
class DashboardServiceTest {

    private DashboardService dashboardService;

    @BeforeEach
    void setup() {
        dashboardService = new DashboardService();
    }

    // ==================== TESTES DE BUSCAR DADOS ====================

    @Test
    @DisplayName("Deve retornar dados do dashboard com usuário")
    void testBuscarDadosComUsuario() {
        Map<String, Object> resumoUsuario = Map.of(
                "nome", "João Silva",
                "matricula", "c123456",
                "pontuacaoTotal", 250,
                "nivel", 2,
                "progressoPercentual", 50,
                "trilhasConcluidas", 1,
                "desafiosRespondidos", 3
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado)
                .containsKeys("nomeUsuario", "pontosUsuario", "nivelUsuario", "ranking", "posicaoUsuario")
                .extracting("nomeUsuario", "pontosUsuario", "nivelUsuario")
                .containsExactly("João Silva", 250, 2);
    }

    @Test
    @DisplayName("Deve retornar dados padrão quando resumo está vazio")
    void testBuscarDadosComResumoParcial() {
        Map<String, Object> resumoUsuario = new HashMap<>();

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado)
                .containsKey("nomeUsuario")
                .extracting("nomeUsuario")
                .isEqualTo("Usuário logado");
    }

    @Test
    @DisplayName("Deve retornar dados padrão quando chamado sem parâmetros")
    void testBuscarDadosSemParametros() {
        Map<String, Object> resultado = dashboardService.buscarDados();

        assertThat(resultado)
                .isNotNull()
                .containsKeys("usuariosSimulados", "trilhasConcluidasGerais", "mediaPontuacao", "ranking");
    }

    @Test
    @DisplayName("Deve calcular corretamente XP para nível 1")
    void testCalcularXPNivel1() {
        Map<String, Object> resumoUsuario = Map.of(
                "nome", "Novo Usuário",
                "matricula", "c000001",
                "pontuacaoTotal", 0,
                "nivel", 1,
                "progressoPercentual", 0,
                "trilhasConcluidas", 0,
                "desafiosRespondidos", 0
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c000001", resumoUsuario);

        assertThat(resultado)
                .extracting("xpBaseNivel", "xpProximoNivel")
                .containsExactly(0, 100);
    }

    @Test
    @DisplayName("Deve calcular corretamente XP para nível 2")
    void testCalcularXPNivel2() {
        Map<String, Object> resumoUsuario = Map.of(
                "pontuacaoTotal", 150,
                "nivel", 2
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado)
                .extracting("xpBaseNivel", "xpProximoNivel")
                .containsExactly(100, 300);
    }

    @Test
    @DisplayName("Deve calcular corretamente XP para nível 3")
    void testCalcularXPNivel3() {
        Map<String, Object> resumoUsuario = Map.of(
                "pontuacaoTotal", 400,
                "nivel", 3
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado)
                .extracting("xpBaseNivel", "xpProximoNivel")
                .containsExactly(300, 600);
    }

    @Test
    @DisplayName("Deve calcular corretamente XP para nível 4")
    void testCalcularXPNivel4() {
        Map<String, Object> resumoUsuario = Map.of(
                "pontuacaoTotal", 700,
                "nivel", 4
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado)
                .extracting("xpBaseNivel", "xpProximoNivel")
                .containsExactly(600, 1000);
    }

    @Test
    @DisplayName("Deve calcular corretamente XP para nível 5")
    void testCalcularXPNivel5() {
        Map<String, Object> resumoUsuario = Map.of(
                "pontuacaoTotal", 1200,
                "nivel", 5
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado)
                .extracting("xpBaseNivel", "xpProximoNivel")
                .containsExactly(1000, 1000);
    }

    @Test
    @DisplayName("Deve calcular progresso de nível corretamente")
    void testCalcularProgressoNivel() {
        Map<String, Object> resumoUsuario = Map.of(
                "pontuacaoTotal", 200,  // Metade do caminho entre 100 e 300
                "nivel", 2
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        int progressoNivel = (Integer) resultado.get("progressoNivel");
        assertThat(progressoNivel).isEqualTo(50);  // 50% de progresso
    }

    @Test
    @DisplayName("Deve calcular XP faltante para próximo nível")
    void testCalcularXPFaltante() {
        Map<String, Object> resumoUsuario = Map.of(
                "pontuacaoTotal", 150,
                "nivel", 2
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        int xpFaltante = (Integer) resultado.get("xpFaltante");
        assertThat(xpFaltante).isEqualTo(150);  // 300 - 150
    }

    @Test
    @DisplayName("Deve retornar XP faltante como zero quando usuário está no topo")
    void testCalcularXPFaltanteNoTopo() {
        Map<String, Object> resumoUsuario = Map.of(
                "pontuacaoTotal", 1500,
                "nivel", 5
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        int xpFaltante = (Integer) resultado.get("xpFaltante");
        assertThat(xpFaltante).isZero();
    }

    // ==================== TESTES DE RANKING ====================

    @Test
    @DisplayName("Deve incluir usuário logado no ranking")
    void testRankingIncluiUsuarioLogado() {
        Map<String, Object> resumoUsuario = Map.of(
                "nome", "João Silva",
                "matricula", "c123456",
                "pontuacaoTotal", 200
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        List<?> ranking = (List<?>) resultado.get("ranking");
        assertThat(ranking)
                .isNotEmpty()
                .anySatisfy(item -> {
                    assertThat((Map<String, Object>) item)
                            .extracting("usuarioLogado")
                            .isEqualTo(true);
                });
    }

    @Test
    @DisplayName("Deve ordenar ranking por pontos decrescentes")
    void testRankingOrdenado() {
        Map<String, Object> resumoUsuario = Map.of(
                "pontuacaoTotal", 100,
                "matricula", "c123456"
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        List<?> ranking = (List<?>) resultado.get("ranking");
        
        // Verificar se está ordenado
        int[] pontos = new int[ranking.size()];
        for (int i = 0; i < ranking.size(); i++) {
            pontos[i] = (Integer) ((Map<String, Object>) ranking.get(i)).get("pontos");
        }
        
        for (int i = 1; i < pontos.length; i++) {
            assertThat(pontos[i]).isLessThanOrEqualTo(pontos[i - 1]);
        }
    }

    @Test
    @DisplayName("Deve calcular posição do usuário no ranking")
    void testCalcularPosicaoUsuario() {
        Map<String, Object> resumoUsuario = Map.of(
                "pontuacaoTotal", 320,  // Segundo maior
                "matricula", "c123456"
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        int posicaoUsuario = (Integer) resultado.get("posicaoUsuario");
        assertThat(posicaoUsuario).isGreaterThan(0);
    }

    // ==================== TESTES DE REGRAS DE PONTUAÇÃO ====================

    @Test
    @DisplayName("Deve retornar regras de pontuação")
    void testRetornarRegrasPontuacao() {
        Map<String, Object> resumoUsuario = new HashMap<>();

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        List<?> regras = (List<?>) resultado.get("regrasPontuacao");
        
        assertThat(regras)
                .isNotEmpty()
                .hasSize(5);
    }

    @Test
    @DisplayName("Deve conter regra de login nas pontuações")
    void testRegrasPontuacaoContemLogin() {
        Map<String, Object> resumoUsuario = new HashMap<>();

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        List<?> regras = (List<?>) resultado.get("regrasPontuacao");
        
        assertThat(regras)
                .anySatisfy(regra -> {
                    assertThat((Map<String, Object>) regra)
                            .extracting("titulo")
                            .asString()
                            .contains("Login");
                });
    }

    // ==================== TESTES DE MEDALHAS ====================

    @Test
    @DisplayName("Deve retornar regras de medalhas")
    void testRetornarRegrasMedalhas() {
        Map<String, Object> resumoUsuario = Map.of("pontuacaoTotal", 0);

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        List<?> medalhas = (List<?>) resultado.get("regrasMedalhas");
        
        assertThat(medalhas).isNotEmpty();
    }

    @Test
    @DisplayName("Deve indicar primeira medalha como não conquistada para novo usuário")
    void testPrimeiraAcessoNaoConquistada() {
        Map<String, Object> resumoUsuario = Map.of(
                "pontuacaoTotal", 0,
                "badges", List.of()
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        List<?> medalhas = (List<?>) resultado.get("regrasMedalhas");
        
        // Primeira medalha deve estar próxima de ser conquistada (0 pontos)
        assertThat(medalhas.get(0))
                .extracting("nome")
                .asString()
                .contains("Primeiro");
    }

    // ==================== TESTES DE MISSÃO RECOMENDADA ====================

    @Test
    @DisplayName("Deve recomendar treinamento para progresso < 100")
    void testMissaoRecomendadaTreinamento() {
        Map<String, Object> resumoUsuario = Map.of(
                "progressoPercentual", 50,
                "desafiosRespondidos", 0
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        Map<String, Object> missao = (Map<String, Object>) resultado.get("missaoRecomendada");
        
        assertThat(missao)
                .extracting("link")
                .isEqualTo("/treinamento");
    }

    @Test
    @DisplayName("Deve recomendar desafio quando treinamento é 100% e desafios = 0")
    void testMissaoRecomendadaDesafio() {
        Map<String, Object> resumoUsuario = Map.of(
                "progressoPercentual", 100,
                "desafiosRespondidos", 0,
                "pontuacaoTotal", 100
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        Map<String, Object> missao = (Map<String, Object>) resultado.get("missaoRecomendada");
        
        assertThat(missao)
                .extracting("link")
                .isEqualTo("/desafio");
    }

    @Test
    @DisplayName("Deve recomendar evolução quando pontuação < 300")
    void testMissaoRecomendadaEvolucao() {
        Map<String, Object> resumoUsuario = Map.of(
                "progressoPercentual", 100,
                "desafiosRespondidos", 3,
                "pontuacaoTotal", 200
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        Map<String, Object> missao = (Map<String, Object>) resultado.get("missaoRecomendada");
        
        assertThat(missao)
                .extracting("titulo")
                .asString()
                .contains("nível 3");
    }

    @Test
    @DisplayName("Deve recomendar compartilhamento quando nível avançado")
    void testMissaoRecomendadaCompartilhamento() {
        Map<String, Object> resumoUsuario = Map.of(
                "progressoPercentual", 100,
                "desafiosRespondidos", 5,
                "pontuacaoTotal", 400
        );

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        Map<String, Object> missao = (Map<String, Object>) resultado.get("missaoRecomendada");
        
        assertThat(missao)
                .extracting("titulo")
                .asString()
                .contains("Compartilhe");
    }

    // ==================== TESTES DE DADOS GERAIS ====================

    @Test
    @DisplayName("Deve incluir estatísticas gerais na dashboard")
    void testIncluiEstatisticasGerais() {
        Map<String, Object> resultado = dashboardService.buscarDados("c123456", new HashMap<>());

        assertThat(resultado)
                .containsKeys("usuariosSimulados", "trilhasConcluidasGerais", "mediaPontuacao", "funcionalidadesAtivas");
    }

    @Test
    @DisplayName("Deve listar funcionalidades mais acessadas")
    void testListarFuncionalidadesAcessadas() {
        Map<String, Object> resultado = dashboardService.buscarDados("c123456", new HashMap<>());

        List<?> funcionalidades = (List<?>) resultado.get("funcionalidadesMaisAcessadas");
        
        assertThat(funcionalidades)
                .isNotEmpty()
                .hasSize(3);
    }

    // ==================== TESTES DE NÍVEIS ====================

    @Test
    @DisplayName("Deve detectar nível 1 para 0 pontos")
    void testCalcularNivel1() {
        Map<String, Object> resumoUsuario = Map.of("pontuacaoTotal", 0);

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado).extracting("nivelUsuario").isEqualTo(1);
    }

    @Test
    @DisplayName("Deve detectar nível 2 para 150 pontos")
    void testCalcularNivel2() {
        Map<String, Object> resumoUsuario = Map.of("pontuacaoTotal", 150);

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado).extracting("nivelUsuario").isEqualTo(2);
    }

    @Test
    @DisplayName("Deve detectar nível 3 para 400 pontos")
    void testCalcularNivel3() {
        Map<String, Object> resumoUsuario = Map.of("pontuacaoTotal", 400);

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado).extracting("nivelUsuario").isEqualTo(3);
    }

    @Test
    @DisplayName("Deve detectar nível 4 para 700 pontos")
    void testCalcularNivel4() {
        Map<String, Object> resumoUsuario = Map.of("pontuacaoTotal", 700);

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado).extracting("nivelUsuario").isEqualTo(4);
    }

    @Test
    @DisplayName("Deve detectar nível 5 para 1200 pontos")
    void testCalcularNivel5() {
        Map<String, Object> resumoUsuario = Map.of("pontuacaoTotal", 1200);

        Map<String, Object> resultado = dashboardService.buscarDados("c123456", resumoUsuario);

        assertThat(resultado).extracting("nivelUsuario").isEqualTo(5);
    }
}

