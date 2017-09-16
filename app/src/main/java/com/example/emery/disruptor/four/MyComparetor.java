package com.example.emery.disruptor.four;

import java.util.Comparator;

/**
 * Created by emery on 2017/5/8.
 */

public class MyComparetor implements Comparator<User> {



    @Override
    public int compare(User o1, User o2) {

        return o1.getId()-o2.getId();
    }
}
