package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.model.AtividadeUsuario;
import br.gov.caixa.treinamento.model.BadgeUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.repository.AtividadeUsuarioRepository;
import br.gov.caixa.treinamento.repository.BadgeUsuarioRepository;
import br.gov.caixa.treinamento.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class GamificacaoService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    AtividadeUsuarioRepository atividadeUsuarioRepository;

    @Inject
    BadgeUsuarioRepository badgeUsuarioRepository;

    @Transactional
    public void registrarLogin(String matricula) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorMatricula(matricula);
        if (usuarioOpt.isEmpty()) {
            return;
        }

        Usuario usuario = usuarioOpt.get();
        adicionarPontos(usuario, 5, "LOGIN", "Login realizado na plataforma.");

        concederBadgeSeNaoPossuir(
                usuario,
                "Primeiro Acesso",
                "Realizou o primeiro acesso à plataforma.",
                "🔐"
        );
    }

    @Transactional
    public void registrarInicioTreinamento(String matricula) {
        usuarioRepository.buscarPorMatricula(matricula).ifPresent(usuario ->
                adicionarPontos(usuario, 10, "TREINAMENTO_INICIADO", "Iniciou o treinamento guiado.")
        );
    }

    @Transactional
    public void registrarEtapaConcluida(String matricula) {
        usuarioRepository.buscarPorMatricula(matricula).ifPresent(usuario -> {
            adicionarPontos(usuario, 20, "ETAPA_CONCLUIDA", "Concluiu uma etapa do treinamento.");
            usuario.progressoPercentual = Math.min(100, usuario.progressoPercentual + 20);

            if (usuario.progressoPercentual >= 100) {
                usuario.trilhasConcluidas = usuario.trilhasConcluidas + 1;
                concederBadgeSeNaoPossuir(
                        usuario,
                        "Trilha Concluída",
                        "Concluiu uma trilha de treinamento.",
                        "🏁"
                );
            }
        });
    }

    @Transactional
    public void registrarDesafioRespondido(String matricula, boolean acertou, int pontos) {
        usuarioRepository.buscarPorMatricula(matricula).ifPresent(usuario -> {
            usuario.desafiosRespondidos = usuario.desafiosRespondidos + 1;

            String tipo = acertou ? "DESAFIO_ACERTADO" : "DESAFIO_RESPONDIDO";
            String descricao = acertou
                    ? "Acertou um desafio prático."
                    : "Respondeu um desafio prático.";

            adicionarPontos(usuario, pontos, tipo, descricao);

            if (acertou) {
                concederBadgeSeNaoPossuir(
                        usuario,
                        "Atenção aos Detalhes",
                        "Acertou um desafio de identificação.",
                        "🎯"
                );
            }
        });
    }

    @Transactional
    public void registrarVisualizacaoAtualizacao(String matricula) {
        usuarioRepository.buscarPorMatricula(matricula).ifPresent(usuario ->
                adicionarPontos(usuario, 15, "MODULO_ATUALIZACAO_VISUALIZADO", "Visualizou uma nova funcionalidade.")
        );
    }

    public Map<String, Object> buscarResumoUsuario(String matricula) {
        Map<String, Object> resumo = new HashMap<>();

        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorMatricula(matricula);

        if (usuarioOpt.isEmpty()) {
            resumo.put("pontuacaoTotal", 0);
            resumo.put("nivel", 1);
            resumo.put("progressoPercentual", 0);
            resumo.put("trilhasConcluidas", 0);
            resumo.put("desafiosRespondidos", 0);
            return resumo;
        }

        Usuario usuario = usuarioOpt.get();

        resumo.put("nome", usuario.nome);
        resumo.put("matricula", usuario.matricula);
        resumo.put("pontuacaoTotal", usuario.pontuacaoTotal);
        resumo.put("nivel", usuario.nivel);
        resumo.put("progressoPercentual", usuario.progressoPercentual);
        resumo.put("trilhasConcluidas", usuario.trilhasConcluidas);
        resumo.put("desafiosRespondidos", usuario.desafiosRespondidos);
        resumo.put("badges", badgeUsuarioRepository.listarPorUsuario(usuario));
        resumo.put("atividades", atividadeUsuarioRepository.listarPorUsuario(usuario));

        return resumo;
    }

    private void adicionarPontos(Usuario usuario, int pontos, String tipoAtividade, String descricao) {
        normalizarCamposGamificacao(usuario);

        usuario.pontuacaoTotal = usuario.pontuacaoTotal + pontos;
        usuario.nivel = calcularNivel(usuario.pontuacaoTotal);

        AtividadeUsuario atividade = new AtividadeUsuario();
        atividade.usuario = usuario;
        atividade.tipoAtividade = tipoAtividade;
        atividade.descricao = descricao;
        atividade.pontosGanhos = pontos;
        atividade.dataHora = LocalDateTime.now();

        atividadeUsuarioRepository.persist(atividade);

        if (usuario.pontuacaoTotal >= 100) {
            concederBadgeSeNaoPossuir(
                    usuario,
                    "Explorador de Funcionalidades",
                    "Alcançou 100 pontos na plataforma.",
                    "⭐"
            );
        }
    }

    private int calcularNivel(int pontos) {
        if (pontos >= 1000) {
            return 5;
        }
        if (pontos >= 600) {
            return 4;
        }
        if (pontos >= 300) {
            return 3;
        }
        if (pontos >= 100) {
            return 2;
        }
        return 1;
    }

    private void concederBadgeSeNaoPossuir(Usuario usuario, String nome, String descricao, String icone) {
        if (badgeUsuarioRepository.usuarioPossuiBadge(usuario, nome)) {
            return;
        }

        BadgeUsuario badge = new BadgeUsuario();
        badge.usuario = usuario;
        badge.nome = nome;
        badge.descricao = descricao;
        badge.icone = icone;
        badge.dataConquista = LocalDateTime.now();

        badgeUsuarioRepository.persist(badge);
    }

    private void normalizarCamposGamificacao(Usuario usuario) {
        if (usuario.pontuacaoTotal == null) {
            usuario.pontuacaoTotal = 0;
        }

        if (usuario.nivel == null) {
            usuario.nivel = 1;
        }

        if (usuario.progressoPercentual == null) {
            usuario.progressoPercentual = 0;
        }

        if (usuario.trilhasConcluidas == null) {
            usuario.trilhasConcluidas = 0;
        }

        if (usuario.desafiosRespondidos == null) {
            usuario.desafiosRespondidos = 0;
        }
    }

    @Transactional
    public void zerarGamificacaoDoUsuario(String matricula) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorMatricula(matricula);

        if (usuarioOpt.isEmpty()) {
            return;
        }

        Usuario usuario = usuarioOpt.get();

        atividadeUsuarioRepository.apagarPorUsuario(usuario);
        badgeUsuarioRepository.apagarPorUsuario(usuario);

        usuario.pontuacaoTotal = 0;
        usuario.nivel = 1;
        usuario.progressoPercentual = 0;
        usuario.trilhasConcluidas = 0;
        usuario.desafiosRespondidos = 0;
    }
}

