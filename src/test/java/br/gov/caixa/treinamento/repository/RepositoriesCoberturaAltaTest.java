package br.gov.caixa.treinamento.repository;

import br.gov.caixa.treinamento.model.AtividadeUsuario;
import br.gov.caixa.treinamento.model.BadgeUsuario;
import br.gov.caixa.treinamento.model.ProgressoTreinamentoUsuario;
import br.gov.caixa.treinamento.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Repositories - Cobertura alta sem banco")
class RepositoriesCoberturaAltaTest {

    @Test
    @DisplayName("AtividadeUsuarioRepository deve listar e apagar atividades do usuário")
    void atividadeRepository_deveListarEApagarPorUsuario() {
        AtividadeUsuarioRepository repository = spy(new AtividadeUsuarioRepository());
        Usuario usuario = usuarioExemplo();
        AtividadeUsuario atividade = new AtividadeUsuario();
        atividade.usuario = usuario;

        doReturn(List.of(atividade))
                .when(repository)
                .list("usuario = ?1 order by dataHora desc", usuario);
        doReturn(1L)
                .when(repository)
                .delete("usuario = ?1", usuario);

        assertThat(repository.listarPorUsuario(usuario)).containsExactly(atividade);
        assertThat(repository.apagarPorUsuario(usuario)).isEqualTo(1L);

        verify(repository).list("usuario = ?1 order by dataHora desc", usuario);
        verify(repository).delete("usuario = ?1", usuario);
    }

    @Test
    @DisplayName("BadgeUsuarioRepository deve listar, verificar existência e apagar badges")
    void badgeRepository_deveCobrirMetodos() {
        BadgeUsuarioRepository repository = spy(new BadgeUsuarioRepository());
        Usuario usuario = usuarioExemplo();
        BadgeUsuario badge = new BadgeUsuario();
        badge.usuario = usuario;
        badge.nome = "Primeiro Acesso";

        doReturn(List.of(badge))
                .when(repository)
                .list("usuario = ?1 order by dataConquista desc", usuario);
        doReturn(1L)
                .when(repository)
                .count("usuario = ?1 and nome = ?2", usuario, "Primeiro Acesso");
        doReturn(0L)
                .when(repository)
                .count("usuario = ?1 and nome = ?2", usuario, "Inexistente");
        doReturn(2L)
                .when(repository)
                .delete("usuario = ?1", usuario);

        assertThat(repository.listarPorUsuario(usuario)).containsExactly(badge);
        assertThat(repository.usuarioPossuiBadge(usuario, "Primeiro Acesso")).isTrue();
        assertThat(repository.usuarioPossuiBadge(usuario, "Inexistente")).isFalse();
        assertThat(repository.apagarPorUsuario(usuario)).isEqualTo(2L);

        verify(repository).list("usuario = ?1 order by dataConquista desc", usuario);
        verify(repository).count("usuario = ?1 and nome = ?2", usuario, "Primeiro Acesso");
        verify(repository).count("usuario = ?1 and nome = ?2", usuario, "Inexistente");
        verify(repository).delete("usuario = ?1", usuario);
    }

    @Test
    @DisplayName("ProgressoTreinamentoUsuarioRepository deve buscar e apagar progresso")
    void progressoRepository_deveCobrirMetodos() {
        ProgressoTreinamentoUsuarioRepository repository = spy(new ProgressoTreinamentoUsuarioRepository());
        Usuario usuario = usuarioExemplo();
        ProgressoTreinamentoUsuario progresso = new ProgressoTreinamentoUsuario();
        progresso.usuario = usuario;
        progresso.codigoTreinamento = "abertura-conta";

        @SuppressWarnings("unchecked")
        PanacheQuery<ProgressoTreinamentoUsuario> query = mock(PanacheQuery.class);
        when(query.firstResultOptional()).thenReturn(Optional.of(progresso));

        doReturn(query)
                .when(repository)
                .find("usuario = ?1 and codigoTreinamento = ?2", usuario, "abertura-conta");
        doReturn(1L)
                .when(repository)
                .delete("usuario = ?1 and codigoTreinamento = ?2", usuario, "abertura-conta");
        doReturn(3L)
                .when(repository)
                .delete("usuario = ?1", usuario);

        assertThat(repository.buscarPorUsuarioECodigo(usuario, "abertura-conta")).contains(progresso);
        assertThat(repository.apagarPorUsuarioECodigo(usuario, "abertura-conta")).isEqualTo(1L);
        assertThat(repository.apagarPorUsuario(usuario)).isEqualTo(3L);

        verify(repository).find("usuario = ?1 and codigoTreinamento = ?2", usuario, "abertura-conta");
        verify(repository).delete("usuario = ?1 and codigoTreinamento = ?2", usuario, "abertura-conta");
        verify(repository).delete("usuario = ?1", usuario);
    }

    @Test
    @DisplayName("UsuarioRepository deve buscar por matrícula, sessão e verificar existência")
    void usuarioRepository_deveCobrirMetodosReais() {
        UsuarioRepository repository = spy(new UsuarioRepository());
        Usuario usuario = usuarioExemplo();
        usuario.sessaoTokenHash = "hash-sessao";

        @SuppressWarnings("unchecked")
        PanacheQuery<Usuario> queryMatricula = mock(PanacheQuery.class);
        when(queryMatricula.firstResultOptional()).thenReturn(Optional.of(usuario));

        @SuppressWarnings("unchecked")
        PanacheQuery<Usuario> querySessao = mock(PanacheQuery.class);
        when(querySessao.firstResultOptional()).thenReturn(Optional.of(usuario));

        doReturn(queryMatricula).when(repository).find("matricula", "c123456");
        doReturn(querySessao).when(repository).find("sessaoTokenHash", "hash-sessao");

        assertThat(repository.buscarPorMatricula("c123456")).contains(usuario);
        assertThat(repository.existeMatricula("c123456")).isTrue();
        assertThat(repository.buscarPorSessaoTokenHash("hash-sessao")).contains(usuario);

        verify(repository, times(2)).find("matricula", "c123456");
        verify(repository).find("sessaoTokenHash", "hash-sessao");
    }

    private Usuario usuarioExemplo() {
        Usuario usuario = new Usuario();
        usuario.nome = "Usuário Teste";
        usuario.matricula = "c123456";
        usuario.idade = 30;
        usuario.senhaHash = "hash";
        return usuario;
    }
}
