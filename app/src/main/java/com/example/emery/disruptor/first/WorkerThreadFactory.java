package com.example.emery.disruptor.first;

import java.util.concurrent.ThreadFactory;

/**
 * Created by emery on 2017/5/6.
 */

public class WorkerThreadFactory implements ThreadFactory {

    private int counter = 0;
    private String prefix = "";

    public WorkerThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    public Thread newThread(Runnable r) {
        return new Thread(r, prefix + "-"+counter++);
    }
}
