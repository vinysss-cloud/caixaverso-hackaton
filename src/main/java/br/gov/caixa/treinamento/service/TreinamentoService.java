package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.dto.EtapaTreinamentoDTO;
import br.gov.caixa.treinamento.dto.TrilhaTreinamentoDTO;
import br.gov.caixa.treinamento.model.ProgressoTreinamentoUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.repository.ProgressoTreinamentoUsuarioRepository;
import br.gov.caixa.treinamento.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TreinamentoService {

    public static final String CODIGO_ABERTURA_CONTA = "abertura-conta";

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    ProgressoTreinamentoUsuarioRepository progressoRepository;

    public TrilhaTreinamentoDTO buscarTrilhaAberturaConta() {
        return new TrilhaTreinamentoDTO(
                CODIGO_ABERTURA_CONTA,
                "Abertura de Conta Bancária",
                "Aprenda o fluxo de criação e abertura de uma conta bancária em ambiente simulado, com foco em acessibilidade e atendimento seguro.",
                "Novos colaboradores e público PCD em treinamento",
                150,
                List.of(
                        new EtapaTreinamentoDTO(
                                1,
                                "Identificar o tipo de conta",
                                "Escolha o tipo de conta mais adequado para o perfil do cliente fictício.",
                                "Use TAB para navegar entre as opções e Enter para selecionar.",
                                "Tipo de conta",
                                "Selecionar Conta Corrente ou Conta Poupança"
                        ),
                        new EtapaTreinamentoDTO(
                                2,
                                "Preencher dados cadastrais",
                                "Preencha os dados básicos fictícios do cliente, como nome, data de nascimento e matrícula simulada.",
                                "Não use dados reais. Este é um ambiente de treinamento.",
                                "Dados cadastrais",
                                "Preencher campos obrigatórios"
                        ),
                        new EtapaTreinamentoDTO(
                                3,
                                "Informar necessidades de acessibilidade",
                                "Registre se o cliente fictício possui alguma necessidade de acessibilidade para adaptar o atendimento.",
                                "As informações são usadas apenas para simulação e adaptação do fluxo.",
                                "Acessibilidade",
                                "Selecionar necessidade, se houver"
                        ),
                        new EtapaTreinamentoDTO(
                                4,
                                "Validar documentação",
                                "Confira se todos os documentos fictícios obrigatórios foram informados antes de avançar.",
                                "Observe os alertas visuais e mensagens de erro da tela.",
                                "Documentação",
                                "Validar documentos simulados"
                        ),
                        new EtapaTreinamentoDTO(
                                5,
                                "Revisar proposta de abertura",
                                "Revise os dados preenchidos antes de concluir a abertura da conta.",
                                "A revisão evita retrabalho e inconsistências.",
                                "Resumo da proposta",
                                "Conferir informações"
                        ),
                        new EtapaTreinamentoDTO(
                                6,
                                "Confirmar abertura da conta",
                                "Finalize a simulação de abertura de conta e libere o quiz associado.",
                                "Após concluir esta etapa, o desafio será desbloqueado.",
                                "Confirmação",
                                "Concluir treinamento"
                        )
                )
        );
    }

    @Transactional
    public ProgressoTreinamentoUsuario iniciarOuBuscarProgresso(String matricula, String codigoTreinamento) {
        Usuario usuario = usuarioRepository.buscarPorMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        TrilhaTreinamentoDTO trilha = buscarTrilhaAberturaConta();

        Optional<ProgressoTreinamentoUsuario> progressoExistente =
                progressoRepository.buscarPorUsuarioECodigo(usuario, codigoTreinamento);

        if (progressoExistente.isPresent()) {
            return progressoExistente.get();
        }

        ProgressoTreinamentoUsuario progresso = new ProgressoTreinamentoUsuario();
        progresso.usuario = usuario;
        progresso.codigoTreinamento = codigoTreinamento;
        progresso.tituloTreinamento = trilha.titulo;
        progresso.etapaAtual = 0;
        progresso.totalEtapas = trilha.etapas.size();
        progresso.progressoPercentual = 0;
        progresso.concluido = false;
        progresso.desafioDesbloqueado = false;
        progresso.desafioRespondido = false;
        progresso.dataInicio = LocalDateTime.now();

        progressoRepository.persist(progresso);

        return progresso;
    }

    @Transactional
    public ProgressoTreinamentoUsuario concluirProximaEtapa(String matricula, String codigoTreinamento) {
        Usuario usuario = usuarioRepository.buscarPorMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        ProgressoTreinamentoUsuario progresso = progressoRepository
                .buscarPorUsuarioECodigo(usuario, codigoTreinamento)
                .orElseGet(() -> iniciarOuBuscarProgresso(matricula, codigoTreinamento));

        if (Boolean.TRUE.equals(progresso.concluido)) {
            return progresso;
        }

        progresso.etapaAtual = progresso.etapaAtual + 1;

        if (progresso.etapaAtual >= progresso.totalEtapas) {
            progresso.etapaAtual = progresso.totalEtapas;
            progresso.progressoPercentual = 100;
            progresso.concluido = true;
            progresso.desafioDesbloqueado = true;
            progresso.dataConclusao = LocalDateTime.now();
        } else {
            progresso.progressoPercentual = (progresso.etapaAtual * 100) / progresso.totalEtapas;
        }

        return progresso;
    }

    public boolean desafioEstaDesbloqueado(String matricula, String codigoTreinamento) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorMatricula(matricula);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        return progressoRepository.buscarPorUsuarioECodigo(usuarioOpt.get(), codigoTreinamento)
                .map(p -> Boolean.TRUE.equals(p.desafioDesbloqueado))
                .orElse(false);
    }

    @Transactional
    public ProgressoTreinamentoUsuario reiniciarTrilha(String matricula, String codigoTreinamento) {
        Usuario usuario = usuarioRepository.buscarPorMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        progressoRepository.apagarPorUsuarioECodigo(usuario, codigoTreinamento);

        TrilhaTreinamentoDTO trilha = buscarTrilhaAberturaConta();

        ProgressoTreinamentoUsuario progresso = new ProgressoTreinamentoUsuario();
        progresso.usuario = usuario;
        progresso.codigoTreinamento = codigoTreinamento;
        progresso.tituloTreinamento = trilha.titulo;
        progresso.etapaAtual = 0;
        progresso.totalEtapas = trilha.etapas.size();
        progresso.progressoPercentual = 0;
        progresso.concluido = false;
        progresso.desafioDesbloqueado = false;
        progresso.desafioRespondido = false;
        progresso.dataInicio = LocalDateTime.now();
        progresso.dataConclusao = null;

        progressoRepository.persist(progresso);

        return progresso;
    }
}