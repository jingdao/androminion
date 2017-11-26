package com.vdom.comms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import com.vdom.core.Cards;
import com.vdom.core.Util.MultilevelComparator;

public class MyCard implements Serializable {
	private static final long serialVersionUID = -1367468781663470597L;

	public int id;
	public String name;
	public String expansion;
	public String originalExpansion;
	public String desc;
	
	public String originalSafeName;
	public String originalName;

	public int cost = 0;
	public int costDebt = 0;
	public boolean costPotion = false;
	public int vp = 0;
	public int gold = 0;

	public boolean isVictory  = false;
	public boolean isCurse    = false;
	public boolean isTreasure = false;
	public boolean isAction   = false;
	public boolean isReaction = false;
	public boolean isAttack   = false;
	public boolean isDuration = false;
	public boolean isPrize    = false;
	public boolean isPotion   = false;
	public boolean isBane     = false;
	public boolean isShelter  = false;
	public boolean isRuins    = false;
	public boolean isLooter   = false;
	public boolean isReserve  = false;
	public boolean isTraveller = false;
	public boolean isCastle = false;
	public boolean isGathering = false;
	public boolean isNight = false;
	public boolean isHeirloom = false;
	public boolean isFate = false;
	public boolean isDoom = false;
	public boolean isSpirit = false;
	public boolean isZombie = false;
	public boolean isKnight   = false;
	public boolean isOverpay  = false;
	public boolean isObelisk = false;
	
	public static final int SUPPLYPILE = 1;
	public static final int MONEYPILE = 2;
	public static final int VPPILE = 3;
	public static final int PRIZEPILE = 4;
	public static final int NON_SUPPLY_PILE = 5;	// Used for DA cards (for now)
	public static final int SHELTER_PILES = 6;
	public static final int RUINS_PILES = 7;
	public static final int KNIGHTS_PILES = 8;
	public static final int EVENTS = 9;
	public static final int CASTLE_PILES = 10;
	public static final int SPLIT_PILES = 11;
	public static final int LANDMARKS = 12;
	public static final int VIRTUAL_PILES = 13;

	public static final MyCard BOONS = new MyCard(-1,"Boons","","");
	public static final MyCard HEXES = new MyCard(-1,"Hexes","","");

	public int pile;

	 
	public MyCard(int id, String name, String originalSafeName, String originalName) {
		this.id = id;
		this.name = name;
		this.originalSafeName = originalSafeName;
		this.originalName = originalName;
		this.isKnight = originalName.equals("VirtualKnight");
	}
	
	/* This method is now unused. It had been copied to CardView.java
     * to simplify card type internationalization.
     */
	public String GetCardTypeString()
    {
	    String cardType = "";
        
        if (isAction)
        {
            cardType += "Action ";
            
            if (isAttack)
            {
                cardType += "- Attack ";
            }
            
            if (isLooter)
            {
                cardType += "- Looter ";
            }
            
            if (isRuins)
            {
                cardType += "- Ruins ";
            }
            
            if (isPrize)
            {
                cardType += "- Prize ";
            }
            
            if (isReaction)
            {
                cardType += "- Reaction ";
            }
            
            if (isDuration)
            {
                cardType += "- Duration ";
            }
            
            if (isVictory)
            {
                cardType += "- Victory ";
            }
            
            if (isKnight)
            {
                cardType += "- Knight ";
            }
            
            if (isShelter)
            {
                cardType += "- Shelter";
            }
        }
        else if (isTreasure)
        {
            cardType += "Treasure ";
            
            if (isVictory)
            {
                cardType += "- Victory ";
            }
            
            if (isReaction)
            {
                cardType += "- Reaction ";
            }
            
            if (isPrize)
            {
                cardType += "- Prize ";
            }
        }
        else if (isVictory)
        {
            cardType += "Victory ";
            
            if (isShelter)
            {
                cardType += "- Shelter";
            }
            
            if (isReaction)
            {
                cardType += "- Reaction";
            }
        }
        else if (name.equalsIgnoreCase("hovel"))
        {
            cardType += "Reaction - Shelter";
        }
        
        return cardType;
    }
	
	public String toString() {
		return "Card #" + id + " (" + cost + ") " + name + ": " + desc;
	}
	
	public MyCard clone(){
		MyCard m = new MyCard(this.id,this.name,this.originalSafeName,this.originalName);
		m.id = this.id;
		m.name = this.name;
		m.expansion = this.expansion;
		m.originalExpansion = this.originalExpansion;
		m.desc = this.desc;
		m.originalSafeName = this.originalSafeName;
		m.originalName = this.originalName;
		m.cost = this.cost;
		m.costDebt = this.costDebt;
		m.costPotion = this.costPotion;
		m.vp = this.vp;
		m.gold = this.gold;
		m.isVictory = this.isVictory;
		m.isCurse = this.isCurse;
		m.isTreasure = this.isTreasure;
		m.isAction = this.isAction;
		m.isReaction = this.isReaction;
		m.isAttack = this.isAttack;
		m.isDuration = this.isDuration;
		m.isPrize = this.isPrize;
		m.isPotion = this.isPotion;
		m.isBane = this.isBane;
		m.isShelter = this.isShelter;
		m.isRuins = this.isRuins;
		m.isLooter = this.isLooter;
		m.isReserve = this.isReserve;
		m.isTraveller = this.isTraveller;
		m.isCastle = this.isCastle;
		m.isGathering = this.isGathering;
		m.isNight = this.isNight;
		m.isHeirloom = this.isHeirloom;
		m.isFate = this.isFate;
		m.isDoom = this.isDoom;
		m.isSpirit = this.isSpirit;
		m.isZombie = this.isZombie;
		m.isKnight = this.isKnight;
		m.isOverpay = this.isOverpay;
		m.pile = this.pile;
		return m;
	}

	static public class CardNameComparator implements Comparator<MyCard> {
		@Override
		public int compare(MyCard card0, MyCard card1) {
			return card0.name.compareTo(card1.name);
		}
	}

	static public class CardCostComparator implements Comparator<MyCard> {
		@Override
		public int compare(MyCard card0, MyCard card1) {
			if(card0.cost < card1.cost) {
				return -1;
			} else if(card0.cost > card1.cost) {
				return 1;
			} else if (card0.costDebt < card1.costDebt) {
				return -1;
			} else if (card0.costDebt > card1.costDebt) {
				return 1;
			} else if(card0.isKnight) {
				return -1;
			} else if(card1.isKnight) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	static public class CardPotionComparator implements Comparator<MyCard> {
		@Override
		public int compare(MyCard card0, MyCard card1) {
			if(card0.costPotion) {
				if(card1.costPotion) {
					return 0;
				} else {
					return 1;
				}
			} else if(card1.costPotion) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	static public class CardTypeComparator implements Comparator<MyCard> {
		@Override
		public int compare(MyCard card0, MyCard card1) {
			if(card0.isAction) {
				if(card1.isAction) {
					return 0;
				} else {
					return -1;
				}
			} else if(card1.isAction) {
				return 1;
			} else if(card0.isTreasure || card0.isPotion) {
				if(card1.isTreasure || card1.isPotion) {
					return 0;
				} else {
					return -1;
				}
			} else if(card1.isTreasure || card1.isPotion) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * Comparator for sorting cards by cost, potion and then by name
	 * Used for sorting on table
	 */
	static public class CardCostNameComparator extends MultilevelComparator<MyCard> {
		private static final ArrayList<Comparator<MyCard>> cmps = new ArrayList<Comparator<MyCard>>();
		static {
			cmps.add(new CardCostComparator());
			cmps.add(new CardPotionComparator());
			cmps.add(new CardNameComparator());
		}
		public CardCostNameComparator() {
			super(cmps);
		}
	}
	
	/**
	 * Comparator for sorting cards in hand.
	 * Sort by type then by cost and last by name
	 */
	static public class CardHandComparator extends MultilevelComparator<MyCard> {
		private static final ArrayList<Comparator<MyCard>> cmps = new ArrayList<Comparator<MyCard>>();
		static {
			cmps.add(new CardTypeComparator());
			cmps.add(new CardCostComparator());
			cmps.add(new CardNameComparator());
		}
		public CardHandComparator() {
			super(cmps);
		}
	}
}
