package com.vdom.api;

public interface DurationCard extends Card{

    public int getAddActionsNextTurn();

    public int getAddBuysNextTurn();

    public int getAddGoldNextTurn();

    public int getAddCardsNextTurn();

    public boolean takeAnotherTurn();

    public int takeAnotherTurnCardCount();

}
