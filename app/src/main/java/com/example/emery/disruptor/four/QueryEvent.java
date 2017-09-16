package com.example.emery.disruptor.four;

/**
 * Created by emery on 2017/5/7.
 */

public class QueryEvent {
    public String table;
    public String[] colums;
    public String selection;
    public String[] selectionArgs;
    public String groupBy;
    public String having;
    public String orderBy;
    public int limit;
    public int offset;
    public int index;
}
