package com.vdom.core;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import com.vdom.api.GameType;
import com.vdom.api.Card;
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
				currentPlayer.gainNewCard(card,Cards.copper, (MoveContext) context);
		}

	}
}
