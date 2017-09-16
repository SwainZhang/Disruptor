package com.example.emery.disruptor.four;

import com.lmax.disruptor.EventFactory;

/**
 * Created by emery on 2017/5/9.
 */

public class InsertEventFactory implements EventFactory<InsertEvent> {
    @Override
    public InsertEvent newInstance() {
        return new InsertEvent();
    }
}
