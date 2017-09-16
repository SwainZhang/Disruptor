package com.example.sqlitetest.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.sqlitetest.MyApplication;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import java.util.Random;

/**
 * Created by emery on 2017/5/7.
 */

public class QueryEventHandler implements EventHandler<QueryEvent>, WorkHandler<QueryEvent> {
    private SQLiteDatabase mDatabase;
    private String mTableName;
    private Cursor mQuery;

    public QueryEventHandler() {

        mDatabase = UserDao.getInstance().getDatabas();
        // mTableName = Producer.mTableName;
    }

    @Override
    public void onEvent(QueryEvent queryEvent, long l, boolean b) throws Exception {
        this.onEvent(queryEvent);
    }

    @Override
    public void onEvent(QueryEvent queryEvent) throws Exception {
         //batchQuery(queryEvent,100);
        batchQuery(queryEvent.sql,queryEvent,5);
        //querySingleThread(queryEvent);



    }

    private void querySingleThread(QueryEvent queryEvent) throws InterruptedException {
        long start=System.currentTimeMillis();
        Cursor cursor = mDatabase.rawQuery(queryEvent.sql+" limit "+queryEvent.limit, null);
        Random random=new Random();
        while (cursor.moveToNext()){

        }
      int sleepTime=  random.nextInt(20);
        Thread.sleep(sleepTime);
   System.out.println("线程休眠时间--"+sleepTime+"--"+"查询数量--"+cursor.getCount()+"--"+"单线程耗时--"+(System.currentTimeMillis()-start));
    cursor.close();
    }

    public void checkTable(String sql){
        Context context = MyApplication.getAppContext();
        Cursor cursor = mDatabase.rawQuery(sql, null);
        System.out.println("^^^^^^" + cursor.getCount() + "^^^^^^");
    }
    public void batchQuery(String sql,QueryEvent queryEvent,int dividPoint) throws
            InterruptedException {
        int queryCount = queryEvent.limit;
        int queryOffset = queryEvent.offset;

        if (queryCount > dividPoint) {
            for (int i = 0; i <= queryCount / dividPoint; i++) {
                queryEvent.limit = dividPoint;
                queryEvent.index = i;
                queryEvent.offset = queryOffset + i * queryEvent.limit;
                UserDao.getInstance().query(queryEvent);
            }
        } else {
            long start = System.currentTimeMillis();
            String sqlafter = sql+" limit " +
                    queryEvent.limit + " offset " + queryEvent.offset + ";";
            System.out.println("sql-->" + sqlafter);
            mQuery = mDatabase.rawQuery(sqlafter, null);
            Random random=new Random();
            int sleepTime = random.nextInt(dividPoint*2);
            Log.i("TAG", queryEvent.index + "--本次查询数--->" + mQuery.getCount() + "<---本次查询总数");
            while (mQuery.moveToNext()) {

            }
            Thread.sleep(sleepTime);
            long end = System.currentTimeMillis();
            Log.i("TAG","线程睡眠时间="+sleepTime+ "--结束时间="+end + "----index=" + queryEvent.index + "--线程id=" + Thread.currentThread
                    ().getId() + "--查询耗时：" + (end - start));

        }


    }

    public void batchQuery(QueryEvent queryEvent, int dividPoint) {
        int queryCount = queryEvent.limit;
        int queryOffset = queryEvent.offset;

        if (queryCount > dividPoint) {
            for (int i = 0; i <= queryCount / dividPoint; i++) {
                queryEvent.limit = dividPoint;
                queryEvent.index = i;
                queryEvent.offset = queryOffset + i * queryEvent.limit;
                UserDao.getInstance().query(queryEvent);
            }
        } else {
            long start = System.currentTimeMillis();
            String sql = "select * from " + queryEvent.table + " limit " +
                    queryEvent.limit + " offset " + queryEvent.offset + ";";
            System.out.println("sql-->" + sql);
            mQuery = mDatabase.rawQuery(sql, null);
            Log.i("TAG", queryEvent.index + "--本次查询数--->" + mQuery.getCount() + "<---本次查询总数");
            while (mQuery.moveToNext()) {
                User user = new User();
                user.id = mQuery.getInt(mQuery.getColumnIndex("_id"));
                user.name = mQuery.getString(mQuery.getColumnIndex(TableColums.NAME));
                user.age = mQuery.getString(mQuery.getColumnIndex(TableColums.AGE));
                user.phone = mQuery.getString(mQuery.getColumnIndex(TableColums.PHONE));
                user.address = mQuery.getString(mQuery.getColumnIndex(TableColums.ADDRESS));
            }
            long end = System.currentTimeMillis();
            Log.i("TAG", end + "----index=" + queryEvent.index + "--线程id=" + Thread.currentThread
                    ().getId() + "--查询耗时：" + (end - start));

        }


    }
}
