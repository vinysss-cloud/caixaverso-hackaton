package br.gov.caixa.treinamento.logging;

import br.gov.caixa.treinamento.util.LoggerFactory;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Loga início/fim das requisições HTML/API do protótipo.
 * Ajuda a rastrear erros sem expor cookies, tokens ou payloads sensíveis.
 */
@Provider
@Priority(Priorities.USER)
public class RequestLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger ACCESS = LoggerFactory.getAccessLogger();
    private static final String REQUEST_ID = "requestId";
    private static final String START_TIME = "requestStartTime";

    private static final Set<String> PREFIXOS_IGNORADOS = Set.of(
            "/css/",
            "/js/",
            "/img/",
            "/assets/",
            "/favicon.ico"
    );

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = normalizarPath(requestContext.getUriInfo().getPath());

        if (ignorar(path)) {
            return;
        }

        String requestId = UUID.randomUUID().toString();
        requestContext.setProperty(REQUEST_ID, requestId);
        requestContext.setProperty(START_TIME, System.currentTimeMillis());
        MDC.put(REQUEST_ID, requestId);

        ACCESS.infof("evento=HTTP_REQUEST_START requestId=%s metodo=%s path=%s",
                requestId,
                LogSanitizer.textoSeguro(requestContext.getMethod()),
                LogSanitizer.textoSeguro(path));
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String path = normalizarPath(requestContext.getUriInfo().getPath());

        if (ignorar(path)) {
            return;
        }

        String requestId = String.valueOf(requestContext.getProperty(REQUEST_ID));
        Object start = requestContext.getProperty(START_TIME);
        long duracaoMs = start instanceof Long inicio ? System.currentTimeMillis() - inicio : -1L;

        ACCESS.infof("evento=HTTP_REQUEST_END requestId=%s metodo=%s path=%s status=%s duracaoMs=%s",
                LogSanitizer.textoSeguro(requestId),
                LogSanitizer.textoSeguro(requestContext.getMethod()),
                LogSanitizer.textoSeguro(path),
                responseContext.getStatus(),
                duracaoMs);

        MDC.remove(REQUEST_ID);
    }

    private boolean ignorar(String path) {
        return PREFIXOS_IGNORADOS.stream().anyMatch(path::startsWith);
    }

    private String normalizarPath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        String normalizado = path.trim();
        return normalizado.startsWith("/") ? normalizado : "/" + normalizado;
    }
}
