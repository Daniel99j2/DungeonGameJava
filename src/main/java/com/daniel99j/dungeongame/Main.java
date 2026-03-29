package com.daniel99j.dungeongame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.ScreenUtils;
import com.daniel99j.dungeongame.entity.Player;
import com.daniel99j.dungeongame.ui.PlayScreen;
import com.daniel99j.dungeongame.world.Level;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        TexturePacker.process("assets/game", "atlases", "main");

        GameConstants.init();
        setScreen(new PlayScreen());
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        GameConstants.viewport.update(width, height, true);

        GameConstants.level = new Level();
        GameConstants.player = new Player();
        GameConstants.player.init(GameConstants.level);

        super.resize(width, height);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        GameConstants.viewport.apply();
        GameConstants.spriteBatch.setProjectionMatrix(GameConstants.viewport.getCamera().combined);
        GameConstants.spriteBatch.begin();

        if(GameConstants.level != null) {
            GameConstants.getLevelOrThrow().tick(Gdx.graphics.getDeltaTime());
            GameConstants.getLevelOrThrow().render();
        }
        super.render();

        GameConstants.spriteBatch.end();
    }
}
