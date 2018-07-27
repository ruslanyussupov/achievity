package com.ruslaniusupov.achievity.data.models;

public class Counter {

    public static final String DOC_LIKE_COUNTER = "likeCounter";
    public static final String DOC_UNLIKE_COUNTER = "unlikeCounter";
    public static final String FIELD_NUM_SHARDS = "numShards";

    private int numShards;

    public Counter(int numShards) {
        this.numShards = numShards;
    }

    public int getNumShards() {
        return numShards;
    }

    public void setNumShards(int numShards) {
        this.numShards = numShards;
    }

}
