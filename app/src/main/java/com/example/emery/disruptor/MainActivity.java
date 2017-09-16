package com.example.emery.disruptor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.emery.disruptor.first.FirstActivity;
import com.example.emery.disruptor.four.FourActivity;
import com.example.emery.disruptor.second.SecondActivity;
import com.example.emery.disruptor.three.ThreeActivity;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void toFirst(View v){
        startActivity(new Intent(MainActivity.this, FirstActivity.class));
    }
    public void toThree(View view){
        startActivity(new Intent(MainActivity.this,ThreeActivity.class));
    }
    public void toSecond(View view){
        startActivity(new Intent(MainActivity.this,SecondActivity.class));
    }
    public void toFour(View view){
        startActivity(new Intent(MainActivity.this, FourActivity.class));
    }
}
