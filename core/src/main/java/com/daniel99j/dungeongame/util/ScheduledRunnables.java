package com.daniel99j.dungeongame.util;

import java.util.ArrayList;

public class ScheduledRunnables {
    public static ArrayList<Runnable> runnables = new ArrayList<>();

    public static void add(Runnable runnable) {
        runnables.add(runnable);
    }
}
