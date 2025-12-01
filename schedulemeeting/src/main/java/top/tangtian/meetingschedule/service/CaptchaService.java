package top.tangtian.meetingschedule.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tangtian
 * @date 2025-11-22 18:09
 */
@Service
public class CaptchaService {

	private final SecureRandom random = new SecureRandom();

	// 存储挑战答案: token -> answer
	private final Map<String, ChallengeData> challenges = new ConcurrentHashMap<>();

	// 挑战过期时间（毫秒）- 5分钟
	private static final long CHALLENGE_EXPIRY_MS = 5 * 60 * 1000;

	/**
	 * 生成简单数学挑战
	 */
	public Challenge generateChallenge() {
		cleanExpiredChallenges();

		int a = random.nextInt(10) + 1;
		int b = random.nextInt(10) + 1;
		int answer = a + b;

		String token = generateToken();
		String question = a + " + " + b + " = ?";

		challenges.put(token, new ChallengeData(answer, System.currentTimeMillis()));

		return new Challenge(token, question);
	}

	/**
	 * 验证挑战答案
	 */
	public boolean verifyChallenge(String token, int answer) {
		ChallengeData data = challenges.remove(token);

		if (data == null) {
			return false;
		}

		// 检查是否过期
		if (System.currentTimeMillis() - data.timestamp > CHALLENGE_EXPIRY_MS) {
			return false;
		}

		return data.answer == answer;
	}

	private String generateToken() {
		byte[] bytes = new byte[16];
		random.nextBytes(bytes);
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	private void cleanExpiredChallenges() {
		long now = System.currentTimeMillis();
		challenges.entrySet().removeIf(entry ->
				now - entry.getValue().timestamp > CHALLENGE_EXPIRY_MS);
	}

	public record Challenge(String token, String question) {}

	private record ChallengeData(int answer, long timestamp) {}
}
