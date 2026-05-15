package br.gov.caixa.treinamento.bootstrap;

import br.gov.caixa.treinamento.dto.CadastroUsuarioDTO;
import br.gov.caixa.treinamento.repository.UsuarioRepository;
import br.gov.caixa.treinamento.service.AuthService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DataLoader {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    AuthService authService;

    @PostConstruct
    void load() {
        try {
            if (!usuarioRepository.existeMatricula("123456")) {
                CadastroUsuarioDTO dto = new CadastroUsuarioDTO();
                dto.nome = "Usuário Teste";
                dto.matricula = "123456";
                dto.idade = 30;
                dto.deficiencias = null;
                dto.senha = "123456";
                dto.repetirSenha = "123456";
                authService.cadastrarUsuario(dto);
            }
        } catch (Exception e) {
            // ignore seed errors
        }
    }
}

