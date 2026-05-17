package br.gov.caixa.treinamento.repository;

import br.gov.caixa.treinamento.model.DesafioAssistivo;
import br.gov.caixa.treinamento.model.ResultadoDesafioUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ResultadoDesafioUsuarioRepository implements PanacheRepository<ResultadoDesafioUsuario> {

    public Optional<ResultadoDesafioUsuario> buscarPorUsuarioEDesafio(Usuario usuario, DesafioAssistivo desafio) {
        return find("usuario = ?1 and desafio = ?2", usuario, desafio).firstResultOptional();
    }

    public boolean usuarioJaConcluiu(Usuario usuario, DesafioAssistivo desafio) {
        return count("usuario = ?1 and desafio = ?2", usuario, desafio) > 0;
    }

    public List<ResultadoDesafioUsuario> listarPorUsuario(Usuario usuario) {
        return list("usuario = ?1 order by dataConclusao desc", usuario);
    }

    public long apagarPorUsuario(Usuario usuario) {
        return delete("usuario = ?1", usuario);
    }
}
