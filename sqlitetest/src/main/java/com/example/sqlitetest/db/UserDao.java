package com.example.sqlitetest.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.sqlitetest.MyApplication;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;

/**
 * Created by emery on 2017/5/7.
 */

public class UserDao {
    public static UserDao dao;
    SQLiteDatabase mDatabase;
    public static final String bak_table = "t_user_bak";
    public static final String mTableName="T_PRODUCTDOC";
    //public static final String mTableName="T_USER";
    private CountDownLatch mCountDownLatch;
    private Disruptor<QueryEvent> mQueryEventDisruptor;
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    private RingBuffer<QueryEvent> mRingBuffer;

    public SQLiteDatabase getDatabas() {
        return mDatabase;
    }

    private UserDao() {
        mQueryEventDisruptor= MyApplication.getQueryDisruptor();
        mDatabase = MyApplication.getAppContext().openOrCreateDatabase("laiqian.db",
                MODE_ENABLE_WRITE_AHEAD_LOGGING, null);

    }


    public static UserDao getInstance() {
        if (dao == null) {
            synchronized (UserDao.class) {
                if (dao == null) {
                    dao = new UserDao();
                }
            }
        }
        return dao;
    }



    public void deleteBak() {
        int delete = mDatabase.delete(bak_table, null, null);
        Log.i("TAG", "删除" + delete);
    }

    public void query(Disruptor<QueryEvent> mQueryEventDisruptor, QueryEvent mEvent) {
        mQueryEventDisruptor.publishEvent(new DbQueryEventTranslator(mEvent));
        System.out.println("发布查询" + mEvent.index);
    }

    public void query(final QueryEvent mEvent) {
        mQueryEventDisruptor.publishEvent(new DbQueryEventTranslator(mEvent));
        System.out.println("发布查询" + mEvent.index);
    }

    public void delete(final String tableName){

        new Thread(new Runnable() {
            @Override
            public void run() {
                int delete = mDatabase.delete(tableName, "_id>?", new String[]{"1456548204947"});
                System.out.println("删除"+delete+"条数据");
            }
        }).start();
    }

    public void insert(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                while (true) {
                    index++;
                    try {
                        mDatabase.beginTransactionNonExclusive();
                        long start = System.currentTimeMillis();
                        for (long i = 0;i<10000; i++) {

                            mDatabase.execSQL(SqlBox.INSERT,new Object[]{1500000000000L+index,i});

                        }
                        mDatabase.setTransactionSuccessful();
                        Log.i("TAG", "插入耗时" + (System.currentTimeMillis() - start));
                    } finally {
                        Log.i("TAG","第"+index+"次插入10000");
                        mDatabase.endTransaction();
                    }


                }


            }
        }).start();

    }
    public void beginTransactionNonExclusive(){
        mDatabase.beginTransactionNonExclusive();
    }
    public void endTransaction(){
        mDatabase.endTransaction();
    }
    public String getDatabasepath(){
        return mDatabase.getPath();
    }

    public void shutDown(){
        mQueryEventDisruptor.shutdown();
    }

}
