package com.daniel99j.dungeongame.mechanic.card.common;

import com.daniel99j.dungeongame.mechanic.card.CardInstance;
import com.daniel99j.dungeongame.mechanic.card.CardType;

public class InstantCard extends CardInstance {
    private final Runnable action;

    public InstantCard(CardType type, Runnable action) {
        super(type);
        this.action = action;
    }

    @Override
    public void onStart() {
        this.action.run();
    }
}
