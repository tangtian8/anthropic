package top.tangtian.meetingschedule.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.tangtian.meetingschedule.service.CaptchaService;

import java.util.Map;

/**
 * @author tangtian
 * @date 2025-11-22 18:09
 */
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CaptchaController {

	private final CaptchaService captchaService;

	@GetMapping("/challenge")
	public Map<String, String> getChallenge() {
		CaptchaService.Challenge challenge = captchaService.generateChallenge();
		return Map.of(
				"token", challenge.token(),
				"question", challenge.question()
		);
	}

	@PostMapping("/verify")
	public Map<String, Object> verify(@RequestBody Map<String, Object> request) {
		String token = (String) request.get("token");
		Integer answer = (Integer) request.get("answer");

		if (token == null || answer == null) {
			return Map.of("valid", false, "message", "参数不完整");
		}

		boolean valid = captchaService.verifyChallenge(token, answer);
		return Map.of("valid", valid);
	}
}
