package com.daniel99j.dungeongame.entity;

import com.daniel99j.dungeongame.world.Level;

public abstract class StaticObject extends AbstractObject {
    @Override
    public void init(Level level) {
        super.init(level);
        level.getStaticObjects().add(this);
    }
}
