package cn.trve.framework.web.aop;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.IOException;

/**
 * 将filter的异常委托给全局异常处理器处理.
 *
 * @author Wangtao
 * @see WebExceptionHandler
 */
public class ExceptionFilter implements Filter {
    private final ExceptionHandlerExceptionResolver resolver;

    public ExceptionFilter(ExceptionHandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            resolver.resolveException((HttpServletRequest) request, (HttpServletResponse) response, null, e);
        }
    }
}
