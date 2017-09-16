package com.example.sqlitetest.db;

import java.util.Arrays;

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
    public String sql;

    @Override
    public String toString() {
        return "QueryEvent{" +
                "table='" + table + '\'' +
                ", colums=" + Arrays.toString(colums) +
                ", selection='" + selection + '\'' +
                ", selectionArgs=" + Arrays.toString(selectionArgs) +
                ", groupBy='" + groupBy + '\'' +
                ", having='" + having + '\'' +
                ", orderBy='" + orderBy + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                ", index=" + index +
                ", sql='" + sql + '\'' +
                '}';
    }
}
