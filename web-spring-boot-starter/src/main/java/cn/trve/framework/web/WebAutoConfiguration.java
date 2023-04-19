package cn.trve.framework.web;

import cn.trve.framework.web.config.prop.WebProperties;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * <pre>
 * <b>web自动配置入口</b>
 * <b>Description:</b>
 * <b>Copyright:</b> Copyright 2023 Wangtao. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   		Date                    Author               	 Detail
 *   ----------------------------------------------------------------------
 *   1.0   2023/4/12 17:33    Wangtao     new file.
 * </pre>
 *
 * @author Wangtao
 * @Date 2023/4/12
 * @since 2023/4/12
 */
@SpringBootConfiguration
@EnableConfigurationProperties(WebProperties.class)
public class WebAutoConfiguration {
}
