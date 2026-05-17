package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.dto.CadastroUsuarioDTO;
import br.gov.caixa.treinamento.logging.AuditLogService;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.repository.UsuarioRepository;
import br.gov.caixa.treinamento.util.LoggerFactory;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

    private static final Logger logger = LoggerFactory.getSecurityLogger();
    private static final Logger auditLogger = LoggerFactory.getAuditLogger();

    private static final int TEMPO_SESSAO_HORAS = 2;
    private static final String LEGACY_SALT = "caixaverso-salt-";

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    AuditLogService auditLogService;

    @Transactional
    public void cadastrarUsuario(CadastroUsuarioDTO dto) {
        validarCadastro(dto);

        Usuario usuario = new Usuario();
        usuario.nome = dto.nome.trim();
        usuario.matricula = normalizarMatricula(dto.matricula);
        usuario.idade = dto.idade;
        usuario.preferenciasAcessibilidade = dto.preferenciasAcessibilidade != null ? dto.preferenciasAcessibilidade : new ArrayList<>();
        usuario.senhaHash = BcryptUtil.bcryptHash(dto.senha);

        normalizarCamposGamificacao(usuario);
        usuarioRepository.persist(usuario);
        
        auditLogger.infof("evento=USUARIO_CADASTRADO matricula=%s", usuario.matricula);
        if (auditLogService != null) {
            auditLogService.usuarioCadastrado(usuario.matricula);
        }
    }

    @Transactional
    public Optional<Usuario> autenticar(String matricula, String senha) {
        if (matricula == null || matricula.isBlank() || senha == null || senha.isBlank()) {
            logger.warn("Tentativa de autenticação com credenciais vazias");
            return Optional.empty();
        }

        String matriculaNormalizada = normalizarMatricula(matricula);
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorMatricula(matriculaNormalizada);

        if (usuarioOpt.isEmpty()) {
            logger.warnf("evento=AUTENTICACAO_FALHA matricula=%s motivo=MATRICULA_INEXISTENTE", matriculaNormalizada);
            if (auditLogService != null) {
                auditLogService.falhaAutenticacao(matriculaNormalizada, "Matrícula inexistente");
            }
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();

        if (senhaConfere(usuario, senha)) {
            normalizarCamposGamificacao(usuario);
            auditLogger.infof("evento=AUTENTICACAO_SUCESSO matricula=%s", matriculaNormalizada);
            if (auditLogService != null) {
                auditLogService.usuarioAutenticado(matriculaNormalizada);
            }
            return Optional.of(usuario);
        }

        logger.warnf("evento=AUTENTICACAO_FALHA matricula=%s motivo=SENHA_INVALIDA", matriculaNormalizada);
        if (auditLogService != null) {
            auditLogService.falhaAutenticacao(matriculaNormalizada, "Senha inválida");
        }
        return Optional.empty();
    }

    public boolean existeMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            return false;
        }

        return usuarioRepository.existeMatricula(normalizarMatricula(matricula));
    }

    @Transactional
    public String criarSessao(Usuario usuario) {
        if (usuario == null || usuario.id == null) {
            throw new IllegalArgumentException("Usuário inválido para criação de sessão.");
        }

        Usuario usuarioGerenciado = usuarioRepository.findById(usuario.id);

        if (usuarioGerenciado == null) {
            throw new IllegalStateException("Usuário não encontrado para criação da sessão.");
        }

        String token = gerarTokenSeguro();

        usuarioGerenciado.sessaoTokenHash = gerarHashToken(token);
        usuarioGerenciado.sessaoExpiraEm = LocalDateTime.now().plusHours(TEMPO_SESSAO_HORAS);

        return token;
    }

    public Optional<Usuario> buscarUsuarioPorTokenSessao(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String tokenHash = gerarHashToken(token);
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorSessaoTokenHash(tokenHash);

        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.sessaoExpiraEm == null || usuario.sessaoExpiraEm.isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }

        return Optional.of(usuario);
    }

    @Transactional
    public void encerrarSessao(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        String tokenHash = gerarHashToken(token);

        usuarioRepository.buscarPorSessaoTokenHash(tokenHash).ifPresent(usuario -> {
            usuario.sessaoTokenHash = null;
            usuario.sessaoExpiraEm = null;
        });
    }

    private boolean senhaConfere(Usuario usuario, String senha) {
        if (usuario.senhaHash == null || usuario.senhaHash.isBlank()) {
            return false;
        }

        if (usuario.senhaHash.startsWith("$2a$") || usuario.senhaHash.startsWith("$2b$") || usuario.senhaHash.startsWith("$2y$")) {
            return BcryptUtil.matches(senha, usuario.senhaHash);
        }

        // Compatibilidade com usuários antigos criados antes do BCrypt.
        String legado = gerarHashSenhaLegado(senha);
        boolean senhaLegadaConfere = legado.equals(usuario.senhaHash);

        if (senhaLegadaConfere) {
            usuario.senhaHash = BcryptUtil.bcryptHash(senha);
        }

        return senhaLegadaConfere;
    }

    private void validarCadastro(CadastroUsuarioDTO dto) {
        if (dto.nome == null || dto.nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }

        if (dto.matricula == null || dto.matricula.isBlank()) {
            throw new IllegalArgumentException("Matrícula é obrigatória.");
        }

        if (dto.idade == null || dto.idade < 14) {
            throw new IllegalArgumentException("Idade inválida. Informe uma idade igual ou maior que 14 anos.");
        }

        validarSenhaForte(dto.senha);

        if (dto.repetirSenha == null || !dto.senha.equals(dto.repetirSenha)) {
            throw new IllegalArgumentException("As senhas não conferem. Digite a mesma senha nos dois campos.");
        }

        if (usuarioRepository.existeMatricula(normalizarMatricula(dto.matricula))) {
            throw new IllegalArgumentException("Já existe cadastro para esta matrícula. Volte para a tela de login para acessar sua conta.");
        }
    }

    private void validarSenhaForte(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }

        if (senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }

        if (!senha.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("A senha deve conter pelo menos uma letra maiúscula.");
        }

        if (!senha.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("A senha deve conter pelo menos uma letra minúscula.");
        }

        if (!senha.matches(".*\\d.*")) {
            throw new IllegalArgumentException("A senha deve conter pelo menos um número.");
        }

        if (!senha.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException("A senha deve conter pelo menos um caractere especial.");
        }
    }

    private String normalizarMatricula(String matricula) {
        return matricula.trim().toLowerCase();
    }

    private String gerarTokenSeguro() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String gerarHashToken(String token) {
        return gerarSha256(token);
    }

    private String gerarHashSenhaLegado(String senha) {
        return gerarSha256(LEGACY_SALT + senha);
    }

    private String gerarSha256(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar hash SHA-256.", e);
        }
    }

    private void normalizarCamposGamificacao(Usuario usuario) {
        if (usuario.pontuacaoTotal == null) {
            usuario.pontuacaoTotal = 0;
        }

        if (usuario.nivel == null) {
            usuario.nivel = 1;
        }

        if (usuario.progressoPercentual == null) {
            usuario.progressoPercentual = 0;
        }

        if (usuario.trilhasConcluidas == null) {
            usuario.trilhasConcluidas = 0;
        }

        if (usuario.desafiosRespondidos == null) {
            usuario.desafiosRespondidos = 0;
        }
    }
}
