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

public class Boons {
	public static ArrayList<Boons> boonsNocturne = new ArrayList<Boons>();
	public static ArrayList<Boons> allBoons = new ArrayList<Boons>();
    public static HashMap<String,Boons> boonsMap = new HashMap<String,Boons>();

	public enum Type {
		Earthsgift, Fieldsgift, Flamesgift, Forestsgift, Moonsgift, Mountainsgift,
		Riversgift, Seasgift, Skysgift, Sunsgift, Swampsgift, Windsgift
	}

	public static final Boons earthsgift;
	public static final Boons fieldsgift;
	public static final Boons flamesgift;
	public static final Boons forestsgift;
	public static final Boons moonsgift;
	public static final Boons mountainsgift;
	public static final Boons riversgift;
	public static final Boons seasgift;
	public static final Boons skysgift;
	public static final Boons sunsgift;
	public static final Boons swampsgift;
	public static final Boons windsgift;

	public Type type;
	public String name;
	public String description = "";
	public String expansion = "";

	public Boons(Type type,String expansion) {
		this.type = type;
		this.name = type.toString().toLowerCase();
		this.expansion = expansion;
	}

	public void applyEffect(Game game, MoveContext context, Player currentPlayer,Card cardResponsible) {
		switch (this.type) {
			case Earthsgift:
				Card toDiscard = currentPlayer.controlPlayer.earthsgift_treasureToDiscard(context);
				if (toDiscard!=null) {
					currentPlayer.hand.remove(toDiscard);
					currentPlayer.reveal(toDiscard, cardResponsible, context);
					currentPlayer.discard(toDiscard, cardResponsible, context);
					Card card = currentPlayer.controlPlayer.earthsgift_cardToObtain(context);
					if (card!=null)
						currentPlayer.gainNewCard(card, cardResponsible, context);
				}
				break;
			case Fieldsgift:
				context.actions++;
				context.addGold++;
				break;
			case Flamesgift:
				Card toTrash = currentPlayer.controlPlayer.flamesgift_cardToTrash(context);
				if (toTrash != null) {
					currentPlayer.hand.remove(toTrash);
					currentPlayer.trash(toTrash, cardResponsible, context);
				}
				break;
			case Forestsgift:
				context.buys++;
				context.addGold++;
				break;
			case Moonsgift:
				HashSet<Card> options = new HashSet<Card>();
				for (Card c : currentPlayer.discard)
					options.add(c);
				if (!options.isEmpty()) {
					Card toTopdeck = currentPlayer.controlPlayer.moonsgift_cardToTopdeck(context, options);
					if (toTopdeck != null ) {
						currentPlayer.discard.remove(toTopdeck);
						currentPlayer.putOnTopOfDeck(toTopdeck);
					}
				}
				break;
			case Mountainsgift:
				currentPlayer.gainNewCard(Cards.silver, cardResponsible, context);
				break;
			case Riversgift:
				currentPlayer.riversgift = true;
				break;
			case Seasgift:
				game.drawToHand(currentPlayer, cardResponsible);
				break;
			case Skysgift:
				Card[] cardsToDiscard = currentPlayer.controlPlayer.skysgift_cardsToDiscard(context);
				if (cardsToDiscard != null) {
					for (Card card : cardsToDiscard) {
						currentPlayer.hand.remove(card);
						currentPlayer.reveal(card, cardResponsible, context);
						currentPlayer.discard(card, cardResponsible, context);
					}
					if (cardsToDiscard.length == 3) {
						currentPlayer.gainNewCard(Cards.gold, cardResponsible, context);
					}
				}
				break;
			case Sunsgift:
				ArrayList<Card> topOfTheDeck = new ArrayList<Card>();
				for (int i = 0; i < 4; i++) {
					Card card = context.game.draw(currentPlayer);
					if (card != null) {
						topOfTheDeck.add(card);
					}
				}
				if (topOfTheDeck.size() > 0) {
					Card[] topdeckToDiscard = currentPlayer.controlPlayer.sunsgift_cardsFromTopOfDeckToDiscard(context, topOfTheDeck.toArray(new Card[topOfTheDeck.size()]));
					for(Card c : topdeckToDiscard) {
						topOfTheDeck.remove(c);
						currentPlayer.discard(c, cardResponsible, null);
					}
					if (topOfTheDeck.size() > 0) {
						Card[] order;
						if(topOfTheDeck.size() == 1) {
							order = topOfTheDeck.toArray(new Card[topOfTheDeck.size()]);
						} else {
							order = currentPlayer.controlPlayer.sunsgift_cardOrder(context, topOfTheDeck.toArray(new Card[topOfTheDeck.size()]));
						}
						for (int i = order.length - 1; i >= 0; i--) {
							currentPlayer.putOnTopOfDeck(order[i]);
						}
					}
				}
				break;
			case Swampsgift:
				currentPlayer.gainNewCard(Cards.willOWisp, cardResponsible, context);
				break;
			case Windsgift:
				game.drawToHand(currentPlayer, cardResponsible);
				game.drawToHand(currentPlayer, cardResponsible);
				Card[] discards = currentPlayer.controlPlayer.windsgift_cardsToDiscard(context);
				if (discards != null) {
					for (Card card : discards) {
						currentPlayer.hand.remove(card);
						currentPlayer.reveal(card, cardResponsible, context);
						currentPlayer.discard(card, cardResponsible, context);
					}
				}
				break;
		}
	}

	static {
		boonsNocturne.add(earthsgift = new Boons(Boons.Type.Earthsgift, "Nocturne"));
		boonsNocturne.add(fieldsgift = new Boons(Boons.Type.Fieldsgift, "Nocturne"));
		boonsNocturne.add(flamesgift = new Boons(Boons.Type.Flamesgift, "Nocturne"));
		boonsNocturne.add(forestsgift = new Boons(Boons.Type.Forestsgift, "Nocturne"));
		boonsNocturne.add(moonsgift = new Boons(Boons.Type.Moonsgift, "Nocturne"));
		boonsNocturne.add(mountainsgift = new Boons(Boons.Type.Mountainsgift, "Nocturne"));
		boonsNocturne.add(riversgift = new Boons(Boons.Type.Riversgift, "Nocturne"));
		boonsNocturne.add(seasgift = new Boons(Boons.Type.Seasgift, "Nocturne"));
		boonsNocturne.add(skysgift = new Boons(Boons.Type.Skysgift, "Nocturne"));
		boonsNocturne.add(sunsgift = new Boons(Boons.Type.Sunsgift, "Nocturne"));
		boonsNocturne.add(swampsgift = new Boons(Boons.Type.Swampsgift, "Nocturne"));
		boonsNocturne.add(windsgift = new Boons(Boons.Type.Windsgift, "Nocturne"));

		for (Boons e: boonsNocturne) allBoons.add(e);
		for (Boons e: allBoons) boonsMap.put(e.name,e);
	}
}
