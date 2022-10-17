package com.example.apiaggregator.queue;

import com.example.apiaggregator.web.model.events.LimitReachedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@RequiredArgsConstructor
public class TimedQueue {

    static final int QUEUE_SIZE = 5;
    static final int QUEUE_MAX_DELAY_IN_MS = 5000;

    private final List<String> items = Collections.synchronizedList(new ArrayList<>());

    private Timer timer;
    private final JmsTemplate jmsTemplate;
    private final String queueName;

    public void push(List<String> items) {
        log.info("Pushing items {}", items);
        for (String item : items) {
            push(item);
        }
    }

    private void push(String item) {
        if (items.isEmpty()) {
            setTimer();
        }

        items.add(item);
        if (items.size() == QUEUE_SIZE) {
            flush();
        }

    }

    private void setTimer() {
        TimerTask task = new TimerTask() {
            public void run() {
                flush();
                log.debug("Task performed on: " + new Date() + "n" +
                        "Thread's name: " + Thread.currentThread().getName());
            }
        };
        timer = new Timer("Timer");
        timer.schedule(task, QUEUE_MAX_DELAY_IN_MS);
    }

    private void flush() {

        log.info("Flushing items {}", items);

        // notify listeners
        jmsTemplate.convertAndSend(queueName, new LimitReachedEvent(items));

        items.clear();
        timer.cancel();
    }
}
