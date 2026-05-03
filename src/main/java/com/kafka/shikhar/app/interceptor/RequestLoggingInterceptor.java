package com.kafka.shikhar.app.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

/**
 * Spring MVC interceptor that auto-generates a unique {@code requestId} per
 * HTTP request and logs entry/exit with timing information.
 *
 * <h2>What it does</h2>
 * <ol>
 *   <li><b>preHandle</b> – Generates a {@code requestId} (UUID), stores it in the
 *       SLF4J {@link MDC} so it appears in every log line for this request, and
 *       logs the incoming HTTP method + URI.</li>
 *   <li><b>afterCompletion</b> – Logs the HTTP status, elapsed time, and then
 *       clears the MDC to prevent context leakage across thread-pool reuse.</li>
 * </ol>
 *
 * <h2>MDC key</h2>
 * The {@code requestId} key can be included in your Logback pattern:
 * <pre>
 *   %X{requestId}
 * </pre>
 * See {@code src/main/resources/logback-spring.xml} for the configured pattern.
 *
 * <h2>Registration</h2>
 * Registered via {@link com.kafka.shikhar.app.config.WebMvcConfig}.
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    /** MDC key used to correlate all log lines within a single HTTP request. */
    public static final String MDC_REQUEST_ID = "requestId";

    /** Request attribute used to store the start timestamp. */
    private static final String ATTR_START_TIME = "reqStartTime";

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull Object              handler
    ) {
        // ── Auto-generate request ID ──────────────────────────────────────────
        String requestId = UUID.randomUUID().toString();
        MDC.put(MDC_REQUEST_ID, requestId);

        // ── Propagate to response header so callers can correlate log traces ──
        response.setHeader("X-Request-Id", requestId);

        // ── Store start time for elapsed-time calculation ─────────────────────
        request.setAttribute(ATTR_START_TIME, System.currentTimeMillis());

        log.info("→ {} {} [requestId={}]",
                request.getMethod(), request.getRequestURI(), requestId);

        return true; // allow request to proceed
    }

    @Override
    public void postHandle(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull Object              handler,
                     ModelAndView        modelAndView
    ) {
        // nothing to do here for REST APIs
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull Object              handler,
                     Exception           ex
    ) {
        long startTime = (Long) request.getAttribute(ATTR_START_TIME);
        long elapsed   = System.currentTimeMillis() - startTime;
        String requestId = MDC.get(MDC_REQUEST_ID);

        if (ex != null) {
            log.error("← {} {} → {} [{} ms] [requestId={}] EXCEPTION: {}",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), elapsed, requestId, ex.getMessage());
        } else {
            log.info("← {} {} → {} [{} ms] [requestId={}]",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), elapsed, requestId);
        }

        // ── Always clear MDC to avoid leaking context across thread reuse ─────
        MDC.remove(MDC_REQUEST_ID);
    }
}
