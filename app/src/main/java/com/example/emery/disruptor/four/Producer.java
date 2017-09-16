package com.example.emery.disruptor.four;

import android.database.sqlite.SQLiteDatabase;

import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.CountDownLatch;

/**
 * Created by emery on 2017/5/7.
 *
 * 一个任务查询10000条750ms
 * 十个任务查询10000条，每个任务查询1000条
 *
 */

public class Producer implements Runnable{

    private SQLiteDatabase mDatabase;
    private CountDownLatch mLatch;
    private Disruptor<QueryEvent> mDisruptor;
    public static final String mTableName="t_user";
    public Producer(Disruptor<QueryEvent> disruptor){
        mDisruptor = disruptor;
    }

    @Override
    public void run() {
        QueryEvent queryEvent = new QueryEvent();
        queryEvent.colums=null;
        queryEvent.selection=null;
        queryEvent.selectionArgs=null;
        queryEvent.groupBy=null;
        queryEvent.having=null;
        queryEvent.orderBy= TableColums.AGE+" desc";
        queryEvent.limit=100;
        queryEvent.table= Producer.mTableName;
        UserDao dao= UserDao.getInstance();
        for (int i = 0; i < 10; i++) {
            queryEvent.offset=i*queryEvent.limit;
            queryEvent.index=i;
           dao.query(queryEvent);
        }
       // mLatch.countDown();

    }
}
