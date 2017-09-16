package com.example.emery.disruptor.three;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

import java.util.ArrayList;

/**
 * Created by emery on 2017/5/7.
 */

public class QueryEventHandler implements EventHandler<QueryEvent>, WorkHandler<QueryEvent> {
    private SQLiteDatabase mDatabase;
    private String mTableName;

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
        ArrayList<User> arrayList=new ArrayList<>();
        long start = System.currentTimeMillis();
       /* Cursor query = mDatabase.query(queryEvent.table, queryEvent.colums, queryEvent.selection,
                queryEvent.selectionArgs, queryEvent.groupBy, queryEvent.having, queryEvent
                        .orderBy,queryEvent.limit);
//       */
      // mDatabase.beginTransactionNonExclusive();
        String sql="select * from " + queryEvent.table + " limit " +
               queryEvent.limit +" offset "+queryEvent.offset +";";
        System.out.println("sql-->"+sql);
        Cursor query = mDatabase.rawQuery(sql, null);
        while (query.moveToNext()) {
            User user = new User();
            user.name = query.getString(query.getColumnIndex(TableColums.NAME));
            user.age = query.getString(query.getColumnIndex(TableColums.AGE));
            user.phone = query.getString(query.getColumnIndex(TableColums.PHONE));
            user.address = query.getString(query.getColumnIndex(TableColums.ADDRESS));

           // long insert = UserDao.getInstance().insert(user);
            //System.out.println("查询QueryEventHandler:" + Thread.currentThread().getName() + "---"+ user.toString());
        }
        query.close();
        long end = System.currentTimeMillis();
         long diff=end-start;
        Log.i("TAG",end+"----index="+queryEvent.index+"--线程id=" + Thread.currentThread().getId()+"--查询耗时：" + (end - start));
     //  mDatabase.setTransactionSuccessful();
     //   mDatabase.endTransaction();
    }
}
