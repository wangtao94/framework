package cn.trve.framework.common.exception;

import cn.trve.framework.common.constant.LogLevel;
import cn.trve.framework.common.constant.ViewLevel;

/**
 * <pre>
 * <b>基础运行时异常</b>
 * <b>Description:</b>
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/3/27 9:40    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @Date 2023/3/27
 * @since 2023/3/27
 */
public class BaseRuntimeException extends RuntimeException {
    /**
     * 日志打印级别
     */
    private LogLevel logLevel = LogLevel.ERROR;

    /**
     * 前端展示消息，默认等于getMessage()
     */
    private String viewMessage = getMessage();

    /**
     * 前端展示级别
     */
    private ViewLevel viewLevel = ViewLevel.ERROR;

    public BaseRuntimeException(String message) {
        super(message);
    }

    private BaseRuntimeException(Builder builder) {
        super(builder.cause);
        logLevel = builder.logLevel;
        viewMessage = builder.viewMessage;
        viewLevel = builder.viewLevel;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public String getViewMessage() {
        return viewMessage;
    }

    public ViewLevel getViewLevel() {
        return viewLevel;
    }

    public static final class Builder {
        private Throwable cause;
        private LogLevel logLevel;
        private String viewMessage;
        private ViewLevel viewLevel;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder cause(Throwable val) {
            cause = val;
            return this;
        }

        public Builder logLevel(LogLevel val) {
            logLevel = val;
            return this;
        }

        public Builder viewMessage(String val) {
            viewMessage = val;
            return this;
        }

        public Builder viewLevel(ViewLevel val) {
            viewLevel = val;
            return this;
        }

        public BaseRuntimeException build() {
            return new BaseRuntimeException(this);
        }
    }
}
