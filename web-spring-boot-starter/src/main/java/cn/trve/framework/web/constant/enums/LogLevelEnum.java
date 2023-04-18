package cn.trve.framework.web.constant.enums;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * <pre>
 * <b>日志级别</b>
 * <b>Description: 定义在全局异常处理的日志打印级别</b>
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/3/27 10:51    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @Date 2023/3/27
 * @since 2023/3/27
 */
public enum LogLevelEnum {
    /**
     * 跟踪
     */
    TRACE(),
    /**
     * 调试
     */
    DEBUG(),
    /**
     * 信息
     */
    INFO(),
    /**
     * 警告
     */
    WARN(),
    /**
     * 错误
     */
    ERROR(),
    /**
     * 不打印日志
     */
    NONE(),

    ;

    public Method getLogMethod() {
        Method method;
        try {
            method = Logger.class.getMethod(Strings.toRootLowerCase(this.name()), String.class, Throwable.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return method;
    }
}
