package com.example.emery.disruptor.event;

import com.example.emery.disruptor.four.User;

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
