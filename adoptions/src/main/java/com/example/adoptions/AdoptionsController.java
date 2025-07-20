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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
               	你是一个中国人，并且你的职业是一名心理医生，现在有一些动物，你知道这些动物的性格，用中文回答
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
				.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
				.call()
				.content();

	}
}
