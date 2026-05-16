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
                "Abertura de Conta Bancária",
                "Trilha guiada para abertura de conta.",
                "Empregados PCD",
                100,
                List.of(etapa)
        );

        assertThat(trilha.codigo).isEqualTo("abertura-conta");
        assertThat(trilha.titulo).isEqualTo("Abertura de Conta Bancária");
        assertThat(trilha.descricao).contains("Trilha guiada");
        assertThat(trilha.publicoAlvo).isEqualTo("Empregados PCD");
        assertThat(trilha.xpConclusao).isEqualTo(100);
        assertThat(trilha.etapas).containsExactly(etapa);
    }
}
