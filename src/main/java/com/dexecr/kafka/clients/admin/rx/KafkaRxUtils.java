package com.dexecr.kafka.clients.admin.rx;

import org.apache.kafka.common.KafkaFuture;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

class KafkaRxUtils {

    public static <K,V> Map<K, Mono<V>> transform(Map<K, KafkaFuture<V>> src) {
        return src.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> KafkaRxCompletableFuture.toMono(e.getValue())));
    }

    public static <T> Mono<T> transform(KafkaFuture<T> src) {
        return KafkaRxCompletableFuture.toMono(src);
    }

    private static class KafkaRxCompletableFuture<T> extends CompletableFuture<T> {
        private final KafkaFuture<T> kafkaFuture;

        private KafkaRxCompletableFuture(KafkaFuture<T> kafkaFuture) {
            this.kafkaFuture = kafkaFuture;
        }

        @Override
        public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
            return new KafkaRxCompletableFuture<>(kafkaFuture.whenComplete(action::accept));
        }

        public static <T> Mono<T> toMono(KafkaFuture<T> kafkaFuture) {
            return Mono.fromFuture(new KafkaRxCompletableFuture<>(kafkaFuture))
                    .take(Duration.of(10, ChronoUnit.SECONDS))
                    .doOnCancel(() -> kafkaFuture.cancel(true));
        }
    }
}
