package com.example.emery.disruptor.second;

import com.lmax.disruptor.EventTranslator;

import java.util.Random;

/**
 * Created by emery on 2017/5/7.
 */

public class TradeTransactionEventTranslator implements EventTranslator<TradeTransaction> {
    private Random random=new Random();
    @Override
    public void translateTo(TradeTransaction event, long sequence) {
        this.generateTradeTransaction(event);
    }
    private TradeTransaction generateTradeTransaction(TradeTransaction trade){
        trade.setPrice(index);
        return trade;
    }
    public int index;

}
