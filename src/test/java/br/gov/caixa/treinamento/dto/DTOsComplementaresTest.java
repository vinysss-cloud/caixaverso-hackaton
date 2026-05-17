package br.gov.caixa.treinamento.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTOs complementares - Cobertura alta")
class DTOsComplementaresTest {

    @Test
    @DisplayName("EtapaTreinamentoDTO deve preencher todos os campos pelo construtor")
    void etapaTreinamentoDTO_devePreencherCampos() {
        EtapaTreinamentoDTO dto = new EtapaTreinamentoDTO(
                1,
                "Identificação do cliente",
                "Informe os dados básicos do cliente.",
                "Use leitura em voz alta se necessário.",
                "cpf",
                "preencher"
        );

        assertThat(dto.ordem).isEqualTo(1);
        assertThat(dto.titulo).isEqualTo("Identificação do cliente");
        assertThat(dto.instrucao).contains("dados básicos");
        assertThat(dto.dica).contains("voz alta");
        assertThat(dto.campoSimulado).isEqualTo("cpf");
        assertThat(dto.acaoEsperada).isEqualTo("preencher");
    }

    @Test
    @DisplayName("LoginDTO deve permitir preenchimento simples dos campos")
    void loginDTO_devePermitirPreenchimento() {
        LoginDTO dto = new LoginDTO();
        dto.matricula = "c123456";
        dto.senha = "Senha@123";

        assertThat(dto.matricula).isEqualTo("c123456");
        assertThat(dto.senha).isEqualTo("Senha@123");
    }

    @Test
    @DisplayName("RespostaDTO deve permitir construtor vazio e construtor com resposta")
    void respostaDTO_deveCobrirConstrutores() {
        RespostaDTO vazio = new RespostaDTO();
        vazio.resposta = "validar documentos";

        RespostaDTO preenchido = new RespostaDTO("conferir dados cadastrais");

        assertThat(vazio.resposta).isEqualTo("validar documentos");
        assertThat(preenchido.resposta).isEqualTo("conferir dados cadastrais");
    }

    @Test
    @DisplayName("TrilhaTreinamentoDTO deve preencher todos os campos e manter lista de etapas")
    void trilhaTreinamentoDTO_devePreencherCampos() {
        EtapaTreinamentoDTO etapa = new EtapaTreinamentoDTO(
                1,
                "Cadastro",
                "Preencha os dados.",
                "Leia as instruções com calma.",
                "nome",
                "digitar"
        );

        TrilhaTreinamentoDTO trilha = new TrilhaTreinamentoDTO(
                "abertura-conta",
                "Conta Fácil: Jornada Assistiva PcD",
                "Trilha guiada para abertura de conta.",
                "Empregados PCD",
                100,
                List.of(etapa)
        );

        assertThat(trilha.codigo).isEqualTo("abertura-conta");
        assertThat(trilha.titulo).isEqualTo("Conta Fácil: Jornada Assistiva PcD");
        assertThat(trilha.descricao).contains("Trilha guiada");
        assertThat(trilha.publicoAlvo).isEqualTo("Empregados PCD");
        assertThat(trilha.xpConclusao).isEqualTo(100);
        assertThat(trilha.etapas).containsExactly(etapa);
    }

    @Test
    @DisplayName("TrilhaTreinamentoDTO deve calcular duração padrão a partir das etapas")
    void trilhaTreinamentoDTO_deveCalcularDuracaoPadrao() {
        EtapaTreinamentoDTO etapa1 = new EtapaTreinamentoDTO(1, "E1", "i1", "d1", "c1", "a1");
        EtapaTreinamentoDTO etapa2 = new EtapaTreinamentoDTO(2, "E2", "i2", "d2", "c2", "a2");

        TrilhaTreinamentoDTO trilha = new TrilhaTreinamentoDTO(
                "teste",
                "Teste",
                "Descrição",
                "Público",
                100,
                List.of(etapa1, etapa2)
        );

        // 2 etapas * 5 minutos = 10
        assertThat(trilha.duracao).isEqualTo(10);
        assertThat(trilha.getDuracao()).isEqualTo(10);
    }

    @Test
    @DisplayName("TrilhaTreinamentoDTO deve retornar 0 para duração com lista vazia")
    void trilhaTreinamentoDTO_duracaoZeroComListaVazia() {
        TrilhaTreinamentoDTO trilha = new TrilhaTreinamentoDTO(
                "teste",
                "Teste",
                "Descrição",
                "Público",
                100,
                List.of()
        );

        assertThat(trilha.duracao).isZero();
    }

    @Test
    @DisplayName("TrilhaTreinamentoDTO deve retornar 0 para duração com lista nula")
    void trilhaTreinamentoDTO_duracaoZeroComListaNula() {
        TrilhaTreinamentoDTO trilha = new TrilhaTreinamentoDTO(
                "teste",
                "Teste",
                "Descrição",
                "Público",
                100,
                null
        );

        assertThat(trilha.duracao).isZero();
    }

    @Test
    @DisplayName("TrilhaTreinamentoDTO com duração explícita não deve calcular duração padrão")
    void trilhaTreinamentoDTO_duracaoExplicita() {
        EtapaTreinamentoDTO etapa = new EtapaTreinamentoDTO(1, "E1", "i1", "d1", "c1", "a1");

        TrilhaTreinamentoDTO trilha = new TrilhaTreinamentoDTO(
                "teste",
                "Teste",
                "Descrição",
                "Público",
                100,
                15,
                List.of(etapa)
        );

        assertThat(trilha.duracao).isEqualTo(15);
        assertThat(trilha.getDuracao()).isEqualTo(15);
    }

    @Test
    @DisplayName("TrilhaTreinamentoDTO getters devem retornar valores corretos")
    void trilhaTreinamentoDTO_gettersDevemRetornarCorretamente() {
        EtapaTreinamentoDTO etapa = new EtapaTreinamentoDTO(1, "E1", "i1", "d1", "c1", "a1");
        TrilhaTreinamentoDTO trilha = new TrilhaTreinamentoDTO(
                "codigo-teste",
                "Título Teste",
                "Descrição Teste",
                "Público Alvo",
                250,
                List.of(etapa)
        );

        assertThat(trilha.getCodigo()).isEqualTo("codigo-teste");
        assertThat(trilha.getTitulo()).isEqualTo("Título Teste");
        assertThat(trilha.getDescricao()).isEqualTo("Descrição Teste");
        assertThat(trilha.getPublicoAlvo()).isEqualTo("Público Alvo");
        assertThat(trilha.getXpConclusao()).isEqualTo(250);
        assertThat(trilha.getEtapas()).containsExactly(etapa);
    }
}
