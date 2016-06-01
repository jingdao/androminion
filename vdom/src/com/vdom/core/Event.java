package com.vdom.core;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import com.vdom.api.GameType;
import com.vdom.api.Card;
import com.vdom.api.ActionCard;
import com.vdom.api.TreasureCard;

public class Event {
	public static ArrayList<Event> eventsAdventures = new ArrayList<Event>();
	public static ArrayList<Event> eventsEmpires = new ArrayList<Event>();
	public static ArrayList<Event> allEvents = new ArrayList<Event>();
    public static HashMap<String,Event> eventsMap = new HashMap<String,Event>();

	public enum Type {
		Alms, Borrow, Quest, Save, ScoutingParty, TravellingFair, Bonfire, Expedition, Ferry, Plan,
		Mission, Pilgrimage, Ball, Raid, Seaway, Trade, LostArts, Training, Inheritance, Pathfinding,
	}

	public static final Event alms;
	public static final Event borrow;
	public static final Event quest;
	public static final Event save;
	public static final Event scoutingParty;
	public static final Event travellingFair;
	public static final Event bonfire;
	public static final Event expedition;
	public static final Event ferry;
	public static final Event plan;
	public static final Event mission;
	public static final Event pilgrimage;
	public static final Event ball;
	public static final Event raid;
	public static final Event seaway;
	public static final Event trade;
	public static final Event lostArts;
	public static final Event training;
	public static final Event inheritance;
	public static final Event pathfinding;

	public Type type;
	public int cost;
	public String name;
	public String displayName = "";
	public String description = "";
	public String expansion = "";

	public Event(Type type,int cost,String expansion) {
		this.type = type;
		this.cost = cost;
		this.name = type.toString();
		this.expansion = expansion;
	}

	public void isBought(MoveContext context) {
		Player currentPlayer = context.getPlayer();
		switch (type) {
			case Alms:
				alms(context,currentPlayer);
				break;
			case Borrow:
				borrow(context,currentPlayer);
				break;
			case Quest:
				quest(context,currentPlayer);
				break;
			case Save:
				save(context,currentPlayer);
				break;
			case ScoutingParty:
				scoutingParty(context,currentPlayer);
				break;
			case TravellingFair:
				travellingFair(context,currentPlayer);
				break;
			case Bonfire:
				bonfire(context,currentPlayer);
				break;
			case Ball:
				ball(context,currentPlayer);
				break;
			case Raid:
				raid(context,currentPlayer);
				break;
			case Seaway:
				seaway(context,currentPlayer);
				break;
			case Trade:
				trade(context,currentPlayer);
				break;
			case Pilgrimage:
				pilgrimage(context,currentPlayer);
				break;
			case Plan:
				plan(context,currentPlayer);
				break;
			case Ferry:
				ferry(context,currentPlayer);
				break;
			case LostArts:
				lostArts(context,currentPlayer);
				break;
			case Training:
				training(context,currentPlayer);
				break;
			case Pathfinding:
				pathfinding(context,currentPlayer);
				break;
			case Inheritance:
				inheritance(context,currentPlayer);
				break;
			default:
				break;
		}
	}

	static {
		eventsAdventures.add(alms = new Event(Event.Type.Alms,0,"Adventures"));
		eventsAdventures.add(borrow = new Event(Event.Type.Borrow,0,"Adventures"));
		eventsAdventures.add(quest = new Event(Event.Type.Quest,0,"Adventures"));
		eventsAdventures.add(save = new Event(Event.Type.Save,1,"Adventures"));
		eventsAdventures.add(scoutingParty = new Event(Event.Type.ScoutingParty,2,"Adventures"));
		eventsAdventures.add(travellingFair = new Event(Event.Type.TravellingFair,2,"Adventures"));
		eventsAdventures.add(bonfire = new Event(Event.Type.Bonfire,3,"Adventures"));
		eventsAdventures.add(expedition = new Event(Event.Type.Expedition,3,"Adventures"));
		eventsAdventures.add(ferry = new Event(Event.Type.Ferry,3,"Adventures"));
		eventsAdventures.add(plan = new Event(Event.Type.Plan,3,"Adventures"));
		eventsAdventures.add(mission = new Event(Event.Type.Mission,4,"Adventures"));
		eventsAdventures.add(pilgrimage = new Event(Event.Type.Pilgrimage,4,"Adventures"));
		eventsAdventures.add(ball = new Event(Event.Type.Ball,5,"Adventures"));
		eventsAdventures.add(raid = new Event(Event.Type.Raid,5,"Adventures"));
		eventsAdventures.add(seaway = new Event(Event.Type.Seaway,5,"Adventures"));
		eventsAdventures.add(trade = new Event(Event.Type.Trade,5,"Adventures"));
		eventsAdventures.add(lostArts = new Event(Event.Type.LostArts,6,"Adventures"));
		eventsAdventures.add(training = new Event(Event.Type.Training,6,"Adventures"));
		eventsAdventures.add(inheritance = new Event(Event.Type.Inheritance,7,"Adventures"));
		eventsAdventures.add(pathfinding = new Event(Event.Type.Pathfinding,8,"Adventures"));

		for (Event e: eventsAdventures) allEvents.add(e);
		for (Event e: eventsEmpires) allEvents.add(e);
		for (Event e: allEvents) eventsMap.put(e.name,e);
	}	

	static ArrayList<Event> getEventSet(GameType gameType) {
		ArrayList<Event> candidates;
		ArrayList<Event> set = new ArrayList<Event>();
		if (gameType == GameType.Random)
			candidates = allEvents;
		else if (gameType == GameType.RandomAdventures)
			candidates = eventsAdventures;
		else if (gameType == GameType.RandomEmpires)
			candidates = eventsEmpires;
		else return set;
		Collections.shuffle(candidates);
		Event e1 = candidates.get(0);
		Event e2 = candidates.get(1);
		if (e1.cost < e2.cost || (e1.cost == e2.cost && e1.name.compareTo(e2.name) < 0)) {
			set.add(e1);
			set.add(e2);
		} else {
			set.add(e2);
			set.add(e1);
		}
		return set;
	}

	public void alms(MoveContext context, Player currentPlayer) {
		for (Event e : currentPlayer.controlPlayer.boughtEvents) {
			if (e.equals(Event.alms))
				return;
		}
		boolean playedTreasure = false;
		for (int i=0;i<currentPlayer.playedCards.size();i++) {
			Card c = currentPlayer.playedCards.get(i);
			if (c instanceof TreasureCard) {
				playedTreasure = true;
				break;
			}	
		}
		if (!playedTreasure) {
			Card card = currentPlayer.alms_cardToObtain((MoveContext) context);
			if (card != null) 
				currentPlayer.gainNewCard(card,Cards.eventCard, (MoveContext) context);
		}

	}

	public void borrow(MoveContext context, Player currentPlayer) {
		context.buys++;
		for (Event e : currentPlayer.controlPlayer.boughtEvents) {
			if (e.equals(Event.borrow))
				return;
		}
		if (!currentPlayer.minusCardToken) {
			currentPlayer.minusCardToken = true;
			context.addGold += 1;
		}
	}

	public void quest(MoveContext context, Player currentPlayer) {
        Player.QuestOption option = currentPlayer.controlPlayer.quest_chooseOption(context);
		boolean valid = false;
		if (option == Player.QuestOption.Attack) {
			int numAttack = 0;
			Card attackCard = null;
			for (Card card : currentPlayer.hand) {
				if (card.isAttack()) {
					numAttack++;
					attackCard = card;
				}
			}
			if (numAttack > 1) {
				attackCard = currentPlayer.controlPlayer.quest_attackToDiscard(context);
			}
			if (attackCard!=null && attackCard.isAttack()) {
				currentPlayer.hand.remove(attackCard);
				valid = true;
			}
		} else if (option == Player.QuestOption.Curses) {
			valid = currentPlayer.hand.remove(Cards.curse) && currentPlayer.hand.remove(Cards.curse);
		} else if (option == Player.QuestOption.Cards) {
			Card[] cards;
			if (currentPlayer.hand.size() > 6) {
				cards = currentPlayer.controlPlayer.quest_cardsToDiscard(context);
			} else {
				cards = currentPlayer.getHand().toArray();
			}
			for (int i = 0; i < cards.length; i++) {
				currentPlayer.hand.remove(cards[i]);
				currentPlayer.discard(cards[i], Cards.eventCard, null);
			}
			valid = cards.length == 6;

		}
		if (valid) 		
			currentPlayer.gainNewCard(Cards.gold,Cards.eventCard, context);
	}

	public void save(MoveContext context, Player currentPlayer) {
		context.buys++;
		for (Event e : currentPlayer.controlPlayer.boughtEvents) {
			if (e.equals(Event.save))
				return;
		}
		if (currentPlayer.hand.size() == 0) 
			return;
        Card card = currentPlayer.controlPlayer.save_cardToSetAside(context);
        if (card != null) {
			currentPlayer.hand.remove(card);
			currentPlayer.save.add(card);
        }
	}

	public void scoutingParty(MoveContext context, Player currentPlayer) {
		context.buys++;
        ArrayList<Card> topOfTheDeck = new ArrayList<Card>();
        for (int i = 0; i < 5; i++) {
            Card card = context.game.draw(currentPlayer);
            if (card != null) {
                topOfTheDeck.add(card);
            }
        }
        if (topOfTheDeck.size() > 0) {
            Card[] cardsToDiscard;
			if (topOfTheDeck.size() <= 3)
				cardsToDiscard = topOfTheDeck.toArray(new Card[0]);
			else
				cardsToDiscard = currentPlayer.controlPlayer.scoutingParty_cardsFromTopOfDeckToDiscard(context, topOfTheDeck.toArray(new Card[topOfTheDeck.size()]));
			for(Card toDiscard : cardsToDiscard) {
				topOfTheDeck.remove(toDiscard);
				currentPlayer.discard(toDiscard, Cards.eventCard, null);
			}
            if (topOfTheDeck.size() > 0) {
                Card[] order;
                if(topOfTheDeck.size() == 1) {
                    order = topOfTheDeck.toArray(new Card[topOfTheDeck.size()]);
                } else {
                    order = currentPlayer.controlPlayer.scoutingParty_cardOrder(context, topOfTheDeck.toArray(new Card[topOfTheDeck.size()]));
                }
                for (int i = order.length - 1; i >= 0; i--) {
                    currentPlayer.putOnTopOfDeck(order[i]);
                }
            }
        }
	}

	public void travellingFair(MoveContext context, Player currentPlayer) {
		context.buys += 2;
	}

	public void bonfire(MoveContext context, Player currentPlayer) {
		ArrayList<Card> inPlay = new ArrayList<Card>();
		for (Card card: currentPlayer.playedCards)
			inPlay.add(card);
		Card[] cardsToTrash = currentPlayer.controlPlayer.bonfire_cardsToTrash(context,inPlay.toArray(new Card[0])); 
		for (Card card: cardsToTrash) {
			currentPlayer.playedCards.remove(card);
			currentPlayer.trash(card, Cards.eventCard, context);
		}
	}

	public void ball(MoveContext context, Player currentPlayer) {
		currentPlayer.minusCoinToken = true;
		Card card = currentPlayer.ball_cardToObtain((MoveContext) context);
		currentPlayer.gainNewCard(card,Cards.eventCard, (MoveContext) context);
		card = currentPlayer.ball_cardToObtain((MoveContext) context);
		currentPlayer.gainNewCard(card,Cards.eventCard, (MoveContext) context);
	}

	public void raid(MoveContext context, Player currentPlayer) {
		int numSilver = 0;
		for (Card card : currentPlayer.playedCards)
			if (card.equals(Cards.silver))
				numSilver++;
		for (int i=0;i<numSilver;i++)
			currentPlayer.gainNewCard(Cards.silver,Cards.eventCard, context);
		for (Player player : context.game.getPlayersInTurnOrder()) {
			if (player != currentPlayer)
				player.minusCardToken = true;
		}
	}

	public void seaway(MoveContext context, Player currentPlayer) {
		Card card = currentPlayer.seaway_cardToObtain((MoveContext) context);
		currentPlayer.gainNewCard(card,Cards.eventCard, (MoveContext) context);
		currentPlayer.plusBuyToken = card;
	}

	public void trade(MoveContext context, Player currentPlayer) {
        Card[] cards = currentPlayer.controlPlayer.trade_cardsToTrash(context);
		if (cards != null) {
			for (Card card : cards) {
				currentPlayer.hand.remove(card);
				currentPlayer.trash(card,Cards.eventCard,context);
			}
			for (int i=0;i<cards.length;i++)
				currentPlayer.gainNewCard(Cards.silver,Cards.eventCard, context);
		}
	}

	public void pilgrimage(MoveContext context, Player currentPlayer) {
		for (Event e : currentPlayer.controlPlayer.boughtEvents) {
			if (e.equals(Event.pilgrimage))
				return;
		}
		if (currentPlayer.journeyToken) {
			HashSet<Card> uniqueCards = new HashSet<Card>();
			for (Card card : currentPlayer.playedCards) {
				AbstractCardPile pile = context.game.getPile(card);
				if (!pile.isEmpty() && pile.isSupply())
					uniqueCards.add(card);
			}
			if (uniqueCards.size() > 0) {
				Card[] selectedCards = currentPlayer.controlPlayer.pilgrimage_cardsToObtain(context,uniqueCards.toArray(new Card[0]));
				for (Card card : selectedCards) {
					currentPlayer.gainNewCard(card,Cards.eventCard,context);
				}
			}
		}
		currentPlayer.journeyToken = !currentPlayer.journeyToken;
	}

	public void plan(MoveContext context, Player currentPlayer) {
		Card card = currentPlayer.plan_placeToken((MoveContext) context);
		currentPlayer.trashingToken = card;
	}

	public void ferry(MoveContext context, Player currentPlayer) {
		Card card = currentPlayer.ferry_placeToken((MoveContext) context);
		currentPlayer.minusCostToken = card;
	}

	public void lostArts(MoveContext context, Player currentPlayer) {
		Card card = currentPlayer.lostArts_placeToken((MoveContext) context);
		currentPlayer.plusActionToken = card;
	}

	public void training(MoveContext context, Player currentPlayer) {
		Card card = currentPlayer.training_placeToken((MoveContext) context);
		currentPlayer.plusCoinToken = card;
	}

	public void pathfinding(MoveContext context, Player currentPlayer) {
		Card card = currentPlayer.pathfinding_placeToken((MoveContext) context);
		currentPlayer.plusCardToken = card;
	}

	public void inheritance(MoveContext context, Player currentPlayer) {
		if (currentPlayer.estateToken==null) {
			Card card = currentPlayer.inheritance_placeToken((MoveContext) context);
			currentPlayer.estateToken = context.game.takeFromPile(card);
			ArrayList<CardList> lists = new ArrayList<CardList>();
			lists.add(currentPlayer.hand);
			lists.add(currentPlayer.deck);
			lists.add(currentPlayer.discard);
			lists.add(currentPlayer.playedCards);
			lists.add(currentPlayer.nextTurnCards);
			lists.add(currentPlayer.nativeVillage);
			lists.add(currentPlayer.island);
			lists.add(currentPlayer.haven);
			lists.add(currentPlayer.horseTraders);
			lists.add(currentPlayer.tavern);
			for (CardList cl : lists) {
				int numEstate = 0;
				while (cl.remove(Cards.estate))
					numEstate++;
				for (int i=0;i<numEstate;i++)
					cl.add(Cards.inheritedEstate.getTemplateCard().instantiate());
			}
		}
	}

}
