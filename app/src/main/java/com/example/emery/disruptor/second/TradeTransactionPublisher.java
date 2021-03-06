package com.example.emery.disruptor.second;

import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.CountDownLatch;

/**
 * Created by emery on 2017/5/7.
 */

public class TradeTransactionPublisher implements Runnable {
    Disruptor<TradeTransaction> disruptor;
    private CountDownLatch latch;
    private static int LOOP = 10000;//模拟一千万次交易的发生

    public TradeTransactionPublisher(CountDownLatch latch, Disruptor<TradeTransaction> disruptor) {
        this.disruptor = disruptor;
        this.latch = latch;
    }

    @Override
    public void run() {
        TradeTransactionEventTranslator tradeTransloator = new TradeTransactionEventTranslator();
        for (int i = 0; i < LOOP; i++) {
            tradeTransloator.index=i;
            disruptor.publishEvent(tradeTransloator);
            System.out.println("---------发布任务---------"+i);
        }
        //latch.countDown();
    }
}
