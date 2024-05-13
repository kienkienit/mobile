package com.app.shopfee.model;

import java.io.Serializable;

public class Rating implements Serializable {

    private String review;
    private double rate;

    public Rating() {}

    public Rating(String review, double rate) {
        this.review = review;
        this.rate = rate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
