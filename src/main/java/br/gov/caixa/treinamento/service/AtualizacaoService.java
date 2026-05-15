package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.model.ModuloAtualizacao;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;

@ApplicationScoped
public class AtualizacaoService {

    public ModuloAtualizacao buscarModuloAtualizacao() {
        return new ModuloAtualizacao(
                "mod-1",
                "Novo fluxo de validação assistida",
                "Introduz um passo extra de verificação com sugestões automáticas.",
                Arrays.asList(
                        "Abra o formulário de atendimento",
                        "Preencha os campos como de costume",
                        "Use a nova seção de verificação para receber sugestões",
                        "Confirme e conclua"
                )
        );
    }
}