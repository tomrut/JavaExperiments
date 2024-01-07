package com.tomrut.streams.lookup;

public class ChildItem {

    private final String childKey;

    private final int value;

    public ChildItem(String childKey, int value) {
        this.childKey = childKey;
        this.value = value;
    }

    public String getChildKey() {
        return childKey;
    }

    public int getValue() {
        return value;
    }

}
