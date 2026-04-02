package com.daniel99j.dungeongame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.Main;
import com.daniel99j.dungeongame.entity.AbstractObject;
import com.daniel99j.dungeongame.util.GsonUtil;
import com.daniel99j.dungeongame.util.LevelLoader;
import com.daniel99j.dungeongame.util.PathUtil;
import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public class Debuggers {
    private static Box2DDebugRenderer box2dDebugRenderer;
    private static ImGuiImplGlfw imGuiGlfw;
    private static ImGuiImplGl3 imGuiGl3;
    private static InputProcessor tmpProcessor;
    private static boolean showing = false;
    private static boolean hitboxes = false;
    private static UUID selectedObjectId = null;
    private static Vector2 oldPos;
    private static String data = null;
    public static boolean noclip = false;
    //short for less memory
    private static final ArrayList<Short> fpsCounter = new ArrayList<>();

    public static void init() {
        if(GameConstants.DEBUGGING) {
            box2dDebugRenderer = new Box2DDebugRenderer();

            imGuiGlfw = new ImGuiImplGlfw();
            imGuiGl3 = new ImGuiImplGl3();
            long windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();
            ImGui.createContext();
            ImGuiIO io = ImGui.getIO();
            io.getFonts().addFontDefault();
            io.getFonts().build();
            imGuiGlfw.init(windowHandle, true);
            imGuiGl3.init("#version 150");
        }
    }

    public static void render() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SCROLL_LOCK)) pause();

        if(Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) showing = !showing;

        if(GameConstants.DEBUGGING && showing) {
            if (tmpProcessor != null) { // Restore the input processor after ImGui caught all inputs, see #end()
                Gdx.input.setInputProcessor(tmpProcessor);
                tmpProcessor = null;
            }

            imGuiGl3.newFrame();
            imGuiGlfw.newFrame();
            ImGui.newFrame();

            //START
            ImGui.begin("Options");

            if(ImGui.button("I'm a Button!")) {
                try {
                    Files.write(Paths.get(PathUtil.data("maps/test.map")).toAbsolutePath(), LevelLoader.saveLevel(GameConstants.level).getBytes());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if(ImGui.checkbox("Hitboxes", hitboxes)) hitboxes = !hitboxes;

            if(ImGui.checkbox("Noclip", noclip)) noclip = !noclip;

            float[] fpsArray = new float[fpsCounter.size()];
            int i = 0;

            for (Short f : fpsCounter) {
                fpsArray[i++] = f;
            }

            ImGui.plotLines("FPS graph", fpsArray, 100, 1, "", 0, 200, new ImVec2(0, 80));
            if(GameConstants.getLevelOrThrow().getTime() % 2 == 0) {
                if(fpsCounter.size() > 100) fpsCounter.removeFirst();
                fpsCounter.add((short) Gdx.graphics.getFramesPerSecond());
            }

            ImGui.text("Current FPS: "+Gdx.graphics.getFramesPerSecond());

            ImGui.end();

            ImGui.begin("Objects");
            ImGui.beginChild("Left Panel", new ImVec2(300, 0), ImGuiChildFlags.Border | ImGuiChildFlags.ResizeX);

            ImGui.separatorText("All Objects");

            if(ImGui.beginTable("Object Selector", 1, ImGuiTableFlags.RowBg)) {
                int id = 0;
                for (AbstractObject allObject : GameConstants.getLevelOrThrow().getAllObjects()) {
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.pushID(id);
                    int flags = ImGuiSelectableFlags.SpanAllColumns;
                    boolean selected = allObject.getUUID().equals(selectedObjectId);
                    if (selected)
                        flags |= ImGuiTreeNodeFlags.Selected;
                    if (ImGui.selectable("hello test"+id, selected, flags)) selectedObjectId = allObject.getUUID();
                    ImGui.popID();

                    id++;
                }
                ImGui.endTable();
            }

            ImGui.endChild();

            ImGui.sameLine();

            ImGui.beginChild("Right Panel", new ImVec2(0, 0), ImGuiChildFlags.Border);

            ImGui.separatorText("Current Object");

            AbstractObject selectedObject;
            if(selectedObjectId != null && (selectedObject = GameConstants.level.getObjectByUUID(selectedObjectId)) != null) {

                Vector2 middle = oldPos == null ? selectedObject.getPos() : oldPos;
                int offset = 10;

                boolean changing = false;
                slider("X Pos", selectedObject.getPos().x, selectedObject::setX, middle.x-offset, middle.x+offset, ImGui.isKeyDown(ImGuiKey.ModAlt) ? "%.0f" : "%.3f");
                if(ImGui.isItemActive()) changing = true;
                slider("Y Pos", selectedObject.getPos().y, selectedObject::setY, middle.y-offset, middle.y+offset, ImGui.isKeyDown(ImGuiKey.ModAlt) ? "%.0f" : "%.3f");
                if(ImGui.isItemActive()) changing = true;

                if(ImGui.button("TP to player")) selectedObject.setPos(GameConstants.player.getPos());
                ImGui.sameLine();
                if(ImGui.button("TP player to this")) GameConstants.player.setPos(selectedObject.getPos());

                if(oldPos == null && changing) {
                    oldPos = selectedObject.getPos();
                }
                if(oldPos != null && !changing) {
                    oldPos = null;
                }

                ImGui.separatorText("Data");
                if(data != null) {
                    ImString input = new ImString(data, data.length()+10000);

                    ImGui.inputTextMultiline("Enter some text", input, ImGuiInputTextFlags.None);

                    data = input.get();
                }
                if(ImGui.button("Get data")) {
                    JsonObject object = selectedObject.write();
                    data = GsonUtil.PARSER.toJson(object);
                }
            }

            ImGui.endChild();

            ImGui.end();

            //END
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            // If ImGui wants to capture the input, disable libGDX's input processor
            if (ImGui.getIO().getWantCaptureKeyboard() || ImGui.getIO().getWantCaptureMouse()) {
                tmpProcessor = Gdx.input.getInputProcessor();
                Gdx.input.setInputProcessor(null);
            }
            //END

            if (GameConstants.level != null && hitboxes) box2dDebugRenderer.render(GameConstants.level.getBox2dWorld(), GameConstants.viewport.getCamera().combined);
        }
    }

    private static void slider(String name, float getter, Consumer<Float> setter, float min, float max, String format) {
        float[] check = {getter};
        if(ImGui.sliderFloat(name, check, min, max, format)) {
            setter.accept(check[0]);
        }
    }

    public static void dispose() {
        if(GameConstants.DEBUGGING) {
            imGuiGl3.shutdown();
            imGuiGl3 = null;
            imGuiGlfw.shutdown();
            imGuiGlfw = null;
            ImGui.destroyContext();
        }
    }

    public static void pause() {
        long t = System.currentTimeMillis();
        if(t >= System.currentTimeMillis()-10) {
            System.err.println("Make sure to add breakpoint here!");
        }
    }
}
