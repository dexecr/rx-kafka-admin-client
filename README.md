RxKafkaAdminClient
========

`RxKafkaAdminClient` - reactive wrapper for KafkaAdminClient that's based on Reactor framework. Under the cover method's results are transformed from `KafkaFuture<T>` to `Mono<T>`

`RxKafkaAdminClient` is based on generated code without using java reflection api

Java version
------------

`RxKafkaAdminClient` requires Java 8 or + to run


Examples
--------

#### Create, describe and delete topic operations using native KafkaAdminClient and RxKafkaAdminClient

Native KafkaAdminClient:

```java
Admin client = Admin.create(Map.of(
        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
        AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000
));
List<String> createdTopics = client.createTopics(List.of(new NewTopic("test", 5, (short) 1))).values()
        .entrySet().stream()
        .map(createdTopic -> createdTopic.getValue()
                .whenComplete((r, e) -> System.out.println("KafkaAdmin. Topic created: " + createdTopic.getKey()))
                .thenApply(r -> createdTopic.getKey())
                .toCompletionStage().toCompletableFuture().join()
        ).toList();
List<String> describedTopics = client.describeTopics(createdTopics).topicNameValues().values().stream()
        .map(descriptionFuture -> descriptionFuture
                .whenComplete((description, throwable) ->
                    System.out.println("KafkaAdmin. Topic description: " + description)
                )
                .thenApply(TopicDescription::name)
                .toCompletionStage().toCompletableFuture().join()
        ).toList();
client.deleteTopics(describedTopics).topicNameValues()
        .entrySet().stream()
        .map(deletedTopic -> deletedTopic.getValue()
                .whenComplete((r, e) -> System.out.println("KafkaAdmin. Topic deleted: " + deletedTopic.getKey()))
                .thenApply(r -> deletedTopic.getKey())
                .toCompletionStage().toCompletableFuture().join()
        ).findFirst();
System.out.println("Close KafkaAdmin...");
client.close();
```

RxKafkaAdminClient:

```java
RxAdmin client = RxAdmin.create(Map.of(
        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
        AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000
));
BiFunction<Mono<Void>, String, Mono<String>> switchEmpty =
     (mono, value) -> mono.map(Optional::of).switchIfEmpty(Mono.just(Optional.empty())).map(r -> value);
//create topic
Flux.concat(client.createTopics(List.of(new NewTopic("test", 5, (short) 1))).values()
    .entrySet().stream()
    .map(entry -> switchEmpty.apply(entry.getValue(), entry.getKey())).toList())
    //topic created
    .doOnNext(topic -> System.out.println("RxAdmin. Topic created: " + topic))
    .collectList()
    //describe created topic
    .map(topics -> client.describeTopics(topics).topicNameValues())
    .flatMapIterable(Map::values).flatMap(Function.identity())
    //print topic description
    .doOnNext(description -> System.out.println("RxAdmin. Topic description: " + description))
    .map(TopicDescription::name)
    .collectList()
    //delete created topic
    .map(topics -> client.deleteTopics(topics).values())
    .flatMapIterable(topicResult -> topicResult
        .entrySet().stream()
        .map(entry -> switchEmpty.apply(entry.getValue(), entry.getKey()))
        .toList()
    ).flatMap(Function.identity())
    //print deleted topic
    .doOnNext(topic -> System.out.println("RxAdmin. Topic deleted: " + topic))
    .collectList()
    //close client
    .doOnSuccess(result -> { System.out.println("Close RxAdmin..."); client.close(); })
    .block();
```

