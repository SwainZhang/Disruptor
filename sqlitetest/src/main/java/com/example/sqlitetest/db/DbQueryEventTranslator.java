package com.example.sqlitetest.db;


import com.lmax.disruptor.EventTranslator;

/**
 * Created by emery on 2017/5/7.
 */

public class DbQueryEventTranslator implements EventTranslator<QueryEvent> {
     QueryEvent mEvent;

    public DbQueryEventTranslator(QueryEvent event){
     this.mEvent=event;
    }
    @Override
    public void translateTo(QueryEvent queryEvent, long l) {

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
        queryEvent.sql=mEvent.sql;

    }
}
