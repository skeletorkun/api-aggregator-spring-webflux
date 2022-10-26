package com.example.apiaggregator.queue;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class PublisherQueue<T> {

    static final int QUEUE_SIZE = 5;
    static final int QUEUE_MAX_DELAY_IN_MS = 5000;

    private final ConnectableFlux<T> connectableFlux;
    private final Sinks.Many<String> sink;


    public PublisherQueue(Function<List<String>, Mono<T>> transform) {
        sink = Sinks.many().multicast().onBackpressureBuffer();

        this.connectableFlux = sink.asFlux()
                .bufferTimeout(QUEUE_SIZE, Duration.ofMillis(QUEUE_MAX_DELAY_IN_MS))
                .flatMap(transform)
                .onErrorResume(RuntimeException.class, (e) -> Mono.just())
                .publish();
    }

    public void push(Set<String> items){
        for (String item: items){
            sink.tryEmitNext(item);
        }
    }

    public Disposable subscribe(Consumer<? super T> consumer) {
        return connectableFlux.subscribe(consumer);
    }

}
