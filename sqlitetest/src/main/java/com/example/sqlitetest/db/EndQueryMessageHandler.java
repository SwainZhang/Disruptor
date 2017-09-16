package com.example.sqlitetest.db;

import android.util.Log;

import com.lmax.disruptor.EventHandler;

/**
 * Created by emery on 2017/5/7.
 */

public class EndQueryMessageHandler implements EventHandler<QueryEvent>{
    @Override
    public void onEvent(QueryEvent queryEvent, long l, boolean b) throws Exception {
        System.out.println("EndMessageHandler参数：long="+l+"--"+"boolean--"+b);
        Log.i("TAG","EndMessageHandler执行任务：" + Thread.currentThread().getName()+"--index="+queryEvent.index);
    }
}
