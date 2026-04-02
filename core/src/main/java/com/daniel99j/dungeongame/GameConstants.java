package com.daniel99j.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.daniel99j.dungeongame.entity.Player;
import com.daniel99j.dungeongame.util.PathUtil;
import com.daniel99j.dungeongame.world.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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

    public static Level getLevelOrThrow() {
        if(level != null) return level;
        throw new IllegalStateException("World is null");
    }

    protected static void init() {

    }
}
