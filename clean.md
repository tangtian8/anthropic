# Your First Spring AI 1.0 Application with Anthropic

Hi, Spring fans! Spring AI is _live_ on the Spring Initializr and everywhere fine bytes might be had. Ask your doctor if AI is right for you! It's an amazing time to be a Java and Spring developer. There's never been a better time to be a Java and Spring developer, and this is doubly true in this unique AI moment. You see, 90% of what people talk about when they talk about AI engineering is just integration with models, most of which have HTTP APIs. And most of what these models take is just human-language `String`s. This is integration code, and what place for these integrations to exist than hanging off the side of your Spring-based workloads? The same workloads who business logic drives your organizations and which guard data that feeds your organization. 


## the Pains and Patterns of AI Engineering

AI is _amazing_, but it's not _perfect_. It has issues, as with all technologies! There are a few things to be on the watch for, and when you explore Spring AI, know that you're doing so in terms of the patterns that support you on your journey to production. Let's look at some of them. 

![the pains and patterns of AI](images/pp.png)

Chat models are amenable to just about anything, and will follow you down just about any rabbit hole. If you want it to stay focused on a particular mission, give it a *system prompt*. 

Models are stateless. You might find that surprising if you've ever dealt with a model through  ChatGPT or Claude Desktop, because they submit a transcript of everything that's been said on each subsequent request. This transcript reminds the model what's been said. The transcript is *chat memory*.

Models live in an isolated sandbox. This makes sense! We've all seen the documentary called _The Terminator_ and know what can go wrong with unruly AI. But, they can do amazing things if you give them a bit of control, via *tool calling*.



Spring AI is a huge leap forward, but for Spring developers it'll feel like a  natural next step. It works like any Spring project. It has portable service abstractions allowing you to work consistently and conveniently with any of a number of models. It provides Spring Boot starters, configuration properties, and autoconfiguration. And, Spring AI carries Spring Boot's production-minded ethic forward, supporting vritual threads, GraalVM native images, and observability through Micrometer. It also offers a great developer experience, integrating Spring Boot's DevTools, and provides rich support for Docker Compose and Testcontainers. As with all Spring projects, you can get started on the [Spring Initializr](https://start.spring.io).

## Meet the Dogs
And that's just what we're going to do! We're going to build an application to support adopting dogs! I'm inspired by a dog that went viral back in 2021. The dog, named _Prancer_, sounds like he'd be quite the handful! Here's my favorite excerpt from [the ad](https://www.facebook.com/tyfanee.fortuna/posts/10219752628710467):

"Ok, I’ve tried. I’ve tried for the last several months to post this dog for adoption and make him sound...palatable. The problem is, he’s just not. There’s not a very big market for neurotic, man-hating, animal-hating, children-hating dogs that look like gremlins. But I have to believe there’s someone out there for Prancer, because I am tired and so is my family. Every day we live in the grips of the demonic Chihuahua hellscape he has created in our home."

## The Pre-requisites
Sounds like quite a handful! But even spicy dogs deserve loving homes. So let's build a service to unite people with the dogs of their dreams (or nightmares?) Hit the Spring Initializr and add the following dependencies to your project: `PgVector`, `GraalVM`, `Actuator`, `Data JDBC`, `JDBC Chat Memory`, `PostgresML`, `Devtools`, and `Web`. Choose Java 24 (or later) and Apache Maven as the build tool. (Strictly speaking, there's no reason you could not use Gradle here, but the example will be in terms of Apache Maven) 

Make sure that in  your `pom.xml`, you've also got: `org.springframework.ai`:`spring-ai-advisors-vector-store`.

Some of these things are familiar. `Data JDBC` just brings in Spring Data JDBC, which is just an ORM mapper that allows you to talk to a SQL database. `Web` brings in Spring MVC. `Actuator` brings in Spring Boot's observability stack, underpinned in part by [Micrometer](https://micrometer.io). `Devtools` is a development-time concern, allowing you to do live-reloads as you make changes. It'll automatically reload the code each time you do a "Save" operation in Visual Studio Code or Eclipse, and it'll automatically kick in each time you alt-tab away from IntelliJ IDEA. `GraalVM` brings in support for the OpenJDK fork, GraalVM, which provides among other things an ahead-of-time compiler (AOT) that produces lightweight, lighting fast binaries. 

We said that Spring Data JDBC will make it easy to connect to a SQL database, but which one? In our application, we'll be using PostgreSQL, but not just vanilla PostgreSQL! We're going to load two very important extensions: `vector` and `postgresml`. The `vector` plugin allows PostgreSQL