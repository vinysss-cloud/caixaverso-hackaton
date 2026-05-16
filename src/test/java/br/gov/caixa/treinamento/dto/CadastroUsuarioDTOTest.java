package br.gov.caixa.treinamento.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CadastroUsuarioDTO - Testes unitários")
class CadastroUsuarioDTOTest {

    private CadastroUsuarioDTO dto;

    @BeforeEach
    void setup() {
        dto = new CadastroUsuarioDTO();
    }

    // ==================== TESTES DE CAMPOS ====================

    @Test
    @DisplayName("DTO deve ter campo nome")
    void testDTOTemNome() {
        dto.nome = "João Silva";
        assertThat(dto.nome).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("DTO deve ter campo matricula")
    void testDTOTemMatricula() {
        dto.matricula = "c123456";
        assertThat(dto.matricula).isEqualTo("c123456");
    }

    @Test
    @DisplayName("DTO deve ter campo idade")
    void testDTOTemIdade() {
        dto.idade = 30;
        assertThat(dto.idade).isEqualTo(30);
    }

    @Test
    @DisplayName("DTO deve ter campo deficiencias")
    void testDTOTemDeficiencias() {
        dto.deficiencias = new ArrayList<>();
        dto.deficiencias.add("visual");
        
        assertThat(dto.deficiencias).contains("visual");
    }

    @Test
    @DisplayName("DTO deve ter campo senha")
    void testDTOTemSenha() {
        dto.senha = "Senha@123";
        assertThat(dto.senha).isEqualTo("Senha@123");
    }

    @Test
    @DisplayName("DTO deve ter campo repetirSenha")
    void testDTOTemRepetirSenha() {
        dto.repetirSenha = "Senha@123";
        assertThat(dto.repetirSenha).isEqualTo("Senha@123");
    }

    @Test
    @DisplayName("Deve criar DTO com todos os campos preenchidos")
    void testDTOComTodosCampos() {
        dto.nome = "João Silva";
        dto.matricula = "c123456";
        dto.idade = 30;
        dto.deficiencias = List.of("visual");
        dto.senha = "Senha@123";
        dto.repetirSenha = "Senha@123";

        assertThat(dto)
                .extracting("nome", "matricula", "idade", "senha", "repetirSenha")
                .containsExactly("João Silva", "c123456", 30, "Senha@123", "Senha@123");
    }

    @Test
    @DisplayName("Deve permitir null em deficiências")
    void testDeficienciasPodemSerNull() {
        dto.deficiencias = null;
        assertThat(dto.deficiencias).isNull();
    }

    @Test
    @DisplayName("Deve permitir lista vazia em deficiências")
    void testDeficienciasVazia() {
        dto.deficiencias = new ArrayList<>();
        assertThat(dto.deficiencias).isEmpty();
    }
}

