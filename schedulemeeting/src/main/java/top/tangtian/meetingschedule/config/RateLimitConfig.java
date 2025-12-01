package top.tangtian.meetingschedule.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author tangtian
 * @date 2025-11-22 18:02
 */
@Configuration
@ConfigurationProperties(prefix = "app.security")
@Data
public class RateLimitConfig {

	// 会话对话次数限制
	private int maxConversationsPerSession = 10;

	// IP限流配置
	private int maxRequestsPerMinute = 20;
	private int maxRequestsPerHour = 100;

	// IP封禁配置
	private int banThreshold = 50;
	private int banDurationMinutes = 60;
}