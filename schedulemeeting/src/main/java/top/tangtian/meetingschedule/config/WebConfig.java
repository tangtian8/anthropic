package top.tangtian.meetingschedule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author tangtian
 * @date 2025-07-05 20:05
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 配置 templates 目录可以直接访问
		registry.addResourceHandler("/templates/**")
				.addResourceLocations("classpath:/templates/");

		// 保持默认的静态资源配置
		registry.addResourceHandler("/static/**")
				.addResourceLocations("classpath:/static/");
	}
}