package com.daniel99j.dungeongame.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.util.PathUtil;

/** First screen of the application. Displayed after the application is created. */
public class PlayScreen implements Screen {
    Texture backgroundTexture;


    public PlayScreen() {
        backgroundTexture = new Texture(PathUtil.asset("game/background.png"));
    }

    @Override
    public void show() {
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        GameConstants.spriteBatch.draw(GameConstants.atlas.findRegion("background"), 0, 0, 1, 1);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}
