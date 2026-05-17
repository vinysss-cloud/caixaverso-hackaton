package br.gov.caixa.treinamento.logging;

import java.text.Normalizer;

/**
 * Utilitário simples para evitar vazamento de dados sensíveis nos logs.
 * Mantém os logs úteis para auditoria sem gravar senha, token, cookie ou CPF completo.
 */
public final class LogSanitizer {

    private LogSanitizer() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }

    public static String textoSeguro(String valor) {
        if (valor == null || valor.isBlank()) {
            return "-";
        }

        String normalizado = Normalizer.normalize(valor.trim(), Normalizer.Form.NFKC)
                .replaceAll("[\\r\\n\\t]", " ")
                .replaceAll("\\s+", " ");

        normalizado = normalizado.replaceAll("(?i)(senha|password|csrf|token|cookie|authorization)=([^\\s&;]+)", "$1=***");
        normalizado = normalizado.replaceAll("\\b\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}\\b", "***CPF***");

        if (normalizado.length() > 180) {
            return normalizado.substring(0, 180) + "...";
        }

        return normalizado;
    }

    public static String matriculaSegura(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            return "anonimo";
        }

        String limpa = textoSeguro(matricula).replaceAll("[^A-Za-z0-9]", "");

        if (limpa.length() <= 3) {
            return "***";
        }

        return limpa.substring(0, Math.min(2, limpa.length()))
                + "***"
                + limpa.substring(Math.max(0, limpa.length() - 2));
    }
}
