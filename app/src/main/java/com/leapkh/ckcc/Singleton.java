package com.leapkh.ckcc;

public class Singleton {

    private static Singleton INSTANCE;

    private Event[] events;


    private Singleton() {

    }

    public static Singleton getInstance() {
        if(INSTANCE == null){
            INSTANCE = new Singleton();
        }
        return INSTANCE;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }

    public Event[] getEvents() {
        return events;
    }
}
