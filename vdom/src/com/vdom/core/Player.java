package com.vdom.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.vdom.api.ActionCard;
import com.vdom.api.Card;
import com.vdom.api.CurseCard;
import com.vdom.api.DurationCard;
import com.vdom.api.GameEvent;
import com.vdom.api.TreasureCard;
import com.vdom.api.VictoryCard;

public abstract class Player {

    Random rand = new Random(System.currentTimeMillis());

	public static final String RANDOM_AI = "Random AI";
	public static final String DISTINCT_CARDS = "Distinct Cards";
	public static final String VICTORY_TOKENS = "Victory Tokens";
	public static final String DISTANT_LANDS_ON_TAVERN = "Distant Lands";
	public static final String CASTLES = "Castles";
	public static final String MISERABLE = "Miserable";

    // Only used by InteractivePlayer currently
    private String name;
    public int playerNumber;
    public int shuffleCount = 0;
    protected int turnCount = 0;
    public int vps;
    public boolean win = false;
	public boolean missionTurn = false;
	public boolean villaGained = false;
	public int charmEffect = 0;
    public int pirateShipTreasure;
    
    // The number of coin tokens held by the player
    private int guildsCoinTokenCount;

	//adventures tokens
	boolean minusCardToken = false;
	boolean minusCoinToken = false;
	boolean journeyToken = false;
	public Card plusCardToken = null;
	public Card plusActionToken = null;
	public Card plusCoinToken = null;
	public Card plusBuyToken = null;
	public Card minusCostToken = null;
	public Card trashingToken = null;
	public Card estateToken = null;

	//nocturne states
	public boolean riversgift = false;
	public Boons savedBoon = null;
	public boolean lostInTheWoods = false;
	public boolean deluded = false;
	public boolean envious = false;
	public boolean returnDeludedEnvious = false;
	public boolean miserable = false;
	public boolean twiceMiserable = false;
    
    private Card checkLeadCard;
    private int victoryTokens;
	public int debtTokens;
    protected CardList hand;
    protected CardList deck;
    protected CardList discard;
    protected CardList playedCards;
    protected CardList nextTurnCards;
    protected CardList nativeVillage;
    protected CardList island;
    protected CardList haven;
    protected CardList horseTraders;
	protected CardList tavern;
	protected CardList gear;
	protected CardList save;
	protected CardList encampments;
	protected CardList faithfulHound;
	public ArrayList<ArrayList<Card>> archive;
	public ArrayList<ArrayList<Card>> crypt;
	public ArrayList<ActionCard> ghost;
	public ArrayList<ActionCard> summon;
	public ArrayList<ActionCard> prince;
	public ArrayList<Event> boughtEvents = new ArrayList<Event>();
    public Game game;
    public Player controlPlayer = this;

    public boolean isPossessed() {
        return !this.equals(controlPlayer);
    }

    public boolean achievementSingleCardFailed;
    public Card achievementSingleCardFirstKingdomCardBought;

    public void addVictoryTokens(MoveContext context, int vt) {
        victoryTokens += vt;
        context.vpsGainedThisTurn += vt;
    }

    public int getTotalCardsBoughtThisTurn(MoveContext context) {
        return context.getTotalCardsBoughtThisTurn();
    }

    public boolean isAi() {
        return true;
    }

    public void setName(String name) {
        this.name = name.replace("_", " ");
    }

    public int getCurrencyTotal(MoveContext context) {
        return getMyCardCount(Cards.copper) + getMyCardCount(Cards.silver) * 2 + getMyCardCount(Cards.gold) * 3 + getMyCardCount(Cards.platinum) * 5;
    }

    public int getMyCardCount(Card card) {
        return Util.getCardCount(getAllCards(), card);
    }
    public int getTurnCount() {
        return turnCount;
    }

    public void newTurn() {
    	turnCount++;
    }

    public ArrayList<Card> getActionCards(Card[] cards) {
        ArrayList<Card> actionCards = new ArrayList<Card>();
        for (Card card : cards) {
            if (card instanceof ActionCardImpl) {
                actionCards.add(card);
            }
        }

        return actionCards;
    }

    public int getActionCardCount(Card[] cards) {
        return getActionCards(cards).size();
    }

    public int getMyAddActionCardCount() {
        int addActionsCards = 0;
        for (Card card : getAllCards()) {
            if (card instanceof ActionCard) {
                if (((ActionCard) card).getAddActions() > 0) {
                    addActionsCards++;
                }
            }
        }

        return addActionsCards;
    }

    public int getMyAddCardCardCount() {
        int addCards = 0;
        for (Card card : getAllCards()) {
            if (card instanceof ActionCard) {
                if (((ActionCard) card).getAddActions() > 0) {
                    addCards++;
                }
            }
        }

        return addCards;
    }

    public int getMyAddActions() {
        int addActions = 0;
        for (Card card : getAllCards()) {
            if (card instanceof ActionCard) {
                addActions += ((ActionCard) card).getAddActions();
            }
        }

        return addActions;
    }

    public int getMyAddCards() {
        int addCards = 0;
        for (Card card : getAllCards()) {
            if (card instanceof ActionCard) {
                addCards += ((ActionCard) card).getAddCards();
            }
        }

        return addCards;
    }

    public int getMyAddBuys() {
        int addBuys = 0;
        for (Card card : getAllCards()) {
            if (card instanceof ActionCard) {
                addBuys += ((ActionCard) card).getAddBuys();
            }
        }

        return addBuys;
    }

	public boolean inHand(Card card) {
        for (Card thisCard : hand) {
            if (thisCard.equals(card)) {
                return true;
            }
        }

        return false;
    }

    public int mineableCards(Card[] hand) {
        int mineableCards = 0;
        for (Card card : hand) {
            if (card.equals(Cards.potion) || card.equals(Cards.loan) || card.equals(Cards.copper) || card.equals(Cards.silver) || card.equals(Cards.gold)) {
                mineableCards++;
            }
        }

        return mineableCards;
    }

    public int inHandCount(Card card) {
        return Util.getCardCount(getHand(), card);
    }

    public Card fromHand(Card card) {
        for (Card thisCard : getHand()) {
            if (thisCard.equals(card)) {
                return thisCard;
            }
        }

        return null;
    }

    public boolean getWin() {
        return win;
    }

    public void initCards() {
        hand = new CardList(this, "Hand");
        deck = new CardList(this, "Deck");
        discard = new CardList(this, "Discard");
        playedCards = new CardList(this, "InPlay");
        nextTurnCards = new CardList(this, "Duration");
        nativeVillage = new CardList(this, "Native Village");
        island = new CardList(this, "Island");
        haven = new CardList(this, "Haven");
        horseTraders = new CardList(this, "Horse Traders");
		tavern = new CardList(this,"Tavern");
		gear = new CardList(this,"Gear");
		save = new CardList(this,"Save");
		encampments = new CardList(this,"Encampments");
		faithfulHound = new CardList(this,"Faithful Hound");
		archive = new ArrayList<ArrayList<Card>>();
		crypt = new ArrayList<ArrayList<Card>>();
		ghost = new ArrayList<ActionCard>();
		summon = new ArrayList<ActionCard>();
		prince = new ArrayList<ActionCard>();
    }

    private List<PutBackOption> getPutBackOptions(MoveContext context) {
    	
        // Determine if criteria were met for certain action cards
        boolean victoryBought = context.getVictoryCardsBoughtThisTurn() > 0;
        boolean potionPlayed   = context.countCardsInPlay(Cards.potion) > 0;
    	boolean treasurePlayed = context.countTreasureCardsInPlayThisTurn() > 0;
    	int actionsPlayed = context.countActionCardsInPlayThisTurn();

    	List<PutBackOption> options = new ArrayList<PutBackOption>();
    	
    	for (Card c: playedCards) {
    		if (c.behaveAsCard().equals(Cards.treasury) && !victoryBought) {
    			options.add(PutBackOption.Treasury);
    		} else if (c.behaveAsCard().equals(Cards.alchemist) && potionPlayed) {
    			options.add(PutBackOption.Alchemist);
    		} else if (c.behaveAsCard().equals(Cards.walledVillage) && actionsPlayed == 1) {
    			options.add(PutBackOption.WalledVillage);
    		} else if (c.behaveAsCard().equals(Cards.herbalist) && treasurePlayed) {
    			options.add(PutBackOption.Coin);
    		}
    	}
    	if (actionsPlayed > 0) {
    		for (int i = 0; i < context.schemesPlayed; i++) {
    			options.add(PutBackOption.Action);
    		}
    	}
    	return options;
    }

    private Card findCard(MoveContext context, Card template) {
		for (Card c: playedCards) {
			if (c.behaveAsCard().equals(template)) {
				return c;
			}
		}
		return null;
    }

    protected void cleanup(MoveContext context) {
        // /////////////////////////////////
        // Discard played cards
        // /////////////////////////////////

    	// reset any lingering CloneCounts
    	for (Card card : playedCards) {
        	CardImpl actualCard = (CardImpl) card;
        	actualCard.cloneCount = 1;
		}
    	
    	// Check for return-to-deck options
    	List<PutBackOption> putBackOptions;
        ArrayList<Card> putBackCards = new ArrayList<Card>();

        while (!(putBackOptions = controlPlayer.getPutBackOptions(context)).isEmpty()) {
            PutBackOption putBackOption = controlPlayer.selectPutBackOption(context, putBackOptions);
            if (putBackOption == PutBackOption.None || (isPossessed() && controlPlayer.isAi())) {
        		break;
        	} else {
        		if (putBackOption == PutBackOption.Treasury) {
        			Card treasury = findCard(context, Cards.treasury);
        			playedCards.remove(treasury);
                    putBackCards.add(treasury);
        		} else if (putBackOption == PutBackOption.Alchemist) {
        			Card alchemist = findCard(context, Cards.alchemist);
        			playedCards.remove(alchemist);
                    putBackCards.add(alchemist);
        		} else if (putBackOption == PutBackOption.WalledVillage) {
                	Card walledVillage = findCard(context, Cards.walledVillage);
                	playedCards.remove(walledVillage);
                	putBackCards.add(walledVillage);
        		} else if (putBackOption == PutBackOption.Coin) {
        			Card herbalist = findCard(context, Cards.herbalist);
        			playedCards.remove(herbalist);
            		discard(herbalist, null, null, false);
                    ArrayList<TreasureCard> treasureCards = new ArrayList<TreasureCard>();
                    for(Card card : playedCards) {
                        if(card instanceof TreasureCard) {
                            treasureCards.add((TreasureCard) card);
                        }
                    }

                    if(treasureCards.size() > 0) {
                        TreasureCard treasureCard = controlPlayer.herbalist_backOnDeck(context, treasureCards.toArray(new TreasureCard[0]));
                        if(treasureCard != null && playedCards.contains(treasureCard)) {
                            playedCards.remove(treasureCard);
                            putBackCards.add(treasureCard);
                        }
                    }
        		} else if (putBackOption == PutBackOption.Action) {
        			context.schemesPlayed --;
                    ArrayList<Card> actions = new ArrayList<Card>();
                    for(Card c : playedCards) {
                        if(c instanceof ActionCard) {
                            actions.add(c);
                        }
                    }
                    if(actions.size() == 0) {
                        break;
                    }

                    ActionCard actionToPutBack = controlPlayer.scheme_actionToPutOnTopOfDeck(((MoveContext) context), actions.toArray(new ActionCard[0]));
                    if(actionToPutBack == null) {
                        break;
                    }
                    int index = playedCards.indexOf((Card) actionToPutBack);
                    if(index == -1) {
                        Util.playerError(this, "Scheme returned invalid card to put back on top of deck, ignoring");
                        break;
                    }

                    Card card = playedCards.remove(index);
                    if (card.behaveAsCard().equals(Cards.hermit) &&
                    	(context != null) && 
                    	(context.totalCardsBoughtThisTurn == 0)) {
            		    	controlPlayer.gainNewCard(Cards.madman, card, context);
            		    }
                    putBackCards.add(card);
        		}
        	}
        }

        if (!putBackCards.isEmpty()) {
        	// reset any lingering Impersonations
	    	for (Card card : putBackCards) {
	        	((CardImpl) card).stopImpersonatingCard();
			}
    	
	        if (putBackCards.size() == 1) {
	            putOnTopOfDeck(putBackCards.get(0), context, true);
	        } else {
	        	Card[] orderedCards = controlPlayer.topOfDeck_orderCards(context, putBackCards.toArray(new Card[0]));
	
	            for (int i = orderedCards.length - 1; i >= 0; i--) {
	                Card card = orderedCards[i];
	                putOnTopOfDeck(card, context, true);
	            }
	        }
        }
        
		for (Card card: playedCards) {
			if (card.equals(Cards.capital)) {
				debtTokens += 6;
				if (context.getCoinAvailableForBuy() > 0)
					resolveDebt(context);
			}
		}

		for (Card card : encampments)
			context.game.getPile(context.game.getOriginCard(card)).addCard(card);
		encampments.clear();

		if (context.game.getCardsObtainedByPlayer().size()==0 && context.game.getLandmarkVictoryTokens(Landmarks.baths) > 0) {
			Landmarks.baths.applyEffect(context.game,context,this,null);
		}

        while (!playedCards.isEmpty()) {
    		discard(playedCards.remove(0), null, context, false);
    	}

        if (isPossessed()) {
            while (!game.possessedTrashPile.isEmpty()) {
                discard(game.possessedTrashPile.remove(0), null, null, false);
            }
            game.possessedBoughtPile.clear();
        }

        // /////////////////////////////////
        // Discard hand
        // /////////////////////////////////

        while (getHand().size() > 0) {
            discard(hand.remove(0, false), null, null, false);
        }

		if (context.gold >= 2) {
			ArrayList<Card> called = new ArrayList<Card>();
			for (Card reserve:tavern) {
				if (reserve.equals(Cards.wineMerchant)) {
					if (callReserveCard(context,reserve))
						called.add(reserve);		
					else
						break;
				}
			}
			for (Card reserve : called) {
				tavern.remove(reserve);
				discard(reserve,null,null,false);
			}
		}

// /////////////////////////////////
        // Double check that deck/discard/hand all have valid cards.
        // /////////////////////////////////
        checkCardsValid();

    }

    public void debug(String msg) {
        Util.debug(this, msg, false);
    }

    public CardList getHand() {
        return hand;
    }

    public CardList getDiscard() {
        return discard;
    }

    public int getDeckSize() {
        return deck.size();
    }

    public int getDiscardSize() {
        return discard.size();
    }

    public CardList getNativeVillage() {
        return nativeVillage;
    }

    public CardList getIsland() {
        return island;
    }

    public CardList getTavern() {
        return tavern;
    }

    public int getPirateShipTreasure() {
        return pirateShipTreasure;
    }
    
    public int getGuildsCoinTokenCount()
    {
        return guildsCoinTokenCount;
    }

	public boolean getMinusCardToken(){
		return minusCardToken;
	}
    
	public boolean getMinusCoinToken(){
		return minusCoinToken;
	}
    
	public boolean getJourneyToken(){
		return journeyToken;
	}
    
    public void gainGuildsCoinTokens(int tokenCount)
    {
        guildsCoinTokenCount += tokenCount;
    }
    
    public void spendGuildsCoinTokens(int tokenCount)
    {
        if (tokenCount <= guildsCoinTokenCount)
        {
            guildsCoinTokenCount -= tokenCount;
        }
        else
        {
            Util.playerError(this, "spendGuildsCoinTokens() - Can't spend " + tokenCount + " coin tokens, only have " + guildsCoinTokenCount);
        }
    }

    public int getVictoryTokens() {
		return victoryTokens;
    }

	public int getDebtTokens() {
		return debtTokens;
	}

	public void resolveDebt(MoveContext context) {
		int paid = controlPlayer.payDebt(context,debtTokens);
		context.gold -= paid;
		debtTokens -= paid;
	}

	public int getAllCardCount() {
        return this.getAllCards().size();
	}

    public ArrayList<Card> getAllCards() {
        ArrayList<Card> allCards = new ArrayList<Card>();
        for (Card card : playedCards) {
            allCards.add(card);
        }
        for (Card card : hand) {
            allCards.add(card);
        }
        for (Card card : discard) {
            allCards.add(card);
        }
        for (Card card : deck) {
            allCards.add(card);
        }
        for (Card card : nextTurnCards) {
            allCards.add(card);
        }
        for (Card card : nativeVillage) {
            allCards.add(card);
        }
        for (Card card : haven) {
            allCards.add(card);
        }
        for (Card card : island) {
            allCards.add(card);
        }
        for (Card card : horseTraders) {
            allCards.add(card);
        }
		for (Card card: tavern) {
			allCards.add(card);
		}
		for (Card card: gear) {
			allCards.add(card);
		}
		for (Card card: encampments) {
			allCards.add(card);
		}
		for (ArrayList<Card> list : archive) {
			for (Card card : list)
				allCards.add(card);
		}
		for (ArrayList<Card> list : crypt) {
			for (Card card : list)
				allCards.add(card);
		}
		for (Card card: ghost) {
			allCards.add(card);
		}
		for (Card card: summon) {
			allCards.add(card);
		}
		for (Card card: prince) {
			allCards.add(card);
		}
		if (estateToken != null)
			allCards.add(estateToken);
        if (checkLeadCard != null) {
        	allCards.add(checkLeadCard);
        }
        return allCards;
    }

	public Map<Object, Integer> getVictoryCardCounts() {
		final HashSet<String> distinctCards = new HashSet<String>();
		final Map<Object, Integer> cardCounts = new HashMap<Object, Integer>();

		// seed counts with all victory cards in play
		for (AbstractCardPile pile : this.game.piles.values()) {
			Card card = pile.card();

			if(card instanceof VictoryCard || card instanceof CurseCard) {
				cardCounts.put(card, 0);
			}
		}

		for(Card card : this.getAllCards()) {
			distinctCards.add(card.getName());
			if (card instanceof VictoryCard || card instanceof CurseCard) {
				if(cardCounts.containsKey(card)) {
					cardCounts.put(card, cardCounts.get(card) + 1);
				} else {
					cardCounts.put(card, 1);
				}
			}
		}

		cardCounts.put(DISTINCT_CARDS, distinctCards.size());

		int numDistantLands=0;
		for (Card card : this.tavern) {
			if (card.getType() == Cards.Type.DistantLands)
				numDistantLands++;
		}
		cardCounts.put(DISTANT_LANDS_ON_TAVERN, numDistantLands);
		for (Object obj : cardCounts.keySet()) {
			if (obj instanceof Card) {
				Card card = (Card) obj;
				if (card.getType() == Cards.Type.DistantLands)
					cardCounts.put(card,numDistantLands);
			}
		}

		int numCastles=0;
		for (Card card : this.getAllCards())
			if (card.isCastle())
				numCastles++;
		cardCounts.put(CASTLES, numCastles);

		if (twiceMiserable)
			cardCounts.put(MISERABLE, 2);
		else if (miserable)
			cardCounts.put(MISERABLE, 1);

		return cardCounts;
	}

	public Map<Object, Integer> getAllCardCounts() {
		final HashSet<String> distinctCards = new HashSet<String>();
		final Map<Object, Integer> cardCounts = new HashMap<Object, Integer>();

		for(Card card : this.getAllCards()) {
			distinctCards.add(card.getName());
			if(cardCounts.containsKey(card)) {
				cardCounts.put(card, cardCounts.get(card) + 1);
			} else {
				cardCounts.put(card, 1);
			}
		}

		cardCounts.put(DISTINCT_CARDS, distinctCards.size());
		return cardCounts;
	}

    public int getCardCount(final Class<?> cardClass) {
        return this.getCardCount(cardClass, getAllCards());
	}

    public int getCardCount(final Class<?> cardClass, ArrayList<Card> cards) {
        int cardCount = 0;

        for (Card card : cards) {
            if (cardClass.isInstance(card)) {
                cardCount++;
            }
        }

        return cardCount;
    }

	public int getActionCardCount() {
        return this.getCardCount(ActionCard.class);
	}

    public int getActionCardCount(ArrayList<Card> cards) {
        return this.getCardCount(ActionCard.class, cards);
    }

	public int getVictoryCardCount() {
		return this.getCardCount(VictoryCard.class);
	}

    public int getDistinctCardCount() {
    	return getDistinctCardCount(null);
    }

    public int getDistinctCardCount(ArrayList<Card> cards) {
    	if (cards==null) cards = this.getAllCards();
//        int cardCount = 0;

		final HashSet<String> distinctCards = new HashSet<String>();
		for(Card card : cards) {
			distinctCards.add(card.getName());
		}

        return distinctCards.size();
    }

    public int calculateLead(Card card) {
    	checkLeadCard = card;
        int lead = getVPs();
    	checkLeadCard = null;
        return lead;
    }

    public int getVPs() {
		return getVPs(null);
	}

	public int getVPs(Map<Object, Integer> totals) {
		if (totals==null) totals = this.getVictoryPointTotals();
		int vp = 0;
		for(Integer total : totals.values())
			vp += total;
		return vp;
	}

	public Map<Object, Integer> getVictoryPointTotals() {
		return getVictoryPointTotals(null);
	}

	public Map<Object, Integer> getVictoryPointTotals(Map<Object, Integer> counts) {
		if (counts == null) counts = this.getVictoryCardCounts();
		Map<Object, Integer> totals = new HashMap<Object, Integer>();

		ArrayList<Object> toRemove = new ArrayList<Object>();
		for (Object o : counts.keySet()) {
			if (counts.get(o)==0)
				toRemove.add(o);
		}
		for (Object o : toRemove)
			counts.remove(o);

		for(Map.Entry<Object, Integer> entry : counts.entrySet()) {
			if(entry.getKey() instanceof VictoryCard) {
				VictoryCard victoryCard = (VictoryCard) entry.getKey();
				totals.put(victoryCard, victoryCard.getVictoryPoints() * entry.getValue());
			} else if(entry.getKey() instanceof CurseCard) {
				CurseCard curseCard = (CurseCard) entry.getKey();
				totals.put(curseCard, curseCard.getVictoryPoints() * entry.getValue());
			}
		}

		if(counts.containsKey(Cards.gardens))
			totals.put(Cards.gardens, counts.get(Cards.gardens) * (this.getAllCards().size() / 10));
		if(counts.containsKey(Cards.duke))
			totals.put(Cards.duke, counts.containsKey(Cards.duchy) ? counts.get(Cards.duke) * counts.get(Cards.duchy) : 0);
		if(counts.containsKey(Cards.fairgrounds))
			totals.put(Cards.fairgrounds, counts.get(Cards.fairgrounds) * ((counts.get(DISTINCT_CARDS) / 5) * 2));
		if(counts.containsKey(Cards.vineyard))
			totals.put(Cards.vineyard, counts.get(Cards.vineyard) * (this.getActionCardCount() / 3));
		if(counts.containsKey(Cards.silkRoad))
			totals.put(Cards.silkRoad, counts.get(Cards.silkRoad) * (this.getVictoryCardCount() / 4));
		if(counts.containsKey(Cards.feodum))
			totals.put(Cards.feodum, counts.get(Cards.feodum) * (Util.getCardCount(getAllCards(), Cards.silver)  / 3));
		if (counts.containsKey(Cards.distantLands))
			totals.put(Cards.distantLands, counts.get(DISTANT_LANDS_ON_TAVERN) * 4);
		if (counts.containsKey(Cards.humbleCastle))
			totals.put(Cards.humbleCastle, counts.get(Cards.humbleCastle) * counts.get(CASTLES));
		if (counts.containsKey(Cards.kingsCastle))
			totals.put(Cards.kingsCastle, counts.get(Cards.kingsCastle) * counts.get(CASTLES) * 2);

		if(counts.containsKey(Cards.pasture))
			totals.put(Cards.pasture, counts.containsKey(Cards.estate) ? counts.get(Cards.pasture) * counts.get(Cards.estate) : 0);
		if (counts.containsKey(MISERABLE))
			totals.put(MISERABLE, counts.get(MISERABLE) * -2);

		for (Landmarks landmark : game.getLandmarksInGame()) {
			totals.put(landmark, landmark.getVPs(game,this,getAllCards()));
		}

		totals.put(Cards.victoryTokens, this.getVictoryTokens());

		return totals;
	}

    public Card peekAtDeckBottom() {
        return deck.get(deck.size() - 1);
    }

    public void removeFromDeckBottom() {
        deck.remove(deck.size() - 1);
    }

    public void putOnTopOfDeck(Card card, MoveContext context, boolean UI) {
        putOnTopOfDeck(card);
        if (UI) {
            GameEvent event = new GameEvent(GameEvent.Type.CardOnTopOfDeck, context);
            event.card = card;
            game.broadcastEvent(event);
        }
    }

    public void putOnTopOfDeck(Card card) {
        deck.add(0, card);
    }

    public void replenishDeck() {
        shuffleCount++;
		ArrayList<Card> stashes = new ArrayList<Card>();
        while (discard.size() > 0) {
			Card card = discard.remove(Game.rand.nextInt(discard.size()));
			if (card.equals(Cards.stash))
				stashes.add(card);
			else
				deck.add(card);
        }
		for (Card card : stashes) {
			int position = stash_positionInDeck(new MoveContext(game,this),deck.size());
			deck.add(position,card);
		}
    }

    public void shuffleDeck() {
        ArrayList<Card> tempDeck = new ArrayList<Card>();
        while (deck.size() > 0) {
            tempDeck.add(deck.remove(Game.rand.nextInt(deck.size())));
        }
        for(Card c : tempDeck) {
            deck.add(c);
        }
    }

    public void checkCardsValid() {
        hand.checkValid();
        discard.checkValid();
        deck.checkValid();
    }

//	protected void discardRemainingCardsFromHand(MoveContext context, Card[] cardsToKeep) {
//		discardRemainingCardsFromHand(context, cardsToKeep, null, -1);
//	}
	protected void discardRemainingCardsFromHand(MoveContext context, Card[] cardsToKeep, Card responsibleCard, int keepCardCount) {
		ArrayList<Card> keepCards = new ArrayList<Card>(Arrays.asList(cardsToKeep));

		if (keepCardCount > 0) {
	        boolean bad = false;
	        if (cardsToKeep == null || cardsToKeep.length != keepCardCount) {
	            bad = true;
	        } else {
	            ArrayList<Card> handCopy = Util.copy(hand);
	            for (Card cardToKeep : cardsToKeep) {
	                if (!handCopy.remove(cardToKeep)) {
	                    bad = true;
	                    break;
	                }
	            }
	        }
	
	        if (bad) {
	            Util.playerError(this, responsibleCard.getName() + " discard error, just keeping first " + keepCardCount);
	            cardsToKeep = new Card[keepCardCount];
	            for (int i = 0; i < keepCardCount; i++) {
	            	cardsToKeep[i] = hand.get(i);
				}
	        }
	
			
		}
		
        // Discard remaining cards
		for (int i = hand.size(); i > 0; ) {
			Card card = hand.get(--i);
			if (keepCards.contains(card)) {
		        keepCards.remove(card);
			} else {
		        hand.remove(i, false);
		        discard(card, responsibleCard, context);
			}
		}
	}
	
    public void discard(Card card, Card responsible, MoveContext context) {
        discard(card, responsible, context, true);
    }

    // TODO make similar way to put cards back on the deck (remove as well?)
    public void discard(Card card, Card responsible, MoveContext context, boolean commandedDiscard) { // See rules explanation of Tunnel for what commandedDiscard means.
        if(commandedDiscard && card.equals(Cards.tunnel)) {

        	MoveContext tunnelContext = new MoveContext(game, this);
        	
            if(game.pileSize(Cards.gold) > 0 && controlPlayer.tunnel_shouldReveal(tunnelContext)) {
                reveal(card, card, tunnelContext);
                gainNewCard(Cards.gold, card, tunnelContext);
            }
        }
        
        if (card.behaveAsCard().equals(Cards.hermit)) {        	
        	if (!commandedDiscard && 
        	    (context != null) && 
        	    (context.totalCardsBoughtThisTurn == 0))
		    {
		    	trash(card, card, context);
		    	controlPlayer.gainNewCard(Cards.madman, card, context);
		    }
    		else
    	    {
                discard.add(card);
    	    }
    	} else if (card.behaveAsCard().isTraveller() && !commandedDiscard && context!=null) {
			Card source = card.behaveAsCard();
			Card target = null;
			if (source.equals(Cards.page)) target = Cards.treasureHunter;
			else if (source.equals(Cards.peasant)) target = Cards.soldier;
			else if (source.equals(Cards.treasureHunter)) target = Cards.warrior;
			else if (source.equals(Cards.soldier)) target = Cards.fugitive;
			else if (source.equals(Cards.warrior)) target = Cards.hero;
			else if (source.equals(Cards.fugitive)) target = Cards.disciple;
			else if (source.equals(Cards.hero)) target = Cards.champion;
			else if (source.equals(Cards.disciple)) target = Cards.teacher;
			AbstractCardPile sourcePile = game.getPile(card);
			AbstractCardPile targetPile = game.getPile(target);
			if (target != null && !targetPile.isEmpty() && controlPlayer.shouldExchangeTraveller(context,source,target)) {
				sourcePile.addCard(card);
				controlPlayer.gainNewCard(target,card,context);
			} else discard.add(card);
		} else if (commandedDiscard && card.equals(Cards.faithfulHound)) {
			faithfulHound.add(card);
		} else
	    {
	    	discard.add(card);
	    }

    	// card left play - stop impersonations
    	((CardImpl) card).stopImpersonatingCard();
    	        
        // XXX making game slow; is this necessary?  For that matter, are discarded cards public?
        if(context != null && commandedDiscard) {
            GameEvent event = new GameEvent(GameEvent.Type.CardDiscarded, context);
            event.card = card;
            event.responsible = responsible;
            event.setPlayer(this);
            context.game.broadcastEvent(event);
        }
    }

    public boolean gainNewCard(Card cardToGain, Card responsible, MoveContext context) {
        Card card = game.takeFromPileCheckTrader(cardToGain, context);
        if (card != null) {
            GameEvent gainEvent = new GameEvent(GameEvent.Type.CardObtained, (MoveContext) context);
            gainEvent.card = card;
            gainEvent.responsible = responsible;
            gainEvent.newCard = true;

            // Check if Trader swapped the card, so it can be made responsible, putting the card in the discard
            // pile rather than were it would go otherwise (according to faq)
            if(!cardToGain.equals(card) && card.equals(Cards.silver)) {
                gainEvent.responsible = Cards.trader;
            }

            context.game.broadcastEvent(gainEvent);
            
            // invoke different actions on gain
            //cardToGain.isGained(context);
			if (cardToGain.equals(Cards.villa))
				context.getPlayer().villaGained = true;
            
            return true;
        }
        return false;
    }

    public boolean gainNewCardFromPile(AbstractCardPile pile, Card responsible, MoveContext context) {
    	switch (pile.type) {
    	case SingleCardPile:
    		return gainNewCard(pile.card(), responsible, context);
    	case RuinsPile:
    		return gainNewCard(game.getTopRuinsCard(), responsible, context);
		case KnightsPile:
			return gainNewCard(game.getTopKnightCard(), responsible, context);
		default:
			break;
    	}
		return false;
    }

    public void gainCardAlreadyInPlay(Card card, Card responsible, MoveContext context) {
        if (context != null) {
            GameEvent event = new GameEvent(GameEvent.Type.CardObtained, context);
            event.card = card;
            event.responsible = responsible;
            event.newCard = false;
            context.game.broadcastEvent(event);
        }
    }

    public void broadcastEvent(GameEvent event) {
        game.broadcastEvent(event);
    }

    public Card takeFromPile(Card card) {
        return game.takeFromPile(card);
    }

    public void trash(Card card, Card responsible, MoveContext context) {
        if(context != null) {
            // TODO: Track in main game event listener instead
            context.cardsTrashedThisTurn++;
        }

        GameEvent event = new GameEvent(GameEvent.Type.CardTrashed, context);
        event.card = card;
        event.responsible = responsible;
        context.game.broadcastEvent(event);

        // Add to trash pile
    	if (isPossessed()) {
            context.game.possessedTrashPile.add(card);
		} else if (card.getTemplateCard()==Cards.inheritedEstate) {
			context.game.trashPile.add(Cards.estate.getTemplateCard().instantiate());
        } else {
            context.game.trashPile.add(card);
        }

        // Execute special card logic when the trashing occurs
        card.isTrashed(context);

        // Market Square trashing reaction
        if (Util.getCardCount(hand, Cards.marketSquare) > 0) {
        	ArrayList<Card> marketSquaresInHand = new ArrayList<Card>();
        	
        	for (Card c : hand) {
        		if (c.getType() == Cards.Type.MarketSquare) {
        			marketSquaresInHand.add(c);
        		}
        	}
        	
        	for (Card c : marketSquaresInHand) {
        		if (controlPlayer.marketSquare_shouldDiscard(context)) {
            		hand.remove(c);
            		discard(c, card, context);
            		gainNewCard(Cards.gold, c, context);
            	}
        	}
        }

		if (game.landmarksList.contains(Landmarks.tomb))
			addVictoryTokens(context,1);

    }

    public abstract HuntingGroundsOption huntingGrounds_chooseOption(MoveContext context);

	public abstract Card catacombs_cardToObtain(MoveContext context);

    public void namedCard(Card card, Card responsible, MoveContext context) {
        GameEvent event = new GameEvent(GameEvent.Type.CardNamed, context);
        event.card = card;
        event.responsible = responsible;
        context.game.broadcastEvent(event);
    }

    public void revealFromHand(Card card, Card responsible, MoveContext context) {
        GameEvent event = new GameEvent(GameEvent.Type.CardRevealedFromHand, context);
        event.card = card;
        event.responsible = responsible;
        context.game.broadcastEvent(event);
    }

    public void reveal(Card card, Card responsible, MoveContext context) {
        GameEvent event = new GameEvent(GameEvent.Type.CardRevealed, context);
        event.card = card;
        event.responsible = responsible;
        context.game.broadcastEvent(event);
    }

    public void attacked(Card card, MoveContext context) {
        context.attackedPlayer = this;
        GameEvent event = new GameEvent(GameEvent.Type.PlayerAttacking, context);
        event.attackedPlayer = this;
        event.card = card.behaveAsCard();
        context.game.broadcastEvent(event);
    }

    public static enum NoblesOption {
        AddCards,
        AddActions
    }

    public static enum TorturerOption {
        TakeCurse,
        DiscardTwoCards
    }

    public static enum MinionOption {
        AddGold,
        RolloverCards
    }

    public static enum PawnOption {
        AddCard,
        AddAction,
        AddBuy,
        AddGold
    }

    public static enum StewardOption {
        AddCards,
        AddGold,
        TrashCards
    }

    public static enum WatchTowerOption {
        TopOfDeck,
        Trash,
        Normal
    }

    public static enum JesterOption {
        GainCopy,
        GiveCopy
    }

    public static enum TournamentOption {
        GainPrize,
        GainDuchy
    }

    public static enum TrustySteedOption {
        AddCards,
        AddActions,
        AddGold,
        GainSilvers
    }

    public static enum SpiceMerchantOption {
        AddCardsAndAction,
        AddGoldAndBuy
    }

    public static enum PutBackOption {
    	Treasury,
    	Alchemist,
    	WalledVillage,
    	Coin,
    	Action,
    	None
    }

    public static enum SquireOption {
    	AddActions,
    	AddBuys,
    	GainSilver
    }

    public enum CountFirstOption {
    	Discard,
    	PutOnDeck,
    	GainCopper
	}

    public enum CountSecondOption {
    	Coins,
    	TrashHand,
    	GainDuchy
	}

	public enum GraverobberOption {
		GainFromTrash,
		TrashActionCard
	}

	public enum HuntingGroundsOption {
		GainDuchy, GainEstates
	}

	public enum GovernorOption {
		AddCards,
		GainTreasure,
		Upgrade
	}
	
	public enum DoctorOverpayOption
	{
	    TrashIt,
	    DiscardIt,
	    PutItBack
	}

	public enum AmuletOption {
		AddGold,
		TrashCard,
		GainSilver
	}

	public enum QuestOption {
		Attack,
		Curses,
		Cards
	}

	public enum TeacherOption {
		PlusCard,
		PlusAction,
		PlusCoin,
		PlusBuy
	}

	public enum WildHuntOption {
		AddCards, GainEstate
	}

	public enum CharmOption {
		AddCoin, GainCard
	}

	public enum SentryOption {
	    TrashIt,
	    DiscardIt,
	    KeepIt
	}

	public enum LurkerOption {
		GainFromTrash,
		TrashActionCard
	}

    public static enum CourtierOption {
        AddAction,
        AddBuy,
        AddGold,
        GainGold
    }

	public static enum MonasteryOption {
		TrashFromHand,
		TrashCopper,
		None
	}

    // Context is passed for the player to add a GameEventListener
    // if they want or to see what cards the game has, etc.
    public void newGame(MoveContext context) {
    }

    public ArrayList<TreasureCard> getTreasuresInHand() {
    	ArrayList<TreasureCard> treasures = new ArrayList<TreasureCard>();

    	for (Card c : getHand())
    		if (c instanceof TreasureCard)
    			treasures.add((TreasureCard) c);

    	return treasures;
    }

    public ArrayList<VictoryCard> getVictoryInHand() {
    	ArrayList<VictoryCard> victory = new ArrayList<VictoryCard>();

    	for (Card c : getHand())
    		if (c instanceof VictoryCard)
    			victory.add((VictoryCard) c);

    	return victory;
    }

    public ArrayList<ActionCard> getActionsInHand() {
        ArrayList<ActionCard> actions = new ArrayList<ActionCard>();

        for (Card c : getHand())
            if (c instanceof ActionCard)
                actions.add((ActionCard) c);

        return actions;
    }

    public abstract String getPlayerName();
    public abstract String getPlayerName(boolean maskName);

    public abstract Card doAction(MoveContext context);

    public abstract Card doNight(MoveContext context);

    public abstract Card[] actionCardsToPlayInOrder(MoveContext context);

    public abstract Card[] nightCardsToPlayInOrder(MoveContext context);

    public abstract Object doBuy(MoveContext context);

    public abstract Card[] topOfDeck_orderCards(MoveContext context, Card[] cards);

    // ////////////////////////////////////////////
    // Card interactions - cards from the base game
    // ////////////////////////////////////////////
    public abstract Card workshop_cardToObtain(MoveContext context);

    public abstract Card feast_cardToObtain(MoveContext context);

    public abstract Card remodel_cardToTrash(MoveContext context);

    public abstract Card remodel_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt);

    public abstract Card[] militia_attack_cardsToKeep(MoveContext context);

    public abstract TreasureCard thief_treasureToTrash(MoveContext context, TreasureCard[] treasures);

    public abstract TreasureCard[] thief_treasuresToGain(MoveContext context, TreasureCard[] treasures);

    public abstract boolean chancellor_shouldDiscardDeck(MoveContext context);

    public abstract TreasureCard mine_treasureFromHandToUpgrade(MoveContext context);

    public abstract TreasureCard mine_treasureToObtain(MoveContext context, int maxCost, boolean potion,int debt);

    public abstract Card[] chapel_cardsToTrash(MoveContext context);

    public abstract Card[] cellar_cardsToDiscard(MoveContext context);

    public abstract boolean library_shouldKeepAction(MoveContext context, ActionCard action);

    public abstract boolean spy_shouldDiscard(MoveContext context, Player targetPlayer, Card card);

    public abstract VictoryCard bureaucrat_cardToReplace(MoveContext context);

    // ////////////////////////////////////////////
    // Card interactions - cards from Intrigue
    // ////////////////////////////////////////////
    public abstract Card[] secretChamber_cardsToDiscard(MoveContext context);

    public abstract PawnOption[] pawn_chooseOptions(MoveContext context);

    public abstract TorturerOption torturer_attack_chooseOption(MoveContext context);

    public abstract StewardOption steward_chooseOption(MoveContext context);

    public abstract Card swindler_cardToSwitch(MoveContext context, int cost, boolean potion,int debt);

    public abstract Card[] steward_cardsToTrash(MoveContext context);

    public abstract Card[] torturer_attack_cardsToDiscard(MoveContext context);

    public abstract Card courtyard_cardToPutBackOnDeck(MoveContext context);

    public abstract boolean baron_shouldDiscardEstate(MoveContext context);

    public abstract Card ironworks_cardToObtain(MoveContext context);

    public abstract Card masquerade_cardToPass(MoveContext context);

    public abstract Card masquerade_cardToTrash(MoveContext context);

    public abstract boolean miningVillage_shouldTrashMiningVillage(MoveContext context);

    public abstract Card saboteur_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt);

    public abstract Card[] scout_orderCards(MoveContext context, Card[] cards);

    public abstract Card[] mandarin_orderCards(MoveContext context, Card[] cards);

    public abstract NoblesOption nobles_chooseOptions(MoveContext context);

    // Either return two cards, or null if you do not want to trash any cards.
    public abstract Card[] tradingPost_cardsToTrash(MoveContext context);

    public abstract Card wishingWell_cardGuess(MoveContext context, ArrayList<Card> cardList);

    public abstract Card upgrade_cardToTrash(MoveContext context);

    public abstract Card upgrade_cardToObtain(MoveContext context, int exactCost, boolean potion,int debt);

    public abstract MinionOption minion_chooseOption(MoveContext context);

    public abstract Card[] secretChamber_cardsToPutOnDeck(MoveContext context);

    // ////////////////////////////////////////////
    // Card interactions - cards from Seaside
    // ////////////////////////////////////////////
    public abstract Card[] ghostShip_attack_cardsToPutBackOnDeck(MoveContext context);

    public abstract Card salvager_cardToTrash(MoveContext context);

    public abstract Card[] warehouse_cardsToDiscard(MoveContext context);

    public abstract boolean pirateShip_takeTreasure(MoveContext context);

    public abstract TreasureCard pirateShip_treasureToTrash(MoveContext context, TreasureCard[] treasures);

    public abstract boolean nativeVillage_takeCards(MoveContext context);

    public abstract Card smugglers_cardToObtain(MoveContext context);

    public abstract Card island_cardToSetAside(MoveContext context);

    public abstract Card haven_cardToSetAside(MoveContext context);

    public abstract boolean navigator_shouldDiscardTopCards(MoveContext context, Card[] cards);

    public abstract Card[] navigator_cardOrder(MoveContext context, Card[] cards);

    public abstract Card embargo_supplyToEmbargo(MoveContext context);

    // Will be passed all three cards
    public abstract Card lookout_cardToTrash(MoveContext context, Card[] cards);

    // Will be passed the two cards leftover after trashing one
    public abstract Card lookout_cardToDiscard(MoveContext context, Card[] cards);

    public abstract Card ambassador_revealedCard(MoveContext context);

    public abstract int ambassador_returnToSupplyFromHand(MoveContext context, Card card);

    public abstract boolean pearlDiver_shouldMoveToTop(MoveContext context, Card card);

    public abstract boolean explorer_shouldRevealProvince(MoveContext context);

    // ////////////////////////////////////////////
    // Card interactions - cards from Alchemy
    // ////////////////////////////////////////////

    public abstract Card transmute_cardToTrash(MoveContext context);

    public abstract ArrayList<Card> apothecary_cardsForDeck(MoveContext context, ArrayList<Card> cards);

    public abstract boolean alchemist_backOnDeck(MoveContext context);

    public abstract TreasureCard herbalist_backOnDeck(MoveContext context, TreasureCard[] cards);

    public abstract Card apprentice_cardToTrash(MoveContext context);

    public abstract ActionCard university_actionCardToObtain(MoveContext context);

    public abstract boolean scryingPool_shouldDiscard(MoveContext context, Player targetPlayer, Card card);

    public abstract ActionCard[] golem_cardOrder(MoveContext context, ActionCard[] cards);

    // ////////////////////////////////////////////
    // Card interactions - cards from Prosperity
    // ////////////////////////////////////////////
    public abstract Card bishop_cardToTrashForVictoryTokens(MoveContext context);

    public abstract Card bishop_cardToTrash(MoveContext context);

    public abstract Card contraband_cardPlayerCantBuy(MoveContext context);

    public abstract Card expand_cardToTrash(MoveContext context);

    public abstract Card expand_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt);

    public abstract Card[] forge_cardsToTrash(MoveContext context);

    public abstract Card forge_cardToObtain(MoveContext context, int exactCost);

    public abstract Card[] goons_attack_cardsToKeep(MoveContext context);

    public abstract ActionCard kingsCourt_cardToPlay(MoveContext context);

    public abstract ActionCard throneRoom_cardToPlay(MoveContext context);

    public abstract boolean loan_shouldTrashTreasure(MoveContext context, TreasureCard treasure);

    public abstract TreasureCard mint_treasureToMint(MoveContext context);

    public abstract boolean mountebank_attack_shouldDiscardCurse(MoveContext context);

    public abstract Card[] rabble_attack_cardOrder(MoveContext context, Card[] cards);

    public abstract boolean royalSeal_shouldPutCardOnDeck(MoveContext context, Card card);

    public abstract Card tradeRoute_cardToTrash(MoveContext context);

    public abstract Card[] vault_cardsToDiscardForGold(MoveContext context);

    public abstract Card[] vault_cardsToDiscardForCard(MoveContext context);

    public abstract WatchTowerOption watchTower_chooseOption(MoveContext context, Card card);

    public abstract ArrayList<TreasureCard> treasureCardsToPlayInOrder(MoveContext context);

    // ////////////////////////////////////////////
    // Card interactions - cards from Cornucopia
    // ////////////////////////////////////////////
    public abstract Card hamlet_cardToDiscardForAction(MoveContext context);

    public abstract Card hamlet_cardToDiscardForBuy(MoveContext context);

    public abstract Card hornOfPlenty_cardToObtain(MoveContext context, int maxCost);

    public abstract Card[] horseTraders_cardsToDiscard(MoveContext context);

    public abstract JesterOption jester_chooseOption(MoveContext context, Player targetPlayer, Card card);

    public abstract Card remake_cardToTrash(MoveContext context);

    public abstract Card remake_cardToObtain(MoveContext context, int exactCost, boolean potion,int debt);

    public abstract boolean tournament_shouldRevealProvince(MoveContext context);

    public abstract TournamentOption tournament_chooseOption(MoveContext context);

    public abstract Card tournament_choosePrize(MoveContext context);

    public abstract Card[] youngWitch_cardsToDiscard(MoveContext context);

    public abstract Card[] followers_attack_cardsToKeep(MoveContext context);

    public abstract TrustySteedOption[] trustySteed_chooseOptions(MoveContext context);

    // ////////////////////////////////////////////
    // Card interactions - cards from Hinterlands
    // ////////////////////////////////////////////
    public abstract Card borderVillage_cardToObtain(MoveContext context);

    public abstract Card farmland_cardToTrash(MoveContext context);

    public abstract Card farmland_cardToObtain(MoveContext context, int cost, boolean potion,int debt);

    public abstract TreasureCard stables_treasureToDiscard(MoveContext context);

    public abstract boolean duchess_shouldDiscardCardFromTopOfDeck(MoveContext context, Card card);

    public abstract boolean duchess_shouldGainBecauseOfDuchy(MoveContext context);

    public abstract Card develop_cardToTrash(MoveContext context);

    public abstract Card develop_lowCardToGain(MoveContext context, int cost, boolean potion,int debt);

    public abstract Card develop_highCardToGain(MoveContext context, int cost, boolean potion,int debt);

    public abstract Card[] develop_orderCards(MoveContext context, Card[] cards);

    public abstract Card oasis_cardToDiscard(MoveContext context);

    public abstract boolean foolsGold_shouldTrash(MoveContext context);

    public abstract TreasureCard nobleBrigand_silverOrGoldToTrash(MoveContext context, TreasureCard[] silverOrGoldCards);

    public abstract boolean jackOfAllTrades_shouldDiscardCardFromTopOfDeck(MoveContext context, Card card);

    public abstract Card jackOfAllTrades_nonTreasureToTrash(MoveContext context);

    public abstract TreasureCard spiceMerchant_treasureToTrash(MoveContext context);

    public abstract SpiceMerchantOption spiceMerchant_chooseOption(MoveContext context);

    public abstract Card[] embassy_cardsToDiscard(MoveContext context);

    public abstract Card[] cartographer_cardsFromTopOfDeckToDiscard(MoveContext context, Card[] cards);

    public abstract Card[] cartographer_cardOrder(MoveContext context, Card[] cards);

    public abstract ActionCard scheme_actionToPutOnTopOfDeck(MoveContext context, ActionCard[] actions);

    public abstract boolean tunnel_shouldReveal(MoveContext context);

    public abstract boolean trader_shouldGainSilverInstead(MoveContext context, Card card);

    public abstract Card trader_cardToTrash(MoveContext context);

    public abstract boolean oracle_shouldDiscard(MoveContext context, Player player, ArrayList<Card> cards);

    public abstract Card[] oracle_orderCards(MoveContext context, Card[] cards);

    public abstract boolean illGottenGains_gainCopper(MoveContext context);

    public abstract Card haggler_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt);

    public abstract Card[] inn_cardsToDiscard(MoveContext context);

    public abstract boolean inn_shuffleCardBackIntoDeck(MoveContext context, ActionCard card);

    public abstract Card mandarin_cardToReplace(MoveContext context);

    public abstract Card[] margrave_attack_cardsToKeep(MoveContext context);

	public abstract Card getAttackReaction(MoveContext context, Card responsible, boolean defended, Card lastCard);

	public abstract boolean revealBane(MoveContext context);

	public abstract PutBackOption selectPutBackOption(MoveContext context, List<PutBackOption> options);

    // ////////////////////////////////////////////
    // Card interactions - cards from Dark Ages
    // ////////////////////////////////////////////
    public abstract Card rats_cardToTrash(MoveContext context);

    public abstract SquireOption squire_chooseOption(MoveContext context);

	public abstract Card altar_cardToTrash(MoveContext context);

	public abstract Card altar_cardToObtain(MoveContext context);

	public abstract boolean beggar_shouldDiscard(MoveContext context);

	public abstract Card armory_cardToObtain(MoveContext context);

	public abstract Card squire_cardToObtain(MoveContext context);

	public abstract boolean catacombs_shouldDiscardTopCards(MoveContext context, Card[] array);

	public abstract CountFirstOption count_chooseFirstOption(MoveContext context);

	public abstract CountSecondOption count_chooseSecondOption(MoveContext context);

	public abstract Card[] count_cardsToDiscard(MoveContext context);

	public abstract Card count_cardToPutBackOnDeck(MoveContext context);

	public abstract Card forager_cardToTrash(MoveContext context);

	public abstract GraverobberOption graverobber_chooseOption(MoveContext context);

	public abstract Card graverobber_cardToGainFromTrash(MoveContext context);

	public abstract Card graverobber_cardToTrash(MoveContext context);

	public abstract Card graverobber_cardToReplace(MoveContext context, int maxCost, boolean potion,int debt);

	public abstract boolean ironmonger_shouldDiscard(MoveContext context, Card card);

	public abstract Card junkDealer_cardToTrash(MoveContext context);

	public abstract boolean marketSquare_shouldDiscard(MoveContext context);

	public abstract Card mystic_cardGuess(MoveContext context, ArrayList<Card> cardList);

	public abstract boolean scavenger_shouldDiscardDeck(MoveContext context);

	public abstract Card scavenger_cardToPutBackOnDeck(MoveContext context);

	public abstract Card[] storeroom_cardsToDiscardForCards(MoveContext context);

	public abstract Card[] storeroom_cardsToDiscardForCoins(MoveContext context);

	public abstract ActionCard procession_cardToPlay(MoveContext context);

	public abstract Card procession_cardToGain(MoveContext context, int maxCost, boolean potion,int debt);

	public abstract Card rebuild_cardToPick(MoveContext context);

	public abstract Card rebuild_cardToGain(MoveContext context, int maxCost, boolean costPotion);

	public abstract Card rogue_cardToGain(MoveContext context);

	public abstract Card rogue_cardToTrash(MoveContext context, ArrayList<Card> canTrash);

	public abstract TreasureCard counterfeit_cardToPlay(MoveContext context);

	public abstract Card pillage_opponentCardToDiscard(MoveContext context, ArrayList<Card> handCards);

	public abstract boolean hovel_shouldTrash(MoveContext context);

	public abstract Card deathCart_actionToTrash(MoveContext context);
	
	public abstract Card[] urchin_attack_cardsToKeep(MoveContext context);
	
	public abstract boolean urchin_shouldTrashForMercenary(MoveContext context);
	
	public abstract Card[] mercenary_cardsToTrash(MoveContext context);
    public abstract Card[] mercenary_attack_cardsToKeep(MoveContext context);

	public abstract boolean madman_shouldReturnToPile(MoveContext context);
	
	public abstract Card hermit_cardToTrash(MoveContext context, ArrayList<Card> cardList, int nonTreasureCountInDiscard);
	public abstract Card hermit_cardToGain(MoveContext context);

	public abstract ActionCard bandOfMisfits_actionCardToImpersonate(MoveContext context);

	// ////////////////////////////////////////////
    // Card interactions - Guilds Expansion
    // ////////////////////////////////////////////
	public abstract int numGuildsCoinTokensToSpend(MoveContext context);
	public abstract int amountToOverpay(MoveContext context, int cardCost);
	public abstract int overpayByPotions(MoveContext context, int availablePotions);
	public abstract TreasureCard taxman_treasureToTrash(MoveContext context);
	public abstract TreasureCard taxman_treasureToObtain(MoveContext context, int maxCost);
	public abstract TreasureCard plaza_treasureToDiscard(MoveContext context);
	public abstract Card butcher_cardToTrash(MoveContext context);
	public abstract Card butcher_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt);
	public abstract Card advisor_cardToDiscard(MoveContext context, Card[] cards);
	public abstract Card journeyman_cardToPick(MoveContext context);
	public abstract Card stonemason_cardToTrash(MoveContext context);
	public abstract Card stonemason_cardToGain(MoveContext context, int maxCost, boolean potion,int debt);
	public abstract Card stonemason_cardToGainOverpay(MoveContext context, int overpayAmount, boolean potion,int debt);
	public abstract Card doctor_cardToPick(MoveContext context);
	public abstract ArrayList<Card> doctor_cardsForDeck(MoveContext context, ArrayList<Card> cards);
	public abstract DoctorOverpayOption doctor_chooseOption(MoveContext context, Card card);
	public abstract Card herald_cardTopDeck(MoveContext context, Card[] cardList);
	
	// ////////////////////////////////////////////
    // Card interactions - Adventures Expansion
    // ////////////////////////////////////////////
	public abstract Card raze_cardToTrash(MoveContext context);
	public abstract Card raze_cardToDraw(MoveContext context, Card[] cardList);
	public abstract Card amulet_cardToTrash(MoveContext context);
    public abstract AmuletOption amulet_chooseOption(MoveContext context);
    public abstract Card[] dungeon_cardsToDiscard(MoveContext context);
    public abstract Card[] gear_cardsToSetAside(MoveContext context);
	public abstract boolean shouldExchangeTraveller(MoveContext context, Card source, Card target);
	public abstract Card fugitive_cardToDiscard(MoveContext context);
    public abstract Card soldier_attack_cardToDiscard(MoveContext context);
	public abstract Card hero_cardToObtain(MoveContext context);
    public abstract ActionCard disciple_cardToPlay(MoveContext context);
    public abstract boolean messenger_shouldDiscardDeck(MoveContext context);
	public abstract Card messenger_cardToObtain(MoveContext context);
	public abstract boolean miser_takeCoin(MoveContext context);
    public abstract Card[] artificer_cardsToDiscard(MoveContext context);
	public abstract Card artificer_cardToObtain(MoveContext context,int maxCost);
	public abstract Card alms_cardToObtain(MoveContext context);
    public abstract QuestOption quest_chooseOption(MoveContext context);
	public abstract Card quest_attackToDiscard(MoveContext context);
	public abstract Card[] quest_cardsToDiscard(MoveContext context);
	public abstract Card save_cardToSetAside(MoveContext context);
    public abstract Card[] scoutingParty_cardsFromTopOfDeckToDiscard(MoveContext context, Card[] cards);
    public abstract Card[] scoutingParty_cardOrder(MoveContext context, Card[] cards);
    public abstract boolean travellingFair_shouldPutCardOnDeck(MoveContext context, Card card);
    public abstract Card[] bonfire_cardsToTrash(MoveContext context, Card[] cards);
	public abstract Card ball_cardToObtain(MoveContext context);
	public abstract Card seaway_cardToObtain(MoveContext context);
	public abstract Card[] trade_cardsToTrash(MoveContext context);
	public abstract boolean callReserveCard(MoveContext context, Card target);
	public abstract Card ratcatcher_cardToTrash(MoveContext context);
	public abstract Card transmogrify_cardToTrash(MoveContext context);
    public abstract Card transmogrify_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt);
    public abstract Card[] pilgrimage_cardsToObtain(MoveContext context,Card[] cards);
	public abstract Card plan_placeToken(MoveContext context);
	public abstract Card ferry_placeToken(MoveContext context);
	public abstract Card lostArts_placeToken(MoveContext context);
	public abstract Card training_placeToken(MoveContext context);
	public abstract Card pathfinding_placeToken(MoveContext context);
	public abstract Card inheritance_placeToken(MoveContext context);
    public abstract TeacherOption teacher_chooseOption(MoveContext context);
	public abstract Card teacher_placeToken(MoveContext context); 
	public abstract Card trashingToken_cardToTrash(MoveContext context);
    public abstract Card[] storyteller_cardsToPlay(MoveContext context);

	// ////////////////////////////////////////////
    // Card interactions - Empires Expansion
    // ////////////////////////////////////////////
	public abstract int payDebt(MoveContext context,int debt);
	public abstract Card engineer_cardToObtain(MoveContext context);
	public abstract boolean engineer_shouldTrash(MoveContext context);
	public abstract ActionCard overlord_actionCardToImpersonate(MoveContext context);
	public abstract boolean settlers_shouldReveal(MoveContext context);
	public abstract boolean bustlingVillage_shouldReveal(MoveContext context);
    public abstract Card catapult_cardToTrash(MoveContext context);
    public abstract Card[] catapult_attack_cardsToKeep(MoveContext context);
    public abstract Card sacrifice_cardToTrash(MoveContext context);
    public abstract Card[] forum_cardsToDiscard(MoveContext context);
	public abstract boolean legionary_shouldRevealGold(MoveContext context);
    public abstract Card[] legionary_attack_cardsToKeep(MoveContext context);
    public abstract Card[] annex_cardsToKeepInDiscard(MoveContext context, Card[] cards);
    public abstract Card advance_cardToTrash(MoveContext context);
    public abstract ActionCard advance_actionCardToObtain(MoveContext context);
    public abstract Card banquet_cardToObtain(MoveContext context);
    public abstract Card ritual_cardToTrash(MoveContext context);
	public abstract Card saltTheEarth_supplyCardToTrash(MoveContext context);
	public abstract Card[] donate_cardsToTrash(MoveContext context,Card[] cards);
    public abstract Card tax_supplyToTax(MoveContext context);
	public abstract Card smallCastle_cardToTrash(MoveContext context,Card[] cards);
    public abstract Card[] hauntedCastle_cardsToPutBackOnDeck(MoveContext context);
    public abstract Card[] opulentCastle_cardsToDiscard(MoveContext context);
    public abstract HuntingGroundsOption sprawlingCastle_chooseOption(MoveContext context);
	public abstract boolean encampment_shouldReveal(MoveContext context);
	public abstract boolean gladiator_shouldReveal(MoveContext context,Card card);
    public abstract Card gladiator_revealedCard(MoveContext context);
    public abstract Card[] temple_cardsToTrash(MoveContext context, Card[] cards);
    public abstract WildHuntOption wildHunt_chooseOption(MoveContext context);
	public abstract Card archive_cardToDraw(MoveContext context, Card[] cardList);
    public abstract CharmOption charm_chooseOption(MoveContext context);
    public abstract Card charm_cardToObtain(MoveContext context, Card[] cardList);
	public abstract ActionCard crown_actionCardToPlay(MoveContext context);
	public abstract TreasureCard crown_treasureCardToPlay(MoveContext context);
	public abstract Card arena_actionToDiscard(MoveContext context);
	public abstract int mountainPass_amountToBid(MoveContext context);

	// ////////////////////////////////////////////
    // Card interactions - Base + Intrigue Updates
    // ////////////////////////////////////////////
	public abstract boolean vassal_shouldPlayAction(MoveContext context, Card card);
	public abstract Card harbinger_cardToTopdeck(MoveContext context, HashSet<Card> options);
    public abstract Card[] poacher_cardsToDiscard(MoveContext context, int num);
    public abstract TreasureCard bandit_treasureToTrash(MoveContext context, TreasureCard[] treasures);
    public abstract SentryOption sentry_chooseOption(MoveContext context, Card card);
    public abstract Card artisan_cardToObtain(MoveContext context);
    public abstract Card artisan_cardToPutBackOnDeck(MoveContext context);
    public abstract LurkerOption lurker_chooseOption(MoveContext context);
	public abstract Card lurker_cardToGainFromTrash(MoveContext context);
	public abstract Card lurker_cardToTrash(MoveContext context);
	public abstract Card secretPassage_cardToPutInDeck(MoveContext context);
	public abstract int secretPassage_positionInDeck(MoveContext context, int deckSize);
    public abstract Card[] diplomat_cardsToDiscard(MoveContext context);
	public abstract Card[] mill_cardsToDiscard(MoveContext context);
    public abstract Card courtier_revealedCard(MoveContext context);
    public abstract CourtierOption[] courtier_chooseOptions(MoveContext context, int num);
	public abstract Card replace_cardToTrash(MoveContext context);
    public abstract Card replace_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt);

	// ////////////////////////////////////////////
    // Card interactions - Nocturnes Expansion
    // ////////////////////////////////////////////
	public abstract Card devilsWorkshop_cardToObtain(MoveContext context);
    public abstract Card vampire_cardToObtain(MoveContext context, Card[] cardList);
	public abstract Card[] bat_cardsToTrash(MoveContext context);
	public abstract Card raider_discard_chooseOption(MoveContext context, Card[] cardList);
	public abstract Card[] crypt_cardsToSetAside(MoveContext context, Card[] cardList);
	public abstract Card crypt_cardToDraw(MoveContext context, Card[] cardList);
    public abstract Card goat_cardToTrash(MoveContext context);
    public abstract Card hauntedMirror_cardToDiscard(MoveContext context);
    public abstract MonasteryOption monastery_chooseOption(MoveContext context);
    public abstract Card monastery_cardToTrash(MoveContext context);
    public abstract Card[] nightWatchman_cardsFromTopOfDeckToDiscard(MoveContext context, Card[] cards);
    public abstract Card[] nightWatchman_cardOrder(MoveContext context, Card[] cards);
	public abstract Card[] secretCave_cardsToDiscard(MoveContext context);
    public abstract Card changeling_cardToObtain(MoveContext context, Card[] cardList);
    public abstract boolean changeling_shouldGain(MoveContext context, Card card);
    public abstract Card[] cemetery_cardsToTrash(MoveContext context);
    public abstract ActionCard conclave_cardToPlay(MoveContext context, ActionCard[] cardList);
    public abstract ActionCard imp_cardToPlay(MoveContext context, ActionCard[] cardList);
    public abstract Card exorcist_cardToTrash(MoveContext context);
    public abstract Card exorcist_cardToObtain(MoveContext context, Card[] cardList);
    public abstract Card[] shepherd_cardsToDiscard(MoveContext context);
	public abstract Card cobbler_cardToObtain(MoveContext context);
    public abstract ActionCard necromancer_cardToPlay(MoveContext context, ActionCard[] cardList);
    public abstract boolean zombieSpy_shouldDiscard(MoveContext context, Card card);
	public abstract Card zombieApprentice_actionToTrash(MoveContext context);
    public abstract Card zombieMason_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt);
	public abstract Card pooka_cardToTrash(MoveContext context,Card[] cards);
	public abstract Card tragicHero_cardToObtain(MoveContext context);
    public abstract TreasureCard earthsgift_treasureToDiscard(MoveContext context);
	public abstract Card earthsgift_cardToObtain(MoveContext context);
    public abstract Card flamesgift_cardToTrash(MoveContext context);
	public abstract Card moonsgift_cardToTopdeck(MoveContext context, HashSet<Card> options);
	public abstract Card[] skysgift_cardsToDiscard(MoveContext context);
    public abstract Card[] sunsgift_cardsFromTopOfDeckToDiscard(MoveContext context, Card[] cards);
    public abstract Card[] sunsgift_cardOrder(MoveContext context, Card[] cards);
	public abstract Card[] windsgift_cardsToDiscard(MoveContext context);
    public abstract boolean pixie_shouldTrash(MoveContext context, Boons boon);
    public abstract boolean sacredGrove_receiveBoons(MoveContext context, Boons boon);
    public abstract Card fear_cardToDiscard(MoveContext context, Card[] cards);
    public abstract Card haunting_cardToPutBackOnDeck(MoveContext context);
    public abstract Card[] poverty_cardsToKeep(MoveContext context);
    public abstract Card locusts_cardToObtain(MoveContext context, Card[] cardList);
    public abstract boolean blessedVillage_receiveBoons(MoveContext context, Boons boon);
    public abstract Card lostInTheWoods_cardToDiscard(MoveContext context);
    public abstract Boons druid_boonToPlay(MoveContext context, Boons[] boons);
    public abstract boolean tracker_shouldPutCardOnDeck(MoveContext context, Card card);
	public abstract Card wish_cardToObtain(MoveContext context);

	// ////////////////////////////////////////////
    // Card interactions - Promotional Cards
    // ////////////////////////////////////////////
	public abstract boolean walledVillage_backOnDeck(MoveContext context);
	public abstract GovernorOption governor_chooseOption(MoveContext context);
    public abstract Card governor_cardToTrash(MoveContext context);
    public abstract Card governor_cardToObtain(MoveContext context, int exactCost, boolean potion,int debt);
    public abstract Card envoy_cardToDiscard(MoveContext context, Card[] revealedCards);
	public abstract int stash_positionInDeck(MoveContext context,int deckSize);
	public abstract Card dismantle_cardToTrash(MoveContext context);
    public abstract Card dismantle_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt);
	public abstract boolean sauna_shouldPlayNext(MoveContext context);
	public abstract boolean avanto_shouldPlayNext(MoveContext context);
    public abstract Card sauna_cardToTrash(MoveContext context);
	public abstract Card summon_cardToObtain(MoveContext context);
    public abstract Card prince_cardToSetAside(MoveContext context, Card[] cardList);

	// ////////////////////////////////////////////
    // Card interactions - Unique Cards
    // ////////////////////////////////////////////
	public abstract boolean survivors_shouldDiscardTopCards(MoveContext context, Card[] array);
	public abstract Card[] survivors_cardOrder(MoveContext context, Card[] array);
	public abstract boolean cultist_shouldPlayNext(MoveContext context);
	public abstract Card[] dameAnna_cardsToTrash(MoveContext context);
	public abstract Card knight_cardToTrash(MoveContext context, ArrayList<Card> canTrash);
	public abstract Card[] sirMichael_attack_cardsToKeep(MoveContext context);
	public abstract Card dameNatalie_cardToObtain(MoveContext context);
}
