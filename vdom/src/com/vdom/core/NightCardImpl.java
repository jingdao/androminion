package com.vdom.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.vdom.api.ActionCard;
import com.vdom.api.Card;
import com.vdom.api.CurseCard;
import com.vdom.api.DurationCard;
import com.vdom.api.GameEvent;
import com.vdom.api.GameEventListener;
import com.vdom.api.TreasureCard;
import com.vdom.api.VictoryCard;

public class NightCardImpl extends CardImpl {

    public NightCardImpl(Builder builder) {
        super(builder);
		this.isNight = true;
    }

    public static class Builder extends CardImpl.Builder{
	    protected boolean attack;

        public Builder(Cards.Type type, int cost) {
            super(type, cost);
        }

        public NightCardImpl build() {
            return new NightCardImpl(this);
        }
    }

    @Override
    public CardImpl instantiate() {
        checkInstantiateOK();
        NightCardImpl c = new NightCardImpl();
        copyValues(c);
        return c;
    }
    
    protected void copyValues(NightCardImpl c) {
        super.copyValues(c);
        c.attack = attack;
    }

    protected NightCardImpl() {
    }

    public void play(Game game, MoveContext context) {
        play(game, context, true);
    }

    public void play(Game game, MoveContext context, boolean fromHand) {
        super.play(game, context);

        Player currentPlayer = context.getPlayer();
        boolean newCard = false;
        NightCardImpl actualCard = (this.getControlCard() != null ? (NightCardImpl) this.getControlCard() : this);

        if (this.numberTimesAlreadyPlayed == 0 && this == actualCard) {
        	newCard = true;
            this.movedToNextTurnPile = false;
            if (fromHand)
                currentPlayer.hand.remove(this);
            if (this instanceof DurationCard) {
              	currentPlayer.nextTurnCards.add((DurationCard) this);
            } else {
            	currentPlayer.playedCards.add(this);
            }
        }

        GameEvent event = new GameEvent(GameEvent.Type.PlayingAction, (MoveContext) context);
        event.card = this;
        event.newCard = newCard;
        game.broadcastEvent(event);

		additionalCardActions(game, context, currentPlayer);
        
        event = new GameEvent(GameEvent.Type.PlayedAction, (MoveContext) context);
        event.card = this;
        game.broadcastEvent(event);

    }

    protected void additionalCardActions(Game game, MoveContext context, Player currentPlayer) {
		if (this.equals(Cards.devilsWorkshop)) {
			int numObtained = game.cardsObtainedLastTurn[game.playersTurn].size();
			if (numObtained>=2) {
				currentPlayer.gainNewCard(Cards.imp, this.controlCard, context);
			} else if (numObtained==1) {
				Card card = currentPlayer.controlPlayer.devilsWorkshop_cardToObtain(context);
				if (card!=null)
					currentPlayer.gainNewCard(card, this.controlCard, context);
			} else {
				currentPlayer.gainNewCard(Cards.gold, this.controlCard, context);
			}
		} else if (this.equals(Cards.vampire)) {
			ArrayList<Card> validCards = new ArrayList<Card>();
			for (Card card : game.getCardsInGame()) {
				if (Cards.isSupplyCard(card) && game.getCardsLeftInPile(card) > 0
					&& game.getPile(game.getOriginCard(card)).card().equals(card) && !card.equals(Cards.vampire)) {
					int gainCardCost = card.getCost(context);
					if (gainCardCost<=5)
						validCards.add(card);
				}
			}
			if (validCards.size() == 0)
				return;
			Card card = currentPlayer.controlPlayer.vampire_cardToObtain(context, validCards.toArray(new Card[0]));
			if (card!=null)
				currentPlayer.gainNewCard(card, this.controlCard, context);
			if (!this.controlCard.movedToNextTurnPile) {
				this.controlCard.movedToNextTurnPile = true;
				currentPlayer.playedCards.remove(currentPlayer.playedCards.lastIndexOf(this.controlCard));
				AbstractCardPile pile = game.getPile(this.controlCard);
				pile.addCard(this.controlCard);
				currentPlayer.gainNewCard(Cards.bat, this.controlCard, context);
			}
		} else if (this.equals(Cards.bat)) {
			Card[] cards = currentPlayer.controlPlayer.bat_cardsToTrash(context);
			if (cards != null) {
				for (Card card : cards) {
					currentPlayer.hand.remove(card);
					currentPlayer.trash(card,Cards.bat,context);
				}
				if (!this.controlCard.movedToNextTurnPile) {
					this.controlCard.movedToNextTurnPile = true;
					currentPlayer.playedCards.remove(currentPlayer.playedCards.lastIndexOf(this.controlCard));
					AbstractCardPile pile = game.getPile(this.controlCard);
					pile.addCard(this.controlCard);
					currentPlayer.gainNewCard(Cards.vampire, this.controlCard, context);
				}
			}
		} else if (this.equals(Cards.raider)) {
			for (Player player : game.getPlayersInTurnOrder()) {
				if (player != currentPlayer && !Util.isDefendedFromAttack(game, player, this.controlCard)) {
					player.attacked(this.controlCard, context);
					MoveContext playerContext = new MoveContext(game, player);
					ArrayList<Card> options = new ArrayList<Card>();
					for (Card card : player.hand) {
						if (currentPlayer.playedCards.contains(card)) {
							options.add(card);
						}
					}
					if (options.size()==0) {
						for (int i = 0; i < player.hand.size(); i++) {
							Card card = player.hand.get(i);
							player.reveal(card, this.controlCard, playerContext);
						}
					} else {
						Card toDiscard;
						if (options.size()==1)
							toDiscard = options.get(0);
						else
							toDiscard = player.controlPlayer.raider_discard_chooseOption(context, options.toArray(new Card[0]));
						player.hand.remove(toDiscard);
						player.discard(toDiscard, this.controlCard, playerContext);
					}
				}
			}
		} else if (this.equals(Cards.crypt)) {
			ArrayList<Card> options = new ArrayList<Card>();
			for (Card card : currentPlayer.playedCards) {
				if (card instanceof TreasureCard) {
					options.add(card);
				}
			}
			Card[] cardsToSetAside = currentPlayer.controlPlayer.crypt_cardsToSetAside(context, options.toArray(new Card[0]));
			for (Card card : cardsToSetAside) {
				currentPlayer.playedCards.remove(card);
			}
			ArrayList<Card> cardsList = new ArrayList<Card>(Arrays.asList(cardsToSetAside));
			currentPlayer.crypt.add(cardsList);
		}
    }
}
