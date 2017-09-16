package com.example.emery.disruptor.second;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * Created by emery on 2017/5/7.
 */

public class TradeTransactionInDBConsumer implements EventHandler<TradeTransaction>,WorkHandler<TradeTransaction> {

    @Override
    public void onEvent(TradeTransaction event, long sequence,
                        boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }

    @Override
    public void onEvent(TradeTransaction event) throws Exception {
        //这里做具体的消费逻辑
       // event.setId(UUID.randomUUID().toString());//简单生成下ID
       // System.out.println(event.getId());
        Thread.currentThread().sleep(5);
        System.out.println("%%%TradeTransactionInDBConsumer执行任务:"+Thread.currentThread().getName()+event.toString());
    }
}
