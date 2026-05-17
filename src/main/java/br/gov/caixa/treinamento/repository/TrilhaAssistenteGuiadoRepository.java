package br.gov.caixa.treinamento.repository;

import br.gov.caixa.treinamento.model.TrilhaAssistenteGuiado;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TrilhaAssistenteGuiadoRepository implements PanacheRepository<TrilhaAssistenteGuiado> {

    public Optional<TrilhaAssistenteGuiado> buscarPorCodigo(String codigo) {
        return find("codigo", codigo).firstResultOptional();
    }

    public List<TrilhaAssistenteGuiado> listarAtivas() {
        return list("ativa = true order by ordem asc, titulo asc");
    }
}
