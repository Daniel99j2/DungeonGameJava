package com.daniel99j.dungeongame.util;

import com.daniel99j.dungeongame.ui.Debuggers;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.Sys;

import java.io.*;

public class Logger {
    public static void info(String s) {
        info(s, (Object) null);
    }

    public static void info(String s, Object... data) {
        String val = s;
        for (Object var : data) {
            if(var != null) val = val.replaceFirst("\\{}", var.toString());
        }
        System.out.println(val);
        Debuggers.log(val);
    }

    public static void info(Object o) {
        info(o.toString());
    }

    public static void error(String s) {
        error(s, (Object) null);
    }

    public static void error(String s, Object... data) {
        String val = s;
        int i = 0;
        Throwable throwable = null;
        for (Object var : data) {
            if(var != null) {
                if (i == 0 && var instanceof Throwable t) {
                    throwable = t;
                } else val = val.replaceFirst("\\{}", var.toString());
                i++;
            }
        }
        System.err.println(val);
        Debuggers.log("<error>"+val);

        if(throwable != null) {
            //noinspection CallToPrintStackTrace
            throwable.printStackTrace();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            Debuggers.log("<error>"+sw);
        }
    }

    public static void error(Object o) {
        error(o.toString());
    }
}
