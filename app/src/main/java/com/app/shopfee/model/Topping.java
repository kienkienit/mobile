package com.app.shopfee.model;

public class Topping {

    public static final String VARIANT_ICE = "variant_ice";
    public static final String VARIANT_HOT = "variant_hot";
    public static final String SIZE_REGULAR = "size_regular";
    public static final String SIZE_MEDIUM = "size_medium";
    public static final String SIZE_LARGE = "size_large";
    public static final String SUGAR_NORMAL = "sugar_normal";
    public static final String SUGAR_LESS = "sugar_less";
    public static final String ICE_NORMAL = "ice_normal";
    public static final String ICE_LESS = "ice_less";

    private int id;
    private String name;
    private int price;
    private boolean isSelected;

    public Topping() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
