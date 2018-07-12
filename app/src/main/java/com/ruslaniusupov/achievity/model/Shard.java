package com.ruslaniusupov.achievity.model;

public class Shard {

    public static final String FIELD_COUNT = "count";

    private int count;

    public Shard() {}

    public Shard(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increment() {
        count++;
    }

}
