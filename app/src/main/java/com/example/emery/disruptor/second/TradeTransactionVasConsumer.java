package com.example.emery.disruptor.second;

import com.lmax.disruptor.EventHandler;

/**
 * Created by emery on 2017/5/7.
 */

public class TradeTransactionVasConsumer implements EventHandler<TradeTransaction> {

    @Override
    public void onEvent(TradeTransaction event, long sequence,
                        boolean endOfBatch) throws Exception {
        //do something....
        Thread.currentThread().sleep(5);
        System.out.println("***TradeTransactionVasConsumer执行任务："+Thread.currentThread().getName()+event.toString());
    }

}
