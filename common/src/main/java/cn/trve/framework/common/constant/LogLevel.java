package cn.trve.framework.common.constant;

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
public enum LogLevel {
    /**
     * 跟踪
     */
    TRACE,
    /**
     * 调试
     */
    DEBUG,
    /**
     * 信息
     */
    INFO,
    /**
     * 警告
     */
    WARN,
    /**
     * 错误
     */
    ERROR,
    /**
     * 不打印日志
     */
    NONE,

}
