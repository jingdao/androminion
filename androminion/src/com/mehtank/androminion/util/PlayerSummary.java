package com.mehtank.androminion.util;

/**
 * Information about one player
 * 
 * deck, hand size, total number of cards etc.
 */

public class PlayerSummary {
	@SuppressWarnings("unused")
	private static final String TAG = "PlayerSummary";
	
	public String name;
	public String realName;
	public int deckSize;
	public int handSize;
	public int numCards;
	public int pt;
	public int vt;
	public int gct; // Guilds Coin Tokens
	public int dt;
	public boolean md;
	public boolean mc;
	public boolean jt;
	public boolean lw;
	public boolean dl;
	public boolean ev;
	public boolean ms;
	public boolean tm;
	public boolean highlight = false;
	public int turns;
	
	public PlayerSummary(String name) {
		this.name = name;
	}
	
	public void set(String name, int turns, int deckSize, int handSize, int numCards, int pt, int vt, int gct, int dt, boolean md, boolean mc, boolean jt, boolean lw, boolean dl, boolean ev, boolean ms, boolean tm, boolean highlight){
		this.name = name;
		this.turns = turns;
		this.deckSize = deckSize;
		this.handSize = handSize;
		this.numCards = numCards;
		this.pt = pt;
		this.vt = vt;
		this.gct = gct;
		this.dt = dt;
		this.md = md;
		this.mc = mc;
		this.jt = jt;
		this.lw = lw;
		this.dl = dl;
		this.ev = ev;
		this.ms = ms;
		this.tm = tm;
		this.highlight = highlight;
	}
	
	@Override
	public String toString() {
		return name; 
	}
}
