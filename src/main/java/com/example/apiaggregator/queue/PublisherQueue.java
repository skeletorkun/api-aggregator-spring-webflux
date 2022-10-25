package com.example.apiaggregator.queue;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class PublisherQueue {

    static final int QUEUE_SIZE = 5;
    static final int QUEUE_MAX_DELAY_IN_MS = 5000;
    private final Sinks.Many<String> sink;
    private final ConnectableFlux<List<String>> connectableFlux;

    public PublisherQueue() {
        sink = Sinks.many().multicast().onBackpressureBuffer();

        connectableFlux = sink.asFlux()
                .bufferTimeout(QUEUE_SIZE, Duration.ofMillis(QUEUE_MAX_DELAY_IN_MS))
                .publish();

        connectableFlux.connect(); // start emission
    }

    public void push(List<String> items) {
        for (String item : items) {
            sink.tryEmitNext(item);
        }
    }

    public Disposable subscribe(Consumer<List<String>> consumer) {
        return connectableFlux.subscribe(consumer);
    }

}
