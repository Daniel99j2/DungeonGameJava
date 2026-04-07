package com.daniel99j.dungeongame.world;

import box2dLight.Light;

import java.util.UUID;

public record LevelLight<T extends Light>(T light, SaveConfig saveConfig, UUID uuid) {
}
