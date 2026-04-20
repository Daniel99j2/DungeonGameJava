package com.daniel99j.dungeongame.entity.living;

import box2dLight.PointLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.daniel99j.djutil.pathfinder.CachedPathfinder;
import com.daniel99j.djutil.pathfinder.PathfindPos;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.AdvancedObject;
import com.daniel99j.dungeongame.entity.CollisionCategories;
import com.daniel99j.dungeongame.ui.Debuggers;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public abstract class PathfindingObject extends AdvancedObject {
    private CachedPathfinder pathfinder;
    private PathfindPos oldPos = null;
    private PointLight light;

    @Override
    public void onAdd(boolean fromLoad) {
        super.onAdd(fromLoad);
        pathfinder = new CachedPathfinder(GameConstants.createPathfinding(this).walkablePredicate(this.createWalkPredicate()).build(), 1);
    }

    @Override
    public void tick() {
        super.tick();
        runPathfinding(false);
    }

    private void runPathfinding(boolean invalid) {
        if(GameConstants.DEBUGGING && Debuggers.isEnabled("disablePathfinding")) return;
        if(Debuggers.isEnabled("pathfindingRender")) {
            if(Debuggers.pathfindDebuggerTimers.getOrDefault(String.valueOf(this.hashCode()), 99999) < 40) Debuggers.pathfindDebuggerTimers.put(String.valueOf(this.hashCode()), 40);
        }
        Vector2 target = getTarget();
        if(target != null) {
            PathfindPos cachedTarget = toPathfindPos(target);
            PathfindPos pos = toPathfindPos(this.getPos());
            PathfindPos nextPos = null;
            List<PathfindPos> nodes = pathfinder.findPath(oldPos == null ? pos : oldPos, cachedTarget, pos);
            if(nodes.size() <= 2) return; //2 so that if the path is just start to end, dont do it
            //its a hack fix to stop it pathfinding to invalid places

            if(pathfinder.wasLastInvalid() || invalid) {
                oldPos = pos;
                nextPos = nodes.get(1);
            } else {
                int i = 0;
                for (PathfindPos node : nodes) {
                    if(node.equals(pos)) {
                        if(i >= nodes.size()-1) return;
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

            float speed = Math.max(this.getSpeed()-this.getVelocity().len(), 0);

            this.moveTowardTarget(new Vector2(nextPos.getX() + 0.5f, nextPos.getY() + 0.5f), speed);
        }
    }

    private static PathfindPos toPathfindPos(Vector2 pos) {
        return new PathfindPos((int) Math.floor(pos.x), (int) Math.floor(pos.y));
    }

    protected Predicate<PathfindPos> createWalkPredicate() {
        Vector4 hitbox = this.getHitbox(this.getPhysics().getFixtureList().first()).sub(this.getPos().x, this.getPos().y, 0, 0);
        Vector2 size = new Vector2(Math.abs(hitbox.x)+Math.abs(hitbox.z), Math.abs(hitbox.y)+Math.abs(hitbox.w));
        float distance = size.len()+0.1f;
        return (pos) -> {
            AtomicBoolean hit = new AtomicBoolean(false);
            QueryCallback callback = fixture -> {
                if (blocksPathfinding(fixture)) {
                    hit.set(true);
                    return false;
                }
                return true;
            };
            GameConstants.level.getBox2dWorld().QueryAABB(callback, pos.getX()+hitbox.x, pos.getY()+hitbox.y, pos.getX()+hitbox.z, pos.getY()+hitbox.w);
            return !hit.get();
        };
    }

    private static boolean blocksPathfinding(Fixture fixture) {
        short categoryBits = fixture.getFilterData().categoryBits;
        return (categoryBits & CollisionCategories.WALL) != 0
            || (categoryBits & CollisionCategories.PATHFIND_BLOCKING) != 0;
    }

    public abstract @Nullable Vector2 getTarget();

    public float getSpeed() {
        return 1;
    }
}
