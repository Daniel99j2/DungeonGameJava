package com.daniel99j.dungeongame.entity;

import com.daniel99j.dungeongame.entity.living.Hog;
import com.daniel99j.dungeongame.entity.living.Player;

import java.util.HashMap;
import java.util.Map;

public class ObjectTypes {
    public static final Map<String, ObjectType<?>> types = new HashMap<>();

    public static final ObjectType<Player> PLAYER = registerObjectType(new ObjectType<>("player", Player::read));
    public static final ObjectType<SpriteObject> SPRITE = registerObjectType(new ObjectType<>("sprite", SpriteObject::read));
    public static final ObjectType<TilesetObject> TILESET = registerObjectType(new ObjectType<>("tileset", TilesetObject::read));
    public static final ObjectType<TreasureObject> TREASURE = registerObjectType(new ObjectType<>("treasure", TreasureObject::read));
    public static final ObjectType<Hog> HOG = registerObjectType(new ObjectType<>("hog", Hog::read));

    private static <T extends AbstractObject> ObjectType<T> registerObjectType(ObjectType<T> type) {
        types.put(type.id(), type);
        return type;
    }
}
