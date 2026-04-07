package com.daniel99j.dungeongame.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;

public class RenderUtil {
    public static void renderWithBlend(Runnable code) {
        boolean wasBlending = Gdx.gl.glIsEnabled(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if(!wasBlending) Gdx.gl.glEnable(GL20.GL_BLEND);
        code.run();
        if(!wasBlending) Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
