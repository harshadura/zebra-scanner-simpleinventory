package com.zebra.scanner.inventory.bus;

import android.support.annotation.NonNull;

/**
 * Created by harshas on 3/20/17.
 */
public class BusEvent {

    public static final String EventCommunicationSessionEstablished = "EventCommunicationSessionEstablished";
    public static final String EventBarcode = "EventBarcode";

    private String eventName;
    private Object payload;

    public BusEvent(@NonNull String eventName, Object payload) {
        this.eventName = eventName;
        this.payload = payload;
    }

    public BusEvent(String eventName){
        this.eventName = eventName;
        this.payload = null;
    }

    public String getEventName() {
        return eventName;
    }

    public Object getPayload() {
        return payload;
    }


}
