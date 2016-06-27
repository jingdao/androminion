package com.vdom.core;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.lang.Math;
import com.vdom.api.GameType;
import com.vdom.api.Card;
import com.vdom.api.ActionCard;
import com.vdom.api.TreasureCard;
import com.vdom.api.VictoryCard;

public class Landmarks {
	public static ArrayList<Landmarks> landmarksEmpires = new ArrayList<Landmarks>();
	public static ArrayList<Landmarks> allLandmarks = new ArrayList<Landmarks>();
    public static HashMap<String,Landmarks> landmarksMap = new HashMap<String,Landmarks>();
	public static boolean mountainPassActive = false;

	public enum Type {
		Aqueduct, Arena, BanditFort, Basilica, Baths, Battlefield, Colonnade, DefiledShrine, Fountain, Keep, Labyrinth,
		MountainPass, Museum, Obelisk, Orchard, Palace, Tomb, Tower, TriumphalArch, Wall, WolfDen,
	}

	public static final Landmarks aqueduct;
	public static final Landmarks arena;
	public static final Landmarks banditFort;
	public static final Landmarks basilica;
	public static final Landmarks baths;
	public static final Landmarks battlefield;
	public static final Landmarks colonnade;
	public static final Landmarks defiledShrine;
	public static final Landmarks fountain;
	public static final Landmarks keep;
	public static final Landmarks labyrinth;
	public static final Landmarks mountainPass;
	public static final Landmarks museum;
	public static final Landmarks obelisk;
	public static final Landmarks orchard;
	public static final Landmarks palace;
	public static final Landmarks tomb;
	public static final Landmarks tower;
	public static final Landmarks triumphalArch;
	public static final Landmarks wall;
	public static final Landmarks wolfDen;

	public Type type;
	public String name;
	public String displayName = "";
	public String description = "";
	public String expansion = "";

	public Landmarks(Type type,String expansion) {
		this.type = type;
		this.name = type.toString();
		this.expansion = expansion;
	}

	public void setup(Game game) {
		switch (this.type) {
			case Aqueduct:
				game.supplyVictoryTokens.put(Cards.silver.getName(), 8);
				game.supplyVictoryTokens.put(Cards.gold.getName(), 8);
				break;	
			case Arena:
			case Basilica:
			case Baths:
			case Battlefield:
			case Colonnade:
			case Labyrinth:
				game.supplyVictoryTokens.put(name, 6 * Game.numPlayers);
				break;
			case DefiledShrine:
				for (String name : game.piles.keySet()) {
					AbstractCardPile pile = game.piles.get(name);
					if (pile.isSupply()) {
						Card card = pile.card();
						if (card.isKnight())
							game.supplyVictoryTokens.put(Cards.virtualKnight.getName(),2);
						else if (card.isRuins())
							game.supplyVictoryTokens.put(Cards.virtualRuins.getName(),2);
						else if (card instanceof ActionCard && !card.isGathering())
							game.supplyVictoryTokens.put(card.getName(),2);
					}
				}
				break;
			case MountainPass:
				mountainPassActive = false;
				break;
			case Obelisk:
				ArrayList<Card> candidates = new ArrayList<Card>();
				for (String name : game.piles.keySet()) {
					AbstractCardPile pile = game.piles.get(name);
					if (pile.isSupply()) {
						Card card = pile.card();
						if (card.isKnight())
							candidates.add(Cards.virtualKnight);
						else if (card.isRuins())
							candidates.add(Cards.virtualRuins);
						else if (card instanceof ActionCard)
							candidates.add(card);
					}
				}
				game.obeliskCard = candidates.get(game.rand.nextInt(candidates.size()));
				break;
		}
	}

	public void applyEffect(Game game, MoveContext context, Player currentPlayer,Card cardResponsible) {
		switch (this.type) {
			case Arena:
				boolean hasAction = false;
				for (Card card : currentPlayer.hand)
					if (card instanceof ActionCard)
						hasAction = true;
				if (!hasAction)
					return;
				Card toDiscard = currentPlayer.controlPlayer.arena_actionToDiscard(context);
				if (toDiscard != null) {
					currentPlayer.hand.remove(toDiscard);
					currentPlayer.discard(toDiscard, Cards.landmarkCard, null);
					game.takeSupplyVictoryTokens(this.name,2);
					currentPlayer.addVictoryTokens(context,2);
				}
				break;
			case Basilica:
			case Baths:
			case Battlefield:
			case Labyrinth:
				game.takeSupplyVictoryTokens(this.name,2);
				currentPlayer.addVictoryTokens(context,2);
				break;
			case Colonnade:
				for (Card card : currentPlayer.playedCards) {
					if (card.equals(cardResponsible)) {
						game.takeSupplyVictoryTokens(this.name,2);
						currentPlayer.addVictoryTokens(context,2);
						break;
					}
				}
				break;
			case MountainPass:
				int highestBid = 0;
				Player highestBidder=null;
				for (Player player : game.getPlayersInTurnOrder()) {
					if (player!=currentPlayer) {
						int bid = player.mountainPass_amountToBid(context);
						if (bid > highestBid) {
							highestBid = bid;
							highestBidder = player;
						}
					}
				}
				int bid = currentPlayer.mountainPass_amountToBid(context);
				if (bid > highestBid) {
					highestBid = bid;
					highestBidder = currentPlayer;
				}
				if (highestBidder != null) {
					highestBidder.addVictoryTokens(context,8);
					highestBidder.debtTokens += highestBid;
				}
				break;
		}
	}

	public int getVPs(Game game, Player currentPlayer, ArrayList<Card> allCards) {
		int count=0;
		HashMap<String,Integer> distinctCards = new HashMap<String,Integer>();
		switch (this.type) {
			case BanditFort:
				for (Card card : allCards)
					if (card.equals(Cards.silver) || card.equals(Cards.gold))
						count -= 2;
				return count;
			case Fountain:
				for (Card card : allCards)
					if (card.equals(Cards.copper))
						count++;
				return count>=10 ? 15 : 0;
			case Museum:
				for (Card card : allCards)
					if (!distinctCards.containsKey(card.getName()))
						distinctCards.put(card.getName(),1);
				return distinctCards.size() * 2;
			case Keep:
				HashMap<String,Integer> maxCounts = new HashMap<String,Integer>();
				for (Player player : game.getPlayersInTurnOrder()) {
					if (player != currentPlayer) {
						HashMap<String,Integer> treasureCards = new HashMap<String,Integer>();
						for (Card card : player.getAllCards()) {
							if (card instanceof TreasureCard) {
								if (treasureCards.containsKey(card.getName()))
									treasureCards.put(card.getName(),treasureCards.get(card.getName())+1);
								else
									treasureCards.put(card.getName(),1);
							}
						}
						for (String name : treasureCards.keySet())
							if (!maxCounts.containsKey(name) || treasureCards.get(name) > maxCounts.get(name))
								maxCounts.put(name,treasureCards.get(name));
					}
				}
				for (Card card : allCards) {
					if (card instanceof TreasureCard) {
						if (distinctCards.containsKey(card.getName()))
							distinctCards.put(card.getName(),distinctCards.get(card.getName())+1);
						else
							distinctCards.put(card.getName(),1);
					}
				}
				for (String name : distinctCards.keySet())
					if (!maxCounts.containsKey(name) || distinctCards.get(name) > maxCounts.get(name))
						count++;
				return count * 5;
			case Obelisk:
				for (Card card : allCards)
					if (game.getOriginCard(card).equals(game.obeliskCard))
						count++;
				return count * 2;
			case Orchard:
				for (Card card : allCards) {
					if (card instanceof ActionCard) {
						if (distinctCards.containsKey(card.getName())) {
							distinctCards.put(card.getName(),distinctCards.get(card.getName())+1);
						} else {
							distinctCards.put(card.getName(),1);
						}
					}
				}
				for (String name : distinctCards.keySet())
					if (distinctCards.get(name) >= 3)
						count++;
				return count * 4;
			case Palace:
				int numCopper=0;
				int numSilver=0;
				int numGold=0;
				for (Card card : allCards) {
					if (card.equals(Cards.copper))
						numCopper++;
					else if (card.equals(Cards.silver))
						numSilver++;
					else if (card.equals(Cards.gold))
						numGold++;
				}
				return Math.min(numCopper,Math.min(numSilver,numGold)) * 3;
			case Tower:
				for (Card card : allCards)
					if (!(card instanceof VictoryCard) && game.isPileEmpty(card))
						count++;
				return count; 
			case TriumphalArch:
				for (Card card : allCards) {
					if (card instanceof ActionCard) {
						if (distinctCards.containsKey(card.getName())) {
							distinctCards.put(card.getName(),distinctCards.get(card.getName())+1);
						} else {
							distinctCards.put(card.getName(),1);
						}
					}
				}
				int highest=0;
				int highest2=0;
				for (String name : distinctCards.keySet()) {
					int n = distinctCards.get(name);
					if (n > highest)
						highest = n;
					else if (n > highest2)
						highest2 = n;
				}
				return highest2 * 3;
			case Wall:
				return allCards.size() > 15 ? 15 - allCards.size() : 0;
			case WolfDen:
				for (Card card : allCards) {
					if (distinctCards.containsKey(card.getName())) {
						distinctCards.put(card.getName(),distinctCards.get(card.getName())+1);
					} else {
						distinctCards.put(card.getName(),1);
					}
				}
				for (String name : distinctCards.keySet())
					if (distinctCards.get(name)==1)
						count++;
				return -3 * count;
		}
		return 0;
	}

	static {
		landmarksEmpires.add(aqueduct = new Landmarks(Landmarks.Type.Aqueduct,"Empires"));
		landmarksEmpires.add(arena = new Landmarks(Landmarks.Type.Arena,"Empires"));
		landmarksEmpires.add(banditFort = new Landmarks(Landmarks.Type.BanditFort,"Empires"));
		landmarksEmpires.add(basilica = new Landmarks(Landmarks.Type.Basilica,"Empires"));
		landmarksEmpires.add(baths = new Landmarks(Landmarks.Type.Baths,"Empires"));
		landmarksEmpires.add(battlefield = new Landmarks(Landmarks.Type.Battlefield,"Empires"));
		landmarksEmpires.add(colonnade = new Landmarks(Landmarks.Type.Colonnade,"Empires"));
		landmarksEmpires.add(defiledShrine = new Landmarks(Landmarks.Type.DefiledShrine,"Empires"));
		landmarksEmpires.add(fountain = new Landmarks(Landmarks.Type.Fountain,"Empires"));
		landmarksEmpires.add(keep = new Landmarks(Landmarks.Type.Keep,"Empires"));
		landmarksEmpires.add(labyrinth = new Landmarks(Landmarks.Type.Labyrinth,"Empires"));
		landmarksEmpires.add(mountainPass = new Landmarks(Landmarks.Type.MountainPass,"Empires"));
		landmarksEmpires.add(museum = new Landmarks(Landmarks.Type.Museum,"Empires"));
		landmarksEmpires.add(obelisk = new Landmarks(Landmarks.Type.Obelisk,"Empires"));
		landmarksEmpires.add(orchard = new Landmarks(Landmarks.Type.Orchard,"Empires"));
		landmarksEmpires.add(palace = new Landmarks(Landmarks.Type.Palace,"Empires"));
		landmarksEmpires.add(tomb = new Landmarks(Landmarks.Type.Tomb,"Empires"));
		landmarksEmpires.add(tower = new Landmarks(Landmarks.Type.Tower,"Empires"));
		landmarksEmpires.add(triumphalArch = new Landmarks(Landmarks.Type.TriumphalArch,"Empires"));
		landmarksEmpires.add(wall = new Landmarks(Landmarks.Type.Wall,"Empires"));
		landmarksEmpires.add(wolfDen = new Landmarks(Landmarks.Type.WolfDen,"Empires"));

		for (Landmarks e: landmarksEmpires) allLandmarks.add(e);
		for (Landmarks e: allLandmarks) landmarksMap.put(e.name,e);
	}

	static ArrayList<Landmarks> getLandmarksSet(GameType gameType,int num) {
		ArrayList<Landmarks> candidates;
		ArrayList<Landmarks> set = new ArrayList<Landmarks>();
		if (gameType == GameType.Random)
			candidates = allLandmarks;
		else if (gameType == GameType.RandomEmpires)
			candidates = landmarksEmpires;
		else return set;
		Collections.shuffle(candidates);
		Landmarks e1 = candidates.get(0);
		Landmarks e2 = candidates.get(1);
		if (num==2) {
			if (e1.name.compareTo(e2.name) < 0) {
				set.add(e1);
				set.add(e2);
			} else {
				set.add(e2);
				set.add(e1);
			}
		} else if (num==1)
			set.add(e1);
		return set;
	}
}
