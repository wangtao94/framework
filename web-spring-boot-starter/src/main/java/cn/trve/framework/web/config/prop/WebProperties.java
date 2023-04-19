package cn.trve.framework.web.config.prop;

import cn.trve.framework.web.constant.dict.SystemConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <pre>
 * <b>基础配置</b>
 * <b>Description:</b>
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/4/12 17:15    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @since 2023/4/12
 */
@ConfigurationProperties(prefix = WebProperties.PREFIX )
public class WebProperties {
    public static final String PREFIX = SystemConstant.DEFAULT_PROP_KEY_PREFIX+ "web";
}
