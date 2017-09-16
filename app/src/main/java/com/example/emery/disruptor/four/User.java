package com.example.emery.disruptor.four;

/**
 * Created by emery on 2017/5/7.
 */

public class User  {
    public int id;
    public String name;
    public String age;
    public String address;
    public String phone;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

}
