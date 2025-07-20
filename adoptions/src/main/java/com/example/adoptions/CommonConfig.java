package com.example.adoptions;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author tangtian
 * @date 2025-07-18 11:16
 */
@Configuration
public class CommonConfig {

	@Bean
	PromptChatMemoryAdvisor promptChatMemoryAdvisor(DataSource dataSource) {
		var jdbc = JdbcChatMemoryRepository
				.builder()
				.dataSource(dataSource)
				.build();

		var chatMessageWindow = MessageWindowChatMemory
				.builder()
				.chatMemoryRepository(jdbc)
				.build();

		return PromptChatMemoryAdvisor
				.builder(chatMessageWindow)
				.build();
	}

	@Bean
	McpSyncClient mcpSyncClient() {
		var mcp = McpClient
				.sync(HttpClientSseClientTransport.builder("http://localhost:8081").build()).build();
		mcp.initialize();
		return mcp;
	}
}
