package com.app.shopfee.event;

import com.app.shopfee.model.Voucher;

public class VoucherSelectedEvent {

    private Voucher voucher;

    public VoucherSelectedEvent(Voucher voucher) {
        this.voucher = voucher;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}
