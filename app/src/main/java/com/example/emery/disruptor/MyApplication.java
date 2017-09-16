package com.example.emery.disruptor;

import android.app.Application;
import android.content.Context;

import com.example.emery.disruptor.first.WorkerThreadFactory;
import com.example.emery.disruptor.four.EndInserHandler;
import com.example.emery.disruptor.four.EndQueryMessageHandler;
import com.example.emery.disruptor.four.InsertEvent;
import com.example.emery.disruptor.four.InsertEventFactory;
import com.example.emery.disruptor.four.InsertEventHandler;
import com.example.emery.disruptor.four.QueryEvent;
import com.example.emery.disruptor.four.QueryEventFactory;
import com.example.emery.disruptor.four.QueryEventHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Created by emery on 2017/5/7.
 */

public class MyApplication  extends Application{
    private static Context mContext;
    private static Disruptor<QueryEvent> mMQueryEventDisruptor;
    private static Disruptor<InsertEvent> mInsertEventDisruptor;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(t.getName()+"-----uncaughtException-----"+e.getMessage());
            }
        });
        initDiruptor();
    }

    private void initDiruptor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mInsertEventDisruptor = new Disruptor<InsertEvent>(
                        new InsertEventFactory(), 1024,
                        new WorkerThreadFactory("insert"),
                        ProducerType.MULTI,
                        new YieldingWaitStrategy());
                EventHandlerGroup insertEventHandlerGroup = mInsertEventDisruptor
                        .handleEventsWithWorkerPool(
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler(),
                                new InsertEventHandler());

                insertEventHandlerGroup.then(new EndInserHandler());
                mInsertEventDisruptor.start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                mMQueryEventDisruptor = new Disruptor<QueryEvent>(
                        new QueryEventFactory(), 1024,
                        new WorkerThreadFactory("query"),
                        ProducerType.MULTI,
                        new YieldingWaitStrategy());

                EventHandlerGroup<QueryEvent> queryEventEventHandlerGroup = mMQueryEventDisruptor
                        .handleEventsWithWorkerPool(
                                new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler());
                queryEventEventHandlerGroup.then(new EndQueryMessageHandler());
                mMQueryEventDisruptor.start();
            }
        }).start();

    }
    public static Disruptor<QueryEvent> getQueryDisruptor(){
        return mMQueryEventDisruptor;
    }
    public static Disruptor<InsertEvent> getInsertDisruptor(){
        return mInsertEventDisruptor;
    }


    public static Context getAppContext(){
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
