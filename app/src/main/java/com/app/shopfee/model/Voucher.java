package com.app.shopfee.model;

import com.app.shopfee.utils.Constant;

import java.io.Serializable;

public class Voucher implements Serializable {

    private int id;
    private int discount;
    private int minimum;
    private boolean isSelected;

    public Voucher() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTitle() {
        return "Giảm giá " + discount + "%";
    }

    public String getMinimumText() {
        if (minimum > 0) {
            return "Áp dụng cho đơn hàng tối thiểu " + minimum + Constant.CURRENCY;
        }
        return "Áp dụng cho mọi đơn hàng";
    }

    public String getCondition(int amount) {
        if (minimum <= 0) return "";
        int condition = minimum - amount;
        if (condition > 0) {
            return "Hãy mua thêm " + condition + Constant.CURRENCY + " để nhận được khuyến mại này";
        }
        return "";
    }

    public boolean isVoucherEnable(int amount) {
        if (minimum <= 0) return true;
        int condition = minimum - amount;
        return condition <= 0;
    }

    public int getPriceDiscount(int amount) {
        return (amount * discount) / 100;
    }
}