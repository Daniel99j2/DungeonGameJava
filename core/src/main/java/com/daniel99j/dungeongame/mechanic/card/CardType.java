package com.daniel99j.dungeongame.mechanic.card;

import java.util.function.Function;
import java.util.function.Supplier;

public class CardType {
    private final boolean isEthereal;
    private final boolean isPermanent;
    private final CardRarity rarity;
    private final int cost;
    private final String name;
    private final String description;
    private final Function<CardType, CardInstance> constructor;

    public CardType(boolean isEthereal, boolean isPermanent, CardRarity rarity, int cost, String name, String description, Function<CardType, CardInstance> constructor) {
        this.isEthereal = isEthereal;
        this.isPermanent = isPermanent;
        this.rarity = rarity;
        this.cost = cost;
        this.name = name;
        this.description = description;
        this.constructor = constructor;
    }
}
