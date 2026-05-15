package br.gov.caixa.treinamento.repository;

import br.gov.caixa.treinamento.model.ProgressoTreinamentoUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class ProgressoTreinamentoUsuarioRepository implements PanacheRepository<ProgressoTreinamentoUsuario> {

    public Optional<ProgressoTreinamentoUsuario> buscarPorUsuarioECodigo(Usuario usuario, String codigoTreinamento) {
        return find("usuario = ?1 and codigoTreinamento = ?2", usuario, codigoTreinamento).firstResultOptional();
    }
}