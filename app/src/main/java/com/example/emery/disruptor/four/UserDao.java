package com.example.emery.disruptor.four;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.emery.disruptor.MyApplication;
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
    public static final String mTableName="t_user";
    private CountDownLatch mCountDownLatch;
    private Disruptor<QueryEvent> mQueryEventDisruptor;
    private Disruptor<InsertEvent> mInsertEventDisruptor;
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    private RingBuffer<QueryEvent> mRingBuffer;
    private RingBuffer<InsertEvent> mMInsertRingBuffer;

    public SQLiteDatabase getDatabas() {
        return mDatabase;
    }

    private UserDao() {
        mQueryEventDisruptor=MyApplication.getQueryDisruptor();
        mInsertEventDisruptor=MyApplication.getInsertDisruptor();
        mDatabase = MyApplication.getAppContext().openOrCreateDatabase("dis.db",
                MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
        String sql_user = "create table if not exists " + mTableName + "(_id integer primary key " +
                "autoincrement,name text,age text,address text,phone text);";
        String sql_userc = "create table if not exists " + bak_table + "(_id integer primary key " +
                "autoincrement,name text,age text,address text,phone text);";
        mDatabase.execSQL(sql_user);
        mDatabase.execSQL(sql_userc);

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






     /*
      mRingBuffer = mQueryEventDisruptor.getRingBuffer();
      long next = mRingBuffer.next();
        try {
            QueryEvent queryEvent = mRingBuffer.get(next);
            queryEvent.table=mEvent.table;
            queryEvent.colums=mEvent.colums;
            queryEvent.selection= mEvent.selection;
            queryEvent.selectionArgs=mEvent.selectionArgs;
            queryEvent.groupBy=mEvent.groupBy;
            queryEvent.having=mEvent.having;
            queryEvent.orderBy=mEvent.orderBy;
            queryEvent.limit=mEvent.limit;
            queryEvent.offset=mEvent.offset;
            queryEvent.index=mEvent.index;
        }finally {
            mRingBuffer.publish(next);
        }
*/
        System.out.println("发布查询" + mEvent.index);
    }
    public void insert(Disruptor<InsertEvent> mInsertEventDisruptor,InsertEvent insertEvent) {
        if(mInsertEventDisruptor!=null) {
            mInsertEventDisruptor.publishEvent(new DbInserTanslator(insertEvent));
        }else{
            Log.i("TAG","插入异常");
        }
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
                        for (int i = 0; i < 10000; i++) {
                            User user = new User();
                            user.address = "address三" + i;
                            user.name = "name三" + i;
                            user.age = "age三" + i;
                            user.phone = "phone三" + i;
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(TableColums.NAME, user.name);
                            contentValues.put(TableColums.AGE, user.age);
                            contentValues.put(TableColums.ADDRESS, user.address);
                            contentValues.put(TableColums.PHONE, user.phone);
                            mDatabase.insert(mTableName, null, contentValues);

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
    public void insert(InsertEvent insertEvent) {
       /* if(mInsertEventDisruptor!=null){
            mInsertEventDisruptor.publishEvent(new DbInserTanslator(insertEvent));
        }else{
            Log.i("TAG","插入异常");
        }
        */

        if(mInsertEventDisruptor==null){
            Log.i("TAG","插入异常");
            return;
        }else {
            mMInsertRingBuffer = mInsertEventDisruptor.getRingBuffer();
        }
        long next = mMInsertRingBuffer.next();
        try {
            InsertEvent insert = mMInsertRingBuffer.get(next);
            insert.tableName=insertEvent.tableName;
            insert.mContentValues=insertEvent.mContentValues;
            insert.index=insertEvent.index;
        }finally {
            mMInsertRingBuffer.publish(next);
        }


        Log.i("TAG","发布插入"+insertEvent.index);



    }
    public void beginTransactionNonExclusive(){
        mDatabase.beginTransactionNonExclusive();
    }
    public void endTransaction(){
        mDatabase.endTransaction();
    }


    public long insert(User user) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableColums.NAME, user.name);
        contentValues.put(TableColums.AGE, user.age);
        contentValues.put(TableColums.ADDRESS, user.address);
        contentValues.put(TableColums.PHONE, user.phone);
        long insert = mDatabase.insert(bak_table, null, contentValues);
        return insert;

    }
    public String getDatabasepath(){
        return mDatabase.getPath();
    }

    public void shutDown(){
        mQueryEventDisruptor.shutdown();
    }

}
