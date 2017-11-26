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
import com.vdom.api.DurationCard;

public class Hexes {
	public static ArrayList<Hexes> hexesNocturne = new ArrayList<Hexes>();
	public static ArrayList<Hexes> allHexes = new ArrayList<Hexes>();
    public static HashMap<String,Hexes> hexesMap = new HashMap<String,Hexes>();

	public enum Type {
		BadOmens, Delusion, Envy, Famine, Fear, Greed,
		Haunting, Locusts, Misery, Plague, Poverty, War
	}

	public static final Hexes badOmens;
	public static final Hexes delusion;
	public static final Hexes envy;
	public static final Hexes famine;
	public static final Hexes fear;
	public static final Hexes greed;
	public static final Hexes haunting;
	public static final Hexes locusts;
	public static final Hexes misery;
	public static final Hexes plague;
	public static final Hexes poverty;
	public static final Hexes war;

	public Type type;
	public String name;
	public String description = "";
	public String expansion = "";

	public Hexes(Type type,String expansion) {
		this.type = type;
		this.name = type.toString().toLowerCase();
		this.expansion = expansion;
	}

	public void applyEffect(Game game, MoveContext context, Player currentPlayer,Card cardResponsible) {
		switch (this.type) {
			case BadOmens:
				while (currentPlayer.getDeckSize() > 0) {
					currentPlayer.discard(game.draw(currentPlayer), cardResponsible, null, false);
				}
				for (int i=0;i<2;i++) {
					if (currentPlayer.discard.contains(Cards.copper)) {
						Card toTopdeck = currentPlayer.discard.get(Cards.copper);
						currentPlayer.discard.remove(toTopdeck);
						currentPlayer.putOnTopOfDeck(toTopdeck);
					}
				}
				break;
			case Delusion:
				break;
			case Envy:
				break;
			case Famine:
                ArrayList<Card> topOfTheDeck = new ArrayList<Card>();
                ArrayList<Card> cardToDiscard = new ArrayList<Card>();
                for (int i = 0; i < 3; i++) {
                    Card card = game.draw(currentPlayer);
                    if (card != null) {
						currentPlayer.reveal(card, cardResponsible, context);
						if (card instanceof ActionCard) {
							cardToDiscard.add(card);
						} else {
							topOfTheDeck.add(card);
						}
					}
                }
                for (Card c: cardToDiscard) {
                	currentPlayer.discard(c, cardResponsible, context);
                }
				while (topOfTheDeck.size() > 0) {
					currentPlayer.deck.add(0,topOfTheDeck.remove(Game.rand.nextInt(topOfTheDeck.size())));
				}
				break;
			case Fear:
				if (currentPlayer.hand.size() >= 5) {
					HashSet<String> cardNames = new HashSet<String>();
					ArrayList<Card> options = new ArrayList<Card>();
					for (Card card : currentPlayer.hand) {
						if ((card instanceof TreasureCard || card instanceof ActionCard) && !cardNames.contains(card.getName())) {
							options.add(card);
							cardNames.add(card.getName());
						}
					}
					if (options.size() > 0) {
						Card ctd = currentPlayer.controlPlayer.fear_cardToDiscard(context,options.toArray(new Card[0]));
						currentPlayer.hand.remove(ctd);
						currentPlayer.discard(ctd, cardResponsible, context);
					} else {
						for (Card card : currentPlayer.getHand()) {
							currentPlayer.reveal(card, cardResponsible, context);
						}
					}
				}
				break;
			case Greed:
				currentPlayer.gainNewCard(Cards.copper, Cards.hexCard, context);
				break;
			case Haunting:
				if (currentPlayer.hand.size() >= 4) {
                    Card cardToTopdeck = currentPlayer.controlPlayer.haunting_cardToPutBackOnDeck(context);
					currentPlayer.hand.remove(cardToTopdeck);
					currentPlayer.putOnTopOfDeck(cardToTopdeck);
				}
				break;
			case Locusts:
				Card topCard = game.draw(currentPlayer);
				if (topCard != null) {
					currentPlayer.trash(topCard, cardResponsible, context);
					if (topCard.equals(Cards.copper) || topCard.equals(Cards.estate)) {
						currentPlayer.gainNewCard(Cards.curse, cardResponsible, context);
					} else {
						ArrayList<Card> validCards = new ArrayList<Card>();
						for (Card card : game.getCardsInGame()) {
							if (Cards.isSupplyCard(card) && game.getCardsLeftInPile(card) > 0
								&& game.getPile(game.getOriginCard(card)).card().equals(card)
								&& card.getCost(context) < topCard.getCost(context)
								&& (card instanceof ActionCard && topCard instanceof ActionCard
								|| card instanceof TreasureCard && topCard instanceof TreasureCard
								|| card instanceof VictoryCard && topCard instanceof VictoryCard
								|| card instanceof DurationCard && topCard instanceof DurationCard
								|| card.isNight() && topCard.isNight()
								|| card.isReserve() && topCard.isReserve()
								|| card.isAttack() && topCard.isAttack()
								|| card.isFate() && topCard.isFate()
								|| Cards.isReaction(card) && Cards.isReaction(topCard))) {
								validCards.add(card);
							}
						}
						if (validCards.size() == 0)
							return;
						Card card;
						if (validCards.size() == 1)
							card = validCards.get(0);
						else
							card = currentPlayer.controlPlayer.locusts_cardToObtain(context, validCards.toArray(new Card[0]));
						currentPlayer.gainNewCard(card, cardResponsible, context);
					}
				}
				break;
			case Misery:
				break;
			case Plague:
				currentPlayer.gainNewCard(Cards.curse, Cards.hexCard, context);
				break;
			case Poverty:
				if (currentPlayer.hand.size() > 4) {
                    Card[] cardsToKeep = currentPlayer.controlPlayer.poverty_cardsToKeep(context);
                    currentPlayer.discardRemainingCardsFromHand(context, cardsToKeep, cardResponsible, 3);
				}
				break;
			case War:
				Card draw;
				while ((draw = game.draw(currentPlayer)) != null) {
					if (draw.getCost(context) == 3 || draw.getCost(context) == 4) {
						currentPlayer.trash(draw, cardResponsible, context);
						break;
					} else {
						currentPlayer.reveal(draw, cardResponsible, context);
						currentPlayer.discard(draw, cardResponsible, null);
					}
				}
				break;
		}
	}

	static {
		hexesNocturne.add(badOmens = new Hexes(Hexes.Type.BadOmens, "Nocturne"));
		hexesNocturne.add(delusion = new Hexes(Hexes.Type.Delusion, "Nocturne"));
		hexesNocturne.add(envy = new Hexes(Hexes.Type.Envy, "Nocturne"));
		hexesNocturne.add(famine = new Hexes(Hexes.Type.Famine, "Nocturne"));
		hexesNocturne.add(fear = new Hexes(Hexes.Type.Fear, "Nocturne"));
		hexesNocturne.add(greed = new Hexes(Hexes.Type.Greed, "Nocturne"));
		hexesNocturne.add(haunting = new Hexes(Hexes.Type.Haunting, "Nocturne"));
		hexesNocturne.add(locusts = new Hexes(Hexes.Type.Locusts, "Nocturne"));
		hexesNocturne.add(misery = new Hexes(Hexes.Type.Misery, "Nocturne"));
		hexesNocturne.add(plague = new Hexes(Hexes.Type.Plague, "Nocturne"));
		hexesNocturne.add(poverty = new Hexes(Hexes.Type.Poverty, "Nocturne"));
		hexesNocturne.add(war = new Hexes(Hexes.Type.War, "Nocturne"));
		for (Hexes e: hexesNocturne) allHexes.add(e);
		for (Hexes e: allHexes) hexesMap.put(e.name,e);
	}
}
