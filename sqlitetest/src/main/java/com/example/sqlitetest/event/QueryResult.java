package com.example.sqlitetest.event;


import com.example.sqlitetest.db.User;

import java.util.ArrayList;

/**
 * Created by emery on 2017/5/8.
 */

public class QueryResult {
    public ArrayList<User> mUsers;

    public ArrayList<User> getUsers() {
        return mUsers;
    }

    public QueryResult(ArrayList<User> users) {
        mUsers = users;
    }

    public void setUsers(ArrayList<User> users) {
        mUsers = users;
    }
}
