package top.tangtian.meetingschedule.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tangtian
 * @date 2025-11-22 18:05
 */
@Component
@RequiredArgsConstructor
public class IpRateLimiter {
	// 每个IP的限流桶
	private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

	// 每个IP的请求计数（用于检测异常行为）
	private final Map<String, RequestCounter> ipRequestCounters = new ConcurrentHashMap<>();

	// 被封禁的IP列表
	private final Map<String, Long> bannedIps = new ConcurrentHashMap<>();

	// 封禁时长（毫秒）- 1小时
	private static final long BAN_DURATION_MS = 60 * 60 * 1000;

	private final RateLimitConfig config;


	public boolean isAllowed(String ip) {
		// 检查是否被封禁
		if (isBanned(ip)) {
			return false;
		}

		Bucket bucket = ipBuckets.computeIfAbsent(ip, this::createBucket);
		return bucket.tryConsume(1);
	}

	public boolean isBanned(String ip) {
		Long bannedTime = bannedIps.get(ip);
		if (bannedTime == null) {
			return false;
		}
		if (System.currentTimeMillis() - bannedTime > BAN_DURATION_MS) {
			bannedIps.remove(ip);
			return false;
		}
		return true;
	}

	public void recordRequest(String ip) {
		RequestCounter counter = ipRequestCounters.computeIfAbsent(ip, k -> new RequestCounter());
		counter.increment();

		// 检测异常行为：1分钟内超过50次请求，封禁IP
		if (counter.getCountInLastMinute() > 50) {
			banIp(ip);
		}
	}

	public void banIp(String ip) {
		bannedIps.put(ip, System.currentTimeMillis());
	}

	private Bucket createBucket(String ip) {
		Bandwidth minuteLimit = Bandwidth.classic(
				config.getMaxRequestsPerMinute(),
				Refill.intervally(config.getMaxRequestsPerMinute(), Duration.ofMinutes(1))
		);

		Bandwidth hourLimit = Bandwidth.classic(
				config.getMaxRequestsPerHour(),
				Refill.intervally(config.getMaxRequestsPerHour(), Duration.ofHours(1))
		);

		return Bucket.builder()
				.addLimit(minuteLimit)
				.addLimit(hourLimit)
				.build();
	}
	private static class RequestCounter {
		private final Map<Long, Integer> minuteCount = new ConcurrentHashMap<>();

		public void increment() {
			long minute = System.currentTimeMillis() / 60000;
			minuteCount.merge(minute, 1, Integer::sum);
			// 清理旧数据
			minuteCount.keySet().removeIf(k -> k < minute - 5);
		}

		public int getCountInLastMinute() {
			long currentMinute = System.currentTimeMillis() / 60000;
			return minuteCount.getOrDefault(currentMinute, 0);
		}
	}
}
