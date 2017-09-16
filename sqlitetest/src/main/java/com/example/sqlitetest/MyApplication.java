package com.example.sqlitetest;

import android.app.Application;
import android.content.Context;

import com.example.sqlitetest.db.EndQueryMessageHandler;
import com.example.sqlitetest.db.QueryEvent;
import com.example.sqlitetest.db.QueryEventFactory;
import com.example.sqlitetest.db.QueryEventHandler;
import com.example.sqlitetest.db.WorkerThreadFactory;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Created by emery on 2017/5/7.
 */

public class MyApplication extends Application{
    private static Context mContext;
    private static Disruptor<QueryEvent> mMQueryEventDisruptor;


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



    public static Context getAppContext(){
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
