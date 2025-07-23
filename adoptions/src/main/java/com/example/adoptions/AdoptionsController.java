package com.example.adoptions;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.image.*;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tangtian
 * @date 2025-07-18 11:17
 */
@Controller
@ResponseBody
public class AdoptionsController {
	private final ChatClient ai;
	private final ImageModel imageModel;


	AdoptionsController(JdbcClient db,
						ImageModel imageModel,
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
               	你是一名优秀的医生，偶尔你也会调用工具。
                """;
		this.ai = ai
				.defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClient))
				.defaultAdvisors(promptChatMemoryAdvisor, new QuestionAnswerAdvisor(vectorStore))
				.defaultSystem(system)
				.build();
		this.imageModel = imageModel;
	}

	@GetMapping("/{user}/assistant")
	String inquire(@PathVariable String user, @RequestParam String question) {
		return ai
				.prompt()
				.user(question)
				.advisors(a -> {
					a.param(ChatMemory.CONVERSATION_ID, user);
				})
				.tools(new DateTimeTools())
				.call()
				.content();

	}


	@GetMapping("/{user}/stream/assistant")
	Flux<String> streamInquire(@PathVariable String user, @RequestParam String question) {
		return ai
				.prompt()
				.user(question)
				.tools(new DateTimeTools())
				.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
				.stream()
				.content();

	}


	@GetMapping("/{user}/image/assistant")
	String image(@PathVariable String user, @RequestParam String question) {
		var options = ImageOptionsBuilder.builder().height(1024).width(1024).build();
		ImagePrompt imagePrompt = new ImagePrompt(question, options);
		ImageResponse imageResponse = this.imageModel.call(imagePrompt);
		return imageResponse.getResult().getOutput().getUrl();
	}
}
