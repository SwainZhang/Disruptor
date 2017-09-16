package com.example.emery.disruptor.three;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.emery.disruptor.R;
import com.example.emery.disruptor.first.WorkerThreadFactory;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by emery on 2017/5/7.
 * <p>
 * 插入10000条350ms
 */

public class ThreeActivity extends AppCompatActivity {

    private Producer mProducer;
    private RecyclerView mRecyclerView;
    private ArrayList<User> mUsers;
    private UserAdapter mUserAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        mRecyclerView = (RecyclerView) findViewById(R.id.rl_user);
        mUsers = new ArrayList<>();
        mUserAdapter = new UserAdapter(mUsers,this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mUserAdapter);
    }

    public void insert(View view) {
        long start = System.currentTimeMillis();
        UserDao.getInstance().insert();
        System.out.println("插入耗时:" + (System.currentTimeMillis() - start));
    }

    public void query(View view) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                        .availableProcessors());
                final Disruptor<QueryEvent> queryEventDisruptor = new Disruptor<QueryEvent>(
                        new QueryEventFactory(), 1024*1024,
                        new WorkerThreadFactory("three"),
                        ProducerType.MULTI,
                        new YieldingWaitStrategy());
      /* QueryEventHandler[] handlers=new QueryEventHandler[Runtime.getRuntime()
      .availableProcessors()];
        for (int i = 0; i < 1; i++) {
            handlers[i]=new QueryEventHandler();
        }
        EventHandlerGroup<QueryEvent> queryEventEventHandlerGroup = queryEventDisruptor
                .handleEventsWith(handlers);*/
                EventHandlerGroup<QueryEvent> queryEventEventHandlerGroup = queryEventDisruptor
                        .handleEventsWithWorkerPool(
                        new QueryEventHandler(),
                        new QueryEventHandler(),
                        new QueryEventHandler(),
                        new QueryEventHandler(),
                        new QueryEventHandler(),
                        new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler(),
                                new QueryEventHandler());
                queryEventEventHandlerGroup.then(new EndMessageHandler());
                CountDownLatch countDownLatch = new CountDownLatch(1);
                queryEventDisruptor.start();

                executorService.execute(new Producer(countDownLatch, queryEventDisruptor));
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                queryEventDisruptor.shutdown();
                executorService.shutdown();
                Log.i("TAG", "查询耗时" + (System.currentTimeMillis() - start));

            }
        }).start();



    }

    public void delete(View view) {
        UserDao.getInstance().deleteBak();
    }

}
