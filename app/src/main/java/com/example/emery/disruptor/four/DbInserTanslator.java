package com.example.emery.disruptor.four;

import com.lmax.disruptor.EventTranslator;

/**
 * Created by emery on 2017/5/9.
 */

public class DbInserTanslator implements EventTranslator<InsertEvent> {
    private InsertEvent mEvent;

    public DbInserTanslator(InsertEvent event){

        mEvent = event;
    }
    @Override
    public void translateTo(InsertEvent insertEvent, long l) {
        insertEvent.tableName=mEvent.tableName;
        insertEvent.nullColumnHack=mEvent.nullColumnHack;
        insertEvent.mContentValues=mEvent.mContentValues;
    }
}
