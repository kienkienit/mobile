package com.app.shopfee.event;

import com.app.shopfee.model.PaymentMethod;

public class PaymentMethodSelectedEvent {

    private PaymentMethod paymentMethod;

    public PaymentMethodSelectedEvent(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
