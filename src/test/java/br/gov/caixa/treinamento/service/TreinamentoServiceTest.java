package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.dto.EtapaTreinamentoDTO;
import br.gov.caixa.treinamento.dto.TrilhaTreinamentoDTO;
import br.gov.caixa.treinamento.model.ProgressoTreinamentoUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.repository.ProgressoTreinamentoUsuarioRepository;
import br.gov.caixa.treinamento.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TreinamentoService - Testes unitários")
class TreinamentoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProgressoTreinamentoUsuarioRepository progressoRepository;

    @InjectMocks
    private TreinamentoService treinamentoService;

    private Usuario usuarioExistente;
    private ProgressoTreinamentoUsuario progressoExistente;

    @BeforeEach
    void setup() {
        usuarioExistente = new Usuario();
        usuarioExistente.id = 1L;
        usuarioExistente.nome = "João Silva";
        usuarioExistente.matricula = "c123456";

        progressoExistente = new ProgressoTreinamentoUsuario();
        progressoExistente.id = 1L;
        progressoExistente.usuario = usuarioExistente;
        progressoExistente.codigoTreinamento = TreinamentoService.CODIGO_ABERTURA_CONTA;
        progressoExistente.tituloTreinamento = "Abertura de Conta Bancária";
        progressoExistente.etapaAtual = 0;
        progressoExistente.totalEtapas = 8;
        progressoExistente.progressoPercentual = 0;
        progressoExistente.concluido = false;
        progressoExistente.desafioDesbloqueado = false;
        progressoExistente.desafioRespondido = false;
        progressoExistente.dataInicio = LocalDateTime.now();
    }

    // ==================== TESTES DE BUSCAR TRILHA ====================

    @Test
    @DisplayName("Deve retornar trilha de abertura de conta")
    void testBuscarTrilhaAberturaConta() {
        TrilhaTreinamentoDTO trilha = treinamentoService.buscarTrilhaAberturaConta();

        assertThat(trilha)
                .isNotNull()
                .extracting(TrilhaTreinamentoDTO::getCodigo, TrilhaTreinamentoDTO::getTitulo)
                .containsExactly(TreinamentoService.CODIGO_ABERTURA_CONTA, "Abertura de Conta Bancária");
    }

    @Test
    @DisplayName("Deve conter 8 etapas na trilha")
    void testTrilhaContem8Etapas() {
        TrilhaTreinamentoDTO trilha = treinamentoService.buscarTrilhaAberturaConta();

        assertThat(trilha.etapas).hasSize(8);
    }

    @Test
    @DisplayName("Deve conter descrição da trilha")
    void testTrilhaTemDescricao() {
        TrilhaTreinamentoDTO trilha = treinamentoService.buscarTrilhaAberturaConta();

        assertThat(trilha.descricao)
                .isNotBlank()
                .contains("acessibilidade");
    }

    @Test
    @DisplayName("Deve conter público alvo da trilha")
    void testTrilhaTemPublicoAlvo() {
        TrilhaTreinamentoDTO trilha = treinamentoService.buscarTrilhaAberturaConta();

        assertThat(trilha.publicoAlvo)
                .isNotBlank()
                .contains("PCD");
    }

    @Test
    @DisplayName("Deve conter duração da trilha")
    void testTrilhaTemDuracao() {
        TrilhaTreinamentoDTO trilha = treinamentoService.buscarTrilhaAberturaConta();

        assertThat(trilha.duracao).isGreaterThan(0);
    }

    @Test
    @DisplayName("Primeira etapa deve ser identificar tipo de conta")
    void testPrimeiraEtapa() {
        TrilhaTreinamentoDTO trilha = treinamentoService.buscarTrilhaAberturaConta();

        EtapaTreinamentoDTO primeira = trilha.etapas.get(0);
        assertThat(primeira.titulo).contains("tipo de conta");
    }

    @Test
    @DisplayName("Última etapa deve ser confirmação")
    void testUltimaEtapa() {
        TrilhaTreinamentoDTO trilha = treinamentoService.buscarTrilhaAberturaConta();

        EtapaTreinamentoDTO ultima = trilha.etapas.get(7);
        assertThat(ultima.titulo).contains("Confirmar");
    }

    // ==================== TESTES DE INICIAR PROGRESSO ====================

    @Test
    @DisplayName("Deve criar novo progresso para usuário novo")
    void testIniciarProgressoUsuarioNovo() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.empty());

        ProgressoTreinamentoUsuario progresso = treinamentoService.iniciarOuBuscarProgresso(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso)
                .isNotNull()
                .extracting("etapaAtual", "progressoPercentual", "concluido", "desafioDesbloqueado")
                .containsExactly(0, 0, false, false);
    }

    @Test
    @DisplayName("Deve retornar progresso existente")
    void testIniciarProgressoExistente() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.of(progressoExistente));

        ProgressoTreinamentoUsuario progresso = treinamentoService.iniciarOuBuscarProgresso(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso).isNotNull();
        verify(progressoRepository, never()).persist(any(ProgressoTreinamentoUsuario.class));
    }

    @Test
    @DisplayName("Deve falhar ao iniciar progresso para usuário inexistente")
    void testIniciarProgressoUsuarioInexistente() {
        when(usuarioRepository.buscarPorMatricula("inexistente"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> treinamentoService.iniciarOuBuscarProgresso(
                "inexistente",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Novo progresso deve ter 8 etapas")
    void testNovoProgressoTem8Etapas() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.empty());

        ProgressoTreinamentoUsuario progresso = treinamentoService.iniciarOuBuscarProgresso(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso.totalEtapas).isEqualTo(8);
    }

    @Test
    @DisplayName("Novo progresso deve ter data de início")
    void testNovoProgressoTemDataInicio() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.empty());

        ProgressoTreinamentoUsuario progresso = treinamentoService.iniciarOuBuscarProgresso(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso.dataInicio).isNotNull();
    }

    // ==================== TESTES DE CONCLUIR ETAPA ====================

    @Test
    @DisplayName("Deve incrementar etapa ao concluir")
    void testConcluirProximaEtapa() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.of(progressoExistente));

        ProgressoTreinamentoUsuario progresso = treinamentoService.concluirProximaEtapa(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso.etapaAtual).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve calcular progresso corretamente após concluir etapa")
    void testProgressoAposConcluirEtapa() {
        progressoExistente.etapaAtual = 0;
        progressoExistente.totalEtapas = 8;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.of(progressoExistente));

        ProgressoTreinamentoUsuario progresso = treinamentoService.concluirProximaEtapa(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        // Após concluir primeira etapa: (1/8)*100 = 12%
        assertThat(progresso.progressoPercentual).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve marcar como concluído ao terminar última etapa")
    void testMarcarComoConcluido() {
        progressoExistente.etapaAtual = 7;  // Penúltima etapa
        progressoExistente.totalEtapas = 8;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.of(progressoExistente));

        ProgressoTreinamentoUsuario progresso = treinamentoService.concluirProximaEtapa(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso.concluido).isTrue();
        assertThat(progresso.progressoPercentual).isEqualTo(100);
    }

    @Test
    @DisplayName("Deve desbloquear desafio ao completar trilha")
    void testDesbloquearDesafio() {
        progressoExistente.etapaAtual = 7;
        progressoExistente.totalEtapas = 8;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.of(progressoExistente));

        ProgressoTreinamentoUsuario progresso = treinamentoService.concluirProximaEtapa(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso.desafioDesbloqueado).isTrue();
    }

    @Test
    @DisplayName("Deve ter data de conclusão ao completar")
    void testDataConclusao() {
        progressoExistente.etapaAtual = 7;
        progressoExistente.totalEtapas = 8;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.of(progressoExistente));

        ProgressoTreinamentoUsuario progresso = treinamentoService.concluirProximaEtapa(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso.dataConclusao).isNotNull();
    }

    @Test
    @DisplayName("Não deve avançar além da última etapa")
    void testNaoAvancarAlemUltimaEtapa() {
        progressoExistente.etapaAtual = 8;
        progressoExistente.concluido = true;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.of(progressoExistente));

        ProgressoTreinamentoUsuario progresso = treinamentoService.concluirProximaEtapa(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso.etapaAtual).isEqualTo(8);
    }

    // ==================== TESTES DE VERIFICAR DESAFIO ====================

    @Test
    @DisplayName("Desafio deve estar desbloqueado para trilha concluída")
    void testDesafioDesbloqueadoParaTrilhaConcluida() {
        progressoExistente.desafioDesbloqueado = true;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.of(progressoExistente));

        boolean desbloqueado = treinamentoService.desafioEstaDesbloqueado(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(desbloqueado).isTrue();
    }

    @Test
    @DisplayName("Desafio não deve estar desbloqueado para trilha em progresso")
    void testDesafioNaoDesbloqueadoParaTrilhaEmProgresso() {
        progressoExistente.desafioDesbloqueado = false;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.of(progressoExistente));

        boolean desbloqueado = treinamentoService.desafioEstaDesbloqueado(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(desbloqueado).isFalse();
    }

    @Test
    @DisplayName("Desafio não deve estar desbloqueado para usuário sem progresso")
    void testDesafioNaoDesbloqueadoSemProgresso() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));
        when(progressoRepository.buscarPorUsuarioECodigo(any(), anyString()))
                .thenReturn(Optional.empty());

        boolean desbloqueado = treinamentoService.desafioEstaDesbloqueado(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(desbloqueado).isFalse();
    }

    @Test
    @DisplayName("Desafio não deve estar desbloqueado para usuário inexistente")
    void testDesafioNaoDesbloqueadoUsuarioInexistente() {
        when(usuarioRepository.buscarPorMatricula("inexistente"))
                .thenReturn(Optional.empty());

        boolean desbloqueado = treinamentoService.desafioEstaDesbloqueado(
                "inexistente",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(desbloqueado).isFalse();
    }

    // ==================== TESTES DE REINICIAR TRILHA ====================

    @Test
    @DisplayName("Deve reiniciar trilha ao zero")
    void testReiniciarTrilha() {
        progressoExistente.etapaAtual = 5;
        progressoExistente.progressoPercentual = 62;
        progressoExistente.concluido = true;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        ProgressoTreinamentoUsuario progresso = treinamentoService.reiniciarTrilha(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso)
                .extracting("etapaAtual", "progressoPercentual", "concluido", "desafioDesbloqueado")
                .containsExactly(0, 0, false, false);
    }

    @Test
    @DisplayName("Deve apagar progresso anterior ao reiniciar")
    void testApagarProgressoAnterior() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        treinamentoService.reiniciarTrilha(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        verify(progressoRepository, times(1)).apagarPorUsuarioECodigo(any(), anyString());
    }

    @Test
    @DisplayName("Deve definir nova data de início ao reiniciar")
    void testNovaDataInicio() {
        LocalDateTime dataAnterior = LocalDateTime.now().minusDays(10);
        progressoExistente.dataInicio = dataAnterior;

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        ProgressoTreinamentoUsuario progresso = treinamentoService.reiniciarTrilha(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso.dataInicio).isAfter(dataAnterior);
    }

    @Test
    @DisplayName("Deve limpar data de conclusão ao reiniciar")
    void testLimparDataConclusao() {
        progressoExistente.dataConclusao = LocalDateTime.now();

        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        ProgressoTreinamentoUsuario progresso = treinamentoService.reiniciarTrilha(
                "c123456",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        );

        assertThat(progresso.dataConclusao).isNull();
    }

    @Test
    @DisplayName("Deve falhar ao reiniciar para usuário inexistente")
    void testReiniciarUsuarioInexistente() {
        when(usuarioRepository.buscarPorMatricula("inexistente"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> treinamentoService.reiniciarTrilha(
                "inexistente",
                TreinamentoService.CODIGO_ABERTURA_CONTA
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuário não encontrado");
    }
}

