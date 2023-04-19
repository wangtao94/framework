package cn.trve.framework.web.model;


import cn.trve.framework.web.constant.enums.ViewLevelEnum;

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
    //<editor-fold desc="常量">
    public static final Status OK = Status.OK;
    public static final Status FAIL = Status.FAIL;
    public static final ViewLevelEnum DEFAULT_OK_LEVEL = ViewLevelEnum.INFO;
    public static final ViewLevelEnum DEFAULT_FAIL_LEVEL = ViewLevelEnum.ERROR;
    //</editor-fold>

    //<editor-fold desc="成员变量">
    /**
     * 状态码
     */
    private String code;

    /**
     * 状态信息,错误描述.
     */
    private Status status;

    /**
     * 显示级别
     */
    private ViewLevelEnum level;

    /**
     * 数据.
     */
    private T data;
    //</editor-fold>

    /**
     * 构造方法
     *
     * @param status 状态
     * @param level  状态级别
     * @param data   数据
     */
    private Result(Status status, ViewLevelEnum level, T data) {
        this.code = code;
        this.status = status;
        this.data = data;
    }

    //<editor-fold desc="静态方法">

    /**
     * 创建一个Result对象
     *
     * @param status 状态
     * @param level  状态级别
     * @param data   数据
     * @param <T>    数据类型
     * @return Result对象
     * @see Result#of(String, String, ViewLevelEnum, Object)
     */
    public static <T> Result<T> of(Status status, ViewLevelEnum level, T data) {
        return new Result<>(status, level, data);
    }

    /**
     * 创建一个Result对象
     *
     * @param code   状态码
     * @param reason 状态信息,错误描述
     * @param level  状态级别
     * @param data   数据
     * @param <T>    数据类型
     * @return Result对象
     */
    public static <T> Result<T> of(String code, String reason, ViewLevelEnum level, T data) {
        return new Result<>(new Status(code, reason), level, data);
    }

    /**
     * non-javadoc.
     *
     * @see Result#of(String, String, ViewLevelEnum, Object)
     */
    public static <T> Result<T> ok(T data) {
        return Result.of(OK, DEFAULT_OK_LEVEL, data);
    }


    /**
     * non-javadoc.
     *
     * @see Result#of(String, String, ViewLevelEnum, Object)
     */
    public static <T> Result<T> ok(String reason, T data) {
        return Result.of(OK.code, reason, DEFAULT_OK_LEVEL, data);
    }


    /**
     * non-javadoc.
     *
     * @see Result#of(String, String, ViewLevelEnum, Object)
     */
    public static <T> Result<T> fail() {
        return Result.of(FAIL, DEFAULT_FAIL_LEVEL, null);
    }

    /**
     * non-javadoc.
     *
     * @see Result#of(String, String, ViewLevelEnum, Object)
     */
    public static <T> Result<T> fail(String reason) {
        return Result.of(FAIL.code, reason, DEFAULT_FAIL_LEVEL, null);
    }

    /**
     * non-javadoc.
     *
     * @see Result#of(String, String, ViewLevelEnum, Object)
     */
    public static <T> Result<T> fail(Status status) {
        return Result.of(status, DEFAULT_FAIL_LEVEL, null);
    }

    /**
     * non-javadoc.
     *
     * @see Result#of(String, String, ViewLevelEnum, Object)
     */
    public static <T> Result<T> fail(Status status, String reason) {
        return Result.of(status.code, reason, DEFAULT_FAIL_LEVEL, null);
    }

    /**
     * non-javadoc.
     *
     * @see Result#of(String, String, ViewLevelEnum, Object)
     */
    public static <T> Result<T> fail(String reason, ViewLevelEnum level) {
        return Result.of(FAIL.code, reason, level, null);
    }

    /**
     * non-javadoc.
     *
     * @see Result#of(String, String, ViewLevelEnum, Object)
     */
    public static <T> Result<T> fail(String reason, T data) {
        return Result.of(FAIL.code, reason, DEFAULT_FAIL_LEVEL, data);
    }

    /**
     * non-javadoc.
     *
     * @see Result#of(String, String, ViewLevelEnum, Object)
     */
    public static <T> Result<T> fail(String reason, ViewLevelEnum level, T data) {
        return Result.of(FAIL.code, reason, level, data);
    }
    //</editor-fold>

    //<editor-fold desc="getter">

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取状态信息,错误描述.
     *
     * @return 状态信息, 错误描述.
     */
    public String getReason() {
        return status.reason;
    }

    /**
     * 获取状态级别
     *
     * @return 状态级别
     */
    public ViewLevelEnum getLevel() {
        return level;
    }

    /**
     * 获取数据.
     *
     * @return 数据.
     */
    public T getData() {
        return data;
    }
    //</editor-fold>


    public record Status(String code, String reason) {
        /**
         * 状态
         */
        public static Status OK = new Status("web.ok", "处理成功");
        public static Status BAD_REQUEST = new Status("web.badRequest", "错误的请求");
        public static Status UNAUTHORIZED = new Status("web.unauthorized", "未认证");
        public static Status FORBIDDEN = new Status("web.forbidden", "未授权");
        public static Status NOT_FOUND = new Status("web.notFound", "没有可用的数据");
        public static Status INTERNAL_SERVER_ERROR = new Status("web.unknown", "服务器遇到了一个未曾预料的状况");
        public static Status SERVICE_UNAVAILABLE = new Status("web.serviceUnavailable", "服务器当前无法处理请求");
        public static Status FAIL = new Status("web.fail", "错误");

        /**
         * 状态码,建议业务名称+数字.
         */
        @Override
        public String toString() {
            return code + ": " + reason;
        }
    }


}
