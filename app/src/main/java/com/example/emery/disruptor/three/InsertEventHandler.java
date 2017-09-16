package com.example.emery.disruptor.three;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by emery on 2017/5/7.
 */

public class InsertEventHandler extends BaseHandler<User>{

    public InsertEventHandler(SQLiteDatabase sqLiteDatabase, String tableName) {
        super(sqLiteDatabase, tableName);
    }

    @Override
    public void onEvent(User user, SQLiteDatabase sqLiteDatabase, String tableName) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(TableColums.NAME, user.name);
        contentValues.put(TableColums.AGE, user.age);
        contentValues.put(TableColums.ADDRESS, user.address);
        contentValues.put(TableColums.PHONE, user.phone);
        sqLiteDatabase.insert(tableName,null,contentValues);
    }
}
