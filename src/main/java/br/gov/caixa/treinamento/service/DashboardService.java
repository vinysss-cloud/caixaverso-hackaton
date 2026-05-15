package br.gov.caixa.treinamento.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço que retorna números mockados para o dashboard.
 */
@ApplicationScoped
public class DashboardService {

    public Map<String, Object> buscarDados() {
        Map<String, Object> dados = new HashMap<>();

        dados.put("usuariosSimulados", 124);
        dados.put("trilhasConcluidas", 38);
        dados.put("mediaPontuacao", 78.6);
        dados.put("modulosDisponiveis", 5);

        return dados;
    }
}