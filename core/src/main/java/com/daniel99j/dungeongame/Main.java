package com.daniel99j.dungeongame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.ScreenUtils;
import com.daniel99j.dungeongame.entity.CollisionCategories;
import com.daniel99j.dungeongame.sounds.SoundManager;
import com.daniel99j.dungeongame.ui.Debuggers;
import com.daniel99j.dungeongame.ui.PlayScreen;
import com.daniel99j.dungeongame.level.LevelLoader;
import com.daniel99j.dungeongame.util.Logger;
import com.daniel99j.dungeongame.util.PathUtil;
import com.daniel99j.dungeongame.util.RenderUtil;
import com.daniel99j.dungeongame.util.ScheduledRunnables;

import java.util.ArrayList;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        GameConstants.MAIN_INSTANCE = this;

        TexturePacker.process(PathUtil.asset("game"), PathUtil.relativize("gen/atlases"), "main");

        GameConstants.init();
        GameConstants.viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        setScreen(new PlayScreen());

        Debuggers.init();

        GameConstants.level = LevelLoader.loadFromData("test"); //
//        StaticObject wall = new StaticObject("16x");
//        GameConstants.level.addObject(wall);
//        wall.setPos(Vector2.One);
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        GameConstants.viewport.update(width, height, true);

        GameConstants.width = width;
        GameConstants.height = height;

        super.resize(width, height);
    }

    @Override
    public void render() {
        SoundManager.tick(Gdx.graphics.getDeltaTime());

        Gdx.input.setCursorCatched(!Debuggers.isDebuggerOpen());

        GameConstants.TIME += Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(new Color(0x111111ff));

        //so adding new ones whilst in the list works
        ArrayList<Runnable> oldRunnables = new ArrayList<>(ScheduledRunnables.runnables);
        ScheduledRunnables.runnables.clear();
        for (Runnable runnable : oldRunnables) {
            runnable.run();
        }
        if (Debuggers.isEnabled("freecam")) {
            GameConstants.camera.position.x = Debuggers.freecam.x;
            GameConstants.camera.position.y = Debuggers.freecam.y;
        } else {
            if(GameConstants.player != null) {
                GameConstants.camera.position.x = GameConstants.player.getPos().x;
                GameConstants.camera.position.y = GameConstants.player.getPos().y;
            }
        }
        GameConstants.camera.update();
        GameConstants.viewport.apply();

        GameConstants.spriteBatch.setProjectionMatrix(GameConstants.camera.combined);

        GameConstants.shapeRenderer.setProjectionMatrix(GameConstants.camera.combined);
        GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        GameConstants.shapeRenderer.setColor(Color.BLACK);
        GameConstants.shapeRenderer.rect(GameConstants.camera.position.x-10, GameConstants.camera.position.y-10, 1000, 1000);
        GameConstants.shapeRenderer.end();

        if(GameConstants.level != null) {
            GameConstants.spriteBatch.begin();
            if(Debuggers.shouldTickWorld()) GameConstants.getLevelOrThrow().tick(Gdx.graphics.getDeltaTime());
            else GameConstants.player.tick();
            GameConstants.getLevelOrThrow().render();
            GameConstants.spriteBatch.end();

            if(!GameConstants.DEBUGGING || Debuggers.isEnabled("lights")) {
                GameConstants.camera.update();
                GameConstants.viewport.apply();
                GameConstants.level.rayHandler.useCustomViewport(GameConstants.viewport.getScreenX(), GameConstants.viewport.getScreenY(), GameConstants.viewport.getScreenWidth(), GameConstants.viewport.getScreenHeight());
                GameConstants.level.rayHandler.setCombinedMatrix(GameConstants.camera);
                GameConstants.level.rayHandler.updateAndRender();
            }
        }

        GameConstants.spriteBatch.begin();
        this.screen.render(Gdx.graphics.getDeltaTime());
        GameConstants.spriteBatch.end();

        Debuggers.render();

        if(Gdx.input.isKeyJustPressed(Input.Keys.F2)) RenderUtil.takeScreenshot();
    }

    @Override
    public void dispose() {
        super.dispose();
        Debuggers.dispose();
        GameConstants.shapeRenderer.dispose();
        GameConstants.spriteBatch.dispose();
    }
}
