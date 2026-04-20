package com.daniel99j.dungeongame.util;

import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.sounds.SoundManager;
import com.daniel99j.dungeongame.ui.YouDiedScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GlobalRunnables {
    public static final Map<String, Runnable> nameToCode = new HashMap<>();
    public static final Map<Runnable, String> codeToName = new HashMap<>();

    public static final Runnable ADD_FROST_EMBER = register("add_frost_ember", () -> {});
    public static final Runnable ADD_TREASURE = register("add_treasure", () -> {
        SoundManager.getSound("coin").play(1);
    });
    public static final Runnable FAIL_RUN = register("fail_run", () -> {
        assert GameConstants.level != null;
        GameConstants.level.dispose();
        GameConstants.level = null;
        GameConstants.MAIN_INSTANCE.setScreen(new YouDiedScreen());
    });

    private static Runnable register(String name, Runnable code) {
        nameToCode.put(name, code);
        codeToName.put(code, name);
        return code;
    }
}
