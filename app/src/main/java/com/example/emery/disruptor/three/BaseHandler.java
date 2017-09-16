package com.example.emery.disruptor.three;

import android.database.sqlite.SQLiteDatabase;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * Created by emery on 2017/5/7.
 */

public abstract class BaseHandler<T> implements EventHandler<T>,WorkHandler<T> {
    private SQLiteDatabase mSqLiteDatabase;
    private String mTableName;


    public BaseHandler(SQLiteDatabase sqLiteDatabase, String tableName){

        mSqLiteDatabase = sqLiteDatabase;
        mTableName = tableName;
    }
    @Override
    public void onEvent(T t) throws Exception {
        onEvent(t,mSqLiteDatabase,mTableName);
    }

    @Override
    public void onEvent(T t, long l, boolean b) throws Exception {
        this.onEvent(t);
    }
    public abstract void onEvent(T t, SQLiteDatabase sqLiteDatabase,String tableName);
}