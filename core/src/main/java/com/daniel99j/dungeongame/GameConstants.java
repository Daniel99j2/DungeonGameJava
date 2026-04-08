package com.daniel99j.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.daniel99j.djutil.pathfinder.PathfindDebugType;
import com.daniel99j.djutil.pathfinder.PathfinderOptions;
import com.daniel99j.dungeongame.entity.living.Player;
import com.daniel99j.dungeongame.util.PathUtil;
import com.daniel99j.dungeongame.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameConstants {
    public static @Nullable Level level;
    public static final float TICKS_PER_SECOND = 200;
    public static final float SECONDS_PER_TICK = 1.0f/TICKS_PER_SECOND;
    public static final SpriteBatch spriteBatch = new SpriteBatch();
    public static final OrthographicCamera camera = new OrthographicCamera();
    public static final FitViewport viewport = new FitViewport(16, 9, camera);
    public static final TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(PathUtil.relativize("gen/atlases/main.atlas")));
    public static @Nullable Player player;
    public static final boolean DEBUGGING = Objects.equals(System.getenv("DEBUGGING_GAME"), "1");
    public static final ShapeRenderer shapeRenderer = new ShapeRenderer();
    public static long TIME = 0L;
    public static final int DATA_VERSION = 1;
    public static final PathfinderOptions DEFAULT_PATHFINDING = PathfinderOptions.builder().diagonalNeighbourProvider().walkablePredicate((pos) -> {
        AtomicBoolean hit = new AtomicBoolean(false);
        QueryCallback callback = fixture -> {
            hit.set(true);
            return true;
        };
        GameConstants.level.getBox2dWorld().QueryAABB(callback, pos.getX(), pos.getY(), pos.getX()+0.999f, pos.getY()+0.999f);
        return hit.get();
    }).debugRenderConsumer(DEBUGGING ? (pathfindDebugPos -> {
        if(pathfindDebugPos.type().equals(PathfindDebugType.SUCCESSFUL_PATH)) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.line(pathfindDebugPos.pos().getX()+0.5f, pathfindDebugPos.pos().getX()+0.5f, pathfindDebugPos.previous().getX()+0.5f, pathfindDebugPos.previous().getX()+0.5f);
            shapeRenderer.rect(pathfindDebugPos.pos().getX() + 0.3f, pathfindDebugPos.pos().getY() + 0.3f, 0.4f, 0.4f);
        } else if(pathfindDebugPos.type().equals(PathfindDebugType.OPEN_SET)) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(pathfindDebugPos.pos().getX() + 0.3f, pathfindDebugPos.pos().getY() + 0.3f, 0.4f, 0.4f);
        } else if(pathfindDebugPos.type().equals(PathfindDebugType.CLOSED_SET)) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(pathfindDebugPos.pos().getX() + 0.3f, pathfindDebugPos.pos().getY() + 0.3f, 0.4f, 0.4f);
        }
        shapeRenderer.end();
    }) : null).build();

    public static Level getLevelOrThrow() {
        if(level != null) return level;
        throw new IllegalStateException("World is null");
    }

    protected static void init() {

    }
}
