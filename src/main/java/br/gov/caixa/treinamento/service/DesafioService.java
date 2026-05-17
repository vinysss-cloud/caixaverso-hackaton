package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.model.Desafio;
import br.gov.caixa.treinamento.model.DesafioAssistivo;
import br.gov.caixa.treinamento.model.ResultadoDesafio;
import br.gov.caixa.treinamento.model.ResultadoDesafioUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.repository.DesafioAssistivoRepository;
import br.gov.caixa.treinamento.repository.ResultadoDesafioUsuarioRepository;
import br.gov.caixa.treinamento.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@ApplicationScoped
public class DesafioService {

    public static final String CODIGO_DESAFIO_CONTA_FACIL = "conta-facil-validacao-assistiva";

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    DesafioAssistivoRepository desafioAssistivoRepository;

    @Inject
    ResultadoDesafioUsuarioRepository resultadoDesafioUsuarioRepository;

    public Desafio buscarDesafioAberturaConta() {
        return new Desafio(
                "quiz-abertura-conta",
                "Validação: Conta Fácil - Jornada Assistiva PcD",
                "Após concluir a jornada assistiva, responda: qual etapa deve ocorrer antes da confirmação final do fluxo piloto de abertura de conta? A validação demonstra autonomia assistida que pode ser aplicada a intranet, SISRH, atender.caixa e outros sistemas internos.",
                Arrays.asList(
                        "Revisar os dados da proposta",
                        "Ignorar alertas de documentação",
                        "Confirmar a conta sem validação",
                        "Pular a identificação do tipo de conta"
                ),
                "Revisar os dados da proposta"
        );
    }

    public ResultadoDesafio validarRespostaAberturaConta(String resposta) {
        if (resposta == null || resposta.trim().isEmpty()) {
            return new ResultadoDesafio(
                    false,
                    0,
                    "Nenhuma resposta foi selecionada."
            );
        }

        Desafio desafio = buscarDesafioAberturaConta();
        boolean acertou = desafio.respostaCorreta.equalsIgnoreCase(resposta.trim());

        if (acertou) {
            return new ResultadoDesafio(
                    true,
                    100,
                    "Correto. Resultado salvo. O empregado demonstrou autonomia assistida, atenção a alertas e revisão segura antes da confirmação final."
            );
        }

        return new ResultadoDesafio(
                false,
                40,
                "Resposta incorreta. Revise a jornada Conta Fácil antes de repetir a prática. Resultado salvo com revisão recomendada."
        );
    }

    public boolean desafioContaFacilJaRespondido(String matricula) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorMatricula(matricula);
        Optional<DesafioAssistivo> desafioOpt = desafioAssistivoRepository.buscarPorCodigo(CODIGO_DESAFIO_CONTA_FACIL);

        if (usuarioOpt.isEmpty() || desafioOpt.isEmpty()) {
            return false;
        }

        return resultadoDesafioUsuarioRepository.usuarioJaConcluiu(usuarioOpt.get(), desafioOpt.get());
    }

    public Optional<ResultadoDesafioUsuario> buscarResultadoContaFacil(String matricula) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorMatricula(matricula);
        Optional<DesafioAssistivo> desafioOpt = desafioAssistivoRepository.buscarPorCodigo(CODIGO_DESAFIO_CONTA_FACIL);

        if (usuarioOpt.isEmpty() || desafioOpt.isEmpty()) {
            return Optional.empty();
        }

        return resultadoDesafioUsuarioRepository.buscarPorUsuarioEDesafio(usuarioOpt.get(), desafioOpt.get());
    }

    @Transactional
    public ResultadoDesafio registrarResultadoContaFacil(String matricula,
                                                         String resposta,
                                                         Integer acertos,
                                                         Integer percentual,
                                                         Integer totalSituacoes) {
        Usuario usuario = usuarioRepository.buscarPorMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        DesafioAssistivo desafio = desafioAssistivoRepository.buscarPorCodigo(CODIGO_DESAFIO_CONTA_FACIL)
                .orElseThrow(() -> new IllegalStateException("Desafio Conta Fácil não foi cadastrado na base."));

        Optional<ResultadoDesafioUsuario> resultadoExistente =
                resultadoDesafioUsuarioRepository.buscarPorUsuarioEDesafio(usuario, desafio);

        if (resultadoExistente.isPresent()) {
            ResultadoDesafioUsuario salvo = resultadoExistente.get();
            return new ResultadoDesafio(
                    Boolean.TRUE.equals(salvo.aprovado),
                    0,
                    "Este desafio já foi concluído por este usuário. O resultado anterior foi mantido para preservar o ranking e evitar pontuação repetida."
            );
        }

        ResultadoDesafio resultado = validarRespostaAberturaConta(resposta);

        int totalNormalizado = normalizarInteiro(totalSituacoes, desafio.quantidadeSituacoes, 1, 100);
        int acertosNormalizados = normalizarInteiro(acertos, resultado.acertou ? totalNormalizado : 0, 0, totalNormalizado);
        int percentualNormalizado = normalizarInteiro(percentual, calcularPercentual(acertosNormalizados, totalNormalizado), 0, 100);
        boolean aprovado = percentualNormalizado >= 70;

        ResultadoDesafioUsuario registro = new ResultadoDesafioUsuario();
        registro.usuario = usuario;
        registro.desafio = desafio;
        registro.totalSituacoes = totalNormalizado;
        registro.acertos = acertosNormalizados;
        registro.percentual = percentualNormalizado;
        registro.pontuacao = resultado.pontuacao;
        registro.aprovado = aprovado;
        registro.status = "CONCLUIDO";
        registro.respostaResumo = resposta;
        registro.dataInicio = LocalDateTime.now();
        registro.dataConclusao = LocalDateTime.now();

        resultadoDesafioUsuarioRepository.persist(registro);

        return resultado;
    }

    private int calcularPercentual(int acertos, int total) {
        if (total <= 0) {
            return 0;
        }
        return Math.round((acertos * 100f) / total);
    }

    private int normalizarInteiro(Integer valor, int padrao, int minimo, int maximo) {
        int normalizado = valor == null ? padrao : valor;
        if (normalizado < minimo) {
            return minimo;
        }
        return Math.min(normalizado, maximo);
    }
}
