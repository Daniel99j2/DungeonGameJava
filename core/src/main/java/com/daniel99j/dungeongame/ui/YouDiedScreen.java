package com.daniel99j.dungeongame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.ui.types.Button;
import com.daniel99j.dungeongame.util.PathUtil;
import com.daniel99j.dungeongame.util.RenderUtil;

/** First screen of the application. Displayed after the application is created. */
public class YouDiedScreen extends UiScreen {
    Texture backgroundTexture;

    public YouDiedScreen() {
        backgroundTexture = new Texture(PathUtil.asset("game/damage_overlay.png"));
    }

    @Override
    public void show() {
        super.show();
        this.addRenderable(new Button(100, 100, 16, 16, "button.png", "Return to camp"));
        // Prepare your screen here.
        GameConstants.viewport = new FillViewport(GameConstants.width, GameConstants.height, GameConstants.camera);
        GameConstants.camera.position.x = 0;
        GameConstants.camera.position.y = 0;
        GameConstants.viewport.update(GameConstants.width, GameConstants.height, true);
    }

    @Override
    public void render(float delta) {
        GameConstants.spriteBatch.end();
        // Draw your screen here. "delta" is the time since last render in seconds.
        GameConstants.camera.update();
            GameConstants.viewport.apply();
        GameConstants.spriteBatch.setProjectionMatrix(GameConstants.camera.combined);

        float worldWidth = GameConstants.viewport.getWorldWidth();
        float worldHeight = GameConstants.viewport.getWorldHeight();

        GameConstants.spriteBatch.begin();

        GameConstants.spriteBatch.setColor(Color.RED);

        GameConstants.spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        RenderUtil.renderText("<colour:red>The end.", 1, 6, 0.1f);
        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        // Destroy screen's assets here.
        backgroundTexture.dispose();
        /////font.dispose();
    }
}
