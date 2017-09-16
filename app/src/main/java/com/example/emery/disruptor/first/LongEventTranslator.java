package com.example.emery.disruptor.first;

import com.lmax.disruptor.EventTranslatorOneArg;

import static android.R.attr.data;

/**
 * Created by emery on 2017/5/7.
 */

public class LongEventTranslator implements EventTranslatorOneArg<LongEvent,Long> {

    @Override
    public void translateTo(LongEvent longEvent, long l, Long aLong) {
        longEvent.set(data);
    }
}
