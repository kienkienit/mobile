package com.app.shopfee.event;

import com.app.shopfee.model.Address;

public class AddressSelectedEvent {

    private Address address;

    public AddressSelectedEvent(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
