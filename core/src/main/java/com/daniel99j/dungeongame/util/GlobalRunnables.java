package com.daniel99j.dungeongame.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GlobalRunnables {
    public static final Map<String, Runnable> nameToCode = new HashMap<>();
    public static final Map<Runnable, String> codeToName = new HashMap<>();

    public static final Runnable ADD_FROST_EMBER = register("add_frost_ember", () -> {});
    public static final Runnable ADD_TREASURE = register("add_treasure", () -> {});

    private static Runnable register(String name, Runnable code) {
        nameToCode.put(name, code);
        codeToName.put(code, name);
        return code;
    }
}
