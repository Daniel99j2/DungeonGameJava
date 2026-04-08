package com.daniel99j.dungeongame.entity.living;

import box2dLight.PointLight;
import com.badlogic.gdx.math.Vector2;
import com.daniel99j.djutil.Either;
import com.daniel99j.djutil.pathfinder.CachedPathfinder;
import com.daniel99j.djutil.pathfinder.PathfindPos;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.AdvancedObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PathfindingObject extends AdvancedObject {
    private final CachedPathfinder pathfinder = new CachedPathfinder(GameConstants.DEFAULT_PATHFINDING, 1);
    private PathfindPos oldPos = null;
    private PointLight light;

    @Override
    public void tick() {
        super.tick();
        runPathfinding(false);
    }

    private void runPathfinding(boolean invalid) {
        Vector2 target = getTarget();
        if(target != null) {
            PathfindPos cachedTarget = new PathfindPos((int) target.x, (int) target.y);
            PathfindPos pos = new PathfindPos((int) this.getPos().x, (int) this.getPos().y);
            PathfindPos nextPos = null;
            List<PathfindPos> nodes = pathfinder.findPath(oldPos, cachedTarget, pos);
            if(nodes.size() < 2) return;

            if(pathfinder.wasLastInvalid() || invalid) {
                oldPos = pos;
                nextPos = nodes.get(2);
            } else {
                int i = 0;
                for (PathfindPos node : nodes) {
                    if(node.equals(pos)) {
                        if(i > nodes.size()-1) return;
                        nextPos = nodes.get(i+1);
                        break;
                    }
                    i++;
                }

                if(nextPos == null) {
                    runPathfinding(true);
                    return;
                }
            }


        }
    }

    public abstract @Nullable Vector2 getTarget();

    public int getSpeed() {
        return 100;
    }
}
