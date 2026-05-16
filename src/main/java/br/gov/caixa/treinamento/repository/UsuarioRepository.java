package br.gov.caixa.treinamento.repository;

import br.gov.caixa.treinamento.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {

    public Optional<Usuario> buscarPorMatricula(String matricula) {
        return find("matricula", matricula).firstResultOptional();
    }

    public boolean existeMatricula(String matricula) {
        return buscarPorMatricula(matricula).isPresent();
    }

    public Optional<Usuario> buscarPorSessaoTokenHash(String sessaoTokenHash) {
        return find("sessaoTokenHash", sessaoTokenHash).firstResultOptional();
    }
}