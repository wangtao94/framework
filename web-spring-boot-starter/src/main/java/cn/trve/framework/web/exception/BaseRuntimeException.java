package cn.trve.framework.web.exception;

import cn.trve.framework.web.constant.dict.SystemConstant;
import cn.trve.framework.web.constant.enums.LogLevelEnum;
import cn.trve.framework.web.constant.enums.ViewLevelEnum;
import org.slf4j.helpers.MessageFormatter;

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
 * @since 2023/3/27
 */
public class BaseRuntimeException extends RuntimeException {
    /**
     * 日志打印级别
     */
    protected LogLevelEnum logLevelEnum = LogLevelEnum.ERROR;

    /**
     * 前端展示消息，默认等于getMessage()
     */
    protected String viewMessage = SystemConstant.BASE_EXCEPTION_VIEW_MESSAGE;

    /**
     * 前端展示级别
     */
    protected ViewLevelEnum viewLevelEnum = ViewLevelEnum.ERROR;


    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }

    public BaseRuntimeException(String messagePattern,Object... args) {
        super(MessageFormatter.format(messagePattern,args).getMessage());
    }

    public BaseRuntimeException(String messagePattern, Throwable cause,Object... args) {
        super(MessageFormatter.format(messagePattern,args).getMessage(), cause);
    }


    public LogLevelEnum getLogLevel() {
        return logLevelEnum;
    }

    public String getViewMessage() {
        return viewMessage;
    }

    public ViewLevelEnum getViewLevel() {
        return viewLevelEnum;
    }


    public void throwIfTrue(boolean condition) {
        if (condition) {
            throw this;
        }
    }

    public void throwIfFalse(boolean condition) {
        if (!condition) {
            throw this;
        }
    }

}
