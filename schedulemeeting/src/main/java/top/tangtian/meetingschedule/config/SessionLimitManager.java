package top.tangtian.meetingschedule.config;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tangtian
 * @date 2025-11-22 18:05
 */
@Component
@AllArgsConstructor
public class SessionLimitManager {

	// 存储每个session的对话次数
	private final Map<String, Integer> sessionConversationCount = new ConcurrentHashMap<>();

	// 存储session创建时间，用于过期清理
	private final Map<String, Long> sessionCreationTime = new ConcurrentHashMap<>();

	// session过期时间（毫秒）- 24小时
	private static final long SESSION_EXPIRY_MS = 24 * 60 * 60 * 1000;

	private final RateLimitConfig config;


	public boolean canChat(String sessionId) {
		cleanExpiredSessions();
		int count = sessionConversationCount.getOrDefault(sessionId, 0);
		return count < config.getMaxConversationsPerSession();
	}

	public int incrementAndGet(String sessionId) {
		sessionCreationTime.putIfAbsent(sessionId, System.currentTimeMillis());
		return sessionConversationCount.merge(sessionId, 1, Integer::sum);
	}

	public int getRemainingCount(String sessionId) {
		int used = sessionConversationCount.getOrDefault(sessionId, 0);
		return Math.max(0, config.getMaxConversationsPerSession() - used);
	}

	public int getUsedCount(String sessionId) {
		return sessionConversationCount.getOrDefault(sessionId, 0);
	}

	public void resetSession(String sessionId) {
		sessionConversationCount.remove(sessionId);
		sessionCreationTime.remove(sessionId);
	}

	private void cleanExpiredSessions() {
		long now = System.currentTimeMillis();
		sessionCreationTime.entrySet().removeIf(entry -> {
			if (now - entry.getValue() > SESSION_EXPIRY_MS) {
				sessionConversationCount.remove(entry.getKey());
				return true;
			}
			return false;
		});
	}
}
