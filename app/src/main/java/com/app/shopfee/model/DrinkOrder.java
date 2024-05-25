package com.app.shopfee.model;

import java.io.Serializable;

public class DrinkOrder implements Serializable {

    private String name;
    private String option;
    private int count;
    private int price;
    private String image;

    public DrinkOrder() {}

    public DrinkOrder(String name, String option, int count, int price, String image) {
        this.name = name;
        this.option = option;
        this.count = count;
        this.price = price;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
