package br.gov.caixa.treinamento.repository;

import br.gov.caixa.treinamento.model.PainelResumoUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PainelResumoUsuarioRepository implements PanacheRepository<PainelResumoUsuario> {

    public Optional<PainelResumoUsuario> buscarPorUsuario(Usuario usuario) {
        return find("usuario", usuario).firstResultOptional();
    }
}
