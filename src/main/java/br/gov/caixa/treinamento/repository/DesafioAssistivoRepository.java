package br.gov.caixa.treinamento.repository;

import br.gov.caixa.treinamento.model.DesafioAssistivo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DesafioAssistivoRepository implements PanacheRepository<DesafioAssistivo> {

    public Optional<DesafioAssistivo> buscarPorCodigo(String codigo) {
        return find("codigo", codigo).firstResultOptional();
    }

    public List<DesafioAssistivo> listarPorStatus(String status) {
        return list("status = ?1 order by id asc", status);
    }
}
