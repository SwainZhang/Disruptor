package com.example.emery.disruptor.three;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.emery.disruptor.MyApplication;
import com.lmax.disruptor.dsl.Disruptor;

import static android.content.Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;
import static com.example.emery.disruptor.three.Producer.mTableName;

/**
 * Created by emery on 2017/5/7.
 */

public class UserDao {
    public static UserDao dao;
    SQLiteDatabase mDatabase;
    public static final String bak_table="t_user_bak";
    public SQLiteDatabase getDatabas(){
        return mDatabase;
    }
    private UserDao(){
        mDatabase = MyApplication.getAppContext().openOrCreateDatabase("dis.db",
                MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
        String sql_user="create table if not exists "+mTableName+"(_id integer primary key autoincrement,name text,age text,address text,phone text);";
        String sql_userc="create table if not exists "+bak_table+"(_id integer primary key autoincrement,name text,age text,address text,phone text);";
        mDatabase.execSQL(sql_user);
        mDatabase.execSQL(sql_userc);
    }
    public static  UserDao getInstance(){
        if(dao==null){
            synchronized (UserDao.class){
                if(dao==null){
                    dao=new UserDao();
                }
            }
        }
        return dao;
    }
    public void deleteBak(){
        int delete = mDatabase.delete(bak_table, null, null);
        Log.i("TAG","删除" + delete);
    }

    public void query(Disruptor<QueryEvent> disruptor, QueryEvent mEvent){
        disruptor.publishEvent(new DbEventTranslator(mEvent));


        /*RingBuffer<QueryEvent> ringBuffer = disruptor.getRingBuffer();

            long next = ringBuffer.next();
        try {
            QueryEvent queryEvent = ringBuffer.get(next);
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
            ringBuffer.publish(next);
        }*/

        System.out.println("发布查询"+mEvent.index);
    }
    public void insert(){
        mDatabase.beginTransactionNonExclusive();
        for (int i = 0; i < 10000; i++) {
            User user=new User();
            user.address="address"+i;
            user.name="name"+i;
            user.age="age"+i;
            user.phone="phone"+i;
            ContentValues contentValues=new ContentValues();
            contentValues.put(TableColums.NAME, user.name);
            contentValues.put(TableColums.AGE, user.age);
            contentValues.put(TableColums.ADDRESS, user.address);
            contentValues.put(TableColums.PHONE, user.phone);
            mDatabase.insert(mTableName,null,contentValues);

        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

    }

    public long insert(User user){

            ContentValues contentValues=new ContentValues();
            contentValues.put(TableColums.NAME, user.name);
            contentValues.put(TableColums.AGE, user.age);
            contentValues.put(TableColums.ADDRESS, user.address);
            contentValues.put(TableColums.PHONE, user.phone);
        long insert = mDatabase.insert(bak_table, null, contentValues);
return insert;

    }

}
