package top.tangtian.meetingschedule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.tangtian.meetingschedule.dto.ChatRequest;
import top.tangtian.meetingschedule.dto.ChatResponse;
import top.tangtian.meetingschedule.service.AIService;

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

	@PostMapping("/chat")
	public ChatResponse chat(
			@RequestBody ChatRequest request,
			@RequestHeader(value = "X-Session-Id", required = false) String sessionId) {

		if (sessionId == null || sessionId.isEmpty()) {
			sessionId = UUID.randomUUID().toString();
		}

		log.info("收到聊天请求 - Session: {}, Message: {}", sessionId, request.getMessage());

		return aiService.chat(sessionId, request.getMessage());
	}

	@GetMapping("/health")
	public String health() {
		return "OK";
	}
}