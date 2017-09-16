package com.example.emery.disruptor.four;

import com.lmax.disruptor.EventFactory;

/**
 * Created by emery on 2017/5/7.
 */

public class QueryEventFactory implements EventFactory<QueryEvent> {
    @Override
    public QueryEvent newInstance() {
        return new QueryEvent();
    }
}
