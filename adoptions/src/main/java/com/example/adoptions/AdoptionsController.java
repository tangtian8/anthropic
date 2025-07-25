package com.example.adoptions;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author tangtian
 * @date 2025-07-18 11:17
 */
@Controller
@ResponseBody
public class AdoptionsController {
	private final ChatClient ai;


	AdoptionsController(JdbcClient db,
						McpSyncClient mcpSyncClient,
						PromptChatMemoryAdvisor promptChatMemoryAdvisor,
						ChatClient.Builder ai,
						DogRepository repository,
						VectorStore vectorStore) {

		var count = db
				.sql("select count(*) from vector_store")
				.query(Integer.class)
				.single();
		if (count == 0) {
			repository.findAll().forEach(dog -> {
				var dogument = new Document("id: %s, name: %s, description: %s".formatted(
						dog.getId(), dog.getName(), dog.getDescription()
				));
				vectorStore.add(List.of(dogument));
			});
		}
		var system = """
				您是一个AI助手，帮助人们从名为"Pooch Palace"的领养机构领养狗狗。该机构在里约热内卢、墨西哥城、首尔、东京、新加坡、巴黎、孟买、新德里、巴塞罗那、伦敦和旧金山设有分店。关于可领养狗狗的信息将在下方提供。如果没有相关信息，请礼貌地回复说我们目前没有可领养的狗狗。
				""";
		this.ai = ai
				.defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClient))
				.defaultAdvisors(promptChatMemoryAdvisor, new QuestionAnswerAdvisor(vectorStore))
				.defaultSystem(system)
				.build();
	}

	@GetMapping("/{user}/assistant")
	String inquire(@PathVariable String user, @RequestParam String question) {
		return ai
				.prompt()
				.user(question)
				.advisors(a -> {
					a.param(ChatMemory.CONVERSATION_ID, user);
				})
				.call()
				.content();

	}


	@GetMapping("/{user}/stream/assistant")
	Flux<String> streamInquire(@PathVariable String user, @RequestParam String question) {
		return ai
				.prompt()
				.user(question)
				.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
				.stream()
				.content();

	}
}
