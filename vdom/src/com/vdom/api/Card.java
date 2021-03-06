package com.vdom.api;

import com.vdom.core.CardImpl;
import com.vdom.core.Cards.Type;
import com.vdom.core.MoveContext;


public interface Card {
	public Type getType();

    public String getName();

    public String getSafeName();
    
    public String getExpansion();

    public String getStats();

    public String getDescription();

    public int getCost(MoveContext context);

    public int getCost(MoveContext context, boolean buyPhase);

    public boolean costPotion();

	public int costDebt();
    
    public boolean isPrize();
    
    public boolean isShelter();
    
    public boolean isLooter();
    
    public boolean isRuins();
    
    public boolean isKnight();
    
    public boolean isOverpay();

	public boolean isReserve();

	public boolean isTraveller();

	public boolean isCastle();

	public boolean isGathering();

	public boolean isNight();

	public boolean isHeirloom();

	public boolean isFate();

	public boolean isDoom();

	public boolean isSpirit();

	public boolean isZombie();

	public boolean isAttack();
    
    public Integer getId();
    
    public void isBought(MoveContext context);
    
    public void isTrashed(MoveContext context);
    
	public boolean isImpersonatingAnotherCard();
    public Card behaveAsCard();
    public CardImpl getControlCard();

    public boolean isTemplateCard();
    public CardImpl getTemplateCard();

    public CardImpl instantiate();
    
    //public void isGained(MoveContext context);
}
