package com.daniel99j.dungeongame.ui.screenss;

import com.daniel99j.djutil.maths.MathsInterpreter;

public class ScreenSS {
    private final String x, y, sizeX, sizeY;

    public ScreenSS(String x, String y, String sizeX, String sizeY) {
        this.x = x;
        this.y = y;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public int getX() {
        return (int) MathsInterpreter.eval(x);
    }

    public int getY() {
        return (int) MathsInterpreter.eval(y);
    }

    public int getSizeX() {
        return (int) MathsInterpreter.eval(sizeX);
    }

    public int getSizeY() {
        return (int) MathsInterpreter.eval(sizeY);
    }
}
