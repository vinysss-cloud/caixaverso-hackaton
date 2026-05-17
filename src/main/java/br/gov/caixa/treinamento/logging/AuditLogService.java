package br.gov.caixa.treinamento.logging;

import br.gov.caixa.treinamento.util.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Serviço centralizado para logs de auditoria do protótipo.
 *
 * Regras:
 * - não registrar senha, token de sessão, CSRF, cookie ou dados sensíveis;
 * - mascarar matrícula quando possível;
 * - usar eventos padronizados para facilitar leitura no arquivo de log.
 */
@ApplicationScoped
public class AuditLogService {

    private static final Logger AUDIT = LoggerFactory.getAuditLogger();
    private static final Logger SECURITY = LoggerFactory.getSecurityLogger();
    private static final Logger BUSINESS = LoggerFactory.getServiceLogger();

    public void usuarioAutenticado(String matricula) {
        AUDIT.infof("evento=USUARIO_AUTENTICADO matricula=%s", LogSanitizer.matriculaSegura(matricula));
    }

    public void usuarioCadastrado(String matricula) {
        AUDIT.infof("evento=USUARIO_CADASTRADO matricula=%s", LogSanitizer.matriculaSegura(matricula));
    }

    public void falhaAutenticacao(String matricula, String motivo) {
        SECURITY.warnf("evento=FALHA_AUTENTICACAO matricula=%s motivo=%s",
                LogSanitizer.matriculaSegura(matricula),
                LogSanitizer.textoSeguro(motivo));
    }

    public void csrfInvalido(String rota, String matricula) {
        SECURITY.warnf("evento=CSRF_INVALIDO rota=%s matricula=%s",
                LogSanitizer.textoSeguro(rota),
                LogSanitizer.matriculaSegura(matricula));
    }

    public void desafioBloqueado(String matricula, String codigoDesafio) {
        BUSINESS.infof("evento=DESAFIO_BLOQUEADO matricula=%s desafio=%s",
                LogSanitizer.matriculaSegura(matricula),
                LogSanitizer.textoSeguro(codigoDesafio));
    }

    public void desafioJaConcluido(String matricula, String codigoDesafio) {
        AUDIT.infof("evento=DESAFIO_JA_CONCLUIDO matricula=%s desafio=%s",
                LogSanitizer.matriculaSegura(matricula),
                LogSanitizer.textoSeguro(codigoDesafio));
    }

    public void desafioConcluido(String matricula, String codigoDesafio, Integer acertos, Integer percentual) {
        AUDIT.infof("evento=DESAFIO_CONCLUIDO matricula=%s desafio=%s acertos=%s percentual=%s",
                LogSanitizer.matriculaSegura(matricula),
                LogSanitizer.textoSeguro(codigoDesafio),
                acertos == null ? "-" : acertos,
                percentual == null ? "-" : percentual);
    }

    public void erroAplicacao(String evento, Throwable erro) {
        BUSINESS.errorf(erro, "evento=%s erro=%s", LogSanitizer.textoSeguro(evento), LogSanitizer.textoSeguro(erro.getMessage()));
    }
}
