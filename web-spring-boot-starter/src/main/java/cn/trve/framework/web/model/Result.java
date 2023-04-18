package cn.trve.framework.web.model;


import cn.trve.framework.web.util.JsonUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <pre>
 * <b>响应结果封装</b>
 * <b>Description:</b>
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/3/27 9:49    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @Date 2023/3/27
 * @since 2023/3/27
 */
public class Result<T> {

    /**
     * 状态码
     */
    private String code;

    /**
     * 状态信息,错误描述.
     */
    private final String message;

    /**
     * 获取状态。
     *
     * @return 状态
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取消息内容。
     *
     * @return 消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 数据.
     */
    private T data;

    /**
     * 获取数据内容。
     *
     * @return 数据
     */
    public T getData() {
        return data;
    }

    private Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private Result(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result(String message) {
        this.message = message;
    }

    /**
     * 创建一个带有状态、消息和数据的结果对象.
     *
     * @param status  状态
     * @param message 消息内容
     * @param data    数据
     * @return 结构数据
     */
    public static <T> Result<T> var(Status status, String message, T data) {
        return new Result<>(status.getCode(), message, data);
    }

    /**
     * 创建一个带有状态、消息和数据的结果对象.
     *
     * @param status  状态
     * @param message 消息内容
     * @return 结构数据
     */
    public static <T> Result<T> var(Status status, String message) {
        return new Result<>(status.getCode(), message);
    }

    /**
     * 创建一个带有状态和数据的结果对象.
     *
     * @param status 状态
     * @param data   数据
     * @return 结构数据
     */
    public static <T> Result<T> var(Status status, T data) {
        return new Result<>(status.getCode(), status.getReason(), data);
    }

    /**
     * 创建一个带有状态的结果对象.
     *
     * @param status 状态
     * @return 结构数据
     */
    public static <T> Result<T> var(Status status) {
        return new Result<>(status.getCode(), status.getReason());
    }

    /**
     *
     */
    public static <T> Result<T> ok() {
        return Result.var(Status.OK);
    }

    /**
     *
     */
    public static <T> Result<T> ok(T data) {
        return Result.var(Status.OK, data);
    }

    /**
     *
     */
    public static <T> Result<T> fail() {
        return Result.var(Status.INTERNAL_SERVER_ERROR);
    }

    /**
     *
     */
    public static <T> Result<T> fail(String reason) {
        return Result.var(Status.INTERNAL_SERVER_ERROR, reason);
    }

    /**
     *
     */
    public static <T> Result<T> fail(Status status) {
        return Result.var(status);
    }

    public void writeTo(OutputStream outputStream) {
        try {
            JsonUtils.getObjectMapper().writeValue(outputStream, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum Status {

        /**
         * 状态
         */
        OK("web.ok", "处理成功"), BAD_REQUEST("web.badRequest", "错误的请求"), UNAUTHORIZED("web.unauthorized", "未认证"),
        FORBIDDEN("web.forbidden", "未授权"), NOT_FOUND("web.notFound", "没有可用的数据"),
        INTERNAL_SERVER_ERROR("web.unknown", "服务器遇到了一个未曾预料的状况"),
        SERVICE_UNAVAILABLE("web.serviceUnavailable", "服务器当前无法处理请求"), ERROR("web.error", "错误");
        /**
         * 状态码,建议业务名称+数字.
         */
        private final String code;

        /**
         * 错误信息.
         */
        private final String reason;

        Status(String code, String reason) {
            this.code = code;
            this.reason = reason;
        }

        public String getCode() {
            return code;
        }

        public String getReason() {
            return reason;
        }

        @Override
        public String toString() {
            return code + ": " + reason;
        }

    }
}
