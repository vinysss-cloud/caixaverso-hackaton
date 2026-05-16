package br.gov.caixa.treinamento.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DashboardService {

    public Map<String, Object> buscarDados(String matriculaLogada, Map<String, Object> resumoUsuario) {
        Map<String, Object> dados = new HashMap<>();

        int pontosUsuario = obterInteiro(resumoUsuario, "pontuacaoTotal", 0);
        int nivelUsuario = obterInteiro(resumoUsuario, "nivel", calcularNivel(pontosUsuario));
        int progressoTrilha = obterInteiro(resumoUsuario, "progressoPercentual", 0);
        int trilhasUsuario = obterInteiro(resumoUsuario, "trilhasConcluidas", 0);
        int desafiosUsuario = obterInteiro(resumoUsuario, "desafiosRespondidos", 0);
        String nomeUsuario = obterTexto(resumoUsuario, "nome", "Usuário logado");
        String matriculaUsuario = obterTexto(resumoUsuario, "matricula", matriculaLogada);

        int xpBaseNivel = xpBaseNivel(nivelUsuario);
        int xpProximoNivel = xpProximoNivel(nivelUsuario);
        int xpFaltante = Math.max(0, xpProximoNivel - pontosUsuario);
        int progressoNivel = calcularProgressoNivel(pontosUsuario, xpBaseNivel, xpProximoNivel);

        List<Map<String, Object>> ranking = montarRanking(nomeUsuario, matriculaUsuario, pontosUsuario, nivelUsuario);
        int posicaoUsuario = calcularPosicaoUsuario(ranking, matriculaUsuario);

        dados.put("usuariosSimulados", 124);
        dados.put("trilhasConcluidasGerais", 38);
        dados.put("mediaPontuacao", 78.6);
        dados.put("funcionalidadesAtivas", 3);
        dados.put("funcionalidadesMaisAcessadas", List.of(
                "Treinamento Guiado",
                "Modo Desafio",
                "Dashboard do Gestor"
        ));

        dados.put("nomeUsuario", nomeUsuario);
        dados.put("matriculaUsuario", matriculaUsuario);
        dados.put("pontosUsuario", pontosUsuario);
        dados.put("nivelUsuario", nivelUsuario);
        dados.put("progressoTrilha", progressoTrilha);
        dados.put("trilhasUsuario", trilhasUsuario);
        dados.put("desafiosUsuario", desafiosUsuario);
        dados.put("xpBaseNivel", xpBaseNivel);
        dados.put("xpProximoNivel", xpProximoNivel);
        dados.put("xpFaltante", xpFaltante);
        dados.put("progressoNivel", progressoNivel);
        dados.put("ranking", ranking);
        dados.put("posicaoUsuario", posicaoUsuario);
        dados.put("totalCompetidores", ranking.size());
        dados.put("regrasPontuacao", montarRegrasPontuacao());
        dados.put("regrasMedalhas", montarRegrasMedalhas(pontosUsuario, resumoUsuario));
        dados.put("missaoRecomendada", montarMissaoRecomendada(progressoTrilha, desafiosUsuario, pontosUsuario));

        return dados;
    }

    /**
     * Compatibilidade com versões antigas que chamavam buscarDados() sem usuário.
     */
    public Map<String, Object> buscarDados() {
        return buscarDados("c159473", Map.of(
                "nome", "VINICIUS PEREIRA D AMICO",
                "matricula", "c159473",
                "pontuacaoTotal", 0,
                "nivel", 1,
                "progressoPercentual", 0,
                "trilhasConcluidas", 0,
                "desafiosRespondidos", 0
        ));
    }

    private List<Map<String, Object>> montarRanking(String nomeUsuario,
                                                     String matriculaUsuario,
                                                     int pontosUsuario,
                                                     int nivelUsuario) {
        List<Map<String, Object>> ranking = new ArrayList<>();

        ranking.add(rankingItem("ANA LUIZA COSTA", "c184211", 420, 3, 5, false,
                "Concluiu trilhas e manteve boa regularidade nos desafios."));
        ranking.add(rankingItem("MARCOS SANTOS", "c177530", 320, 3, 4, false,
                "Tem bom desempenho em desafios práticos."));
        ranking.add(rankingItem("JULIANA ROCHA", "c162048", 260, 2, 3, false,
                "Evoluiu com consistência no treinamento guiado."));
        ranking.add(rankingItem(nomeUsuario, matriculaUsuario, pontosUsuario, nivelUsuario, quantidadeBadgesPorPontos(pontosUsuario), true,
                "Esta é a sua posição atual no ranking fictício de aprendizagem."));
        ranking.add(rankingItem("RAFAEL MENDES", "c199842", 150, 2, 2, false,
                "Concluiu a primeira trilha e iniciou o modo desafio."));
        ranking.add(rankingItem("BEATRIZ ALMEIDA", "c201119", 80, 1, 1, false,
                "Está no começo da jornada de treinamento."));

        ranking.sort(Comparator
                .comparing((Map<String, Object> item) -> obterInteiro(item, "pontos", 0))
                .reversed()
                .thenComparing(item -> obterTexto(item, "nome", "")));

        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).put("posicao", i + 1);
        }

        return ranking;
    }

    private Map<String, Object> rankingItem(String nome,
                                            String matricula,
                                            int pontos,
                                            int nivel,
                                            int badges,
                                            boolean usuarioLogado,
                                            String descricaoAcessivel) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("posicao", 0);
        item.put("nome", nome);
        item.put("matricula", matricula);
        item.put("pontos", pontos);
        item.put("nivel", nivel);
        item.put("badges", badges);
        item.put("usuarioLogado", usuarioLogado);
        item.put("descricaoAcessivel", descricaoAcessivel);
        return item;
    }

    private int calcularPosicaoUsuario(List<Map<String, Object>> ranking, String matriculaUsuario) {
        for (Map<String, Object> item : ranking) {
            if (matriculaUsuario.equals(obterTexto(item, "matricula", ""))) {
                return obterInteiro(item, "posicao", 0);
            }
        }
        return 0;
    }

    private List<Map<String, Object>> montarRegrasPontuacao() {
        List<Map<String, Object>> regras = new ArrayList<>();
        regras.add(regraPontuacao("🔐", "Login na plataforma", "+5 XP", "Pontuação de presença ao acessar a plataforma."));
        regras.add(regraPontuacao("🚀", "Iniciar treinamento", "+10 XP", "Ganha ao começar uma trilha guiada."));
        regras.add(regraPontuacao("✅", "Concluir etapa", "+20 XP", "Ganha a cada etapa concluída no treinamento guiado."));
        regras.add(regraPontuacao("🎯", "Acertar desafio", "+30 XP", "Ganha ao responder corretamente o desafio prático."));
        regras.add(regraPontuacao("🏁", "Concluir trilha", "Medalha", "Ao finalizar a trilha, libera medalha de conclusão."));
        return regras;
    }

    private Map<String, Object> regraPontuacao(String icone, String titulo, String pontos, String descricao) {
        Map<String, Object> regra = new LinkedHashMap<>();
        regra.put("icone", icone);
        regra.put("titulo", titulo);
        regra.put("pontos", pontos);
        regra.put("descricao", descricao);
        return regra;
    }

    private List<Map<String, Object>> montarRegrasMedalhas(int pontosUsuario, Map<String, Object> resumoUsuario) {
        List<String> badgesAtuais = extrairNomesBadges(resumoUsuario);
        List<Map<String, Object>> regras = new ArrayList<>();

        regras.add(regraMedalha("🔐", "Primeiro Acesso", "Entrar na plataforma pela primeira vez.",
                badgesAtuais.contains("Primeiro Acesso"), pontosUsuario >= 5 ? 100 : 0));
        regras.add(regraMedalha("🏁", "Trilha Concluída", "Finalizar 100% do treinamento guiado.",
                badgesAtuais.contains("Trilha Concluída"), badgesAtuais.contains("Trilha Concluída") ? 100 : Math.min(100, pontosUsuario * 100 / 150)));
        regras.add(regraMedalha("⭐", "Explorador de Funcionalidades", "Alcançar pelo menos 100 XP.",
                badgesAtuais.contains("Explorador de Funcionalidades") || pontosUsuario >= 100, Math.min(100, pontosUsuario)));
        regras.add(regraMedalha("🎯", "Atenção aos Detalhes", "Acertar um desafio prático.",
                badgesAtuais.contains("Atenção aos Detalhes"), badgesAtuais.contains("Atenção aos Detalhes") ? 100 : 0));
        regras.add(regraMedalha("🏆", "Referência da Jornada", "Chegar a 300 XP e entrar no nível 3.",
                pontosUsuario >= 300, Math.min(100, pontosUsuario * 100 / 300)));

        return regras;
    }

    @SuppressWarnings("unchecked")
    private List<String> extrairNomesBadges(Map<String, Object> resumoUsuario) {
        Object badges = resumoUsuario.get("badges");
        if (!(badges instanceof Iterable<?> iterable)) {
            return List.of();
        }

        List<String> nomes = new ArrayList<>();
        for (Object badge : iterable) {
            try {
                Object nome = badge.getClass().getField("nome").get(badge);
                if (nome != null) {
                    nomes.add(String.valueOf(nome));
                }
            } catch (Exception ignored) {
                // Mantém compatibilidade caso o objeto venha em outro formato.
            }
        }
        return nomes;
    }

    private Map<String, Object> regraMedalha(String icone,
                                             String nome,
                                             String criterio,
                                             boolean conquistada,
                                             int progresso) {
        Map<String, Object> regra = new LinkedHashMap<>();
        regra.put("icone", icone);
        regra.put("nome", nome);
        regra.put("criterio", criterio);
        regra.put("conquistada", conquistada);
        regra.put("status", conquistada ? "Conquistada" : "Em progresso");
        regra.put("progresso", Math.max(0, Math.min(100, progresso)));
        return regra;
    }

    private Map<String, Object> montarMissaoRecomendada(int progressoTrilha, int desafiosRespondidos, int pontosUsuario) {
        Map<String, Object> missao = new LinkedHashMap<>();

        if (progressoTrilha < 100) {
            missao.put("icone", "🎧");
            missao.put("titulo", "Continue o treinamento guiado");
            missao.put("descricao", "Avance uma etapa por vez. Use o botão de áudio sempre que quiser ouvir a explicação da tela.");
            missao.put("link", "/treinamento");
            missao.put("acao", "Continuar treinamento");
            return missao;
        }

        if (desafiosRespondidos == 0) {
            missao.put("icone", "🎯");
            missao.put("titulo", "Faça o primeiro desafio");
            missao.put("descricao", "Você já concluiu a trilha. Agora valide o aprendizado no Modo Desafio.");
            missao.put("link", "/desafio");
            missao.put("acao", "Responder desafio");
            return missao;
        }

        if (pontosUsuario < 300) {
            missao.put("icone", "⭐");
            missao.put("titulo", "Busque o nível 3");
            missao.put("descricao", "Continue praticando para chegar a 300 XP e liberar a medalha Referência da Jornada.");
            missao.put("link", "/treinamento");
            missao.put("acao", "Revisar trilha");
            return missao;
        }

        missao.put("icone", "🏆");
        missao.put("titulo", "Compartilhe boas práticas");
        missao.put("descricao", "Você está avançado na jornada. Use seu aprendizado para apoiar colegas em novas trilhas.");
        missao.put("link", "/dashboard");
        missao.put("acao", "Ver evolução");
        return missao;
    }

    private int obterInteiro(Map<String, Object> mapa, String chave, int valorPadrao) {
        Object valor = mapa.get(chave);
        if (valor instanceof Number number) {
            return number.intValue();
        }
        if (valor != null) {
            try {
                return Integer.parseInt(String.valueOf(valor));
            } catch (NumberFormatException ignored) {
                return valorPadrao;
            }
        }
        return valorPadrao;
    }

    private String obterTexto(Map<String, Object> mapa, String chave, String valorPadrao) {
        Object valor = mapa.get(chave);
        if (valor == null || String.valueOf(valor).isBlank()) {
            return valorPadrao;
        }
        return String.valueOf(valor);
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

    private int xpBaseNivel(int nivel) {
        return switch (nivel) {
            case 1 -> 0;
            case 2 -> 100;
            case 3 -> 300;
            case 4 -> 600;
            case 5 -> 1000;
            default -> 0;
        };
    }

    private int xpProximoNivel(int nivel) {
        return switch (nivel) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 600;
            case 4 -> 1000;
            default -> 1000;
        };
    }

    private int calcularProgressoNivel(int pontos, int base, int proximo) {
        if (pontos >= proximo) {
            return 100;
        }
        if (proximo <= base) {
            return 100;
        }
        int progresso = ((pontos - base) * 100) / (proximo - base);
        return Math.max(0, Math.min(100, progresso));
    }

    private int quantidadeBadgesPorPontos(int pontos) {
        if (pontos >= 300) {
            return 5;
        }
        if (pontos >= 150) {
            return 3;
        }
        if (pontos >= 100) {
            return 2;
        }
        if (pontos > 0) {
            return 1;
        }
        return 0;
    }
}
