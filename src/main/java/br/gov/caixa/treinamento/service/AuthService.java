package br.gov.caixa.treinamento.service;

import br.gov.caixa.treinamento.dto.CadastroUsuarioDTO;
import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Transactional
    public void cadastrarUsuario(CadastroUsuarioDTO dto) {
        validarCadastro(dto);

        Usuario usuario = new Usuario();
        usuario.nome = dto.nome.trim();
        usuario.matricula = dto.matricula.trim();
        usuario.idade = dto.idade;
        usuario.deficiencias = dto.deficiencias != null ? dto.deficiencias : new ArrayList<>();
        usuario.senhaHash = gerarHashSenha(dto.senha);

        usuarioRepository.persist(usuario);
    }

    public Optional<Usuario> autenticar(String matricula, String senha) {
        if (matricula == null || senha == null) {
            return Optional.empty();
        }

        Optional<Usuario> usuario = usuarioRepository.buscarPorMatricula(matricula.trim());

        if (usuario.isEmpty()) {
            return Optional.empty();
        }

        boolean senhaValida = usuario.get().senhaHash.equals(gerarHashSenha(senha));

        return senhaValida ? usuario : Optional.empty();
    }

    private void validarCadastro(CadastroUsuarioDTO dto) {
        if (dto.nome == null || dto.nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }

        if (dto.matricula == null || dto.matricula.isBlank()) {
            throw new IllegalArgumentException("Matrícula é obrigatória.");
        }

        if (dto.idade == null || dto.idade < 14) {
            throw new IllegalArgumentException("Idade inválida.");
        }

        if (dto.senha == null || dto.senha.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }

        if (!dto.senha.equals(dto.repetirSenha)) {
            throw new IllegalArgumentException("As senhas não conferem.");
        }

        if (usuarioRepository.existeMatricula(dto.matricula.trim())) {
            throw new IllegalArgumentException("Já existe cadastro para esta matrícula.");
        }
    }

    private String gerarHashSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(("caixaverso-salt-" + senha).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar hash da senha.", e);
        }
    }
}

