package com.daniel99j.dungeongame.ui;

import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.physics.box2d.*;
import com.daniel99j.djutil.ValueHolder;
import com.daniel99j.djutil.pathfinder.PathfindDebugPos;
import com.daniel99j.djutil.pathfinder.PathfindDebugType;
import com.daniel99j.dungeongame.GameConstants;
import com.daniel99j.dungeongame.entity.AbstractObject;
import com.daniel99j.dungeongame.entity.PositionMarker;
import com.daniel99j.dungeongame.entity.TilesetObject;
import com.daniel99j.dungeongame.entity.TreasureObject;
import com.daniel99j.dungeongame.sounds.SoundInstance;
import com.daniel99j.dungeongame.sounds.SoundManager;
import com.daniel99j.dungeongame.util.*;
import com.daniel99j.dungeongame.level.LevelLight;
import com.daniel99j.dungeongame.level.LevelLoader;
import com.daniel99j.dungeongame.level.SaveConfig;
import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Debuggers {
    private static Box2DDebugRenderer box2dDebugRenderer;
    private static ImGuiImplGlfw imGuiGlfw;
    private static ImGuiImplGl3 imGuiGl3;
    private static InputProcessor tmpProcessor;
    private static Map<String, ValueHolder<Boolean>> debugOptions = new HashMap<>();
    private static UUID selectedObjectId = null;
    private static UUID selectedLightId = null;
    private static Vector2 oldPos;
    private static Vector2 oldLightPos;
    private static String data = null;
    //short for less memory
    private static final ArrayList<Short> fpsCounter = new ArrayList<>();
    private static String createObjectData = null;
    private static String soundName = null;
    private static final ArrayList<String> logger = new ArrayList<>();
    public static final Map<String, ArrayList<PathfindDebugPos>> pathfindDebuggers = new HashMap<>();
    public static final Map<String, Integer> pathfindDebuggerTimers = new HashMap<>();
    public static Vector2 freecam = Vector2.Zero;
    private static float lastTime = 0;
    private static ArrayList<String> audioNames = new ArrayList<>();

    static {
        if (GameConstants.DEBUGGING) {
            debugOptions.put("showing", new ValueHolder<>(false));
            debugOptions.put("hitboxes", new ValueHolder<>(false));
            debugOptions.put("lights", new ValueHolder<>(true));
            debugOptions.put("noclip", new ValueHolder<>(false));
            debugOptions.put("selecting", new ValueHolder<>(false));
            debugOptions.put("selectingLight", new ValueHolder<>(false));
            debugOptions.put("staticLightUpdates", new ValueHolder<>(false));
            debugOptions.put("pathfindingRender", new ValueHolder<>(false));
            debugOptions.put("disablePathfinding", new ValueHolder<>(false));
            debugOptions.put("freecam", new ValueHolder<>(false));
            debugOptions.put("tick", new ValueHolder<>(true));
            debugOptions.put("markers", new ValueHolder<>(false));
            debugOptions.put("invulnerable", new ValueHolder<>(false));

            for (FileHandle e : Gdx.files.internal(PathUtil.asset("sounds/")).list()) {
                audioNames.add(e.name());
            }
        }
    }

    public static void init() {
        if (GameConstants.DEBUGGING) {
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

    public static boolean isDebuggerOpen() {
        return GameConstants.DEBUGGING && isEnabled("showing");
    }

    public static void render() {
        if (!GameConstants.DEBUGGING) return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.SCROLL_LOCK)) pause();

        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) debugOptions.get("showing").object = !isEnabled("showing");

        if (isDebuggerOpen()) {
            if (isEnabled("staticLightUpdates") && GameConstants.level != null) {
                for (LevelLight<?> light : GameConstants.level.getLights()) {
                    if (light.light().isStaticLight()) {
                        light.light().setStaticLight(false);
                        light.light().setStaticLight(true);
                    }
                }
            }

            if(isEnabled("pathfindingRender")) {
                RenderUtil.enableBlending();
                pathfindDebuggers.forEach((hash, debuggers) -> {
                    for (PathfindDebugPos pathfindDebugPos : debuggers) {
                        float transparency = pathfindDebuggerTimers.get(hash).floatValue()/(5*GameConstants.TICKS_PER_SECOND);

                        GameConstants.shapeRenderer.setProjectionMatrix(GameConstants.camera.combined);
                        if (pathfindDebugPos.type().equals(PathfindDebugType.SUCCESSFUL_PATH)) {
                            GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                            GameConstants.shapeRenderer.setColor(Color.GREEN.cpy().mul(1, 1, 1, transparency));
                            GameConstants.shapeRenderer.line(pathfindDebugPos.pos().getX() + 0.5f, pathfindDebugPos.pos().getY() + 0.5f, pathfindDebugPos.previous().getX() + 0.5f, pathfindDebugPos.previous().getY() + 0.5f);
                        } else if (pathfindDebugPos.type().equals(PathfindDebugType.CONNECTION)) {
                            GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                            GameConstants.shapeRenderer.setColor(Color.YELLOW.cpy().mul(1, 1, 1, transparency));
                            GameConstants.shapeRenderer.line(pathfindDebugPos.pos().getX() + 0.5f, pathfindDebugPos.pos().getY() + 0.5f, pathfindDebugPos.previous().getX() + 0.5f, pathfindDebugPos.previous().getY() + 0.5f);
                        } else if (pathfindDebugPos.type().equals(PathfindDebugType.INVALID)) {
                            GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                            GameConstants.shapeRenderer.setColor(Color.GRAY.cpy().mul(1, 1, 1, transparency));
                            GameConstants.shapeRenderer.rect(pathfindDebugPos.pos().getX() + 0.3f, pathfindDebugPos.pos().getY() + 0.3f, 0.4f, 0.4f);
                        } else if (pathfindDebugPos.type().equals(PathfindDebugType.OPEN_SET)) {
                            GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                            GameConstants.shapeRenderer.setColor(Color.RED.cpy().mul(1, 1, 1, transparency));
                            GameConstants.shapeRenderer.rect(pathfindDebugPos.pos().getX() + 0.3f, pathfindDebugPos.pos().getY() + 0.3f, 0.4f, 0.4f);
                        } else if (pathfindDebugPos.type().equals(PathfindDebugType.CLOSED_SET)) {
                            GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                            GameConstants.shapeRenderer.setColor(Color.YELLOW.cpy().mul(1, 1, 1, transparency));
                            GameConstants.shapeRenderer.rect(pathfindDebugPos.pos().getX() + 0.3f, pathfindDebugPos.pos().getY() + 0.3f, 0.4f, 0.4f);
                        } else if (pathfindDebugPos.type().equals(PathfindDebugType.START)) {
                            GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                            GameConstants.shapeRenderer.setColor(Color.BLUE.cpy().mul(1, 1, 1, transparency));
                            GameConstants.shapeRenderer.rect(pathfindDebugPos.pos().getX() + 0.3f, pathfindDebugPos.pos().getY() + 0.3f, 0.4f, 0.4f);
                        } else if (pathfindDebugPos.type().equals(PathfindDebugType.END)) {
                            GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                            GameConstants.shapeRenderer.setColor(Color.PURPLE.cpy().mul(1, 1, 1, transparency));
                            GameConstants.shapeRenderer.rect(pathfindDebugPos.pos().getX() + 0.3f, pathfindDebugPos.pos().getY() + 0.3f, 0.4f, 0.4f);
                        }
                        GameConstants.shapeRenderer.end();
                    }
                });
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                GameConstants.level.addObject(new TreasureObject(GlobalRunnables.COLLECT_TREASURE, "coin", Color.valueOf("#fcb603")));
            }

            if (tmpProcessor != null) { // Restore the input processor after ImGui caught all inputs, see #end()
                Gdx.input.setInputProcessor(tmpProcessor);
                tmpProcessor = null;
            }

            imGuiGl3.newFrame();
            imGuiGlfw.newFrame();
            ImGui.newFrame();

            //START

            ImGui.begin("Logger");
            for (String s : logger) {
                if (s.startsWith("<error>")) {
                    ImGui.textColored(0xff0000ff, s.replace("<error>", ""));
                } else ImGui.text(s);

                if (!ImGui.isWindowHovered()) ImGui.setScrollY(10000);
            }
            ImGui.end();

            ImGui.begin("Options");

            if (ImGui.button("Save map")) {
                try {
                    Files.write(Paths.get(PathUtil.data("maps/test.map")).toAbsolutePath(), LevelLoader.saveLevel(GameConstants.level).getBytes());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (ImGui.button("Kill")) {
                GameConstants.player.damage(100000);
            }

            debugOptions.forEach((s, valueHolder) -> {
                if (!s.equals("showing") && !s.equals("selecting") && !s.equals("selectingLight"))
                    if (ImGui.checkbox(s, valueHolder.object)) {
                        valueHolder.object = !valueHolder.object;
                        if(s.equals("freecam")) freecam = new Vector2(GameConstants.camera.position.x, GameConstants.camera.position.y);
                    }
            });

            float[] fpsArray = new float[fpsCounter.size()];
            int i = 0;

            for (Short f : fpsCounter) {
                fpsArray[i++] = f;
            }

            ImGui.plotLines("FPS graph", fpsArray, 100, 1, "", 0, 200, new ImVec2(0, 80));
            if (GameConstants.TIME > lastTime+0.02f) {
                lastTime = GameConstants.TIME;
                if (fpsCounter.size() > 100) fpsCounter.removeFirst();
                fpsCounter.add((short) Gdx.graphics.getFramesPerSecond());
            }

            ImGui.text("Current FPS: " + Gdx.graphics.getFramesPerSecond());

            ImGui.end();

            ImGui.showDemoWindow();

            UUID hoveredObject = null;

            ImGui.begin("Lights");
            UUID hoveredLight = renderLightSelector();
            boolean showLights = ImGui.isWindowFocused(ImGuiFocusedFlags.RootAndChildWindows);
            ImGui.end();

            ImGui.begin("Objects");

            if (createObjectData != null) {
                renderObjectCreator();
            } else {
                hoveredObject = renderObjectSelector();
            }

            ImGui.end();

            //DEBUGGERS

            // incase imgui changes the viewport
            GameConstants.camera.update();
            GameConstants.viewport.apply();

            if(GameConstants.level != null) {

                if (GameConstants.level != null && isEnabled("hitboxes"))
                    box2dDebugRenderer.render(GameConstants.level.getBox2dWorld(), GameConstants.camera.combined);

                AbstractObject selectedObject;
                if (hoveredObject != null && (selectedObject = GameConstants.level.getObjectByUUID(hoveredObject)) != null) {
                    RenderUtil.enableBlending();
                    GameConstants.shapeRenderer.setProjectionMatrix(GameConstants.camera.combined);
                    GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    if (selectedObject.hasPhysics()) {
                        for (Fixture fixture : selectedObject.getPhysics().getFixtureList()) {
                            Vector4 hitbox = selectedObject.getHitbox(fixture);
                            GameConstants.shapeRenderer.setColor(0xdf / 255.0f, 0xf0 / 255.0f, 0x29 / 255.0f, 0.5f);
                            GameConstants.shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.z, hitbox.w);
                        }
                    } else if (selectedObject instanceof TilesetObject tilesetObject) {
                        GameConstants.shapeRenderer.setColor(0xdf / 255.0f, 0xf0 / 255.0f, 0x29 / 255.0f, 0.5f);
                        GameConstants.shapeRenderer.rect(tilesetObject.getPos().x, tilesetObject.getPos().y, tilesetObject.getWidth(), tilesetObject.getHeight());
                    }
                    GameConstants.shapeRenderer.end();
                }

                if (showLights) for (LevelLight<?> light : GameConstants.level.getLights()) {
                    Color c = light.light().getColor().cpy();
                    if (light.uuid().equals(hoveredLight)) c = Color.YELLOW;
                    GameConstants.shapeRenderer.setProjectionMatrix(GameConstants.camera.combined);
                    GameConstants.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    GameConstants.shapeRenderer.setColor(c);
                    GameConstants.shapeRenderer.circle(light.light().getPosition().x, light.light().getPosition().y, 0.2f, 20);
                    GameConstants.shapeRenderer.end();
                }
            }

            //END
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            // If ImGui wants to capture the input, disable libGDX's input processor
            if (ImGui.getIO().getWantCaptureKeyboard() || ImGui.getIO().getWantCaptureMouse()) {
                tmpProcessor = Gdx.input.getInputProcessor();
                Gdx.input.setInputProcessor(null);
            }
            //END

            if (ImGui.isWindowHovered(ImGuiHoveredFlags.AnyWindow) || ImGui.isWindowFocused(
                ImGuiFocusedFlags.AnyWindow)) {
                ImGui.getStyle().setAlpha(1.0f);
            } else {
                ImGui.getStyle().setAlpha(0.2f);
            }
        }
    }

    private static void renderObjectCreator() {
        if (ImGui.button("Edit objects"))
            createObjectData = null;
        else {
            ImString objectCreator = new ImString(createObjectData, 10000);

            ImGui.inputTextMultiline("Create object", objectCreator, ImGuiInputTextFlags.None);

            createObjectData = objectCreator.get();

            boolean create = ImGui.button("Create");
            ImGui.sameLine();
            boolean forcedUUID = ImGui.button("Create (force UUID)");
            if (create || forcedUUID) {
                try {
                    JsonObject data = GsonUtil.parse(createObjectData);
                    if (!forcedUUID) {
                        data.addProperty("uuid", UUID.randomUUID().toString());
                    }
                    AbstractObject object = LevelLoader.createObject(data, GameConstants.level);
                    createObjectData = null;
                    assert object != null;
                    selectedObjectId = object.getUUID();
                } catch (Exception e) {
                    Logger.error("Error creating object", e);
                }
            }
        }
    }

    private static UUID renderObjectSelector() {
        if(GameConstants.level == null) return null;
        UUID hoveredObject = null;
        if (ImGui.button("Add object")) createObjectData = "";

        ImGui.sameLine();

        ImVec4 oldColour = ImGui.getStyle().getColor(ImGuiCol.Button);
        ImVec4 selectedColour = ImGui.getStyle().getColor(ImGuiCol.ButtonActive);
        if (isEnabled("selecting"))
            ImGui.getStyle().setColor(ImGuiCol.Button, selectedColour.x, selectedColour.y, selectedColour.z, selectedColour.w);
        if (ImGui.button("Pick Object")) {
            debugOptions.get("selecting").object = !isEnabled("selecting");
        } else {
            if (debugOptions.get("selecting").object) {
                if (ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                    selectedObjectId = getHoveredObject() == null ? null : getHoveredObject().getUUID();
                    debugOptions.get("selecting").object = false;
                } else {
                    hoveredObject = getHoveredObject() == null ? null : getHoveredObject().getUUID();
                }
            }
        }
        ImGui.getStyle().setColor(ImGuiCol.Button, oldColour.x, oldColour.y, oldColour.z, oldColour.w);

        ImGui.beginChild("Left Panel", new ImVec2(300, 0), ImGuiChildFlags.Border | ImGuiChildFlags.ResizeX);
        ImGui.separatorText("All Objects");

        if (ImGui.beginTable("Object Selector", 1, ImGuiTableFlags.RowBg)) {
            int id = 0;
            for (AbstractObject allObject : GameConstants.getLevelOrThrow().getAllObjects()) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.pushID(id);
                int flags = ImGuiSelectableFlags.SpanAllColumns;
                boolean selected = allObject.getUUID().equals(selectedObjectId);
                if (selected)
                    flags |= ImGuiTreeNodeFlags.Selected;
                if (ImGui.selectable(allObject.toString() + " (" + id + ")", selected, flags))
                    selectedObjectId = allObject.getUUID();
                if (ImGui.isItemHovered()) hoveredObject = allObject.getUUID();
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
        if (selectedObjectId != null && (selectedObject = GameConstants.level.getObjectByUUID(selectedObjectId)) != null) {

            Vector2 middle = oldPos == null ? selectedObject.getPos() : oldPos;
            int posOffset = 10;

            boolean changing = false;
            slider("X Pos", selectedObject.getPos().x, selectedObject::setX, middle.x - posOffset, middle.x + posOffset, ImGui.isKeyDown(ImGuiKey.ModAlt) ? "%.0f" : "%.3f");
            if (ImGui.isItemActive()) changing = true;
            slider("Y Pos", selectedObject.getPos().y, selectedObject::setY, middle.y - posOffset, middle.y + posOffset, ImGui.isKeyDown(ImGuiKey.ModAlt) ? "%.0f" : "%.3f");
            if (ImGui.isItemActive()) changing = true;

            if (ImGui.button("TP to player")) selectedObject.setPos(GameConstants.player.getPos());
            ImGui.sameLine();
            if (ImGui.button("TP player to this")) GameConstants.player.setPos(selectedObject.getPos());

            if (oldPos == null && changing) {
                oldPos = selectedObject.getPos();
            }
            if (oldPos != null && !changing) {
                oldPos = null;
            }

            if (selectedObject instanceof TilesetObject tilesetObject) {
                ImGui.separatorText("Custom object data");

                intInput("Width", tilesetObject.getWidth(), tilesetObject::setWidth);
                intInput("Height", tilesetObject.getHeight(), tilesetObject::setHeight);
            }

            if (ImGui.button("Duplicate")) {
                if(selectedObject instanceof PositionMarker m) {
                    GameConstants.level.addObject(new PositionMarker(m));
                } else {
                    try {
                        JsonObject data = selectedObject.write();
                        data.addProperty("uuid", UUID.randomUUID().toString());
                        AbstractObject object = LevelLoader.createObject(data, GameConstants.level);
                        assert object != null;
                        selectedObjectId = object.getUUID();
                    } catch (Exception e) {
                        Logger.error("Error duplicating object", e);
                    }
                }
            }

            ImGui.sameLine();

            if (ImGui.button("Delete")) {
                if(selectedObject instanceof PositionMarker p) p.delete();
                GameConstants.level.removeObject(selectedObject);
            }

            ImGui.separatorText("Data");
            if (data != null) {
                ImString input = new ImString(data, data.length() + 10000);

                ImGui.inputTextMultiline(" ", input, ImGuiInputTextFlags.None);

                data = input.get();
            }
            JsonObject object = selectedObject.write();
            data = GsonUtil.PARSER.toJson(object);
        }

        ImGui.endChild();

        return hoveredObject;
    }

    private static UUID renderLightSelector() {
        if(GameConstants.level == null) return null;
        UUID hoveredLight = null;
        if (ImGui.button("Add point light")) {
            assert GameConstants.level != null;
            GameConstants.level.addLight((rayHandler -> new PointLight(rayHandler, 128)), SaveConfig.ALWAYS);
        }

        ImGui.sameLine();

        if (ImGui.button("Add cone light")) {
            assert GameConstants.level != null;
            selectedLightId = GameConstants.level.addLight((rayHandler -> new ConeLight(rayHandler, 128, Color.RED, 5, 0, 0, 0, 45)), SaveConfig.ALWAYS).uuid();
        }

        ImGui.sameLine();

        if (ImGui.button("Add directional light")) {
            assert GameConstants.level != null;
            selectedLightId = GameConstants.level.addLight((rayHandler -> new DirectionalLight(rayHandler, 128, Color.RED, 30)), SaveConfig.ALWAYS).uuid();
        }

        ImGui.sameLine();

        ImVec4 oldColour = ImGui.getStyle().getColor(ImGuiCol.Button);
        ImVec4 selectedColour = ImGui.getStyle().getColor(ImGuiCol.ButtonActive);
        if (isEnabled("selectingLight"))
            ImGui.getStyle().setColor(ImGuiCol.Button, selectedColour.x, selectedColour.y, selectedColour.z, selectedColour.w);
        if (ImGui.button("Pick Light")) {
            debugOptions.get("selectingLight").object = !isEnabled("selectingLight");
        } else {
            if (debugOptions.get("selectingLight").object) {
                if (ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                    selectedLightId = getHoveredLight() == null ? null : getHoveredLight().uuid();
                    debugOptions.get("selectingLight").object = false;
                } else {
                    hoveredLight = getHoveredLight() == null ? null : getHoveredLight().uuid();
                }
            }
        }
        ImGui.getStyle().setColor(ImGuiCol.Button, oldColour.x, oldColour.y, oldColour.z, oldColour.w);

        ImGui.beginChild("Light Left Panel", new ImVec2(300, 0), ImGuiChildFlags.Border | ImGuiChildFlags.ResizeX);
        ImGui.separatorText("All Lights");

        if (ImGui.beginTable("Light Selector", 1, ImGuiTableFlags.RowBg)) {
            int id = 0;
            for (LevelLight<?> light : GameConstants.getLevelOrThrow().getLights()) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.pushID(id);
                int flags = ImGuiSelectableFlags.SpanAllColumns;
                boolean selected = light.uuid().equals(selectedLightId);
                if (selected)
                    flags |= ImGuiTreeNodeFlags.Selected;
                if (ImGui.selectable(light.toString() + " (" + id + ")", selected, flags))
                    selectedLightId = light.uuid();
                if (ImGui.isItemHovered()) hoveredLight = light.uuid();
                ImGui.popID();

                id++;
            }
            ImGui.endTable();
        }

        ImGui.endChild();

        ImGui.sameLine();

        ImGui.beginChild("Light Right Panel", new ImVec2(0, 0), ImGuiChildFlags.Border);

        ImGui.separatorText("Current Light");

        LevelLight<?> selectedLight;
        if (selectedLightId != null && (selectedLight = GameConstants.level.getLights().stream().filter((o) -> o.uuid().equals(selectedLightId)).findFirst().orElse(null)) != null) {
            Vector2 middle = oldLightPos == null ? selectedLight.light().getPosition().cpy() : oldLightPos;
            int posOffset = 10;

            boolean changing = false;
            slider("X Pos", selectedLight.light().getPosition().x, (x) -> selectedLight.light().setPosition(x, selectedLight.light().getY()), middle.x - posOffset, middle.x + posOffset, ImGui.isKeyDown(ImGuiKey.ModAlt) ? "%.0f" : "%.3f");
            if (ImGui.isItemActive()) changing = true;
            slider("Y Pos", selectedLight.light().getPosition().y, (y) -> selectedLight.light().setPosition(selectedLight.light().getX(), y), middle.y - posOffset, middle.y + posOffset, ImGui.isKeyDown(ImGuiKey.ModAlt) ? "%.0f" : "%.3f");
            if (ImGui.isItemActive()) changing = true;

            if (ImGui.button("TP to player")) selectedLight.light().setPosition(GameConstants.player.getPos());
            ImGui.sameLine();
            if (ImGui.button("TP player to this")) GameConstants.player.setPos(selectedLight.light().getPosition());

            if (oldLightPos == null && changing) {
                oldLightPos = selectedLight.light().getPosition().cpy();
            }
            if (oldLightPos != null && !changing) {
                oldLightPos = null;
            }

            float[] colours = {
                selectedLight.light().getColor().r,
                selectedLight.light().getColor().g,
                selectedLight.light().getColor().b,
                selectedLight.light().getColor().a
            };
            if (ImGui.colorPicker4("Colour", colours)) {
                selectedLight.light().setColor(colours[0], colours[1], colours[2], colours[3]);
            }

            if (ImGui.checkbox("X-Ray", selectedLight.light().isXray())) {
                selectedLight.light().setXray(!selectedLight.light().isXray());
            }

            if (ImGui.checkbox("Static", selectedLight.light().isStaticLight())) {
                selectedLight.light().setStaticLight(!selectedLight.light().isStaticLight());
            }

            if (ImGui.checkbox("Soft", selectedLight.light().isSoft())) {
                selectedLight.light().setSoft(!selectedLight.light().isSoft());
            }

            if (ImGui.checkbox("Active", selectedLight.light().isActive())) {
                selectedLight.light().setActive(!selectedLight.light().isActive());
            }

            slider("Softness", selectedLight.light().getSoftShadowLength(), selectedLight.light()::setSoftnessLength, 0, 5, "%.3f");

            slider("Distance", selectedLight.light().getDistance(), selectedLight.light()::setDistance, 0, 100, "%.3f");

            if (selectedLight.light() instanceof ConeLight coneLight) {
                ImGui.separatorText("Cone Light");

                slider("Direction", selectedLight.light().getDirection(), selectedLight.light()::setDirection, 0, 360, "%.0f");

                slider("Cone size", coneLight.getConeDegree(), coneLight::setConeDegree, 0, 180, "%.3f");
            }

            if (selectedLight.light() instanceof DirectionalLight) {
                ImGui.separatorText("Directional Light");

                slider("Direction", selectedLight.light().getDirection(), selectedLight.light()::setDirection, 0, 360, "%.0f");
            }

            if (ImGui.button("Delete")) {
                GameConstants.level.removeLight(selectedLight);
            }
        }

        ImGui.endChild();

        return hoveredLight;
    }

    private static void renderSoundPlayer() {
//        if (ImGui.button("Play new")) soundName = "";
//
//        ImGui.beginChild("Left Audio Panel", new ImVec2(300, 0), ImGuiChildFlags.Border | ImGuiChildFlags.ResizeX);
//        ImGui.separatorText("Active sounds");
//
//        if (ImGui.beginTable("Sound Selector", 1, ImGuiTableFlags.RowBg)) {
//            int id = 0;
//            for (SoundInstance soundInstance : SoundManager.getActiveSounds()) {
//                ImGui.tableNextRow();
//                ImGui.tableNextColumn();
//                ImGui.pushID(id);
//                int flags = ImGuiSelectableFlags.SpanAllColumns;
//                boolean selected = allObject.getUUID().equals(selectedObjectId);
//                if (selected)
//                    flags |= ImGuiTreeNodeFlags.Selected;
//                if (ImGui.selectable(allObject.toString() + " (" + id + ")", selected, flags))
//                    selectedObjectId = allObject.getUUID();
//                if (ImGui.isItemHovered()) hoveredObject = allObject.getUUID();
//                ImGui.popID();
//
//                id++;
//            }
//            ImGui.endTable();
//        }
//
//        ImGui.endChild();
//
//        ImGui.sameLine();
//
//        ImGui.beginChild("Right Panel", new ImVec2(0, 0), ImGuiChildFlags.Border);
//
//        ImGui.separatorText("Current Object");
//
//        AbstractObject selectedObject;
//        if (selectedObjectId != null && (selectedObject = GameConstants.level.getObjectByUUID(selectedObjectId)) != null) {
//
//            Vector2 middle = oldPos == null ? selectedObject.getPos() : oldPos;
//            int posOffset = 10;
//
//            boolean changing = false;
//            slider("X Pos", selectedObject.getPos().x, selectedObject::setX, middle.x - posOffset, middle.x + posOffset, ImGui.isKeyDown(ImGuiKey.ModAlt) ? "%.0f" : "%.3f");
//            if (ImGui.isItemActive()) changing = true;
//            slider("Y Pos", selectedObject.getPos().y, selectedObject::setY, middle.y - posOffset, middle.y + posOffset, ImGui.isKeyDown(ImGuiKey.ModAlt) ? "%.0f" : "%.3f");
//            if (ImGui.isItemActive()) changing = true;
//
//            if (ImGui.button("TP to player")) selectedObject.setPos(GameConstants.player.getPos());
//            ImGui.sameLine();
//            if (ImGui.button("TP player to this")) GameConstants.player.setPos(selectedObject.getPos());
//
//            if (oldPos == null && changing) {
//                oldPos = selectedObject.getPos();
//            }
//            if (oldPos != null && !changing) {
//                oldPos = null;
//            }
//
//            if (selectedObject instanceof TilesetObject tilesetObject) {
//                ImGui.separatorText("Custom object data");
//
//                intInput("Width", tilesetObject.getWidth(), tilesetObject::setWidth);
//                intInput("Height", tilesetObject.getHeight(), tilesetObject::setHeight);
//            }
//
//            if (ImGui.button("Duplicate")) {
//                try {
//                    JsonObject data = selectedObject.write();
//                    data.addProperty("uuid", UUID.randomUUID().toString());
//                    AbstractObject object = LevelLoader.createObject(data, GameConstants.level);
//                    assert object != null;
//                    selectedObjectId = object.getUUID();
//                } catch (Exception e) {
//                    Logger.error("Error duplicating object", e);
//                }
//            }
//
//            ImGui.sameLine();
//
//            if (ImGui.button("Delete")) {
//                GameConstants.level.removeObject(selectedObject);
//            }
//
//            ImGui.separatorText("Data");
//            if (data != null) {
//                ImString input = new ImString(data, data.length() + 10000);
//
//                ImGui.inputTextMultiline(" ", input, ImGuiInputTextFlags.None);
//
//                data = input.get();
//            }
//            JsonObject object = selectedObject.write();
//            data = GsonUtil.PARSER.toJson(object);
//        }
//
//        ImGui.endChild();
    }

    private static void intInput(String name, int getter, Consumer<Integer> setter) {
        ImInt check = new ImInt(getter);
        if (ImGui.inputInt(name, check)) {
            setter.accept(check.get());
        }
    }

    private static void slider(String name, float getter, Consumer<Float> setter, float min, float max, String format) {
        float[] check = {getter};
        if (ImGui.sliderFloat(name, check, min, max, format)) {
            setter.accept(check[0]);
        }
    }

    private static AbstractObject getHoveredObject() {
        float mouseX = ImGui.getMousePosX();
        float mouseY = ImGui.getMousePosY();

        Vector3 screenCoords = new Vector3(mouseX, mouseY, 0);
        Vector3 worldCoords = GameConstants.camera.unproject(screenCoords);

        Vector2 point = new Vector2(worldCoords.x, worldCoords.y);
        float range = 0.001f;

        ValueHolder<AbstractObject> out = new ValueHolder<>(null);

        QueryCallback callback = fixture -> {
            if (fixture.getBody().getUserData() instanceof AbstractObject object) out.object = object;
            return true;
        };

        GameConstants.level.getBox2dWorld().QueryAABB(callback, point.x - range, point.y - range, point.x + range, point.y + range);

        return out.object;
    }

    private static LevelLight getHoveredLight() {
        float mouseX = ImGui.getMousePosX();
        float mouseY = ImGui.getMousePosY();

        Vector3 screenCoords = new Vector3(mouseX, mouseY, 0);
        Vector3 worldCoords = GameConstants.camera.unproject(screenCoords);

        Vector2 point = new Vector2(worldCoords.x, worldCoords.y);
        float range = 0.3f;

        ValueHolder<LevelLight> out = new ValueHolder<>(null);

        GameConstants.level.getLights().forEach(light -> {
            if (light.light().getPosition().cpy().sub(point).len() <= range) out.object = light;
        });

        return out.object;
    }

    public static void dispose() {
        if (GameConstants.DEBUGGING) {
            imGuiGl3.shutdown();
            imGuiGl3 = null;
            imGuiGlfw.shutdown();
            imGuiGlfw = null;
            ImGui.destroyContext();
        }
    }

    public static void log(String s) {
        if (!GameConstants.DEBUGGING) return;
        Debuggers.logger.add(s);
    }

    public static void pause() {
        long t = System.currentTimeMillis();
        if (t >= System.currentTimeMillis() - 10) {
            Logger.error("Make sure to add breakpoint here!");
        }
    }

    public static boolean isEnabled(String noclip) {
        return debugOptions.get(noclip).object;
    }

    public static boolean shouldTickWorld() {
        return !GameConstants.DEBUGGING || debugOptions.get("tick").object;
    }
}
