package com.batch.itemreader;

import org.springframework.batch.item.ItemReader;

import java.util.Iterator;
import java.util.List;

public class StateLessItemReader implements ItemReader {

    private final Iterator<String> data;

    /*Constructor based injection, data will inject when you create bean*/
    public StateLessItemReader(List<String> data) {
        this.data = data.iterator();
    }

    @Override
    public Object read() {
        if (this.data.hasNext()) {
            String value = this.data.next();
            System.out.println("Reading data from list " + value);
            return value;
        } else {
            return null;
        }
    }
}
