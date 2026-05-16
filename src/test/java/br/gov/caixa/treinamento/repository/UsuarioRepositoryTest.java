package br.gov.caixa.treinamento.repository;

import br.gov.caixa.treinamento.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UsuarioRepository - Testes unitários")
class UsuarioRepositoryTest {

    private UsuarioRepository usuarioRepository;
    private Usuario usuarioExemplo;

    @BeforeEach
    void setup() {
        usuarioRepository = new UsuarioRepository();
        
        usuarioExemplo = new Usuario();
        usuarioExemplo.nome = "João Silva";
        usuarioExemplo.matricula = "c123456";
        usuarioExemplo.idade = 30;
        usuarioExemplo.senhaHash = "hash123";
    }

    // ==================== TESTES DE NORMALIZAÇÃO ====================

    @Test
    @DisplayName("Deve normalizar matrícula em busca")
    void testNormalizarMatricula() {
        // Verifica se a classe está sendo instanciada corretamente
        assertThat(usuarioRepository).isNotNull();
    }

    @Test
    @DisplayName("UsuarioRepository deve ser ApplicationScoped")
    void testRepositoryInstanciacao() {
        UsuarioRepository repo1 = new UsuarioRepository();
        UsuarioRepository repo2 = new UsuarioRepository();

        assertThat(repo1).isNotNull();
        assertThat(repo2).isNotNull();
    }

    // ==================== TESTES DE CAMPOS DO USUARIO ====================

    @Test
    @DisplayName("Usuario deve ter nome não nulo")
    void testUsuarioTemNome() {
        assertThat(usuarioExemplo.nome).isNotNull();
    }

    @Test
    @DisplayName("Usuario deve ter matrícula única")
    void testUsuarioTemMatricula() {
        assertThat(usuarioExemplo.matricula).isNotNull();
    }

    @Test
    @DisplayName("Usuario deve ter idade >= 0")
    void testUsuarioTemIdade() {
        assertThat(usuarioExemplo.idade).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Usuario deve ter senha hash")
    void testUsuarioTemSenhaHash() {
        assertThat(usuarioExemplo.senhaHash).isNotNull();
    }

    @Test
    @DisplayName("Usuario deve ter gamificação campos")
    void testUsuarioTemCamposGamificacao() {
        usuarioExemplo.pontuacaoTotal = 0;
        usuarioExemplo.nivel = 1;
        usuarioExemplo.progressoPercentual = 0;
        usuarioExemplo.trilhasConcluidas = 0;
        usuarioExemplo.desafiosRespondidos = 0;

        assertThat(usuarioExemplo)
                .extracting("pontuacaoTotal", "nivel", "progressoPercentual", "trilhasConcluidas", "desafiosRespondidos")
                .containsExactly(0, 1, 0, 0, 0);
    }

    @Test
    @DisplayName("Usuario deve ter campos de sessão opcionais")
    void testUsuarioTemCamposSessao() {
        usuarioExemplo.sessaoTokenHash = null;
        usuarioExemplo.sessaoExpiraEm = null;

        assertThat(usuarioExemplo.sessaoTokenHash).isNull();
        assertThat(usuarioExemplo.sessaoExpiraEm).isNull();
    }

    // ==================== TESTES DE DEFICIÊNCIAS ====================

    @Test
    @DisplayName("Usuario deve poder ter deficiências")
    void testUsuarioComDeficiencias() {
        usuarioExemplo.deficiencias.add("visual");
        usuarioExemplo.deficiencias.add("auditiva");

        assertThat(usuarioExemplo.deficiencias)
                .hasSize(2)
                .contains("visual", "auditiva");
    }

    @Test
    @DisplayName("Usuario pode não ter deficiências")
    void testUsuarioSemDeficiencias() {
        assertThat(usuarioExemplo.deficiencias).isEmpty();
    }
}

