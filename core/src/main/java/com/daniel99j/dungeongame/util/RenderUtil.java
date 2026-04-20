package com.daniel99j.dungeongame.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.daniel99j.djutil.MiscUtils;
import com.daniel99j.dungeongame.GameConstants;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.zip.Deflater;

public class RenderUtil {
    public static void enableBlending() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void takeScreenshot() {
        String name = "Screenshot "+DateTimeFormatter.ofPattern("dd MMM uuuu HH:mm:ss").format(LocalDateTime.now())+".png";
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        PixmapIO.writePNG(Gdx.files.external(name), pixmap, Deflater.DEFAULT_COMPRESSION, true);
        pixmap.dispose();
    }

    public static void renderText(String text, int x, int y, float size) {
        GameConstants.FONT.getData().setScale(size);

        String newText = text.replace("[", "[[");
        while(newText.contains("<colour:")) {
            String data = MiscUtils.getTextBetween(newText, "<colour:", ">");
            newText = newText.replace("<colour:"+data+">", "["+data.toUpperCase()+"]");
        }
        GameConstants.FONT.draw(GameConstants.spriteBatch, newText, x, y);
    }
}
