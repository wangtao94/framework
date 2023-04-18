package cn.trve.framework.web.exception;

import cn.trve.framework.web.constant.dict.SystemConstant;
import cn.trve.framework.web.constant.enums.LogLevelEnum;
import cn.trve.framework.web.constant.enums.ViewLevelEnum;
import java.util.Optional;
import org.slf4j.helpers.MessageFormatter;

/**
 * <pre>
 * <b>业务异常</b>
 * <b>Description:</b> 对前端响应友好的错误信息@{link cn.trve.framework.web.exception.BusinessException#DEFAULT_BUSINESS_EXCEPTION_VIEW_MESSAGE}
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/4/12 17:41    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @since 2023/4/12
 */
public class BusinessException extends BaseRuntimeException {

    /**
     * 前端展示消息，默认等于getMessage()
     */
    protected String viewMessage = SystemConstant.DEFAULT_BUSINESS_EXCEPTION_VIEW_MESSAGE;

    public BusinessException(Throwable cause) {
        super(cause);
        super.viewMessage = this.viewMessage;
    }

    public BusinessException(String messagePattern, Object... args) {
        super(messagePattern, args);
        super.viewMessage = this.viewMessage;
    }

    public BusinessException(String messagePattern, Throwable cause, Object... args) {
        super(messagePattern, cause, args);
        super.viewMessage = this.viewMessage;
    }

    private BusinessException(Builder builder) {
        super(builder.message, builder.cause);
        setStackTrace(builder.stackTrace);
        logLevelEnum = builder.logLevelEnum;
        viewMessage = Optional.ofNullable(builder.viewMessage).orElse(this.viewMessage);
        viewLevelEnum = builder.viewLevelEnum;
    }



    public static Builder builder() {
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

        public BusinessException build() {
            return new BusinessException(this);
        }
    }
}
