package com.example.emery.disruptor.three;

import android.util.Log;

import com.lmax.disruptor.EventHandler;

/**
 * Created by emery on 2017/5/7.
 */

public class EndMessageHandler implements EventHandler<QueryEvent>{
    @Override
    public void onEvent(QueryEvent queryEvent, long l, boolean b) throws Exception {
        Log.i("TAG","EndMessageHandler执行任务：" + Thread.currentThread().getName()+"--index="+queryEvent.index);
    }
}
