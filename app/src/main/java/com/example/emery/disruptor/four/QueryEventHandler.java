package com.example.emery.disruptor.four;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

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
        batchQuery(queryEvent,100);
/*
        long start = System.currentTimeMillis();
        try {
       *//* Cursor query = mDatabase.query(queryEvent.table, queryEvent.colums, queryEvent.selection,
                queryEvent.selectionArgs, queryEvent.groupBy, queryEvent.having, queryEvent
                        .orderBy,queryEvent.limit);
//       *//*
            // mDatabase.beginTransactionNonExclusive();
            String sql = "select * from " + queryEvent.table + " limit " +
                    queryEvent.limit + " offset " + queryEvent.offset + ";";
            System.out.println("sql-->" + sql);
           // mQuery = mDatabase.rawQuery(sql, null);
            mQuery=mDatabase.rawQuery("select * from "+UserDao.mTableName,null);
            System.out.println("-------))))");
            ArrayList<User> arrayList = new ArrayList<>();
            int count = mQuery.getCount();
            System.out.println("共有多少条-->"+count);
            while (mQuery.moveToNext()) {
                User user = new User();
                user.id = mQuery.getInt(mQuery.getColumnIndex("_id"));
                user.name = mQuery.getString(mQuery.getColumnIndex(TableColums.NAME));
                user.age = mQuery.getString(mQuery.getColumnIndex(TableColums.AGE));
                user.phone = mQuery.getString(mQuery.getColumnIndex(TableColums.PHONE));
                user.address = mQuery.getString(mQuery.getColumnIndex(TableColums.ADDRESS));
                //arrayList.add(user);
                // long insert = UserDao.getInstance().insert(user);
              // System.out.println("查询QueryEventHandler:" + Thread.currentThread().getName() + "---"+ user.toString());

            }
           // EventBus.getDefault().post(new QueryResult(arrayList));
        }finally {

            mQuery.close();
        }

        long end = System.currentTimeMillis();
         long diff=end-start;
        Log.i("TAG",end+"----index="+queryEvent.index+"--线程id=" + Thread.currentThread().getId()+"--查询耗时：" + (end - start));
     //  mDatabase.setTransactionSuccessful();
     //   mDatabase.endTransaction();*/
    }

    public void batchQuery(QueryEvent  queryEvent,int dividPoint){
        int queryCount=queryEvent.limit;
        int queryOffset=queryEvent.offset;

            if(queryCount>dividPoint) {
                for (int i = 0; i <= queryCount / dividPoint; i++) {
                    queryEvent.limit = dividPoint;
                    queryEvent.index = i;
                    queryEvent.offset = queryOffset + i * queryEvent.limit;
                    UserDao.getInstance().query(queryEvent);
                }
            }else{
                long start=System.currentTimeMillis();
                String sql = "select * from " + queryEvent.table + " limit " +
                        queryEvent.limit + " offset " + queryEvent.offset + ";";
                System.out.println("sql-->" + sql);
                 mQuery = mDatabase.rawQuery(sql, null);
                Log.i("TAG",queryEvent.index+"--本次查询数--->"+mQuery.getCount()+"<---本次查询总数");
                while (mQuery.moveToNext()){
                    User user = new User();
                    user.id = mQuery.getInt(mQuery.getColumnIndex("_id"));
                    user.name = mQuery.getString(mQuery.getColumnIndex(TableColums.NAME));
                    user.age = mQuery.getString(mQuery.getColumnIndex(TableColums.AGE));
                    user.phone = mQuery.getString(mQuery.getColumnIndex(TableColums.PHONE));
                    user.address = mQuery.getString(mQuery.getColumnIndex(TableColums.ADDRESS));
                }
                long end=System.currentTimeMillis();
                Log.i("TAG",end+"----index="+queryEvent.index+"--线程id=" + Thread.currentThread().getId()+"--查询耗时：" + (end - start));

            }



    }
}
