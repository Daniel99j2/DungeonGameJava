package com.daniel99j.dungeongame.mechanic.card;

import com.daniel99j.dungeongame.mechanic.card.common.InstantCard;
import com.daniel99j.dungeongame.util.GlobalRunnables;

public class Cards {
    public static final CardType TREASURE = new CardType(false, false, CardRarity.COMMON, 10, "Treasure", "Coins galore", (type) -> new InstantCard(type, GlobalRunnables.SPAWN_TREASURE));

}
