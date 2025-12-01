package top.tangtian.meetingschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import top.tangtian.meetingschedule.dto.ChatResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tangtian
 * @date 2025-11-16 10:28
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {


	private final ChatClient.Builder chatClientBuilder;

	// 自定义会话历史管理，限制历史消息数量
	private final Map<String, List<Message>> sessionHistory = new ConcurrentHashMap<>();

	// 最大保留的历史消息轮数（一问一答算一轮）
	private static final int MAX_HISTORY_ROUNDS = 3;

	public ChatResponse chat(String sessionId, String userMessage) {
		log.info("处理用户消息 - Session: {}, Message: {}", sessionId, userMessage);

		try {
			// 构建消息列表
			List<Message> messages = buildMessages(sessionId, userMessage);

			// 创建ChatClient并调用
			ChatClient chatClient = chatClientBuilder
					.defaultFunctions("bookMeetingRoom")
					.build();

			String response = chatClient.prompt(new Prompt(messages))
					.call()
					.content();

			log.info("AI响应: {}", response);

			// 保存历史（限制数量）
			saveHistory(sessionId, userMessage, response);

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

	private List<Message> buildMessages(String sessionId, String userMessage) {
		List<Message> messages = new ArrayList<>();

		// 1. 添加系统提示
		messages.add(new SystemMessage(buildSystemPrompt()));

		// 2. 添加历史消息（有限制）
		List<Message> history = sessionHistory.getOrDefault(sessionId, new ArrayList<>());
		messages.addAll(history);

		// 3. 添加当前用户消息
		messages.add(new UserMessage(userMessage));

		log.debug("构建消息列表 - 系统提示1条, 历史{}条, 当前1条", history.size());

		return messages;
	}

	private void saveHistory(String sessionId, String userMessage, String assistantResponse) {
		List<Message> history = sessionHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());

		// 添加本轮对话
		history.add(new UserMessage(userMessage));
		history.add(new AssistantMessage(assistantResponse));

		// 限制历史数量（保留最近N轮对话）
		int maxMessages = MAX_HISTORY_ROUNDS * 2; // 每轮2条消息
		while (history.size() > maxMessages) {
			history.remove(0); // 移除最早的消息
			history.remove(0); // 移除配对的消息
		}

		log.debug("会话 {} 历史消息数: {}", sessionId, history.size());
	}

	/**
	 * 清除会话历史
	 */
	public void clearHistory(String sessionId) {
		sessionHistory.remove(sessionId);
		log.info("已清除会话 {} 的历史", sessionId);
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
            1. 理解用户的预订需求
            2. 如果信息不完整，简洁地询问缺失信息
            3. 当获得所有必要信息后，调用bookMeetingRoom工具完成预订
            4. 将相对时间转换为具体时间（如"明天下午2点"）
            
            必需信息: 会议室名称、会议主题、组织者姓名、开始时间、结束时间
            可选信息: 参会人数、会议描述
            
            时间格式: yyyy-MM-dd HH:mm:ss
            
            注意:
            - 回复要简洁，不要重复之前说过的内容
            - 不要列出所有会议室信息，除非用户询问
            - 预订成功后只需确认关键信息
            """, currentTime);
	}
}