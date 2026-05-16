package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.dto.CadastroUsuarioDTO;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes unitários")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthService authService;

    private CadastroUsuarioDTO dto;
    private Usuario usuarioExistente;

    @BeforeEach
    void setup() {
        dto = new CadastroUsuarioDTO();
        dto.nome = "João Silva";
        dto.matricula = "c123456";
        dto.idade = 30;
        dto.deficiencias = new ArrayList<>();
        dto.senha = "Senha@123";
        dto.repetirSenha = "Senha@123";

        usuarioExistente = new Usuario();
        usuarioExistente.id = 1L;
        usuarioExistente.nome = "João Silva";
        usuarioExistente.matricula = "c123456";
        usuarioExistente.idade = 30;
        usuarioExistente.deficiencias = new ArrayList<>();
        usuarioExistente.senhaHash = BcryptUtil.bcryptHash("Senha@123");
        usuarioExistente.pontuacaoTotal = 0;
        usuarioExistente.nivel = 1;
        usuarioExistente.progressoPercentual = 0;
        usuarioExistente.trilhasConcluidas = 0;
        usuarioExistente.desafiosRespondidos = 0;
    }

    // ==================== TESTES DE CADASTRO ====================

    @Test
    @DisplayName("Deve cadastrar usuário com dados válidos")
    void testCadastrarUsuarioComDadosValidos() {
        when(usuarioRepository.existeMatricula(anyString())).thenReturn(false);

        authService.cadastrarUsuario(dto);

        verify(usuarioRepository, times(1)).persist(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar sem nome")
    void testCadastrarUsuarioSemNome() {
        dto.nome = null;

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome é obrigatório");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar com nome em branco")
    void testCadastrarUsuarioComNomeEmBranco() {
        dto.nome = "   ";

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome é obrigatório");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar sem matricula")
    void testCadastrarUsuarioSemMatricula() {
        dto.matricula = null;

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Matrícula é obrigatória");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar com idade inferior a 14 anos")
    void testCadastrarUsuarioComIdadeInvalida() {
        dto.idade = 13;

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Idade inválida");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar com senha fraca (sem maiúscula)")
    void testCadastrarUsuarioComSenhaFracaSemMaiuscula() {
        dto.senha = "senha@123";
        dto.repetirSenha = "senha@123";

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("letra maiúscula");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar com senha fraca (sem minúscula)")
    void testCadastrarUsuarioComSenhaFracaSemMinuscula() {
        dto.senha = "SENHA@123";
        dto.repetirSenha = "SENHA@123";

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("letra minúscula");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar com senha fraca (sem número)")
    void testCadastrarUsuarioComSenhaFracaSemNumero() {
        dto.senha = "Senha@abc";
        dto.repetirSenha = "Senha@abc";

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("número");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar com senha fraca (sem caractere especial)")
    void testCadastrarUsuarioComSenhaFracaSemEspecial() {
        dto.senha = "Senha123";
        dto.repetirSenha = "Senha123";

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("caractere especial");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar com senha muito curta")
    void testCadastrarUsuarioComSenhaCorta() {
        dto.senha = "S@1a";
        dto.repetirSenha = "S@1a";

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("8 caracteres");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar com senhas não correspondentes")
    void testCadastrarUsuarioComSenhasNaoCorrespondentes() {
        dto.repetirSenha = "OutraSenha@123";

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não conferem");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar com matrícula já existente")
    void testCadastrarUsuarioComMatriculaDuplicada() {
        when(usuarioRepository.existeMatricula(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.cadastrarUsuario(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Já existe cadastro para esta matrícula");
    }

    // ==================== TESTES DE AUTENTICAÇÃO ====================

    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas")
    void testAutenticarComCredenciaisValidas() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        Optional<Usuario> resultado = authService.autenticar("c123456", "Senha@123");

        assertThat(resultado)
                .isPresent()
                .get()
                .extracting(Usuario::getClass)
                .isNotNull();

        verify(usuarioRepository, times(1)).buscarPorMatricula("c123456");
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com matrícula nula")
    void testAutenticarComMatriculaNula() {
        Optional<Usuario> resultado = authService.autenticar(null, "Senha@123");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com matrícula em branco")
    void testAutenticarComMatriculaEmBranco() {
        Optional<Usuario> resultado = authService.autenticar("   ", "Senha@123");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com senha nula")
    void testAutenticarComSenhaNula() {
        Optional<Usuario> resultado = authService.autenticar("c123456", null);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com senha em branco")
    void testAutenticarComSenhaEmBranco() {
        Optional<Usuario> resultado = authService.autenticar("c123456", "   ");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com matrícula não encontrada")
    void testAutenticarComMatriculaInexistente() {
        when(usuarioRepository.buscarPorMatricula("inexistente"))
                .thenReturn(Optional.empty());

        Optional<Usuario> resultado = authService.autenticar("inexistente", "Senha@123");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com senha incorreta")
    void testAutenticarComSenhaIncorreta() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        Optional<Usuario> resultado = authService.autenticar("c123456", "SenhaErrada@123");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve normalizar matrícula para minúscula durante autenticação")
    void testAutenticarComMatriculaMaiuscula() {
        when(usuarioRepository.buscarPorMatricula("c123456"))
                .thenReturn(Optional.of(usuarioExistente));

        authService.autenticar("C123456", "Senha@123");

        verify(usuarioRepository, times(1)).buscarPorMatricula("c123456");
    }

    // ==================== TESTES DE VERIFICAÇÃO DE MATRÍCULA ====================

    @Test
    @DisplayName("Deve verificar que matrícula existe")
    void testExisteMatricula() {
        when(usuarioRepository.existeMatricula("c123456")).thenReturn(true);

        boolean existe = authService.existeMatricula("c123456");

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve verificar que matrícula não existe")
    void testNaoExisteMatricula() {
        when(usuarioRepository.existeMatricula("inexistente")).thenReturn(false);

        boolean existe = authService.existeMatricula("inexistente");

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false para matrícula nula")
    void testExisteMatriculaNula() {
        boolean existe = authService.existeMatricula(null);

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false para matrícula em branco")
    void testExisteMatriculaEmBranco() {
        boolean existe = authService.existeMatricula("   ");

        assertThat(existe).isFalse();
    }

    // ==================== TESTES DE SESSÃO ====================

    @Test
    @DisplayName("Deve criar sessão para usuário válido")
    void testCriarSessaoParaUsuarioValido() {
        when(usuarioRepository.findById(1L)).thenReturn(usuarioExistente);

        String token = authService.criarSessao(usuarioExistente);

        assertThat(token).isNotBlank();
        assertThat(usuarioExistente.sessaoTokenHash).isNotNull();
        assertThat(usuarioExistente.sessaoExpiraEm).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve falhar ao criar sessão com usuário nulo")
    void testCriarSessaoComUsuarioNulo() {
        assertThatThrownBy(() -> authService.criarSessao(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    @DisplayName("Deve falhar ao criar sessão com id nulo")
    void testCriarSessaoComIdNulo() {
        usuarioExistente.id = null;

        assertThatThrownBy(() -> authService.criarSessao(usuarioExistente))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve buscar usuário por token válido")
    void testBuscarUsuarioPorTokenValido() {
        String token = "token123";
        String tokenHashEsperado = "b13ae34b9231f95675ff502761b64aedd0c278f5b6e8730ce99b72f35b2ddfb0";

        usuarioExistente.sessaoTokenHash = tokenHashEsperado;
        usuarioExistente.sessaoExpiraEm = LocalDateTime.now().plusHours(1);

        when(usuarioRepository.buscarPorSessaoTokenHash(tokenHashEsperado))
                .thenReturn(Optional.of(usuarioExistente));

        Optional<Usuario> resultado = authService.buscarUsuarioPorTokenSessao(token);

        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isSameAs(usuarioExistente);
        verify(usuarioRepository).buscarPorSessaoTokenHash(tokenHashEsperado);
    }

    @Test
    @DisplayName("Deve rejeitar token nulo")
    void testBuscarUsuarioPorTokenNulo() {
        Optional<Usuario> resultado = authService.buscarUsuarioPorTokenSessao(null);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar token expirado")
    void testBuscarUsuarioPorTokenExpirado() {
        usuarioExistente.sessaoTokenHash = "hash123";
        usuarioExistente.sessaoExpiraEm = LocalDateTime.now().minusHours(1);

        when(usuarioRepository.buscarPorSessaoTokenHash(anyString()))
                .thenReturn(Optional.of(usuarioExistente));

        Optional<Usuario> resultado = authService.buscarUsuarioPorTokenSessao("token123");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve encerrar sessão válida")
    void testEncerrarSessaoValida() {
        usuarioExistente.sessaoTokenHash = "hash123";
        usuarioExistente.sessaoExpiraEm = LocalDateTime.now().plusHours(1);

        when(usuarioRepository.buscarPorSessaoTokenHash(anyString()))
                .thenReturn(Optional.of(usuarioExistente));

        authService.encerrarSessao("token123");

        verify(usuarioRepository, times(1)).buscarPorSessaoTokenHash(anyString());
    }

    @Test
    @DisplayName("Deve ignorar encerramento com token nulo")
    void testEncerrarSessaoComTokenNulo() {
        authService.encerrarSessao(null);

        verify(usuarioRepository, never()).buscarPorSessaoTokenHash(anyString());
    }
}

