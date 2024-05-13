package com.app.shopfee.model;

import java.io.Serializable;

public class RatingReview implements Serializable {

    public static final int TYPE_RATING_REVIEW_DRINK = 1;
    public static final int TYPE_RATING_REVIEW_ORDER = 2;

    private int type;
    private String id;

    public RatingReview(int type, String id) {
        this.type = type;
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
