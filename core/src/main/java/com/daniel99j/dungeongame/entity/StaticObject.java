package com.daniel99j.dungeongame.entity;

import com.daniel99j.dungeongame.level.Level;

public abstract class StaticObject extends AbstractObject {
    @Override
    public void onAdd(boolean fromLoad) {
        super.onAdd(fromLoad);
        this.getLevel().getStaticObjects().add(this);
    }
}
