package com.example.emery.disruptor.second;

import com.lmax.disruptor.EventHandler;

/**
 * Created by emery on 2017/5/7.
 */

public class TradeTransactionJMSNotifyConsumer implements EventHandler<TradeTransaction> {

    @Override
    public void onEvent(TradeTransaction event, long sequence,
                        boolean endOfBatch) throws Exception {
        System.out.println("&&&TradeTransactionJMSNotifyHandler执行任务："+Thread.currentThread().getName()+event.toString());

    }
}





