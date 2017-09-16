package com.example.emery.disruptor.four;

import android.database.sqlite.SQLiteDatabase;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * Created by emery on 2017/5/7.
 */

public class InsertEventHandler implements EventHandler<InsertEvent>, WorkHandler<InsertEvent> {



   SQLiteDatabase mDatabase;
    public InsertEventHandler(){
        mDatabase = UserDao.getInstance().getDatabas();
     }

    @Override
    public void onEvent(InsertEvent insertEvent, long l, boolean b) throws Exception {
        this.onEvent(insertEvent);
    }

    @Override
    public void onEvent(InsertEvent insertEvent){

        mDatabase.insert(insertEvent.tableName,null,insertEvent.mContentValues);
        System.out.println("插入："+insertEvent.index+"--"+insertEvent.tableName+"--"+ insertEvent.mContentValues.toString());
         /*  System.out.println("");

            insertEvent.mContentValues.get(TableColums.NAME);
            insertEvent.mContentValues.get(TableColums.AGE);
            insertEvent.mContentValues.get(TableColums.ADDRESS);
            insertEvent.mContentValues.get(TableColums.PHONE);*/



    }
}
