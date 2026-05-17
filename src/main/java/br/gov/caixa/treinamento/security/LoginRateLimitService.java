package br.gov.caixa.treinamento.security;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class LoginRateLimitService {

    private static final int MAX_TENTATIVAS = 5;
    private static final Duration JANELA = Duration.ofMinutes(15);
    private static final Duration BLOQUEIO = Duration.ofMinutes(15);

    private final Map<String, TentativaLogin> tentativas = new ConcurrentHashMap<>();

    public boolean estaBloqueado(String matricula, String origem) {
        String chave = montarChave(matricula, origem);
        TentativaLogin tentativa = tentativas.get(chave);

        if (tentativa == null) {
            return false;
        }

        Instant agora = Instant.now();

        if (tentativa.bloqueadoAte != null && tentativa.bloqueadoAte.isAfter(agora)) {
            return true;
        }

        if (tentativa.bloqueadoAte != null && !tentativa.bloqueadoAte.isAfter(agora)) {
            tentativas.remove(chave);
        }

        return false;
    }

    public void registrarFalha(String matricula, String origem) {
        String chave = montarChave(matricula, origem);
        Instant agora = Instant.now();

        tentativas.compute(chave, (k, tentativa) -> {
            if (tentativa == null || tentativa.inicioJanela.plus(JANELA).isBefore(agora)) {
                tentativa = new TentativaLogin();
                tentativa.inicioJanela = agora;
                tentativa.quantidade = 0;
            }

            tentativa.quantidade++;

            if (tentativa.quantidade >= MAX_TENTATIVAS) {
                tentativa.bloqueadoAte = agora.plus(BLOQUEIO);
            }

            return tentativa;
        });
    }

    public void registrarSucesso(String matricula, String origem) {
        tentativas.remove(montarChave(matricula, origem));
    }

    private String montarChave(String matricula, String origem) {
        String matriculaNormalizada = matricula == null ? "matricula-ausente" : matricula.trim().toLowerCase();
        String origemNormalizada = origem == null || origem.isBlank() ? "origem-desconhecida" : origem.trim();
        return matriculaNormalizada + "|" + origemNormalizada;
    }

    private static class TentativaLogin {
        private Instant inicioJanela;
        private int quantidade;
        private Instant bloqueadoAte;
    }
}
