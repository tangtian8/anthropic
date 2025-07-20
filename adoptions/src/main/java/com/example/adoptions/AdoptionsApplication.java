package com.example.adoptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories  // 添加这个注解
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
    }
//
//    @Bean
//    PromptChatMemoryAdvisor promptChatMemoryAdvisor(DataSource dataSource) {
//        var jdbc = JdbcChatMemoryRepository
//                .builder()
//                .dataSource(dataSource)
//                .build();
//
//        var chatMessageWindow = MessageWindowChatMemory
//                .builder()
//                .chatMemoryRepository(jdbc)
//                .build();
//
//        return PromptChatMemoryAdvisor
//                .builder(chatMessageWindow)
//                .build();
//    }
//
//    @Bean
//    McpSyncClient mcpSyncClient() {
//        var mcp = McpClient
//                .sync(HttpClientSseClientTransport.builder("http://localhost:8081").build()).build();
//        mcp.initialize();
//        return mcp;
//    }
}
//
//@Controller
//@ResponseBody
//class AdoptionsController {
//
//    private final ChatClient ai;
//
//    AdoptionsController(JdbcClient db,
//
//                        McpSyncClient mcpSyncClient,
//                        PromptChatMemoryAdvisor promptChatMemoryAdvisor,
//                        ChatClient.Builder ai,
//                        DogRepository repository,
//                        VectorStore vectorStore) {
//
//        var count = db
//                .sql("select count(*) from vector_store")
//                .query(Integer.class)
//                .single();
//        if (count == 0) {
//            repository.findAll().forEach(dog -> {
//                var dogument = new Document("id: %s, name: %s, description: %s".formatted(
//                        dog.getId(), dog.getName(), dog.getDescription()
//                ));
//                vectorStore.add(List.of(dogument));
//            });
//        }
//        var system = """
//                You are an AI powered assistant to help people adopt a dog from the adoption agency named Pooch Palace with locations in Rio de Janeiro, Mexico City, Seoul, Tokyo, Singapore, Paris, Mumbai, New Delhi, Barcelona, London, and San Francisco. Information about the dogs available will be presented below. If there is no information, then return a polite response suggesting we don't have any dogs available.
//                """;
//        this.ai = ai
//                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClient))
//                .defaultAdvisors(promptChatMemoryAdvisor, new QuestionAnswerAdvisor(vectorStore))
//                .defaultSystem(system)
//                .build();
//    }
//
//    @GetMapping("/{user}/assistant")
//    String inquire(@PathVariable String user, @RequestParam String question) {
//        return ai
//                .prompt()
//                .user(question)
//                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
//                .call()
//                .content();
//
//    }
//}
//@Controller
//@ResponseBody
//class AdoptionsController {
//
//    private final ChatClient ai;
//
//    AdoptionsController(JdbcClient db,
//
//                        McpSyncClient mcpSyncClient,
//                        PromptChatMemoryAdvisor promptChatMemoryAdvisor,
//                        ChatClient.Builder ai,
//                        DogRepository repository,
//                        VectorStore vectorStore) {
//
//        var count = db
//                .sql("select count(*) from vector_store")
//                .query(Integer.class)
//                .single();
//        if (count == 0) {
//            repository.findAll().forEach(dog -> {
//                var dogument = new Document("id: %s, name: %s, description: %s".formatted(
//                        dog.getId(), dog.getName(), dog.getDescription()
//                ));
//                vectorStore.add(List.of(dogument));
//            });
//        }
//        var system = """
//                You are an AI powered assistant to help people adopt a dog from the adoption agency named Pooch Palace with locations in Rio de Janeiro, Mexico City, Seoul, Tokyo, Singapore, Paris, Mumbai, New Delhi, Barcelona, London, and San Francisco. Information about the dogs available will be presented below. If there is no information, then return a polite response suggesting we don't have any dogs available.
//                """;
//        this.ai = ai
//                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClient))
//                .defaultAdvisors(promptChatMemoryAdvisor, new QuestionAnswerAdvisor(vectorStore))
//                .defaultSystem(system)
//                .build();
//    }
//
//    @GetMapping("/{user}/assistant")
//    String inquire(@PathVariable String user, @RequestParam String question) {
//        return ai
//                .prompt()
//                .user(question)
//                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
//                .call()
//                .content();
//
//    }
//}
//
//@Repository
//interface DogRepository extends ListCrudRepository<Dog, Integer> {
//}
//
//record Dog() {
//}
