package com.daniel99j.dungeongame.mechanic;

import com.badlogic.gdx.graphics.Color;
import com.daniel99j.djutil.NumberUtils;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.StaticObject;
import com.daniel99j.dungeongame.entity.TreasureObject;
import com.daniel99j.dungeongame.entity.TreasureSpawnerObject;
import com.daniel99j.dungeongame.util.GlobalRunnables;

public class RunManager {
    private int treasureToGenerate = 0;
    private int treasureTimer = 0;

    public void setTreasureToGenerate(int treasureToGenerate) {
        this.treasureToGenerate = treasureToGenerate;
    }

    public void tick() {
        if(treasureToGenerate > 0) {
            treasureTimer++;
            if(treasureTimer == 20) {
                treasureTimer = 0;
                treasureToGenerate--;
                for (StaticObject staticObject : GameConstants.level.getStaticObjects()) {
                    if(staticObject instanceof TreasureSpawnerObject treasureSpawner && treasureSpawner.getSpawnType().equals("treasure")) {
                        treasureSpawner.fire();
                    }
                }
//                int i = NumberUtils.getRandomInt(0, GameConstants.level.getTreasurePositions().size());
//                TreasureObject t = new TreasureObject(GlobalRunnables.COLLECT_TREASURE, "coin", Color.valueOf("#fcb603"));
//                GameConstants.level.addObject(t);
//                t.setPos(GameConstants.level.getTreasurePositions().get(i));
            }
        }
    }
}
