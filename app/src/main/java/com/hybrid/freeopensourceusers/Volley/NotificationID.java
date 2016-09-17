package com.hybrid.freeopensourceusers.Volley;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by adarsh on 16/9/16.
 */

public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);


    public NotificationID(){

    }
    public static int getID() {
        return c.incrementAndGet();
    }
}
