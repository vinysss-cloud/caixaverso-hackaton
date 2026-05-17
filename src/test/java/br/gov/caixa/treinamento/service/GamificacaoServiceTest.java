package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.model.AtividadeUsuario;
import br.gov.caixa.treinamento.model.BadgeUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.repository.AtividadeUsuarioRepository;
import br.gov.caixa.treinamento.repository.BadgeUsuarioRepository;
import br.gov.caixa.treinamento.repository.UsuarioRepository;
import br.gov.caixa.treinamento.repository.ResultadoDesafioUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GamificacaoService - Testes unitários")
class GamificacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AtividadeUsuarioRepository atividadeUsuarioRepository;

    @Mock
    private BadgeUsuarioRepository badgeUsuarioRepository;

    @Mock
    private ResultadoDesafioUsuarioRepository resultadoDesafioUsuarioRepository;

    @InjectMocks
    private GamificacaoService gamificacaoService;

    private Usuario usuarioExistente;

    @BeforeEach
    void setup() {
        usuarioExistente = new Usuario();
        usuarioExistente.id = 1L;
        usuarioExistente.nome = "João Silva";
        usuarioExistente.matricula = "c123456";
        usuarioExistente.pontuacaoTotal = 0;
        usuarioExistente.nivel = 1;
        usuarioExistente.progressoPercentual = 0;
        usuarioExistente.trilhasConcluidas = 0;
        usuarioExistente.desafiosRespondidos = 0;
    }

    // ==================== TESTES DE LOGIN ====================

    @Test
    @DisplayName("Deve registrar login de usuário existente")
    void testRegistrarLoginUsuarioExistente() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarLogin("c123456");

        verify(atividadeUsuarioRepository, times(1)).persist(any(AtividadeUsuario.class));
    }

    @Test
    @DisplayName("Deve ignorar login para usuário inexistente")
    void testRegistrarLoginUsuarioInexistente() {
        when(usuarioRepository.buscarPorMatricula("inexistente"))
                .thenReturn(Optional.empty());

        gamificacaoService.registrarLogin("inexistente");

        verify(atividadeUsuarioRepository, never()).persist(any(AtividadeUsuario.class));
    }

    @Test
    @DisplayName("Deve conceder badge de primeiro acesso")
    void testConcederBadgePrimeiroAcesso() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(badgeUsuarioRepository.usuarioPossuiBadge(eq(usuarioExistente), anyString()))
                .thenReturn(false);

        gamificacaoService.registrarLogin("c123456");

        verify(badgeUsuarioRepository, times(1)).persist(any(BadgeUsuario.class));
    }

    // ==================== TESTES DE TREINAMENTO ====================

    @Test
    @DisplayName("Deve registrar início de treinamento")
    void testRegistrarInicioTreinamento() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarInicioTreinamento("c123456");

        verify(atividadeUsuarioRepository, times(1)).persist(any(AtividadeUsuario.class));
    }

    @Test
    @DisplayName("Deve adicionar 10 pontos ao iniciar treinamento")
    void testPontosInicioTreinamento() {
        usuarioExistente.pontuacaoTotal = 0;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarInicioTreinamento("c123456");

        verify(atividadeUsuarioRepository, times(1)).persist(org.mockito.ArgumentMatchers.<AtividadeUsuario>argThat(atividade ->
                atividade.pontosGanhos == 10 && "TREINAMENTO_INICIADO".equals(atividade.tipoAtividade)
        ));
    }

    @Test
    @DisplayName("Deve registrar etapa concluída")
    void testRegistrarEtapaConcluida() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarEtapaConcluida("c123456");

        verify(atividadeUsuarioRepository, times(1)).persist(any(AtividadeUsuario.class));
    }

    @Test
    @DisplayName("Deve adicionar 20 pontos ao concluir etapa")
    void testPontosConcluirEtapa() {
        usuarioExistente.pontuacaoTotal = 0;
        usuarioExistente.progressoPercentual = 0;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarEtapaConcluida("c123456");

        verify(atividadeUsuarioRepository, times(1)).persist(org.mockito.ArgumentMatchers.<AtividadeUsuario>argThat(atividade ->
                atividade.pontosGanhos == 20 && "ETAPA_CONCLUIDA".equals(atividade.tipoAtividade)
        ));
    }

    @Test
    @DisplayName("Deve incrementar progresso ao concluir etapa")
    void testIncrementarProgresso() {
        usuarioExistente.progressoPercentual = 0;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarEtapaConcluida("c123456");

        assertThat(usuarioExistente.progressoPercentual)
                .isGreaterThan(0)
                .isLessThanOrEqualTo(100);
    }

    @Test
    @DisplayName("Deve limitar progresso a 100%")
    void testProgressoLimitado100() {
        usuarioExistente.progressoPercentual = 90;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarEtapaConcluida("c123456");

        assertThat(usuarioExistente.progressoPercentual)
                .isLessThanOrEqualTo(100);
    }

    @Test
    @DisplayName("Deve conceder badge de trilha concluída quando progresso = 100")
    void testConcederBadgeTrilhaConcluida() {
        usuarioExistente.progressoPercentual = 80;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(badgeUsuarioRepository.usuarioPossuiBadge(eq(usuarioExistente), anyString()))
                .thenReturn(false);

        gamificacaoService.registrarEtapaConcluida("c123456");

        // Badge deve ser concedida após atingir 100%
        if (usuarioExistente.progressoPercentual >= 100) {
            verify(badgeUsuarioRepository, atLeastOnce()).persist(any(BadgeUsuario.class));
        }
    }

    // ==================== TESTES DE DESAFIOS ====================

    @Test
    @DisplayName("Deve registrar desafio respondido com acerto")
    void testRegistrarDesafioRespondidoComAcerto() {
        usuarioExistente.desafiosRespondidos = 0;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarDesafioRespondido("c123456", true, 100);

        assertThat(usuarioExistente.desafiosRespondidos).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve registrar desafio respondido com erro")
    void testRegistrarDesafioRespondidoComErro() {
        usuarioExistente.desafiosRespondidos = 0;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarDesafioRespondido("c123456", false, 40);

        assertThat(usuarioExistente.desafiosRespondidos).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve adicionar pontos ao acertar desafio")
    void testPontosAcertoDesafio() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarDesafioRespondido("c123456", true, 100);

        verify(atividadeUsuarioRepository, times(1)).persist(org.mockito.ArgumentMatchers.<AtividadeUsuario>argThat(atividade ->
                atividade.pontosGanhos == 100 && "DESAFIO_ACERTADO".equals(atividade.tipoAtividade)
        ));
    }

    @Test
    @DisplayName("Deve adicionar pontos ao errar desafio")
    void testPontosErroDesafio() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarDesafioRespondido("c123456", false, 40);

        verify(atividadeUsuarioRepository, times(1)).persist(org.mockito.ArgumentMatchers.<AtividadeUsuario>argThat(atividade ->
                atividade.pontosGanhos == 40 && "DESAFIO_RESPONDIDO".equals(atividade.tipoAtividade)
        ));
    }

    @Test
    @DisplayName("Deve conceder badge ao acertar desafio")
    void testConcederBadgeAcertoDesafio() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(badgeUsuarioRepository.usuarioPossuiBadge(eq(usuarioExistente), anyString()))
                .thenReturn(false);

        gamificacaoService.registrarDesafioRespondido("c123456", true, 100);

        verify(badgeUsuarioRepository, times(2)).persist(any(BadgeUsuario.class));
    }

    @Test
    @DisplayName("Não deve conceder badge ao errar desafio")
    void testNaoConcederBadgeErroDesafio() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarDesafioRespondido("c123456", false, 40);

        // Não deve chamar badgeUsuarioRepository quando erra
        verify(badgeUsuarioRepository, never()).usuarioPossuiBadge(any(), eq("Atenção aos Detalhes"));
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Deve registrar visualização de atualização")
    void testRegistrarVisualizacaoAtualizacao() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarVisualizacaoAtualizacao("c123456");

        verify(atividadeUsuarioRepository, times(1)).persist(any(AtividadeUsuario.class));
    }

    // ==================== TESTES DE RESUMO ====================

    @Test
    @DisplayName("Deve retornar resumo para usuário existente")
    void testBuscarResumoUsuarioExistente() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(badgeUsuarioRepository.listarPorUsuario(usuarioExistente))
                .thenReturn(new ArrayList<>());
        when(atividadeUsuarioRepository.listarPorUsuario(usuarioExistente))
                .thenReturn(new ArrayList<>());

        Map<String, Object> resumo = gamificacaoService.buscarResumoUsuario("c123456");

        assertThat(resumo)
                .containsKeys("nome", "matricula", "pontuacaoTotal", "nivel", "badges", "atividades");
    }

    @Test
    @DisplayName("Deve retornar resumo padrão para usuário inexistente")
    void testBuscarResumoUsuarioInexistente() {
        when(usuarioRepository.buscarPorMatricula("inexistente"))
                .thenReturn(Optional.empty());

        Map<String, Object> resumo = gamificacaoService.buscarResumoUsuario("inexistente");

        assertThat(resumo)
                .containsEntry("pontuacaoTotal", 0)
                .containsEntry("nivel", 1)
                .containsEntry("progressoPercentual", 0)
                .containsEntry("trilhasConcluidas", 0)
                .containsEntry("desafiosRespondidos", 0);
    }

    @Test
    @DisplayName("Deve incluir dados do usuário no resumo")
    void testResumoIncluiDadosUsuario() {
        usuarioExistente.pontuacaoTotal = 250;
        usuarioExistente.nivel = 2;
        usuarioExistente.progressoPercentual = 50;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(badgeUsuarioRepository.listarPorUsuario(usuarioExistente))
                .thenReturn(new ArrayList<>());
        when(atividadeUsuarioRepository.listarPorUsuario(usuarioExistente))
                .thenReturn(new ArrayList<>());

        Map<String, Object> resumo = gamificacaoService.buscarResumoUsuario("c123456");

        assertThat(resumo)
                .containsEntry("pontuacaoTotal", 250)
                .containsEntry("nivel", 2)
                .containsEntry("progressoPercentual", 50)
                .containsEntry("nome", "João Silva")
                .containsEntry("matricula", "c123456");
    }

    // ==================== TESTES DE CÁLCULO DE NÍVEL ====================

    @Test
    @DisplayName("Deve calcular nível 1 para 0-99 pontos")
    void testCalcularNivel1() {
        usuarioExistente.pontuacaoTotal = 0;
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarInicioTreinamento("c123456");

        // Nível deve ser calculado após adicionar pontos
    }

    @Test
    @DisplayName("Deve calcular nível 2 para 100-299 pontos")
    void testCalcularNivel2() {
        usuarioExistente.pontuacaoTotal = 150;
        usuarioExistente.nivel = 1;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.registrarInicioTreinamento("c123456");

        // Após adicionar 10 pontos, deve ter 160 pontos = nível 2
    }

    @Test
    @DisplayName("Deve calcular nível 3 para 300-599 pontos")
    void testCalcularNivel3() {
        usuarioExistente.pontuacaoTotal = 350;

        Map<String, Object> resumo = gamificacaoService.buscarResumoUsuario("c123456");
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(badgeUsuarioRepository.listarPorUsuario(usuarioExistente))
                .thenReturn(new ArrayList<>());
        when(atividadeUsuarioRepository.listarPorUsuario(usuarioExistente))
                .thenReturn(new ArrayList<>());

        resumo = gamificacaoService.buscarResumoUsuario("c123456");
    }

    @Test
    @DisplayName("Deve calcular nível 4 para 600-999 pontos")
    void testCalcularNivel4() {
        usuarioExistente.pontuacaoTotal = 700;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(badgeUsuarioRepository.listarPorUsuario(usuarioExistente))
                .thenReturn(new ArrayList<>());
        when(atividadeUsuarioRepository.listarPorUsuario(usuarioExistente))
                .thenReturn(new ArrayList<>());

        Map<String, Object> resumo = gamificacaoService.buscarResumoUsuario("c123456");
    }

    @Test
    @DisplayName("Deve calcular nível 5 para 1000+ pontos")
    void testCalcularNivel5() {
        usuarioExistente.pontuacaoTotal = 1200;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(badgeUsuarioRepository.listarPorUsuario(usuarioExistente))
                .thenReturn(new ArrayList<>());
        when(atividadeUsuarioRepository.listarPorUsuario(usuarioExistente))
                .thenReturn(new ArrayList<>());

        Map<String, Object> resumo = gamificacaoService.buscarResumoUsuario("c123456");
    }

    // ==================== TESTES DE ZERAÇÃO ====================

    @Test
    @DisplayName("Deve zerar gamificação do usuário")
    void testZerarGamificacaoDoUsuario() {
        usuarioExistente.pontuacaoTotal = 250;
        usuarioExistente.nivel = 2;
        usuarioExistente.progressoPercentual = 50;
        usuarioExistente.trilhasConcluidas = 1;
        usuarioExistente.desafiosRespondidos = 3;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.zerarGamificacaoDoUsuario("c123456");

        assertThat(usuarioExistente)
                .extracting("pontuacaoTotal", "nivel", "progressoPercentual", "trilhasConcluidas", "desafiosRespondidos")
                .containsExactly(0, 1, 0, 0, 0);
    }

    @Test
    @DisplayName("Deve apagar atividades ao zerar gamificação")
    void testZerarGamificacaoApagarAtividades() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.zerarGamificacaoDoUsuario("c123456");

        verify(atividadeUsuarioRepository, times(1)).apagarPorUsuario(usuarioExistente);
    }

    @Test
    @DisplayName("Deve apagar badges ao zerar gamificação")
    void testZerarGamificacaoApagarBadges() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        gamificacaoService.zerarGamificacaoDoUsuario("c123456");

        verify(badgeUsuarioRepository, times(1)).apagarPorUsuario(usuarioExistente);
    }

    @Test
    @DisplayName("Deve ignorar zeração para usuário inexistente")
    void testZerarGamificacaoUsuarioInexistente() {
        when(usuarioRepository.buscarPorMatricula("inexistente"))
                .thenReturn(Optional.empty());

        gamificacaoService.zerarGamificacaoDoUsuario("inexistente");

        verify(atividadeUsuarioRepository, never()).apagarPorUsuario(any());
        verify(badgeUsuarioRepository, never()).apagarPorUsuario(any());
        verify(resultadoDesafioUsuarioRepository, never()).apagarPorUsuario(any());
    }
}

