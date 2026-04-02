package com.daniel99j.dungeongame.util;

public class PathUtil {
    public static String relativize(String old) {
        return "core/src/main/"+old;
    }

    public static String asset(String old) {
        return relativize("resources/assets/"+old);
    }

    public static String data(String old) {
        return relativize("resources/data/"+old);
    }
}
