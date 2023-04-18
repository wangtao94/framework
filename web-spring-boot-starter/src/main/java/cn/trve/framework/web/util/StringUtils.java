package cn.trve.framework.web.util;

import org.slf4j.helpers.MessageFormatter;

/**
 * <pre>
 * <b>字符串工具类</b>
 * <b>Description:</b> 主要是 rg.apache.commons.lang3.StringUtils 的扩展，提供一些没有的方法
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/4/13 12:05    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @since 2023/4/13
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static String format(String messagePattern, Object... args) {
        return MessageFormatter.format(messagePattern, args).getMessage();
    }
}
