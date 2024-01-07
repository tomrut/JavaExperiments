package com.tomrut.streams.lookup;

import java.util.List;

public class ParentItem {

    private final String key;
    private final List<ChildItem> childItemList;

    public String getKey() {
        return key;
    }

    public ParentItem(String key, List<ChildItem> childItemList) {
        this.key = key;
        this.childItemList = childItemList;
    }

    public List<ChildItem> getChildItemList() {
        return childItemList;
    }

}
