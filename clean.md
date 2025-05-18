# Your First Spring AI 1.0 Application with Anthropic

Hi, Spring fans! Spring AI is _live_ on the Spring Initializr and everywhere fine bytes might be had. Ask your doctor if AI is right for you! It's an amazing time to be a Java and Spring developer. There's never been a better time to be a Java and Spring developer, and this is doubly true in this unique AI moment. You see, 90% of what people talk about when they talk about AI engineering is just integration with models, most of which have HTTP APIs. And most of what these models take is just human-language `String`s. This is integration code, and what place for these integrations to exist than hanging off the side of your Spring-based workloads? The same workloads who business logic drives your organizations and which guard data that feeds your organization. 


## the Pains and Patterns of AI Engineering

AI is _amazing_, but it's not _perfect_. It has issues, as with all technologies! There are a few things to be on the watch for, and when you explore Spring AI, know that you're doing so in terms of the patterns that support you on your journey to production. Let's look at some of them. 

![the pains and patterns of AI](images/pp.png)

Chat models are amenable to just about anything, and will follow you down just about any rabbit hole. If you want it to stay focused on a particular mission, give it a **system prompt**. 

Models are stateless. You might find that surprising if you've ever dealt with a model through  ChatGPT or Claude Desktop, because they submit a transcript of everything that's been said on each subsequent request. This transcript reminds the model what's been said. The transcript is **chat memory**.

Models live in an isolated sandbox. This makes sense! We've all seen the documentary called _The Terminator_ and know what can go wrong with unruly AI. But, they can do amazing things if you give them a bit of control, via **tool calling**.

Models are pretty darned smart, but they're not omniscient! You can give them data in the body of the request to help better inform their responses. This is called **prompt stuffing**. 

But don't send _too much_ data! Instead, send only that which might be germaine to the query at hand. You can do this by chucking the data into a **vector store**, to support finding records that are similar to one another. Then, do **retrieval augmented generation (RAG)**, whereby you send the subselection of results from the vector store to the model for final analysis. 

Chat models love to chat, even if they're wrong. This can sometimes produce interesting and incorrect results called _hallucinations_. Use **evaluators** to validate that the response is basically what you intended it to be.

## One Small Step for Spring Developers, One Giant Leap for AI 

Spring AI is a huge leap forward, but for Spring developers it'll feel like a  natural next step. It works like any Spring project. It has portable service abstractions allowing you to work consistently and conveniently with any of a number of models. It provides Spring Boot starters, configuration properties, and autoconfiguration. And, Spring AI carries Spring Boot's production-minded ethic forward, supporting vritual threads, GraalVM native images, and observability through Micrometer. It also offers a great developer experience, integrating Spring Boot's DevTools, and provides rich support for Docker Compose and Testcontainers. As with all Spring projects, you can get started on the [Spring Initializr](https://start.spring.io).

## Meet the Dogs
And that's just what we're going to do! We're going to build an application to support adopting dogs! I'm inspired by a dog that went viral back in 2021. The dog, named _Prancer_, sounds like he'd be quite the handful! Here's my favorite excerpt from [the ad](https://www.facebook.com/tyfanee.fortuna/posts/10219752628710467):

"Ok, I’ve tried. I’ve tried for the last several months to post this dog for adoption and make him sound...palatable. The problem is, he’s just not. There’s not a very big market for neurotic, man-hating, animal-hating, children-hating dogs that look like gremlins. But I have to believe there’s someone out there for Prancer, because I am tired and so is my family. Every day we live in the grips of the demonic Chihuahua hellscape he has created in our home."

## The Pre-requisites
Sounds like quite a handful! But even spicy dogs deserve loving homes. So let's build a service to unite people with the dogs of their dreams (or nightmares?) Hit the [Spring Initializr](https://start.spring.io) and add the following dependencies to your project: `PgVector`, `GraalVM`, `Actuator`, `Data JDBC`, `JDBC Chat Memory`, `PostgresML`, `Devtools`, and `Web`. Choose Java 24 (or later) and Apache Maven as the build tool. (Strictly speaking, there's no reason you could not use Gradle here, but the example will be in terms of Apache Maven) Make sure the artifact is named `adoptions`.

Make sure that in  your `pom.xml`, you've also got: `org.springframework.ai`:`spring-ai-advisors-vector-store`.

Some of these things are familiar. `Data JDBC` just brings in Spring Data JDBC, which is just an ORM mapper that allows you to talk to a SQL database. `Web` brings in Spring MVC. `Actuator` brings in Spring Boot's observability stack, underpinned in part by [Micrometer](https://micrometer.io). `Devtools` is a development-time concern, allowing you to do live-reloads as you make changes. It'll automatically reload the code each time you do a "Save" operation in Visual Studio Code or Eclipse, and it'll automatically kick in each time you alt-tab away from IntelliJ IDEA. `GraalVM` brings in support for the OpenJDK fork, GraalVM, which provides, among other things, an ahead-of-time compiler (AOT) that produces lightweight, lighting fast binaries. 

We said that Spring Data JDBC will make it easy to connect to a SQL database, but which one? In our application, we'll be using PostgreSQL, but not just vanilla PostgresSQL! We're going to load two very important extensions: `vector` and `postgresml`. The `vector` plugin allows PostgresSQL to act as a _vector store_. You'll need to turn arbitrary (text, image, audio) data into _embeddings_ before they can be persisted. For this, you'll need an embedding model. `PostgresML` provides that capability here. These concerns are usually orthaganol—it's just very convenient that PostgreSQL can do both chores. A big part of building a Spring AI application is deciding upon which vector store, embedding model, and chat model you will use.   

`Claude` is, of course, the chat model we're going to be using today. To connect to it, you'll need an API key. You can secure one from [the Anthropic developer portal](https://www.anthropic.com/api). Claude is an awesome fit for most enterprise workloads. It is often more polite, stable, and conservative in uncertain or sensitive contexts. This makes it a great choice for enterprise applications. Claude's also great at document comprehension and at following multistep instructions.

## The Database 

As I said before, we're going to use PostgreSQL. It's not too difficult to get a Docker image working that supports both `vector` and `postgresml`. I've included a file, `adoptions/db/run.sh`. Run that. It'll launch a Docker image. You'll then need to initialize it with an application user. Run `adoptions/db/init.sh`.

Now you're all set. 

Specify your everything to do with your database connectivity in `application.properties`:

```properties
spring.sql.init.mode=always
#
spring.datasource.url=jdbc:postgresql://localhost:5433/postgresml
spring.datasource.username=myappuser
spring.datasource.password=mypassword
#
spring.ai.postgresml.embedding.create-extension=true
spring.ai.postgresml.embedding.options.vector-type=pg_vector
#
spring.ai.vectorstore.pgvector.dimensions=768
spring.ai.vectorstore.pgvector.initialize-schema=true
#
spring.ai.chat.memory.repository.jdbc.initialize-schema=always
```

Here we're specifying what kind of vector we want, whether we want Spring AI to initialize the `PostgresML` extension. We're specifying what dimensions we want for vectors stored in PostgreSQL, and whether we want Spring AI to initialize the schema required to use it as a vector store. 

We also want to install some data (the dogs!) into the database, so we'll tell Spring Boot to run `schema.sql` and `data.sql` which creates a table and installs data in the database, respectively.

We'll need to talk to the just-created `dog` table, so we've got a Spring Data JDBC entity and repository. Add the following types to the bottom of `AdoptionsApplication.java`, after the last `}`.

```java

interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record Dog(@Id int id, String name, String owner, String description {
}

```

## The Assistant 

We're going to field questions from users via our HTTP controller. Here's the skeleton definition:

```java


@Controller
@ResponseBody
class AdoptionsController {

    private final ChatClient ai;

    AdoptionsController (ChatClient.Builder ai  ) {
        this.ai = ai.build();
    }

    @GetMapping("/{user}/assistant")
    String inquire(@PathVariable String user, @RequestParam String question) {
        return ai
                .prompt()
                .user(question)
                .call()
                .content();
    }
}

```

So, basically, we can ask questions by making HTTP requests to `:8080/youruser/assistant`. Try it out.

```
http :8080/jlong/assistant question=="my name is Josh" 
```

You shoudl get an effusive response. We're friends, it sounds like! Let's put that friendship to the test.


```
http :8080/jlong/assistant question=="what's my name?" 
```

In my run, I was disappointed to learn that Claude had already forgotten about me. It has no memory of me whatsoever! Let's give our model some memory. We'll do this with an advisor, which pre- and post-processes requests to the model, called `PromptChatMemoryAdvisor`. Add its definition to the `AdoptionsApplication`.

```java
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
```

Advisors are like filters or interceptors. They're a great way to add to the body of a request or handle the response in a generic, cross-cutting kind of way. Sort of like Spring's aspect-oriented programming support. 

This advisor will persist the messages for you. In this instance, it'll persist it to our PostgreSQL database, using schema we've already told Spring AI to initialize (`spring.ai.chat.memory.repository.jdbc.initialize-schema=always`). 

Change the configuration for the `ChatClient`:

```java
    // ..
    AdoptionsController (PromptChatMemoryAdvisor promptChatMemoryAdvisor,
                         ChatClient.Builder ai  ) {
        this.ai = ai
                .defaultAdvisors(promptChatMemoryAdvisor)
                .build();
    }
    // ..
```

In order for the `PromptChatMemoryAdvisor` to do its work, it needs to some way to correlate the request from you with a given conversation. You can do this by assigning a conversation ID on the request. Modify the `inquire` method:

```java
  @GetMapping("/{user}/assistant")
    String inquire(@PathVariable String user, @RequestParam String question) {
        return ai
                .prompt()
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user)) // new
                .call()
                .content();

    }

``` 

In this instance, we're simply using the path variable from the URL to create distinct conversations. Naturally, a much more suitable approach might be to use the Spring Security authenticated `Principal#getName()` call, instead. If you have Spring Security installed, you could inject the authenticated principal as a parameter of the controller method. 


Relaunch the program and then re-run the same HTTP interactions and this time you should find the model remembers you. NB: you can always reset the memory by deleting the data in that particular table. 

Nice! If you just built a quick UI, you'd have—in effect—your own Claude Desktop. Which is not exactly what we want. Remember, we're trying to help people adopt  dog from our fictitious dog adoption agency _Pooch Palace_. We don't want people doing their homework or getting coding help from our assistant. Let's give out model a mission statement by configuring a system priompt. Change the configuration again:


```java
    // ..
    AdoptionsController (PromptChatMemoryAdvisor promptChatMemoryAdvisor,
                         ChatClient.Builder ai  ) {
        var system = """
                You are an AI powered assistant to help people adopt a dog from the adoption agency named Pooch Palace with locations in Rio de Janeiro, Mexico City, Seoul, Tokyo, Singapore, Paris, Mumbai, New Delhi, Barcelona, London, and San Francisco. Information about the dogs available will be presented below. If there is no information, then return a polite response suggesting we don't have any dogs available.
                """;
        this.ai = ai
                .defaultSystem(system)
                // ..
                .build();
    }
    // ..
```

Let's try asking a question more on point: 

```shell
http :8080/jlong/assistant question=="do you have any neurotic dogs?"
```

We're hoping the model will know about our friend _Prancer_. It should return, alas, that it does not. And this is to be expected. After all, we haven't extended access to our SQL database to the model (yet). We could read all the database in and then just concatenate it all into the body of the request. Conceptually, assuming we have a small enough data set and a large enough token count, that would work. But it's the principle of the thing! Remember, all interactions with the model incur a token _cost_. This cost may be born in dollars and cents, such as when using hosted multitenant LLMs like Claude, or at the very least its born in complexity (CPU and GPU resource consumption) costs. Either way: we want to reduce those costs, whenever possible. 

We'll read all the data from the SQL database using the newly minted `DogRepository` and then write out Spring AI `Document`s to the `VectorStore` in the constructor.

```java
    //...
    AdoptionsController(JdbcClient db,
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
                        dog.id(), dog.name(), dog.description()
                ));
                vectorStore.add(List.of(dogument));
            });
        }

        // ... same as before 
    }

```

This will use PostgresML behind the scenes to do the work. We must configure a `QuestionAnswerAdvisor` so that the `ChatClient` will know to consult the vector store for supporting documents ("doguments"?) the requests before sending the request off to the model for final analysis. Modify the definition of the `ChatClient` later on in the constructor accordingly: 

```java

        this.ai = ai
                // ...
                .defaultAdvisors(promptChatMemoryAdvisor, 
                                 new QuestionAnswerAdvisor(vectorStore))
                // ... 
                .build();
```

