package com.app.shopfee.event;

public class AddressDeletedEvent {
    private final long addressId;

    public AddressDeletedEvent(long addressId) {
        this.addressId = addressId;
    }

    public long getAddressId() {
        return addressId;
    }
}
