package cn.trve.framework.web.exception;

import cn.trve.framework.web.constant.dict.SystemConstant;
import cn.trve.framework.web.constant.enums.LogLevelEnum;
import cn.trve.framework.web.constant.enums.ViewLevelEnum;
import org.slf4j.helpers.MessageFormatter;

/**
 * <pre>
 * <b>服务内部错误</b>
 * <b>Description:</b> 前端提示友好错误信息
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/4/12 17:11    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @Date 2023/4/12
 * @since 2023/4/12
 */
public class ServerInternalException extends BaseRuntimeException {

    public ServerInternalException(Throwable cause) {
        super(cause);
    }

    public ServerInternalException(String messagePattern, Object... args) {
        super(messagePattern, args);
    }

    public ServerInternalException(String messagePattern, Throwable cause, Object... args) {
        super(messagePattern, cause, args);
    }

    private ServerInternalException(Builder builder) {
        super(builder.message, builder.cause);
        setStackTrace(builder.stackTrace);
        logLevelEnum = builder.logLevelEnum;
        viewMessage = builder.viewMessage;
        viewLevelEnum = builder.viewLevelEnum;
    }

    public static ServerInternalException.Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private String message;
        private Throwable cause;
        private StackTraceElement[] stackTrace;
        private LogLevelEnum logLevelEnum;
        private String viewMessage;
        private ViewLevelEnum viewLevelEnum;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder message(String messagePattern, Object... args) {
            message = MessageFormatter.format(messagePattern, args).getMessage();
            return this;
        }

        public Builder cause(Throwable val) {
            cause = val;
            return this;
        }

        public Builder stackTrace(StackTraceElement[] val) {
            stackTrace = val;
            return this;
        }

        public Builder logLevelEnum(LogLevelEnum val) {
            logLevelEnum = val;
            return this;
        }

        public Builder viewMessage(String messagePattern, Object... args) {
            viewMessage = MessageFormatter.format(messagePattern, args).getMessage();
            return this;
        }

        public Builder viewLevelEnum(ViewLevelEnum val) {
            viewLevelEnum = val;
            return this;
        }

        public ServerInternalException build() {
            return new ServerInternalException(this);
        }
    }
}
