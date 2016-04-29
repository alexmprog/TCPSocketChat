package com.renovavision.tcpsocketchat.utils;

import android.support.annotation.NonNull;

public class BusManager {

    private static volatile BusManager sInstance;

    public static BusManager getInstance() {
        if (sInstance == null) {
            synchronized (BusManager.class) {
                if (sInstance == null) {
                    sInstance = new BusManager();
                }
            }
        }
        return sInstance;
    }

    private final MainThreadBus mainThreadBus;

    private BusManager() {
        this.mainThreadBus = new MainThreadBus();
    }

    public void registerMainThreadListener(@NonNull Object object) {
        mainThreadBus.register(object);
    }

    public void unregisterMainThreadListener(@NonNull Object object) {
        mainThreadBus.unregister(object);
    }

    public void postInMainThread(@NonNull Object event) {
        mainThreadBus.post(event);
    }
}
