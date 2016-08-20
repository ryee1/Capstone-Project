package com.project.richard.insightjournal.events;

import android.location.Address;

/**
 * Created by richard on 8/16/16.
 */
public class OnAddressFetchedEvent {
    public Address address;

    public OnAddressFetchedEvent() {
    }

    public OnAddressFetchedEvent(Address address) {
        this.address = address;
    }
}
