package br.gov.caixa.treinamento.repository;

import br.gov.caixa.treinamento.model.AtividadeUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AtividadeUsuarioRepository implements PanacheRepository<AtividadeUsuario> {

    public List<AtividadeUsuario> listarPorUsuario(Usuario usuario) {
        return list("usuario = ?1 order by dataHora desc", usuario);
    }

    public long apagarPorUsuario(Usuario usuario) {
        return delete("usuario = ?1", usuario);
    }
}