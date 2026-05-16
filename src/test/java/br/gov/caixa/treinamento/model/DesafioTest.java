package br.gov.caixa.treinamento.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Desafio - Testes unitários")
class DesafioTest {

    private Desafio desafio;

    @BeforeEach
    void setup() {
        desafio = new Desafio(
                "quiz-01",
                "Quiz Teste",
                "Descrição do quiz",
                Arrays.asList("Opção A", "Opção B", "Opção C"),
                "Opção A"
        );
    }

    // ==================== TESTES DE CONSTRUÇÃO ====================

    @Test
    @DisplayName("Deve criar desafio com construtor padrão")
    void testCriarComConstrutorPadrao() {
        Desafio novo = new Desafio();
        assertThat(novo).isNotNull();
    }

    @Test
    @DisplayName("Deve criar desafio com construtor parametrizado")
    void testCriarComParametros() {
        assertThat(desafio)
                .extracting("id", "titulo", "descricao", "respostaCorreta")
                .containsExactly("quiz-01", "Quiz Teste", "Descrição do quiz", "Opção A");
    }

    // ==================== TESTES DE CAMPOS ====================

    @Test
    @DisplayName("Desafio deve ter id")
    void testDesafioTemId() {
        assertThat(desafio.id).isEqualTo("quiz-01");
    }

    @Test
    @DisplayName("Desafio deve ter título")
    void testDesafioTemTitulo() {
        assertThat(desafio.titulo).isEqualTo("Quiz Teste");
    }

    @Test
    @DisplayName("Desafio deve ter descrição")
    void testDesafioTemDescricao() {
        assertThat(desafio.descricao).isEqualTo("Descrição do quiz");
    }

    @Test
    @DisplayName("Desafio deve ter alternativas")
    void testDesafioTemAlternativas() {
        assertThat(desafio.alternativas).hasSize(3);
    }

    @Test
    @DisplayName("Desafio deve ter resposta correta")
    void testDesafioTemRespostaCorreta() {
        assertThat(desafio.respostaCorreta).isEqualTo("Opção A");
    }

    // ==================== TESTES DE ALTERNATIVAS ====================

    @Test
    @DisplayName("Alternativas devem estar na lista")
    void testAlternativasEstaoNaLista() {
        assertThat(desafio.alternativas).contains("Opção A", "Opção B", "Opção C");
    }

    @Test
    @DisplayName("Resposta correta deve estar nas alternativas")
    void testRespostaCorretaEmAlternativas() {
        assertThat(desafio.alternativas).contains(desafio.respostaCorreta);
    }

    @Test
    @DisplayName("Desafio pode ter muitas alternativas")
    void testDesafioComMuitasAlternativas() {
        List<String> muitasAlternativas = Arrays.asList("A", "B", "C", "D", "E", "F");
        Desafio desafioGrande = new Desafio("quiz-grande", "Título", "Desc", muitasAlternativas, "A");

        assertThat(desafioGrande.alternativas).hasSize(6);
    }

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("Desafio com campos nulos não deve quebrar")
    void testDesafioComCamposNulos() {
        Desafio desafioNulo = new Desafio(null, null, null, null, null);

        assertThat(desafioNulo)
                .extracting("id", "titulo", "descricao", "alternativas", "respostaCorreta")
                .containsOnlyNulls();
    }

    @Test
    @DisplayName("Deve permitir modificação de campos")
    void testModificarCampos() {
        desafio.titulo = "Novo Título";
        desafio.respostaCorreta = "Opção B";

        assertThat(desafio.titulo).isEqualTo("Novo Título");
        assertThat(desafio.respostaCorreta).isEqualTo("Opção B");
    }
}

