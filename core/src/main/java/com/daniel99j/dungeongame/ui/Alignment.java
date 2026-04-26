package com.daniel99j.dungeongame.ui;

import com.badlogic.gdx.math.Vector2;
import com.daniel99j.dungeongame.GameConstants;

import java.util.function.Function;
import java.util.function.Supplier;

public class Alignment {
    private final Supplier<Vector2> positionGetter;

    public static final Alignment TOP_LEFT = percentage(0, 0);
    public static final Alignment TOP_CENTER = percentage(0.5f, 0);
    public static final Alignment TOP_RIGHT = percentage(1, 0);

    public static final Alignment MIDDLE_LEFT = percentage(0, 0.5f);
    public static final Alignment MIDDLE_CENTER = percentage(0.5f, 0.5f);
    public static final Alignment MIDDLE_RIGHT = percentage(1, 0.5f);

    public static final Alignment BOTTOM_LEFT = percentage(0, 1);
    public static final Alignment BOTTOM_CENTER = percentage(0.5f, 1);
    public static final Alignment BOTTOM_RIGHT = percentage(1, 1);

    public Alignment(Supplier<Vector2> positionGetter) {
        this.positionGetter = positionGetter;
    }

    public int getX() {
        return (int) positionGetter.get().x;
    }

    public int getY() {
        return GameConstants.height-(int) positionGetter.get().y;
    }

    public Alignment offset(int x, int y) {
        return new Alignment(() -> this.positionGetter.get().cpy().add(new Vector2(x, -y)));
    }

    public static Alignment percentage(float x, float y) {
        return new Alignment(() -> new Vector2(GameConstants.width*x, GameConstants.height*y));
    }
}
