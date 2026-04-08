package com.daniel99j.dungeongame.entity;

import com.google.gson.JsonObject;

import java.util.function.Function;
import java.util.function.Supplier;

public record ObjectType <T extends AbstractObject>(String id, Function<JsonObject, T> reader) {
}
