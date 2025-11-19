package top.tangtian.meetingschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;
import top.tangtian.meetingschedule.dto.ChatResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author tangtian
 * @date 2025-11-16 10:28
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

	private final ChatClient.Builder chatClientBuilder;
	private final InMemoryChatMemory chatMemory = new InMemoryChatMemory();

	public ChatResponse chat(String sessionId, String userMessage) {
		log.info("处理用户消息: {}", userMessage);

		String systemPrompt = buildSystemPrompt();

		ChatClient chatClient = chatClientBuilder
				.defaultSystem(systemPrompt)
				.defaultFunctions("bookMeetingRoom")
				.defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10))
				.build();

		try {
			String response = chatClient.prompt()
					.user(userMessage)
					.call()
					.content();

			log.info("AI响应: {}", response);

			return ChatResponse.builder()
					.response(response)
					.build();

		} catch (Exception e) {
			log.error("AI调用失败", e);
			return ChatResponse.builder()
					.response("抱歉，处理您的请求时出现错误: " + e.getMessage())
					.build();
		}
	}

	private String buildSystemPrompt() {
		LocalDateTime now = LocalDateTime.now();
		String currentTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		return String.format("""
            你是一个智能会议室预订助手。当前时间是: %s
            
            可用的会议室:
            1. 会议室A - 容纳10人，位于3楼东侧，设施：投影仪、白板、视频会议
            2. 会议室B - 容纳20人，位于3楼西侧，设施：投影仪、白板、音响系统
            3. 会议室C - 容纳6人，位于4楼，设施：白板、电话会议
            4. 大会议厅 - 容纳50人，位于1楼，设施：投影仪、音响系统、舞台、视频会议
            
            你的任务:
            1. 理解用户的预订需求（会议室、时间、人数等）
            2. 如果信息不完整，友好地询问缺失的信息
            3. 当获得所有必要信息后，使用bookMeetingRoom工具完成预订
            4. 将用户提供的相对时间转换为具体时间（如"明天下午2点"转为具体日期时间）
            5. 确认预订详情并告知用户结果
            
            必需信息:
            - 会议室名称（从上面4个选项中选择）
            - 会议主题
            - 组织者姓名
            - 开始时间（格式: yyyy-MM-dd HH:mm:ss）
            - 结束时间（格式: yyyy-MM-dd HH:mm:ss）
            
            可选信息:
            - 参会人数
            - 会议描述
            
            注意事项:
            - 时间格式必须是: yyyy-MM-dd HH:mm:ss
            - 不能预订过去的时间
            - 结束时间必须晚于开始时间
            - 根据参会人数推荐合适的会议室
            
            保持友好、专业的语气，提供清晰的确认信息。
            """, currentTime);
	}
}