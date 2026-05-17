package br.gov.caixa.treinamento.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Modelos complementares - Cobertura alta")
class ModelosComplementaresTest {

    @Test
    @DisplayName("AtividadeUsuario deve armazenar os dados da atividade")
    void atividadeUsuario_deveArmazenarDados() {
        Usuario usuario = usuarioExemplo();
        LocalDateTime agora = LocalDateTime.now();

        AtividadeUsuario atividade = new AtividadeUsuario();
        atividade.usuario = usuario;
        atividade.tipoAtividade = "TREINAMENTO_INICIADO";
        atividade.descricao = "Usuário iniciou treinamento";
        atividade.pontosGanhos = 10;
        atividade.dataHora = agora;

        assertThat(atividade.usuario).isSameAs(usuario);
        assertThat(atividade.tipoAtividade).isEqualTo("TREINAMENTO_INICIADO");
        assertThat(atividade.descricao).contains("iniciou");
        assertThat(atividade.pontosGanhos).isEqualTo(10);
        assertThat(atividade.dataHora).isEqualTo(agora);
    }

    @Test
    @DisplayName("BadgeUsuario deve armazenar os dados da conquista")
    void badgeUsuario_deveArmazenarDados() {
        Usuario usuario = usuarioExemplo();
        LocalDateTime conquista = LocalDateTime.now();

        BadgeUsuario badge = new BadgeUsuario();
        badge.usuario = usuario;
        badge.nome = "Primeiro Acesso";
        badge.descricao = "Realizou o primeiro acesso";
        badge.icone = "star";
        badge.dataConquista = conquista;

        assertThat(badge.usuario).isSameAs(usuario);
        assertThat(badge.nome).isEqualTo("Primeiro Acesso");
        assertThat(badge.descricao).contains("primeiro acesso");
        assertThat(badge.icone).isEqualTo("star");
        assertThat(badge.dataConquista).isEqualTo(conquista);
    }

    @Test
    @DisplayName("EtapaTreinamento deve cobrir construtor vazio e completo")
    void etapaTreinamento_deveCobrirConstrutores() {
        EtapaTreinamento vazia = new EtapaTreinamento();
        vazia.id = "manual";

        EtapaTreinamento etapa = new EtapaTreinamento(
                "etapa-1",
                "Dados pessoais",
                "Preencha nome e CPF.",
                "Use contraste alto.",
                1
        );

        assertThat(vazia.id).isEqualTo("manual");
        assertThat(etapa.id).isEqualTo("etapa-1");
        assertThat(etapa.titulo).isEqualTo("Dados pessoais");
        assertThat(etapa.instrucao).contains("CPF");
        assertThat(etapa.dicaAcessibilidade).contains("contraste");
        assertThat(etapa.ordem).isEqualTo(1);
    }

    @Test
    @DisplayName("ModuloAtualizacao deve cobrir construtor vazio e completo")
    void moduloAtualizacao_deveCobrirConstrutores() {
        ModuloAtualizacao vazio = new ModuloAtualizacao();
        vazio.nome = "Módulo manual";

        ModuloAtualizacao modulo = new ModuloAtualizacao(
                "mod-1",
                "Novo fluxo",
                "Resumo da atualização",
                List.of("Ler instrução", "Executar ação")
        );

        assertThat(vazio.nome).isEqualTo("Módulo manual");
        assertThat(modulo.id).isEqualTo("mod-1");
        assertThat(modulo.nome).isEqualTo("Novo fluxo");
        assertThat(modulo.resumo).contains("atualização");
        assertThat(modulo.passos).containsExactly("Ler instrução", "Executar ação");
    }

    @Test
    @DisplayName("ProgressoTreinamentoUsuario deve possuir defaults e permitir atualização")
    void progressoTreinamento_deveCobrirDefaultsEAtualizacao() {
        ProgressoTreinamentoUsuario progresso = new ProgressoTreinamentoUsuario();
        assertThat(progresso.etapaAtual).isZero();
        assertThat(progresso.totalEtapas).isZero();
        assertThat(progresso.progressoPercentual).isZero();
        assertThat(progresso.concluido).isFalse();
        assertThat(progresso.desafioDesbloqueado).isFalse();
        assertThat(progresso.desafioRespondido).isFalse();

        Usuario usuario = usuarioExemplo();
        LocalDateTime inicio = LocalDateTime.now().minusMinutes(10);
        LocalDateTime conclusao = LocalDateTime.now();

        progresso.usuario = usuario;
        progresso.codigoTreinamento = "abertura-conta";
        progresso.tituloTreinamento = "Conta Fácil: Jornada Assistiva PcD";
        progresso.etapaAtual = 5;
        progresso.totalEtapas = 5;
        progresso.progressoPercentual = 100;
        progresso.concluido = true;
        progresso.desafioDesbloqueado = true;
        progresso.desafioRespondido = true;
        progresso.dataInicio = inicio;
        progresso.dataConclusao = conclusao;

        assertThat(progresso.usuario).isSameAs(usuario);
        assertThat(progresso.codigoTreinamento).isEqualTo("abertura-conta");
        assertThat(progresso.tituloTreinamento).contains("Abertura");
        assertThat(progresso.etapaAtual).isEqualTo(5);
        assertThat(progresso.totalEtapas).isEqualTo(5);
        assertThat(progresso.progressoPercentual).isEqualTo(100);
        assertThat(progresso.concluido).isTrue();
        assertThat(progresso.desafioDesbloqueado).isTrue();
        assertThat(progresso.desafioRespondido).isTrue();
        assertThat(progresso.dataInicio).isEqualTo(inicio);
        assertThat(progresso.dataConclusao).isEqualTo(conclusao);
    }

    @Test
    @DisplayName("Usuario deve iniciar gamificação com valores padrão")
    void usuario_deveIniciarGamificacaoComDefaults() {
        Usuario usuario = new Usuario();

        assertThat(usuario.deficiencias).isEmpty();
        assertThat(usuario.pontuacaoTotal).isZero();
        assertThat(usuario.nivel).isEqualTo(1);
        assertThat(usuario.progressoPercentual).isZero();
        assertThat(usuario.trilhasConcluidas).isZero();
        assertThat(usuario.desafiosRespondidos).isZero();
    }

    private Usuario usuarioExemplo() {
        Usuario usuario = new Usuario();
        usuario.nome = "Maria Silva";
        usuario.matricula = "c123456";
        usuario.idade = 30;
        usuario.senhaHash = "hash";
        return usuario;
    }
}
