package com.daniel99j.dungeongame.sounds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    protected static final Map<String, SoundFile> loadedFiles = new HashMap<>();

    public static SoundFile getSound(String name) {
        if(loadedFiles.containsKey(name)) return loadedFiles.get(name);
        SoundFile newFile = new SoundFile(name);
        loadedFiles.put(name, newFile);
        return newFile;
    }

    public static void tick(float deltaTime) {
        ArrayList<SoundFile> files = new ArrayList<>(loadedFiles.values());
        for (SoundFile f : files) {
            f.tick(deltaTime);
        }
    }

    public static ArrayList<SoundInstance> getActiveSounds() {
        ArrayList<SoundInstance> out = new ArrayList<>();
        loadedFiles.forEach((name, v) -> {
            out.addAll(v.getInstances());
        });

        return out;
    }
}
