package util;

import java.util.Arrays;

public class Item {
    private boolean meta;
    private int order;
    //1: medium block
    //2: blob
    //3: tiny
    private int type;
    private int[] counters;

    private String key;
    private String value;

    public Item(){}

    public Item(int order, int type, String key, String value) {
        this.order = order;
        this.type = type;
        this.key = key;
        this.value = value;
        this.meta = false;
    }

    public Item(String key, int[] counters) {
        this.counters = counters;
        this.key = key;
        this.meta = true;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMeta(boolean meta) {
        this.meta = meta;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCounters(int[] counters) {
        this.counters = counters;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isMeta(){
        return meta;
    }

    public int getOrder() {
        return order;
    }

    public int[] getCounters() {
        return counters;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public String toStringMeta() {
        return "Item{" +
                "meta=" + meta +
                ", counters=" + Arrays.toString(counters) +
                ", key='" + key + '\'' +
                '}';
    }

    public String toStringBlock() {
        return "Item{" +
                "order=" + order +
                ", type=" + type +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
