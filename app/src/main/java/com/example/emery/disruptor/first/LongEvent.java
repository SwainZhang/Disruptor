package com.example.emery.disruptor.first;

/**
 * Created by emery on 2017/5/6.
 *
 * 1.定义事件
 事件(Event)就是通过 Disruptor 进行交换的数据类型。
 */

public class LongEvent {
    private long value;

    public void set(long value)
    {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
