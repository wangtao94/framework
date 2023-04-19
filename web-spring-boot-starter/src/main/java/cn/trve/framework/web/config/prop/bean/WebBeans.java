package cn.trve.framework.web.config.prop.bean;

import cn.trve.framework.web.aop.ExceptionFilter;
import cn.trve.framework.web.aop.WebExceptionHandler;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

/**
 * <pre>
 * <b></b>
 * <b>Description:</b>
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/4/19 14:59    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @Date 2023/4/19
 * @since 2023/4/19
 */
public class WebBeans {
    //<editor-fold desc="异常处理">
    @Bean
    public WebExceptionHandler globalExceptionHandler() {
        return new WebExceptionHandler();
    }

    /**
     * 异常过滤器
     *
     * @param resolver 默认的异常处理解析器
     * @return 异常过滤器
     * 将filter的异常委托给全局异常处理器处理.
     */
    @Bean
    public FilterRegistrationBean<ExceptionFilter> exceptionFilterRegistration(ExceptionHandlerExceptionResolver resolver) {
        FilterRegistrationBean<ExceptionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ExceptionFilter(resolver));
        //尽可能的早执行，以处理更多的异常
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
    //</editor-fold>
}
