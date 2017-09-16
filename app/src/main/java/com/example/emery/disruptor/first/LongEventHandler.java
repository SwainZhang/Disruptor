package com.example.emery.disruptor.first;

import com.lmax.disruptor.EventHandler;

/**
 * Created by emery on 2017/5/6.
 * <p>
 * 定义事件处理的具体实现
 * 通过实现接口 com.lmax.disruptor.EventHandler<T> 定义事件处理的具体实现。
 */

public class LongEventHandler implements EventHandler<LongEvent> {
    @Override
    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        System.out.println("Event: " + longEvent);
    }
}
