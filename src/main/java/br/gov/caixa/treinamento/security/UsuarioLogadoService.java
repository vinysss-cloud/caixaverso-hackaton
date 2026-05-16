package br.gov.caixa.treinamento.security;

import br.gov.caixa.treinamento.model.Usuario;
import br.gov.caixa.treinamento.service.AuthService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class UsuarioLogadoService {

    @Inject
    AuthService authService;

    public Optional<Usuario> buscarPorToken(String tokenSessao) {
        return authService.buscarUsuarioPorTokenSessao(tokenSessao);
    }

    public String matriculaOuNulo(String tokenSessao) {
        return buscarPorToken(tokenSessao)
                .map(usuario -> usuario.matricula)
                .orElse(null);
    }
}
