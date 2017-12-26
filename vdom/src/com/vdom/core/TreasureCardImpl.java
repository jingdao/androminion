package com.vdom.core;

import java.util.ArrayList;
import java.util.HashSet;

import com.vdom.api.Card;
import com.vdom.api.GameEvent;
import com.vdom.api.TreasureCard;
import com.vdom.api.VictoryCard;
import com.vdom.api.ActionCard;

public class TreasureCardImpl extends CardImpl implements TreasureCard {
    int value;
    boolean providePotion;
    
    public TreasureCardImpl(Cards.Type type, int cost, int value) {
        super(type, cost);
        this.value = value;
    }

    public TreasureCardImpl(Builder builder) {
        super(builder);
        value = builder.value;
        providePotion = builder.providePotion;
    }

    public static class Builder extends CardImpl.Builder {
        protected int value;
        protected boolean providePotion = false;

        public Builder(Cards.Type type, int cost, int value) {
            super(type, cost);
            this.value = value;
        }

        public Builder providePotion() {
            providePotion = true;
            return this;
        }

        public TreasureCardImpl build() {
            return new TreasureCardImpl(this);
        }

    }

    protected TreasureCardImpl() {
    }
    
    public TreasureCardImpl(String name, int cost, int value2, boolean costPotion, boolean b) {
    }

    public int getValue() {
        return value;
    }

    @Override
    public CardImpl instantiate() {
        checkInstantiateOK();
        TreasureCardImpl c = new TreasureCardImpl();
        copyValues(c);
        return c;
    }

    public boolean providePotion() {
        return providePotion;
    }

    protected void copyValues(TreasureCardImpl c) {
        super.copyValues(c);
        c.value = value;
        c.providePotion = providePotion;
    }

    @Override
    // return true if Treasure cards should be re-evaluated since might affect
    // coin play order
    public boolean playTreasure(MoveContext context) {
    	return playTreasure(context, false);
    }
    
    public boolean playTreasure(MoveContext context, boolean isClone) {
        boolean reevaluateTreasures = false;
        Player player = context.player;
        Game game = context.game;

        GameEvent event = new GameEvent(GameEvent.Type.PlayingCoin, (MoveContext) context);
        event.card = this;
        game.broadcastEvent(event);

        if (this.numberTimesAlreadyPlayed == 0) {
        	player.playedCards.add(player.hand.removeCard(this));
        }
        
        if (!isClone)
        {
            context.treasuresPlayedSoFar++;
        }
        
        context.gold += getValue();
        
        if (providePotion()) {
            context.potions++;
        }

        // Special cards
        if (equals(Cards.foolsGold)) {
            foolsGold(context);
        } else if (equals(Cards.philosophersStone)) {
            context.gold += (player.getDeckSize() + player.getDiscardSize()) / 5;
        } else if (equals(Cards.diadem)) {
            context.gold += context.getActionsLeft();
        } else if (equals(Cards.copper)) {
            context.gold += context.coppersmithsPlayed;
        } else if (equals(Cards.bank)) {
            context.gold += context.treasuresPlayedSoFar;
        } else if (equals(Cards.contraband)) {
            reevaluateTreasures = contraband(context, game, reevaluateTreasures);
        } else if (equals(Cards.loan) || equals(Cards.venture)) {
            reevaluateTreasures = loanVenture(context, player, game, reevaluateTreasures);
        } else if (equals(Cards.hornOfPlenty)) {
            hornOfPlenty(context, player, game);
        } else if (equals(Cards.illGottenGains)) {
            reevaluateTreasures = illGottenGains(context, player, reevaluateTreasures);
        } else if (equals(Cards.counterfeit)) {
        	reevaluateTreasures = counterfeit(context, game, reevaluateTreasures, player);
        } else if (equals(Cards.spoils)) {
			if (!isClone) {
				// Return to the spoils pile
	            AbstractCardPile pile = game.getPile(this);
	            pile.addCard(player.playedCards.remove(player.playedCards.indexOf(this.getId())));
			}
        } else if (equals(Cards.coinOfTheRealm)) {
			if (!isClone) {
				player.playedCards.remove(player.playedCards.indexOf(this.getId()));
				player.tavern.add(this);
			}
		} else if (equals(Cards.treasureTrove)) {
			treasureTrove(context,game,player);
		} else if (equals(Cards.relic)) {
			relic(context,game,player);
		} else if (equals(Cards.plunder)) {
			player.addVictoryTokens(context,1);
		} else if (equals(Cards.fortune)) {
			context.buys++;
			if (!context.hasDoubledCoin) {
				context.hasDoubledCoin = true;
				context.addGold += context.getCoinAvailableForBuy();
			}
		} else if (equals(Cards.capital)) {
			context.buys++;
		} else if (equals(Cards.charm)) {
			charm(game,context,player);
		} else if (equals(Cards.silver)) {
			if (player.envious && player.returnDeludedEnvious)
				context.gold--;
			merchant(game,context,player);
			sauna(game,context,player);
		} else if (equals(Cards.gold)) {
			if (player.envious && player.returnDeludedEnvious)
				context.gold -= 2;
		} else if (equals(Cards.goat)) {
			goat(game,context,player);
		} else if (equals(Cards.pouch)) {
			pouch(game,context,player);
		} else if (equals(Cards.cursedGold)) {
			cursedGold(game,context,player);
		} else if (equals(Cards.luckyCoin)) {
			luckyCoin(game,context,player);
		} else if (equals(Cards.magicLamp)) {
			magicLamp(game,context,player);
		} else if (equals(Cards.idol)) {
			idol(game,context,player);
		}

        return reevaluateTreasures;
    }

    protected void foolsGold(MoveContext context) {
        context.foolsGoldPlayed++;
        if (context.foolsGoldPlayed > 1) {
            context.gold += 3;
        }
    }

    protected boolean contraband(MoveContext context, Game game, boolean reevaluateTreasures) {
        context.buys++;
        Card cantBuyCard = game.getNextPlayer().controlPlayer.contraband_cardPlayerCantBuy(context);

        if (cantBuyCard != null && !context.cantBuy.contains(cantBuyCard)) {
            context.cantBuy.add(cantBuyCard);
            GameEvent e = new GameEvent(GameEvent.Type.CantBuy, (MoveContext) context);
            game.broadcastEvent(e);
        }
        return true;
    }

    protected boolean loanVenture(MoveContext context, Player player, Game game, boolean reevaluateTreasures) {
        ArrayList<Card> toDiscard = new ArrayList<Card>();
        TreasureCard treasureCardFound = null;
        GameEvent event = null;

        while (treasureCardFound == null) {
            Card draw = game.draw(player);
            if (draw == null) {
                break;
            }

            event = new GameEvent(GameEvent.Type.CardRevealed, context);
            event.card = draw;
            game.broadcastEvent(event);

            if (draw instanceof TreasureCard) {
                treasureCardFound = (TreasureCard) draw;
            } else {
                toDiscard.add(draw);
            }
        }

        if (treasureCardFound != null) {
            if (equals(Cards.loan)) {
                if (player.controlPlayer.loan_shouldTrashTreasure(context, treasureCardFound)) {
                    player.trash(treasureCardFound, this, context);
                } else {
                    player.discard(treasureCardFound, this, null);
                }
            } else if (equals(Cards.venture)) {
                player.hand.add(treasureCardFound);
                treasureCardFound.playTreasure(context);
                reevaluateTreasures = true;
            }
        }

        while (!toDiscard.isEmpty()) {
            player.discard(toDiscard.remove(0), this, null);
        }
        return reevaluateTreasures;
    }

    protected boolean illGottenGains(MoveContext context, Player player, boolean reevaluateTreasures) {
        if (context.getCardsLeftInPile(Cards.copper) > 0) {
            if (player.controlPlayer.illGottenGains_gainCopper(context)) {
                player.gainNewCard(Cards.copper, this, context);
                reevaluateTreasures = true;
            }
        }
        return reevaluateTreasures;
    }

    protected void hornOfPlenty(MoveContext context, Player player, Game game) {
        GameEvent event;

        int maxCost = context.countUniqueCardsInPlayThisTurn();
        Card toObtain = player.controlPlayer.hornOfPlenty_cardToObtain(context, maxCost);
        if (toObtain != null) {
            // check cost
            if (toObtain.getCost(context) <= maxCost) {
                toObtain = game.takeFromPile(toObtain);
                // could still be null here if the pile is empty.
                if (toObtain != null) {
                    event = new GameEvent(GameEvent.Type.CardObtained, context);
                    event.card = toObtain;
                    event.responsible = this;
                    game.broadcastEvent(event);
                    
                    if (toObtain instanceof VictoryCard) {
                    	player.playedCards.remove(this);
                        player.trash(this, toObtain, context);
                        event = new GameEvent(GameEvent.Type.CardTrashed, context);
                        event.card = this;
                        game.broadcastEvent(event);
                    }
                }
            }
        }
    }
    
    protected boolean counterfeit(MoveContext context, Game game, boolean reevaluateTreasures, Player currentPlayer) {
        context.buys++;
        
    	TreasureCard treasure = currentPlayer.controlPlayer.counterfeit_cardToPlay(context);
    	
    	if (treasure != null) {
    		TreasureCardImpl cardToPlay = (TreasureCardImpl) treasure;
            cardToPlay.cloneCount = 2;
            for (int i = 0; i < cardToPlay.cloneCount;) {
                cardToPlay.numberTimesAlreadyPlayed = i++;
                cardToPlay.playTreasure(context, cardToPlay.numberTimesAlreadyPlayed == 0 ? false : true);
            }
            
            cardToPlay.cloneCount = 0;
            cardToPlay.numberTimesAlreadyPlayed = 0;    		
            
            // A counterfeited card will not count in the calculations of future cards that care about the number of treasures played (such as Bank)
            context.treasuresPlayedSoFar--; 
            
            if (!treasure.equals(Cards.spoils) && !treasure.equals(Cards.coinOfTheRealm)) {
                if (currentPlayer.playedCards.getLastCard().getId() == cardToPlay.getId()) {
                	currentPlayer.trash(currentPlayer.playedCards.removeLastCard(), this, context);
	    		}
    		}
    	}

        return true;

    }
    
    @Override
    public void isBought(MoveContext context) 
    {
        switch (this.controlCard.getType()) 
        {
        case Masterpiece:
            masterpiece(context);
            break;
        default:
            break;
        }
    }
    
	public void isTrashed(MoveContext context) {
		switch (this.controlCard.behaveAsCard().getType()) {
			case Rocks:
				context.player.controlPlayer.gainNewCard(Cards.silver,this,context);
				break;
			case HauntedMirror:
				hauntedMirror(context.game, context, context.player);
		}
	}

    public void masterpiece(MoveContext context)
    {
        for (int i = 0; i < context.overpayAmount; ++i)
        {
            if(!context.getPlayer().gainNewCard(Cards.silver, this.controlCard, context)) 
            {
                break;
            }
        }
    }

    public void treasureTrove(MoveContext context,Game game,  Player currentPlayer) {
        currentPlayer.gainNewCard(Cards.gold, this.controlCard, context);
        currentPlayer.gainNewCard(Cards.copper, this.controlCard, context);
	}

	public void relic(MoveContext context,Game game, Player currentPlayer) {
		for (Player player : game.getPlayersInTurnOrder()) {
			if (player != currentPlayer && !Util.isDefendedFromAttack(game, player, this.controlCard)) {
				player.minusCardToken = true;
			}
		}
	}

	public void charm(Game game, MoveContext context, Player currentPlayer) {
		switch (currentPlayer.charm_chooseOption(context)) {
			case AddCoin:
				context.buys++;
				context.addGold += 2;
				break;
			case GainCard:
				currentPlayer.charmEffect++;
				break;
		}
	}

	public void charmEffect(Game game, MoveContext context, Player currentPlayer, Card cardBought) {
        int cost = cardBought.getCost(context);
        boolean potion = cardBought.costPotion();
		int debt = cardBought.costDebt();
        ArrayList<Card> validCards = new ArrayList<Card>();
		for (Card card : game.getCardsInGame()) {
			if (Cards.isSupplyCard(card) && game.getCardsLeftInPile(card) > 0
				&& !card.equals(cardBought) && game.getPile(game.getOriginCard(card)).card().equals(card)) {
				int gainCardCost = card.getCost(context);
				boolean gainCardPotion = card.costPotion();
				int gainCardDebt = card.costDebt();
				if (gainCardCost==cost && gainCardPotion==potion && gainCardDebt==debt)
					validCards.add(card);
			}
		}
		if (validCards.size() == 0)
			return;
		Card toGain = currentPlayer.controlPlayer.charm_cardToObtain(context, validCards.toArray(new Card[0]));
		if (toGain != null)
			currentPlayer.gainNewCard(toGain, Cards.charm, context);
	}

	public void merchant(Game game, MoveContext context, Player currentPlayer) {
        int silverPlayed = this.controlCard.numberTimesAlreadyPlayed;
        silverPlayed += context.countCardsInPlay(Cards.silver);
		if (silverPlayed == 1)
			context.gold += context.countCardsInPlay(Cards.merchant);

	}

	public void goat(Game game, MoveContext context, Player currentPlayer) {
        Card toTrash = currentPlayer.controlPlayer.goat_cardToTrash(context);
        if (toTrash != null) {
			currentPlayer.hand.remove(toTrash);
			currentPlayer.trash(toTrash, this.controlCard, context);
        }
	}

	public void pouch(Game game, MoveContext context, Player currentPlayer) {
        context.buys++;
	}

	public void cursedGold(Game game, MoveContext context, Player currentPlayer) {
        currentPlayer.gainNewCard(Cards.curse, this.controlCard, context);
	}

	public void luckyCoin(Game game, MoveContext context, Player currentPlayer) {
        currentPlayer.gainNewCard(Cards.silver, this.controlCard, context);
	}

	public void hauntedMirror(Game game, MoveContext context, Player currentPlayer) {
		boolean hasAction = false;
		for (Card card : currentPlayer.hand) {
			if (card instanceof ActionCard) {
				hasAction = true;
				break;
			}
		}
		if (!hasAction)
			return;
		Card cardToDiscard = currentPlayer.controlPlayer.hauntedMirror_cardToDiscard(context);
		if (cardToDiscard != null) {
            currentPlayer.hand.remove(cardToDiscard);
            currentPlayer.reveal(cardToDiscard, this.controlCard, context);
            currentPlayer.discard(cardToDiscard, this.controlCard, null);
			currentPlayer.gainNewCard(Cards.ghost, this.controlCard, context);
		}
	}

	public void magicLamp(Game game, MoveContext context, Player currentPlayer) {
        HashSet<String> cardNames = new HashSet<String>();
		for (Card card : currentPlayer.playedCards) {
			if (cardNames.contains(card.getName()))
				cardNames.remove(card.getName());
			else
				cardNames.add(card.getName());
		}
		for (Card card : currentPlayer.nextTurnCards) {
			if (cardNames.contains(card.getName()))
				cardNames.remove(card.getName());
			else
				cardNames.add(card.getName());
		}
		if (cardNames.size() >= 6) {
			if (!this.controlCard.movedToNextTurnPile) {
				this.controlCard.movedToNextTurnPile = true;
				currentPlayer.trash(this.controlCard, null, context);
				currentPlayer.playedCards.remove(currentPlayer.playedCards.lastIndexOf(this.controlCard));
				currentPlayer.gainNewCard(Cards.wish, this.controlCard, context);
				currentPlayer.gainNewCard(Cards.wish, this.controlCard, context);
				currentPlayer.gainNewCard(Cards.wish, this.controlCard, context);
			}
		}
	}

	public void idol(Game game, MoveContext context, Player currentPlayer) {
		int numIdols = 0;
		for (Card card : currentPlayer.playedCards) {
			if (card.equals(Cards.idol))
				numIdols++;
		}
		if (numIdols % 2 == 1) {
			Boons boon = game.receiveBoons(context);
			boon.applyEffect(game,context,currentPlayer,this.controlCard);
		} else {
			for (Player player : game.getPlayersInTurnOrder()) {
				if (player != currentPlayer && !Util.isDefendedFromAttack(game, player, this.controlCard)) {
					player.attacked(this.controlCard, context);
					player.gainNewCard(Cards.curse, this.controlCard, new MoveContext(game, player));
				}
			}
		}
	}

	public void sauna(Game game, MoveContext context, Player currentPlayer) {
		int numSauna = context.countCardsInPlay(Cards.sauna);
		for (int i=0;i<numSauna;i++) {
			Card toTrash = currentPlayer.controlPlayer.sauna_cardToTrash(context);
			if (toTrash != null) {
				currentPlayer.hand.remove(toTrash);
				currentPlayer.trash(toTrash, Cards.sauna, context);
			}
		}
	}

}
