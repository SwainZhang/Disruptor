package com.example.emery.disruptor.four;

import android.util.Log;

import com.lmax.disruptor.EventHandler;

/**
 * Created by emery on 2017/5/9.
 */

public class EndInserHandler implements EventHandler<InsertEvent> {
    @Override
    public void onEvent(InsertEvent queryEvent, long l, boolean b) throws Exception {
        System.out.println("EndMessageHandler参数：long=" + l + "--" + "boolean--" + b);
        Log.i("TAG", "EndMessageHandler执行任务：" + Thread.currentThread().getName()
                + "--index=" + queryEvent.index);
    }
}