package br.gov.caixa.treinamento.util;

import org.jboss.logging.Logger;

/**
 * Classe utilitária para obter loggers personalizados com níveis apropriados.
 * Facilita rastreabilidade, debugging e segurança.
 */
public class LoggerFactory {

    private LoggerFactory() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
    }

    /**
     * Obtém um logger para a classe fornecida.
     *
     * @param clazz a classe para a qual obter o logger
     * @return Logger para a classe
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz);
    }

    /**
     * Obtém um logger com nome customizado.
     *
     * @param name nome do logger
     * @return Logger com o nome fornecido
     */
    public static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }

    /**
     * Logger para segurança (autenticação, autorização)
     */
    public static Logger getSecurityLogger() {
        return Logger.getLogger("br.gov.caixa.treinamento.security");
    }

    /**
     * Logger para auditoria (ações de usuários)
     */
    public static Logger getAuditLogger() {
        return Logger.getLogger("br.gov.caixa.treinamento.audit");
    }

    /**
     * Logger para serviços de negócio
     */
    public static Logger getServiceLogger() {
        return Logger.getLogger("br.gov.caixa.treinamento.service");
    }

    /**
     * Logger para acesso a dados.
     */
    public static Logger getDataAccessLogger() {
        return Logger.getLogger("br.gov.caixa.treinamento.repository");
    }

    /**
     * Logger para requisições HTTP.
     */
    public static Logger getAccessLogger() {
        return Logger.getLogger("br.gov.caixa.treinamento.access");
    }
}

