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
                                "Preencher nome do cliente",
                                "Preencha o nome completo do cliente fictício.",
                                "Não use dados reais. Este é um ambiente de treinamento.",
                                "Nome completo",
                                "Preencher nome e sobrenome"
                        ),
                        new EtapaTreinamentoDTO(
                                3,
                                "Validar CPF e data de nascimento",
                                "Preencha CPF e data de nascimento do cliente fictício.",
                                "Use CPF com 11 números e data no formato dd/mm/aaaa.",
                                "CPF e data de nascimento",
                                "Preencher dados cadastrais"
                        ),
                        new EtapaTreinamentoDTO(
                                4,
                                "Informar telefone para contato",
                                "Preencha um telefone de contato fictício para o cliente.",
                                "Use DDD e número. Exemplo: (21) 99999-9999.",
                                "Telefone para contato",
                                "Preencher telefone"
                        ),
                        new EtapaTreinamentoDTO(
                                5,
                                "Preencher endereço residencial",
                                "Informe os dados de endereço do cliente fictício.",
                                "Preencha CEP, endereço, número, bairro, cidade e UF.",
                                "Endereço residencial",
                                "Preencher endereço completo"
                        ),
                        new EtapaTreinamentoDTO(
                                6,
                                "Informar necessidades de acessibilidade",
                                "Registre se o cliente fictício possui alguma necessidade de acessibilidade para adaptar o atendimento.",
                                "As informações são usadas apenas para simulação e adaptação do fluxo.",
                                "Acessibilidade",
                                "Selecionar necessidade, se houver"
                        ),
                        new EtapaTreinamentoDTO(
                                7,
                                "Validar documentação",
                                "Confira se todos os documentos fictícios obrigatórios foram informados antes de avançar.",
                                "Observe os alertas visuais e mensagens de erro da tela.",
                                "Documentação",
                                "Validar documentos simulados"
                        ),
                        new EtapaTreinamentoDTO(
                                8,
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
            ProgressoTreinamentoUsuario progresso = progressoExistente.get();
            sincronizarTotalEtapas(progresso, trilha.etapas.size());
            return progresso;
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


    private void sincronizarTotalEtapas(ProgressoTreinamentoUsuario progresso, int totalEtapasAtualizado) {
        if (progresso.totalEtapas == null || progresso.totalEtapas != totalEtapasAtualizado) {
            progresso.totalEtapas = totalEtapasAtualizado;

            if (progresso.etapaAtual == null) {
                progresso.etapaAtual = 0;
            }

            if (progresso.etapaAtual > totalEtapasAtualizado) {
                progresso.etapaAtual = totalEtapasAtualizado;
            }

            if (Boolean.TRUE.equals(progresso.concluido)) {
                progresso.etapaAtual = totalEtapasAtualizado;
                progresso.progressoPercentual = 100;
                progresso.desafioDesbloqueado = true;
                return;
            }

            progresso.progressoPercentual = totalEtapasAtualizado == 0
                    ? 0
                    : (progresso.etapaAtual * 100) / totalEtapasAtualizado;
        }
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