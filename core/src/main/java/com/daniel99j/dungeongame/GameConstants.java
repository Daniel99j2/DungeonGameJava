package com.daniel99j.dungeongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.daniel99j.djutil.pathfinder.PathfindDebugPos;
import com.daniel99j.djutil.pathfinder.PathfindDebugType;
import com.daniel99j.djutil.pathfinder.PathfinderOptions;
import com.daniel99j.dungeongame.entity.AbstractObject;
import com.daniel99j.dungeongame.entity.living.Player;
import com.daniel99j.dungeongame.ui.Debuggers;
import com.daniel99j.dungeongame.util.PathUtil;
import com.daniel99j.dungeongame.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class GameConstants {
    public static @Nullable Level level;
    public static final int TICKS_PER_SECOND = 40;
    public static final float SECONDS_PER_TICK = 1.0f/TICKS_PER_SECOND;
    public static final float SECONDS_PER_PHYSICS_TICK = 1.0f/TICKS_PER_SECOND/10.0f;
    public static final SpriteBatch spriteBatch = new SpriteBatch();
    public static final OrthographicCamera camera = new OrthographicCamera();
    public static Viewport viewport = new FitViewport(16, 9, camera);
    public static final TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(PathUtil.relativize("gen/atlases/main.atlas")));
    public static @Nullable Player player;
    public static final boolean DEBUGGING = Objects.equals(System.getenv("DEBUGGING_GAME"), "1");
    public static final ShapeRenderer shapeRenderer = new ShapeRenderer();
    public static float TIME = 0L;
    public static final int DATA_VERSION = 1;
    public static BitmapFont FONT;
    public static BitmapFontCache FONT_CACHE;
    public static Main MAIN_INSTANCE;
    public static int width, height;

    public static PathfinderOptions.Builder createPathfinding(AbstractObject from) {
        String name = String.valueOf(from.hashCode());
        return PathfinderOptions.builder().diagonalNeighbourProvider().maxIterations(500).debugRenderConsumer(DEBUGGING ? pathfindDebugPos -> {
            Debuggers.pathfindDebuggerTimers.put(name, GameConstants.TICKS_PER_SECOND*5);
            if (pathfindDebugPos.type().equals(PathfindDebugType.BEGIN_MARKER_NOTREAL)) {
                Debuggers.pathfindDebuggers.put(name, new ArrayList<>());
            } else if (!pathfindDebugPos.type().equals(PathfindDebugType.END_MARKER_NOTREAL)) {
                Debuggers.pathfindDebuggers.get(name).add(pathfindDebugPos);
            }
        } : null);
    }

    public static Level getLevelOrThrow() {
        if(level != null) return level;
        throw new IllegalStateException("World is null");
    }

    protected static void init() {
        GameConstants.spriteBatch.enableBlending();
        GameConstants.spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //https://www.1001fonts.com/born2bsporty-fs-font.html
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(PathUtil.asset("font.tff")));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameters.size = 48;
        fontParameters.color = Color.WHITE;
        fontParameters.borderWidth = 2;
        fontParameters.borderColor = Color.BLACK;
        fontParameters.borderStraight = true;
        fontParameters.minFilter = Texture.TextureFilter.Nearest;
        fontParameters.magFilter = Texture.TextureFilter.Nearest;

        FONT = fontGenerator.generateFont(fontParameters);
        FONT.getData().markupEnabled = true;
        FONT_CACHE = new BitmapFontCache(FONT);
    }
}
