package com.vdom.comms;

import java.io.Serializable;

public class GameStatus implements Serializable {
	private static final long serialVersionUID = -5928579898003313213L;

	public int whoseTurn;
	public String name;
	public String[] realNames;
	public boolean isFinal;
    public boolean isPossessed;
    public int[] turnCounts;
	public int[] myHand;
	public int[] playedCards;
	public int[] myIsland;
	public int[] myVillage;
	public int[] myTavern;
	public int[] trashPile;
	public int[] turnStatus;
	public int[] supplySizes;
	public int[] embargos;
	public int[] costs;
	public int[] deckSizes;
	public int[] handSizes;
	public int[] numCards;
	public int[] pirates;
	public int[] victoryTokens;
	public int[] guildsCoinTokens;
	public int[] debtTokens;
	public boolean[] minusCardToken;
	public boolean[] minusCoinToken;
	public boolean[] journeyToken;
	public boolean[] lostInTheWoods;
	public boolean[] deluded;
	public boolean[] envious;
	public boolean[] miserable;
	public boolean[] twiceMiserable;
	public int[] supplyTokens;
	public int[] tax;
	public int[] supplyVictoryTokens;
	public int[] landmarkVictoryTokens;
	public int cardCostModifier;
	public int potions;
	public String ruinsTopCard;
	public String ruinsTopCardDesc;
	public int ruinsID;
	public String knightsTopCard;
	public String knightsTopCardDesc;
	public int knightsTopCardCost;
	public int knightsID;
	public String castleTopCard;
	public String castleTopCardDesc;
	public int castleTopCardCost;
	public int castleID = -1;
	public int virtualCastleID = -1;
	public int[] splitPileID;
	public int[] splitPileTopCard;
	public String[] boons;
	public String[] hexes;

	public GameStatus setFinal(boolean b) {isFinal = b; return this;}
    public GameStatus setPossessed(boolean b) {isPossessed = b; return this;}
    public GameStatus setTurnCounts(int[] is) {turnCounts = is; return this;};
    public GameStatus setRealNames(String[] is) {realNames = is; return this;};
	public GameStatus setCurPlayer(int i) {whoseTurn = i; return this;}
	public GameStatus setCurName(String s) {name = s; return this;}
	public GameStatus setHand(int[] is) {myHand = is; return this;};
	public GameStatus setPlayedCards(int[] is) {playedCards = is; return this;};
	public GameStatus setIsland(int[] is) {myIsland = is; return this;};
	public GameStatus setVillage(int[] is) {myVillage = is; return this;};
	public GameStatus setTavern(int[] is) {myTavern = is; return this;};
	public GameStatus setTurnStatus(int[] is) {turnStatus = is; return this;};
	public GameStatus setSupplySizes(int[] is) {supplySizes = is; return this;};
	public GameStatus setEmbargos(int[] is) {embargos = is; return this;};
	public GameStatus setCosts(int[] is) {costs = is; return this;};
	public GameStatus setDeckSizes(int[] is) {deckSizes = is; return this;};
	public GameStatus setHandSizes(int[] is) {handSizes = is; return this;};
	public GameStatus setNumCards(int[] is) {numCards = is; return this;}
	public GameStatus setPirates(int[] is) {pirates = is; return this;}
    public GameStatus setVictoryTokens(int[] is) {victoryTokens = is; return this;}
    public GameStatus setGuildsCoinTokens(int[] is) {guildsCoinTokens = is; return this;}
    public GameStatus setDebtTokens(int[] is) {debtTokens = is; return this;}
    public GameStatus setMinusCardToken(boolean[] is) {minusCardToken = is; return this;}
    public GameStatus setMinusCoinToken(boolean[] is) {minusCoinToken = is; return this;}
    public GameStatus setJourneyToken(boolean[] is) {journeyToken = is; return this;}
    public GameStatus setLostInTheWoods(boolean[] is) {lostInTheWoods = is; return this;}
    public GameStatus setDeluded(boolean[] is) {deluded = is; return this;}
    public GameStatus setEnvious(boolean[] is) {envious = is; return this;}
    public GameStatus setMiserable(boolean[] is) {miserable = is; return this;}
    public GameStatus setTwiceMiserable(boolean[] is) {twiceMiserable = is; return this;}
	public GameStatus setSupplyTokens(int[] is) {supplyTokens = is; return this;};
	public GameStatus setTax(int[] is) {tax = is; return this;};
	public GameStatus setSupplyVictoryTokens(int[] is) {supplyVictoryTokens = is; return this;};
	public GameStatus setLandmarkVictoryTokens(int[] is) {landmarkVictoryTokens = is; return this;};
	public GameStatus setBoons(String[] is) {boons = is; return this;}
	public GameStatus setHexes(String[] is) {hexes = is; return this;}
	public GameStatus setCardCostModifier(int i) {cardCostModifier = i; return this;}
    public GameStatus setPotions(int i) {potions = i; return this;}
    public GameStatus setTrash(int[] is) {trashPile = is; return this;}
    public GameStatus setRuinsTopCard(int i, String s, String d) {ruinsTopCard = s; ruinsTopCardDesc = d; ruinsID = i; return this;}
    public GameStatus setKnightTopCard(int i, String s, String d, int c) {knightsTopCard = s; knightsTopCardDesc = d; knightsID = i; knightsTopCardCost = c; return this;}
    
	public String toString() {
		String str = name + "(" + whoseTurn + ")";
		return str;
	}
}
