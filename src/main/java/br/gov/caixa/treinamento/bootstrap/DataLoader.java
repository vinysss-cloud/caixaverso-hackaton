package br.gov.caixa.treinamento.bootstrap;

import br.gov.caixa.treinamento.model.DesafioAssistivo;
import br.gov.caixa.treinamento.model.TrilhaAssistenteGuiado;
import br.gov.caixa.treinamento.repository.DesafioAssistivoRepository;
import br.gov.caixa.treinamento.repository.TrilhaAssistenteGuiadoRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

/**
 * Carga estrutural do protótipo.
 *
 * Não cria usuário mockado. Apenas registra as trilhas e desafios disponíveis
 * para que o H2 já nasça com uma estrutura relacional próxima da versão futura.
 */
@ApplicationScoped
public class DataLoader {

    @Inject
    TrilhaAssistenteGuiadoRepository trilhaRepository;

    @Inject
    DesafioAssistivoRepository desafioRepository;

    @Transactional
    void carregarBaseEstrutural(@Observes StartupEvent event) {
        TrilhaAssistenteGuiado contaFacil = criarOuAtualizarTrilha(
                "abertura-conta",
                "Conta Fácil: Jornada Assistiva PcD",
                "Fluxo piloto de abertura de conta bancária em ambiente simulado, com apoio visual, leitura por voz, linguagem simples e validação por etapa.",
                "Empregados CAIXA PcD, baixa visão e usuários com baixa familiaridade digital.",
                "DISPONIVEL",
                1,
                true
        );

        criarOuAtualizarTrilha(
                "sisrh-atualizacao-cadastral",
                "SISRH: atualização cadastral interna",
                "Jornada futura para atualização funcional, campos obrigatórios, mensagens de RH e orientação assistiva.",
                "Empregados que utilizam sistemas internos de RH.",
                "EM_BREVE",
                2,
                true
        );

        criarOuAtualizarTrilha(
                "intranet-comunicado",
                "Intranet: comunicado institucional",
                "Jornada futura para localização e interpretação de comunicados internos com linguagem simples.",
                "Empregados que precisam consultar orientações institucionais.",
                "EM_BREVE",
                3,
                true
        );

        criarOuAtualizarTrilha(
                "atender-caixa-solicitacao",
                "Atender.caixa: solicitação assistida",
                "Jornada futura para abertura, descrição e acompanhamento de demanda interna.",
                "Empregados que registram solicitações internas.",
                "EM_BREVE",
                4,
                true
        );

        criarOuAtualizarTrilha(
                "seguranca-digital",
                "Segurança digital: decisão segura",
                "Jornada futura para identificação de links suspeitos, dados sensíveis e condutas seguras.",
                "Empregados em fluxos com risco operacional e segurança da informação.",
                "EM_BREVE",
                5,
                true
        );

        criarOuAtualizarTrilha(
                "acessibilidade-atendimento",
                "Acessibilidade: apoio adequado",
                "Jornada futura para decisões de linguagem simples, voz, transcrição, foco por teclado e redução de estímulos.",
                "Empregados que precisam adaptar a comunicação ao usuário PcD.",
                "EM_BREVE",
                6,
                true
        );

        criarOuAtualizarDesafio(
                "conta-facil-validacao-assistiva",
                contaFacil,
                "Conta Fácil: abertura de conta assistida",
                "Missão prática com 8 situações sobre conferência de dados, documentos, alertas, aceite consciente e orientação assistiva por etapa.",
                8,
                100,
                "LIBERADO",
                false
        );
    }

    private TrilhaAssistenteGuiado criarOuAtualizarTrilha(String codigo,
                                                          String titulo,
                                                          String descricao,
                                                          String publicoAlvo,
                                                          String status,
                                                          int ordem,
                                                          boolean ativa) {
        TrilhaAssistenteGuiado trilha = trilhaRepository.buscarPorCodigo(codigo)
                .orElseGet(TrilhaAssistenteGuiado::new);

        trilha.codigo = codigo;
        trilha.titulo = titulo;
        trilha.descricao = descricao;
        trilha.publicoAlvo = publicoAlvo;
        trilha.status = status;
        trilha.ordem = ordem;
        trilha.ativa = ativa;

        if (trilha.dataCriacao == null) {
            trilha.dataCriacao = LocalDateTime.now();
        }

        if (trilha.id == null) {
            trilhaRepository.persist(trilha);
        }

        return trilha;
    }

    private void criarOuAtualizarDesafio(String codigo,
                                         TrilhaAssistenteGuiado trilha,
                                         String titulo,
                                         String descricao,
                                         int quantidadeSituacoes,
                                         int pontuacaoMaxima,
                                         String status,
                                         boolean permiteRefazer) {
        DesafioAssistivo desafio = desafioRepository.buscarPorCodigo(codigo)
                .orElseGet(DesafioAssistivo::new);

        desafio.codigo = codigo;
        desafio.trilha = trilha;
        desafio.titulo = titulo;
        desafio.descricao = descricao;
        desafio.quantidadeSituacoes = quantidadeSituacoes;
        desafio.pontuacaoMaxima = pontuacaoMaxima;
        desafio.status = status;
        desafio.permiteRefazer = permiteRefazer;

        if (desafio.dataCriacao == null) {
            desafio.dataCriacao = LocalDateTime.now();
        }

        if (desafio.id == null) {
            desafioRepository.persist(desafio);
        }
    }
}
