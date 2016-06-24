package com.vdom.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.vdom.api.ActionCard;
import com.vdom.api.Card;

public class VariableCardPile extends AbstractCardPile {
	
	HashMap<String, SingleCardPile> piles;
	Card top;
	Card bottom;
	
	public VariableCardPile(PileType piletype, int count) {
		this.type = piletype;
		this.cards = new ArrayList<Card>();
		this.piles = new HashMap<String, SingleCardPile>();

		// TODO: put in checks to make sure template card is never
        // "put into play".
		switch (this.type) {
		case RuinsPile:
			cards = this.generateRuinsPile(count);
			break;
		case KnightsPile:
			for (Card c: Cards.knightsCards) {
				cards.add(c);
			}
			Collections.shuffle(cards);
			break;
		case CastlePile:
			for (Card c : Cards.castleCards)
				cards.add(c);
			if (count > 8) {
				cards.add(0,Cards.humbleCastle);
				cards.add(3,Cards.smallCastle);
				cards.add(6,Cards.opulentCastle);
				cards.add(10,Cards.kingsCastle);
			}
			break;
		default:
			break;
		}
	}

	public VariableCardPile(Card top, Card bottom) {
		this.top = top;
		this.bottom = bottom;
		this.type = PileType.SplitPile;
		this.cards = new ArrayList<Card>();
		this.piles = new HashMap<String, SingleCardPile>();
		for (int i=0;i<5;i++)
			cards.add(top.instantiate());
		for (int i=0;i<5;i++)
			cards.add(bottom.instantiate());
	}

	@Override
	public Card card() {
		if (cards.isEmpty()) {
			switch (type) {
			case RuinsPile:
				return Cards.virtualRuins;
			case KnightsPile:
				return Cards.virtualKnight;
			case CastlePile:
				return Cards.virtualCastle;
			default:
				return null;
			}
		}
		if (this.type == PileType.SplitPile)
			return topCard();
		if (piles.isEmpty()) {
			return null;
		}
		return piles.get(topCard().getName()).card();
	}
	
	public void addLinkedPile(SingleCardPile p) {
		this.piles.put(p.card().getName(), p);
	}

	@Override
	public void addCard(Card card) {
		ActionCard ac = null;
		if (card instanceof ActionCard) {
			ac = (ActionCard) card;
		} else return;
		
		if (piles.containsKey(card.getName()))
			piles.get(card.getName()).addCard(card);

		switch (type) {
		case KnightsPile:
			if (ac.isKnight()) {
				cards.add(0, card);
			}
			break;
		case RuinsPile:
			if (ac.isRuins()) {
				cards.add(0, card);
			}
			break;
		case CastlePile:
			if (card.isCastle()) {
				cards.add(0, card);
			}
			break;
		case SplitPile:
			cards.add(0,card);
			break;
		default:
			break;
		}
		
	}

	@Override
	public Card removeCard() {
		if (cards.isEmpty()) {
			return null;
		}
		return cards.remove(0);
	}
	
    private ArrayList<Card> generateRuinsPile(int count) {
    	ArrayList<Card> ruins = new ArrayList<Card>();
    	ArrayList<Card> ret = new ArrayList<Card>();
    	
    	for (int i = 0; i < 10; i++) {
    		ruins.add(Cards.abandonedMine);
    		ruins.add(Cards.ruinedLibrary);
    		ruins.add(Cards.ruinedMarket);
    		ruins.add(Cards.ruinedVillage);
    		ruins.add(Cards.survivors);
    	}
    	
    	Collections.shuffle(ruins);
    	
    	for (Card c : ruins) {
    		ret.add(c);
    		if (ret.size() >= count) break;
    	}
    	
    	return ret;
    }

	public SingleCardPile getTopLinkedPile() {
		if (topCard() == null) return null;
		return piles.get(topCard().getName());
	}
	
	private Card topCard() {
		if (cards.isEmpty()) return null;
		return cards.get(0);
	}


}
