package com.app.shopfee.model;

public class TabOrder {

    public static final int TAB_ORDER_PROCESS = 1;
    public static final int TAB_ORDER_DONE = 2;

    private int type;
    private String name;

    public TabOrder(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
