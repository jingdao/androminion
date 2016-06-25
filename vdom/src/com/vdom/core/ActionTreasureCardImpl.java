package com.vdom.core;

import com.vdom.api.TreasureCard;
import com.vdom.api.GameEvent;

public class ActionTreasureCardImpl extends ActionCardImpl implements TreasureCard {
    protected ActionTreasureCardImpl(Builder builder) {
        super(builder);
    }

    public static class Builder extends ActionCardImpl.Builder {
        public Builder(Cards.Type type, int cost) {
            super(type, cost);
        }

        public ActionTreasureCardImpl build() {
            return new ActionTreasureCardImpl(this);
        }
    }

    @Override
    public CardImpl instantiate() {
        checkInstantiateOK();
        ActionTreasureCardImpl c = new ActionTreasureCardImpl();
        copyValues(c);
        return c;
    }

    protected void copyValues(ActionVictoryCardImpl c) {
        super.copyValues(c);
    }

    protected ActionTreasureCardImpl() {
    }

    public int getValue() {return 0;}

    public boolean providePotion() {return false;}

    public boolean playTreasure(MoveContext context) {
        Player player = context.player;
        Game game = context.game;
        GameEvent event = new GameEvent(GameEvent.Type.PlayingCoin, (MoveContext) context);
        event.card = this;
        game.broadcastEvent(event);
        if (this.numberTimesAlreadyPlayed == 0)
        	player.playedCards.add(player.hand.removeCard(this));
		context.treasuresPlayedSoFar++;
    	TreasureCard treasure = player.controlPlayer.crown_treasureCardToPlay(context);
    	if (treasure != null) {
			if (treasure instanceof TreasureCardImpl) {
				TreasureCardImpl cardToPlay = (TreasureCardImpl) treasure;
				cardToPlay.cloneCount = 2;
				for (int i = 0; i < cardToPlay.cloneCount;) {
					cardToPlay.numberTimesAlreadyPlayed = i++;
					cardToPlay.playTreasure(context, cardToPlay.numberTimesAlreadyPlayed == 0 ? false : true);
				}
				cardToPlay.cloneCount = 0;
				cardToPlay.numberTimesAlreadyPlayed = 0;    		
			} else {
				ActionTreasureCardImpl cardToPlay = (ActionTreasureCardImpl) treasure;
				cardToPlay.cloneCount = 2;
				for (int i = 0; i < cardToPlay.cloneCount;) {
					cardToPlay.numberTimesAlreadyPlayed = i++;
					cardToPlay.playTreasure(context);
				}
				cardToPlay.cloneCount = 0;
				cardToPlay.numberTimesAlreadyPlayed = 0;    		
			}
			context.treasuresPlayedSoFar--; 
    	}
        return true;
	}
}
