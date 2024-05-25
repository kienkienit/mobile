package com.app.shopfee.event;

import com.app.shopfee.model.Address;

public class AddressUpdatedEvent {
    private final Address address;

    public AddressUpdatedEvent(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }
}
