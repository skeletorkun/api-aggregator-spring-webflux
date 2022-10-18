package com.example.apiaggregator.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class TimedQueue {

    static final int QUEUE_SIZE = 5;
    static final int QUEUE_MAX_DELAY_IN_MS = 5000;

    Timer timer;
    List<String> queue; // use sth synchronized

    public void push(List<String> items){
        for (String item: items){
            push(item);
        }
    }

    private void push(String item) {
        if(queue.isEmpty()){
            setTimer();
        }

        queue.add(item);
        if(queue.size() == QUEUE_SIZE){
            flush();
        }

    }

    private void setTimer(){
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

    private void flush(){

        // notify listeners

        queue.clear();
        timer.cancel();
    }
}
