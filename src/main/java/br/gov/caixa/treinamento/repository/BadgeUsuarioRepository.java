package br.gov.caixa.treinamento.repository;

import br.gov.caixa.treinamento.model.BadgeUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class BadgeUsuarioRepository implements PanacheRepository<BadgeUsuario> {

    public List<BadgeUsuario> listarPorUsuario(Usuario usuario) {
        return list("usuario = ?1 order by dataConquista desc", usuario);
    }

    public boolean usuarioPossuiBadge(Usuario usuario, String nome) {
        return count("usuario = ?1 and nome = ?2", usuario, nome) > 0;
    }
}