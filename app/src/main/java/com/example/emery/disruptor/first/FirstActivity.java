package com.example.emery.disruptor.first;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.emery.disruptor.R;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Created by emery on 2017/5/7.
 */

public class FirstActivity extends AppCompatActivity {

    private Disruptor<LongEvent> mDisruptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frist);
    }
    public void close(View view){
        mDisruptor.shutdown();
    }

    public void publish(View view){
        RingBuffer<LongEvent> ringBuffer = mDisruptor.getRingBuffer();
        long sequence = ringBuffer.next();//请求下一个事件序号；

        try {
            LongEvent event = ringBuffer.get(sequence);//获取该序号对应的事件对象；
            long data = getEventData();//获取要通过事件传递的业务数据；
            event.set(data);
        } finally{
            ringBuffer.publish(sequence);//发布事件；
        }
    }
    public void start(View view){
        EventFactory<LongEvent> eventFactory = new LongEventFactory();
        int ringBufferSize = 1024 * 1024; // RingBuffer 大小，必须是 2 的 N 次方；
        mDisruptor = new Disruptor<LongEvent>(eventFactory,
                ringBufferSize,new WorkerThreadFactory("test") , ProducerType.MULTI,
                new YieldingWaitStrategy());

        EventHandler<LongEvent> eventHandler = new LongEventHandler();
        mDisruptor.handleEventsWith(eventHandler);

        mDisruptor.start();

    }




    public  LongEventTranslator TRANSLATOR = new LongEventTranslator();

    public  void publishEvent2(Disruptor<LongEvent> disruptor) {
        // 发布事件；
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        long data = getEventData();//获取要通过事件传递的业务数据；
        ringBuffer.publishEvent(TRANSLATOR, data);
    }

    public static long getEventData(){
        return 100000L;
    }

}
