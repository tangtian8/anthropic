package top.tangtian.meetingschedule.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.tangtian.meetingschedule.config.IpRateLimiter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author tangtian
 * @date 2025-11-27 16:44
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

	private final IpRateLimiter ipRateLimiter;
	private final ObjectMapper objectMapper = new ObjectMapper();

	// 可疑的User-Agent模式（常见爬虫）
	private static final Set<String> BLOCKED_USER_AGENTS = Set.of(
			"python-requests",
			"curl",
			"wget",
			"scrapy",
			"httpclient",
			"java/",
			"go-http-client",
			"bot",
			"spider",
			"crawler"
	);

	// 需要限流的API路径
	private static final Set<String> RATE_LIMITED_PATHS = Set.of(
			"/api/chat"
	);

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {

		String clientIp = getClientIp(request);
		String userAgent = request.getHeader("User-Agent");
		String path = request.getRequestURI();

		// 1. 检查是否是可疑的User-Agent
		if (isSuspiciousUserAgent(userAgent)) {
			log.warn("可疑User-Agent被拦截 - IP: {}, UA: {}", clientIp, userAgent);
			sendErrorResponse(response, HttpStatus.FORBIDDEN, "Access denied");
			return;
		}

		// 2. 检查IP是否被封禁
		if (ipRateLimiter.isBanned(clientIp)) {
			log.warn("被封禁IP尝试访问 - IP: {}", clientIp);
			sendErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS,
					"您的IP已被暂时封禁，请1小时后再试");
			return;
		}

		// 3. 对聊天API进行限流
		if (RATE_LIMITED_PATHS.contains(path)) {
			ipRateLimiter.recordRequest(clientIp);

			if (!ipRateLimiter.isAllowed(clientIp)) {
				log.warn("IP请求过于频繁 - IP: {}", clientIp);
				sendErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS,
						"请求过于频繁，请稍后再试");
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean isSuspiciousUserAgent(String userAgent) {
		if (userAgent == null || userAgent.isEmpty()) {
			return true; // 没有User-Agent的请求视为可疑
		}

		String lowerUA = userAgent.toLowerCase();
		return BLOCKED_USER_AGENTS.stream().anyMatch(lowerUA::contains);
	}

	private String getClientIp(HttpServletRequest request) {
		// 支持代理情况下获取真实IP
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}

		String xRealIp = request.getHeader("X-Real-IP");
		if (xRealIp != null && !xRealIp.isEmpty()) {
			return xRealIp;
		}

		return request.getRemoteAddr();
	}

	private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message)
			throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		Map<String, Object> body = new HashMap<>();
		body.put("error", true);
		body.put("message", message);
		body.put("status", status.value());

		response.getWriter().write(objectMapper.writeValueAsString(body));
	}
}
