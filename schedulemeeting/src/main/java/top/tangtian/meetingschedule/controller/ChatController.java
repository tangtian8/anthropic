package top.tangtian.meetingschedule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.tangtian.meetingschedule.config.RateLimitConfig;
import top.tangtian.meetingschedule.config.SessionLimitManager;
import top.tangtian.meetingschedule.dto.ChatRequest;
import top.tangtian.meetingschedule.dto.ChatResponse;
import top.tangtian.meetingschedule.service.AIService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author tangtian
 * @date 2025-11-16 10:27
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChatController {

	private final AIService aiService;
	private final SessionLimitManager sessionLimitManager;
	private final RateLimitConfig config;

	@PostMapping("/chat")
	public ResponseEntity<?> chat(
			@RequestBody ChatRequest request,
			@RequestHeader(value = "X-Session-Id", required = false) String sessionId) {

		// 生成或验证sessionId
		if (sessionId == null || sessionId.isEmpty()) {
			sessionId = UUID.randomUUID().toString();
		}

		log.info("收到聊天请求 - Session: {}, Message: {}", sessionId, request.getMessage());

		// 检查会话次数限制
		if (!sessionLimitManager.canChat(sessionId)) {
			log.warn("会话次数已用完 - Session: {}", sessionId);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("error", true);
			errorResponse.put("message", "本次会话对话次数已用完（最多" +
					config.getMaxConversationsPerSession() + "次），请刷新页面开启新会话");
			errorResponse.put("sessionExpired", true);
			errorResponse.put("remainingCount", 0);
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
		}

		// 先调用AI服务（在增加计数之前，以防调用失败）
		ChatResponse chatResponse = aiService.chat(sessionId, request.getMessage());

		// 调用成功后增加会话计数
		int usedCount = sessionLimitManager.incrementAndGet(sessionId);
		int remainingCount = sessionLimitManager.getRemainingCount(sessionId);

		// 返回结果，包含剩余次数
		Map<String, Object> result = new HashMap<>();
		result.put("response", chatResponse.getResponse());
		result.put("bookingInfo", chatResponse.getBookingInfo());
		result.put("remainingCount", remainingCount);
		result.put("usedCount", usedCount);
		result.put("maxCount", config.getMaxConversationsPerSession());
		result.put("sessionId", sessionId);

		log.info("会话 {} 已使用 {}/{} 次，剩余 {} 次", sessionId, usedCount,
				config.getMaxConversationsPerSession(), remainingCount);

		return ResponseEntity.ok(result);
	}

	@PostMapping("/session/reset")
	public ResponseEntity<?> resetSession(
			@RequestHeader(value = "X-Session-Id", required = false) String sessionId) {

		if (sessionId != null && !sessionId.isEmpty()) {
			sessionLimitManager.resetSession(sessionId);
			aiService.clearHistory(sessionId);
			log.info("会话已重置: {}", sessionId);
		}

		return ResponseEntity.ok(Map.of(
				"success", true,
				"message", "会话已重置"
		));
	}

	@GetMapping("/session/remaining")
	public ResponseEntity<?> getRemaining(
			@RequestHeader(value = "X-Session-Id", required = false) String sessionId) {

		if (sessionId == null || sessionId.isEmpty()) {
			return ResponseEntity.ok(Map.of(
					"remaining", config.getMaxConversationsPerSession(),
					"total", config.getMaxConversationsPerSession()
			));
		}

		int remaining = sessionLimitManager.getRemainingCount(sessionId);
		return ResponseEntity.ok(Map.of(
				"remaining", remaining,
				"total", config.getMaxConversationsPerSession()
		));
	}

	@GetMapping("/health")
	public String health() {
		return "OK";
	}

	@GetMapping("/config")
	public ResponseEntity<?> getConfig() {
		return ResponseEntity.ok(Map.of(
				"maxConversationsPerSession", config.getMaxConversationsPerSession(),
				"maxRequestsPerMinute", config.getMaxRequestsPerMinute(),
				"maxRequestsPerHour", config.getMaxRequestsPerHour(),
				"banThreshold", config.getBanThreshold(),
				"banDurationMinutes", config.getBanDurationMinutes()
		));
	}
}