package com.ruslaniusupov.achievity.model;

public class Shard {

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
