package com.daniel99j.dungeongame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.daniel99j.dungeongame.ui.renderable.RenderState;
import com.daniel99j.dungeongame.ui.renderable.Renderable;

import java.util.ArrayList;

public class UiScreen implements Screen {
    private ArrayList<Renderable> renderables = new ArrayList<>();

    public void addRenderable(Renderable renderable) {
        this.renderables.add(renderable);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        RenderState state = new RenderState(
            Gdx.input.isButtonPressed(Input.Buttons.LEFT),
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT),
            Gdx.input.isButtonPressed(Input.Buttons.MIDDLE),
            Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE),
            Gdx.input.isButtonPressed(Input.Buttons.RIGHT),
            Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT),
            Gdx.input.getX(), Gdx.input.getY(),
            delta);
        for (Renderable renderable : this.renderables) {
            renderable.render(state);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.renderables.clear();
    }
}
