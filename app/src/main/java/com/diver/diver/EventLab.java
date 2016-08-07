package com.diver.diver;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos on 29/01/2016.
 */
public class EventLab {
    private static List<Event> Events = new ArrayList<Event>();

    public EventLab() {
    }

    public static void setEvents(List<Event> events) {
        Events=events;
    }

    public static Event getEventById(int id) {

        for (Event e : Events) {
            if (e.getEventID() == id) {
                return e;
            }
        }
        return null;
    }

    public static void deleteEvents(){
        Events = new ArrayList<Event>();
    }
    public static void updateEvents(Context context) {

    }

    public static void addEvent(Event event){
        Events.add(event);
    }

    public static List<Event> getEvents() {
        return Events;
    }
}

