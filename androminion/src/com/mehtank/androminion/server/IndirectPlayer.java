package com.mehtank.androminion.server;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;

import com.mehtank.androminion.R;

import com.vdom.api.ActionCard;
import com.vdom.api.Card;
import com.vdom.api.CardCostComparator;
import com.vdom.api.TreasureCard;
import com.vdom.api.VictoryCard;
import com.vdom.comms.SelectCardOptions;
import com.vdom.comms.SelectCardOptions.PickType;
import com.vdom.comms.SelectCardOptions.ActionType;
import com.vdom.core.ActionCardImpl;
import com.vdom.core.CardList;
import com.vdom.core.Cards;
import com.vdom.core.Game;
import com.vdom.core.MoveContext;
import com.vdom.core.MoveContext.PileSelection;
import com.vdom.core.Player;
import com.vdom.core.QuickPlayPlayer;
import com.vdom.core.Event;
import com.vdom.core.Landmarks;
import com.vdom.core.Boons;
/**
 * Class that you can use to play remotely.
 */
public abstract class IndirectPlayer extends QuickPlayPlayer {
    @SuppressWarnings("unused")
    private static final String TAG = "IndirectPlayerOrig";
    
    public abstract Card intToCard(int i);
    public abstract int cardToInt(Card card);
    public abstract int[] cardArrToIntArr(Card[] cards);
    public Card nameToCard(String o, Card[] cards) {
        for (Card c : cards)
            if (c.getName().equals(o))
                return c;
        return null;
    }

    public Card localNameToCard(String o, Card[] cards) {
        for (Card c : cards)
            if (Strings.getCardName(c).equals(o))
                return c;
        return null;
    }

    abstract protected String selectString(MoveContext context, String header, String[] s);
    abstract protected int[] orderCards(MoveContext context, int[] cards);
    abstract protected int[] orderCards(MoveContext context, int[] cards, String header);

    abstract protected Object[] pickCards(MoveContext context, String header, SelectCardOptions sco, int count, boolean exact);
    private Object pickACard(MoveContext context, String header, SelectCardOptions sco) {
        Object[] cs = pickCards(context, header, sco, 1, true);
        return (cs == null ? null : cs[0]);
    }

    @Override
    public boolean isAi() {
        return false;
    }

    public String selectString(MoveContext context, Card cardResponsible, String[] s) {
        return selectString(context, Strings.format(R.string.card_options_header, getCardName(cardResponsible)), s);
    }

    public String selectString(MoveContext context, int resId, Card cardResponsible, String[] s) {
        return selectString(context, getString(resId) + " [" + getCardName(cardResponsible) + "]", s);
    }

    public String getString(int id) {
        return Strings.getString(id);
    }

    public String getCardName(Card card) {
        return Strings.getCardName(card);
    }

    public String getActionString(ActionType action, Card cardResponsible, String opponentName) {
        switch (action) {
        case DISCARD: return Strings.format(R.string.card_to_discard, getCardName(cardResponsible));
        case DISCARDFORCARD: return Strings.format(R.string.card_to_discard_for_card, getCardName(cardResponsible));
        case DISCARDFORCOIN: return Strings.format(R.string.card_to_discard_for_coin, getCardName(cardResponsible));
        case REVEAL: return Strings.format(R.string.card_to_reveal, getCardName(cardResponsible));
        case GAIN: return Strings.format(R.string.card_to_gain, getCardName(cardResponsible));
        case TRASH: return Strings.format(R.string.card_to_trash, getCardName(cardResponsible));
        case NAMECARD: return Strings.format(R.string.card_to_name, getCardName(cardResponsible));
        case OPPONENTDISCARD: return Strings.format(R.string.opponent_discard, opponentName, getCardName(cardResponsible));
		case PLAY: return Strings.format(R.string.card_to_play, getCardName(cardResponsible));
        }
        return null;
    }

    public String getActionString(ActionType action, Event cardResponsible, String opponentName) {
        switch (action) {
        case DISCARD: return Strings.format(R.string.card_to_discard, cardResponsible.name);
        case DISCARDFORCARD: return Strings.format(R.string.card_to_discard_for_card, cardResponsible.name);
        case DISCARDFORCOIN: return Strings.format(R.string.card_to_discard_for_coin, cardResponsible.name);
        case REVEAL: return Strings.format(R.string.card_to_reveal, cardResponsible.name);
        case GAIN: return Strings.format(R.string.card_to_gain, cardResponsible.name);
        case TRASH: return Strings.format(R.string.card_to_trash, cardResponsible.name);
        case NAMECARD: return Strings.format(R.string.card_to_name, cardResponsible.name);
        case OPPONENTDISCARD: return Strings.format(R.string.opponent_discard, opponentName, cardResponsible.name);
        }
        return null;
    }

    public String getActionString(ActionType action, Landmarks cardResponsible, String opponentName) {
        switch (action) {
        case DISCARD: return Strings.format(R.string.card_to_discard, cardResponsible.name);
        case DISCARDFORCARD: return Strings.format(R.string.card_to_discard_for_card, cardResponsible.name);
        case DISCARDFORCOIN: return Strings.format(R.string.card_to_discard_for_coin, cardResponsible.name);
        case REVEAL: return Strings.format(R.string.card_to_reveal, cardResponsible.name);
        case GAIN: return Strings.format(R.string.card_to_gain, cardResponsible.name);
        case TRASH: return Strings.format(R.string.card_to_trash, cardResponsible.name);
        case NAMECARD: return Strings.format(R.string.card_to_name, cardResponsible.name);
        case OPPONENTDISCARD: return Strings.format(R.string.opponent_discard, opponentName, cardResponsible.name);
        }
        return null;
    }

    private String getActionString(ActionType action, Card cardResponsible) {
        return getActionString(action, cardResponsible, null);
    }
    
    private Card getCardFromHand(MoveContext context, String header, SelectCardOptions sco) {
        Card[] cs = getFromHand(context, header, sco.setCount(1).exactCount());
        return (cs == null ? null : cs[0]);
    }
    
    private Card[] getFromHand(MoveContext context, String header, SelectCardOptions sco) {
      sco =  sco.fromHand();
      CardList localHand = (context.player.isPossessed()) ? context.player.getHand() : getHand();
      if (localHand.size() == 0) {
          return null;
      } else if (sco.count == Integer.MAX_VALUE) {
          sco.setCount(localHand.size());
      } else if (sco.count < 0) {
          sco.setCount(localHand.size() + sco.count).exactCount();
      } else if (localHand.size() < sco.count && sco.exactCount) {
        sco.setCount(localHand.size());
      }

      ArrayList<Card> handList = new ArrayList<Card>();

      for (Card card : localHand) {
          if (sco.checkValid(card)) {
              handList.add(card);
              sco.addValidCard(cardToInt(card));
          }
      }
      
      if (sco.allowedCards.size() == 0)
          return null;
      else if (sco.allowedCards.size() == 1  || ((sco.isAction || sco.isNight) && Collections.frequency(sco.allowedCards, sco.allowedCards.get(0)) == sco.allowedCards.size()))
          sco.defaultCardSelected = sco.allowedCards.get(0);

      String str = "";
      if (sco.isAction) {
         if(sco.count == 1)
             str = Strings.format(R.string.select_one_action_from_hand, header);
         else if(sco.exactCount)
             str = Strings.format(R.string.select_exactly_x_actions_from_hand, "" + sco.count, header);
         else
             str = Strings.format(R.string.select_up_to_x_actions_from_hand, "" + sco.count, header);
      } else if (sco.isTreasure) {
          if(sco.count == 1)
              str = Strings.format(R.string.select_one_treasure_from_hand, header);
          else if(sco.exactCount)
              str = Strings.format(R.string.select_exactly_x_treasures_from_hand, "" + sco.count, header);
          else
              str = Strings.format(R.string.select_up_to_x_treasures_from_hand, "" + sco.count, header);
      } else if (sco.isVictory) {
          if(sco.count == 1)
              str = Strings.format(R.string.select_one_victory_from_hand, header);
          else if(sco.exactCount)
              str = Strings.format(R.string.select_exactly_x_victorys_from_hand, "" + sco.count, header);
          else
              str = Strings.format(R.string.select_up_to_x_victorys_from_hand, "" + sco.count, header);
      } else if (sco.isNonTreasure) {
          if(sco.count == 1)
              str = Strings.format(R.string.select_one_nontreasure_from_hand, header);
          else if(sco.exactCount)
              str = Strings.format(R.string.select_exactly_x_nontreasures_from_hand, "" + sco.count, header);
          else
              str = Strings.format(R.string.select_up_to_x_nontreasures_from_hand, "" + sco.count, header);
      } else if (sco.isNight) {
          if(sco.count == 1)
              str = Strings.format(R.string.select_one_night_from_hand, header);
          else if(sco.exactCount)
              str = Strings.format(R.string.select_exactly_x_night_from_hand, "" + sco.count, header);
          else
              str = Strings.format(R.string.select_up_to_x_night_from_hand, "" + sco.count, header);
      } else {
          if(sco.count == 1)
              str = Strings.format(R.string.select_one_card_from_hand, header);
          else if(sco.exactCount)
              str = Strings.format(R.string.select_exactly_x_cards_from_hand, "" + sco.count, header);
          else
              str = Strings.format(R.string.select_up_to_x_cards_from_hand, "" + sco.count, header);
      }

      Card[] tempCards = (Card[]) pickCards(context, str, sco, sco.count, sco.exactCount);
      if (tempCards == null)
          return null;

      // Hack to notify that "All" was selected
      if(tempCards.length == 0) {
          return tempCards;
      }

      for (int i=0; i<tempCards.length; i++)
          for (Card c : handList)
              if (c.equals(tempCards[i])) {
                  tempCards[i] = c;
                  handList.remove(c);
                  break;
              }

      return tempCards;
  }
    
    private Card getFromTable(MoveContext context, String header, SelectCardOptions sco) {
        sco.fromTable();
        Card[] cards = context.getCardsInGame();
        
        for (Card card : cards) {
            if (sco.allowEmpty || !context.game.isPileEmpty(card)) {
                if (sco.checkValid(card, card.getCost(context))) {
                    sco.addValidCard(cardToInt(card));
                }
            }
        }
        
        if (sco.getAllowedCardCount() == 0) {
            // No cards fit the filter, so return early
            return null;
        }
        else if (sco.getAllowedCardCount() == 1 && !sco.isPassable()) {
            // Only one card available and player can't pass...go ahead and return
            return intToCard(sco.allowedCards.get(0));
        }
        
        String minCostString = (sco.minCost <= 0) ? "" : "" + sco.minCost;
        String maxCostString = (sco.maxCost == Integer.MAX_VALUE) ? "" : "" + sco.maxCost + sco.potionString();
        String selectString;
	    
        if (sco.fromPrizes)
        	selectString = header;
        else if (sco.minCost == sco.maxCost) {
            if (sco.isAttack) {
                selectString = Strings.format(R.string.select_from_table_attack, maxCostString, header);
            } else if (sco.isAction) {
                selectString = Strings.format(R.string.select_from_table_exact_action, maxCostString, header);
            } else {
                selectString = Strings.format(R.string.select_from_table_exact, maxCostString, header);
            }
        } else if (sco.minCost <= 0 && sco.maxCost < Integer.MAX_VALUE) {
		    if (sco.isVictory) {
		        selectString = Strings.format(R.string.select_from_table_max_vp, maxCostString, header);
		    } else if (sco.isNonVictory) {
		        selectString = Strings.format(R.string.select_from_table_max_non_vp, maxCostString, header);
		    } else if (sco.isTreasure) {
		        selectString = Strings.format(R.string.select_from_table_max_treasure, maxCostString, header);
		    } else if (sco.isAction) {
		        selectString = Strings.format(R.string.select_from_table_max_action, maxCostString, header);
		    } else {
		    	selectString = Strings.format(R.string.select_from_table_max, maxCostString, header);
		    }
    	} else if (sco.minCost > 0 && sco.maxCost < Integer.MAX_VALUE) {
            selectString = Strings.format(R.string.select_from_table_between, minCostString, maxCostString, header);
    	} else if (sco.minCost > 0) {
            selectString = Strings.format(R.string.select_from_table_min, minCostString + sco.potionString(), header);
    	} else {
            selectString = Strings.format(R.string.select_from_table, header);
    	}
        return (Card) pickACard(context, selectString, sco);
    }
    
    private Object getCardOrEventFromTable(MoveContext context, String header, SelectCardOptions sco) {
        sco.fromTable();
        Card[] cards = context.getCardsInGame();
        
        for (Card card : cards) {
            if (sco.allowEmpty || !context.game.isPileEmpty(card)) {
                if (sco.checkValid(card, card.getCost(context))) {
                    sco.addValidCard(cardToInt(card));
                }
            }
        }
        
		if (!context.player.missionTurn) {
			if (sco.getAllowedCardCount() == 0) {
				// No cards fit the filter, so return early
				return null;
			}
			else if (sco.getAllowedCardCount() == 1 && !sco.isPassable()) {
				// Only one card available and player can't pass...go ahead and return
				return intToCard(sco.allowedCards.get(0));
			}
		}
        
        String minCostString = (sco.minCost <= 0) ? "" : "" + sco.minCost;
        String maxCostString = (sco.maxCost == Integer.MAX_VALUE) ? "" : "" + sco.maxCost + sco.potionString();
        String selectString;
	    
        if (sco.fromPrizes)
        	selectString = header;
        else if (sco.minCost == sco.maxCost) {
            if (sco.isAttack) {
                selectString = Strings.format(R.string.select_from_table_attack, maxCostString, header);
            } else if (sco.isAction) {
                selectString = Strings.format(R.string.select_from_table_exact_action, maxCostString, header);
            } else {
                selectString = Strings.format(R.string.select_from_table_exact, maxCostString, header);
            }
        } else if (sco.minCost <= 0 && sco.maxCost < Integer.MAX_VALUE) {
		    if (sco.isVictory) {
		        selectString = Strings.format(R.string.select_from_table_max_vp, maxCostString, header);
		    } else if (sco.isNonVictory) {
		        selectString = Strings.format(R.string.select_from_table_max_non_vp, maxCostString, header);
		    } else if (sco.isTreasure) {
		        selectString = Strings.format(R.string.select_from_table_max_treasure, maxCostString, header);
		    } else if (sco.isAction) {
		        selectString = Strings.format(R.string.select_from_table_max_action, maxCostString, header);
		    } else {
		    	selectString = Strings.format(R.string.select_from_table_max, maxCostString, header);
		    }
    	} else if (sco.minCost > 0 && sco.maxCost < Integer.MAX_VALUE) {
            selectString = Strings.format(R.string.select_from_table_between, minCostString, maxCostString, header);
    	} else if (sco.minCost > 0) {
            selectString = Strings.format(R.string.select_from_table_min, minCostString + sco.potionString(), header);
    	} else {
            selectString = Strings.format(R.string.select_from_table, header);
    	}
        return pickACard(context, selectString, sco);
    }
    
    public int selectInt(MoveContext context, String header, int maxInt, int errVal) {
        ArrayList<String> options = new ArrayList<String>();
        for (int i=0; i<=maxInt; i++)
            options.add("" + i);

        String o = selectString(context, header, options.toArray(new String[0]));

        try {
            return Integer.parseInt(o);
        } catch (NumberFormatException e) {
            return errVal;
        }
    }
    public boolean selectBoolean(MoveContext context, Card c, String strTrue, String strFalse) {
        String header = getCardName(c);
        return selectBoolean(context, header, strTrue, strFalse);
    }

    public boolean selectBoolean(MoveContext context, String header, String strTrue, String strFalse) {
        String [] s = new String [] {strTrue, strFalse};
        String r = selectString(context, header, s);
        if (strTrue.equals(r))
            return true;
        return false;
    }
    public boolean selectBooleanWithCard(MoveContext context, String header, Card c, String strTrue, String strFalse) {
        return selectBoolean(context, header + Strings.getCardName(c), strTrue, strFalse);
    }
    public boolean selectBooleanCardRevealed(MoveContext context, Card cardResponsible, Card cardRevealed, String strTrue, String strFalse) {
        String c1 = getCardName(cardResponsible);
        String c2 = getCardName(cardRevealed);
        String query = Strings.format(R.string.card_revealed, c1, c2);
        return selectBoolean(context, query, strTrue, strFalse);
    }

    public boolean selectBooleanCardRevealedFromHand(MoveContext context, Card cardResponsible, Card cardRevealed, String strTrue, String strFalse) {
        String c1 = getCardName(cardResponsible);
        String c2 = getCardName(cardRevealed);
        String query = Strings.format(R.string.card_revealed_from_hand, c1, c2);
        return selectBoolean(context, query, strTrue, strFalse);
    }

    public boolean selectBooleanCardRevealedAndPlayer(MoveContext context, Card cardResponsible, Card cardRevealed, Player p, String strTrue, String strFalse) {
        String c1 = getCardName(cardResponsible);
        String c2 = getCardName(cardRevealed);
        String query = Strings.format(R.string.card_revealed_from_player, p.getPlayerName(), c1, c2);
        return selectBoolean(context, query, strTrue, strFalse);
    }

    @Override
    public Card[] topOfDeck_orderCards(MoveContext context, Card[] cards) {
        if (context.isQuickPlay() && shouldAutoPlay_topOfDeck_orderCards(context, cards)) {
            return super.topOfDeck_orderCards(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards));
        for (int i : order)
            orderedCards.add(cards[i]);
        return orderedCards.toArray(new Card[0]);
    }

    private Card[] doAction(MoveContext context, boolean singleCard) {
        int actionCount = 0;
        Card actionCard = null;
        for (Card card : (context.player.isPossessed()) ? context.player.getHand() : getHand()) {
            if (card instanceof ActionCard) {
                actionCount++;
                actionCard = card;
            }
        }
        if (actionCount == 0)
            return null;

        SelectCardOptions sco = new SelectCardOptions().isAction().setPassable(getString(R.string.none));
        if (singleCard) 
        	sco.setCount(1).setPickType(PickType.PLAY);
        else 
        	sco.setCount(actionCount).ordered().setPickType(PickType.PLAY_IN_ORDER);
        
        Card[] cards = getFromHand(context, getString(R.string.part_play), sco);
        	
        if (cards == null)
            return null;
        // Hack that tells us that "Play the only one card" was selected
        else if (actionCount == 1 && cards.length == 0) {
            cards = new Card[1];
            cards[0] = actionCard;
        	}
        return cards;
    }
    
    private Card[] doNight(MoveContext context, boolean singleCard) {
        int actionCount = 0;
        Card actionCard = null;
        for (Card card : (context.player.isPossessed()) ? context.player.getHand() : getHand()) {
            if (card.isNight()) {
                actionCount++;
                actionCard = card;
            }
        }
        if (actionCount == 0)
            return null;

        SelectCardOptions sco = new SelectCardOptions().isNight().setPassable(getString(R.string.none));
        if (singleCard) 
        	sco.setCount(1).setPickType(PickType.PLAY);
        else 
        	sco.setCount(actionCount).ordered().setPickType(PickType.PLAY_IN_ORDER);
        
        Card[] cards = getFromHand(context, getString(R.string.part_play), sco);
        	
        if (cards == null)
            return null;
        // Hack that tells us that "Play the only one card" was selected
        else if (actionCount == 1 && cards.length == 0) {
            cards = new Card[1];
            cards[0] = actionCard;
        	}
        return cards;
    }
    
    @Override
    public Card doAction(MoveContext context) {
    	Card[] cards = doAction(context, true);
    	return (cards == null ? null : cards[0]); 
    }

    public Card doNight(MoveContext context) {
    	Card[] cards = doNight(context, true);
    	return (cards == null ? null : cards[0]); 
    }

    @Override
    public Card[] actionCardsToPlayInOrder(MoveContext context) {
        return doAction(context, false);
    }

    public Card[] nightCardsToPlayInOrder(MoveContext context) {
        return doNight(context, false);
    }

    @Override
    public Object doBuy(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isBuy().maxCost(context.getCoinAvailableForBuy()).copperCountInPlay(context.countCardsInPlay(Cards.copper)).potionCost(context.getPotions()).buyCardsAllowed(!context.player.missionTurn).setPassable(getString(R.string.end_turn)).setPickType(PickType.BUY);
		if (context.player.deluded && context.player.returnDeludedEnvious)
			sco.isNonAction();
        return getCardOrEventFromTable(context, getString(R.string.part_buy), sco);
    }

    @Override
    public ArrayList<TreasureCard> treasureCardsToPlayInOrder(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.treasureCardsToPlayInOrder(context);
        }

        int treasureCount = 0;
        for (Card card : (context.player.isPossessed()) ? context.player.getHand() : getHand()) {
            if (card instanceof TreasureCard) {
                treasureCount++;
            }
        }

        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPassable(getString(R.string.none)).setCount(treasureCount).ordered().setPickType(PickType.SELECT_WITH_ALL);
        Card[] cards = getFromHand(context, getString(R.string.use_for_money), sco);
        if (cards == null) {
            return null;
        }

        // Hack that tells us that "All" was selected
        if(cards.length == 0) {
            return super.treasureCardsToPlayInOrder(context);
        }

        ArrayList<TreasureCard> treasures = new ArrayList<TreasureCard>();
        for (int i = 0; i < cards.length; i++) {
            treasures.add((TreasureCard) cards[i]);
        }
        return treasures;
    }

    // ////////////////////////////////////////////
    // Card interactions - cards from the base game
    // ////////////////////////////////////////////
    @Override
    public Card workshop_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.workshop_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(4).potionCost(0);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.workshop), sco);
    }

    @Override
    public Card feast_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_feast_cardToObtain(context)) {
            return super.feast_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(5);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.feast), sco);
    }

    @Override
    public Card remodel_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_remodel_cardToTrash(context)) {
            return super.remodel_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.UPGRADE);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.remodel), sco);
    }

    @Override
    public Card remodel_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_remodel_cardToObtain(context, maxCost, potion)) {
            return super.remodel_cardToObtain(context, maxCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.remodel), sco);
    }

    @Override
    public TreasureCard mine_treasureToObtain(MoveContext context, int maxCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_mine_treasureToObtain(context, maxCost, potion)) {
            return super.mine_treasureToObtain(context, maxCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure().maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return (TreasureCard) getFromTable(context, getString(R.string.mine_part), sco);
    }

    @Override
    public Card[] militia_attack_cardsToKeep(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_militia_attack_cardsToKeep(context)) {
            return super.militia_attack_cardsToKeep(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.KEEP);
        return getFromHand(context, getString(R.string.militia_part), sco);
    }

    @Override
    public boolean chancellor_shouldDiscardDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_chancellor_shouldDiscardDeck(context)) {
            return super.chancellor_shouldDiscardDeck(context);
        }
        return selectBoolean(context, getCardName(Cards.chancellor), getString(R.string.chancellor_query), getString(R.string.pass));
    }

    @Override
    public TreasureCard mine_treasureFromHandToUpgrade(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_mine_treasureFromHandToUpgrade(context)) {
            return super.mine_treasureFromHandToUpgrade(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPickType(PickType.UPGRADE);
        return (TreasureCard) getCardFromHand(context, getCardName(Cards.mine), sco);
    }

    @Override
    public Card[] chapel_cardsToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_chapel_cardsToTrash(context)) {
            return super.chapel_cardsToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(4).setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getFromHand(context, getActionString(ActionType.TRASH, Cards.chapel), sco);
    }

    @Override
    public Card[] cellar_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_cellar_cardsToDiscard(context)) {
            return super.cellar_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.cellar), sco);
    }

    @Override
    public boolean library_shouldKeepAction(MoveContext context, ActionCard action) {
        if(context.isQuickPlay() && shouldAutoPlay_library_shouldKeepAction(context, action)) {
            return super.library_shouldKeepAction(context, action);
        }
        return selectBooleanCardRevealed(context, Cards.library, action, getString(R.string.keep), getString(R.string.discard));
    }

    @Override
    public boolean spy_shouldDiscard(MoveContext context, Player targetPlayer, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_spy_shouldDiscard(context, targetPlayer, card)) {
            return super.spy_shouldDiscard(context, targetPlayer, card);
        }
        return selectBooleanCardRevealedAndPlayer(context, Cards.spy, card, targetPlayer, getString(R.string.discard), getString(R.string.replace));
    }

    // ////////////////////////////////////////////
    // Card interactions - cards from the Intrigue
    // ////////////////////////////////////////////
    @Override
    public Card[] secretChamber_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_secretChamber_cardsToDiscard(context)) {
            return super.secretChamber_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.secretChamber), sco);
    }

    @Override
    public PawnOption[] pawn_chooseOptions(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_pawn_chooseOptions(context)) {
            return super.pawn_chooseOptions(context);
        }
        PawnOption[] os = new PawnOption[2];

        LinkedHashMap<String, PawnOption> h = new LinkedHashMap<String, PawnOption>();

        h.put(getString(R.string.pawn_one), PawnOption.AddCard);
        h.put(getString(R.string.pawn_two), PawnOption.AddAction);
        h.put(getString(R.string.pawn_three), PawnOption.AddBuy);
        h.put(getString(R.string.pawn_four), PawnOption.AddGold);

        String o1 = selectString(context, getString(R.string.pawn_option_one), h.keySet().toArray(new String[0]));
        os[0] = h.get(o1);
        h.remove(o1);
        String o2 = selectString(context, getString(R.string.pawn_option_one), h.keySet().toArray(new String[0]));
        os[1] = h.get(o2);
        return os;
    }

    @Override
    public TorturerOption torturer_attack_chooseOption(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_torturer_attack_chooseOption(context)) {
            return super.torturer_attack_chooseOption(context);
        }
        LinkedHashMap<String, TorturerOption> h = new LinkedHashMap<String, TorturerOption>();
        h.put(getString(R.string.torturer_option_one), TorturerOption.TakeCurse);
        h.put(getString(R.string.torturer_option_two), TorturerOption.DiscardTwoCards);

        return h.get(selectString(context, Cards.torturer, h.keySet().toArray(new String[0])));
    }

    @Override
    public StewardOption steward_chooseOption(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_steward_chooseOption(context)) {
            return super.steward_chooseOption(context);
        }
        LinkedHashMap<String, StewardOption> h = new LinkedHashMap<String, StewardOption>();

        h.put(getString(R.string.steward_option_one), StewardOption.AddCards);
        h.put(getString(R.string.steward_option_two), StewardOption.AddGold);
        h.put(getString(R.string.steward_option_three), StewardOption.TrashCards);

        return h.get(selectString(context, Cards.steward, h.keySet().toArray(new String[0])));
    }

    @Override
    public Card swindler_cardToSwitch(MoveContext context, int cost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_swindler_cardToSwitch(context, cost, potion)) {
            return super.swindler_cardToSwitch(context, cost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().exactCost(cost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, Strings.format(R.string.swindler_part, "" + cost + (potion ? "p" : "")), sco);
    }

    @Override
    public Card[] steward_cardsToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_steward_cardsToTrash(context)) {
            return super.steward_cardsToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.TRASH);
        return getFromHand(context, getActionString(ActionType.TRASH, Cards.steward), sco);
    }

    @Override
    public Card[] torturer_attack_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_torturer_attack_cardsToDiscard(context)) {
            return super.torturer_attack_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.torturer), sco);
    }

    @Override
    public Card courtyard_cardToPutBackOnDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_courtyard_cardToPutBackOnDeck(context)) {
            return super.courtyard_cardToPutBackOnDeck(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, Strings.format(R.string.courtyard_part_top_of_deck, getCardName(Cards.courtyard)), sco);
    }

    @Override
    public boolean baron_shouldDiscardEstate(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_baron_shouldDiscardEstate(context)) {
            return super.baron_shouldDiscardEstate(context);
        }
        return selectBoolean(context, getCardName(Cards.baron), getString(R.string.baron_option_one), getString(R.string.baron_option_two));
    }

    @Override
    public Card ironworks_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_ironworks_cardToObtain(context)) {
            return super.ironworks_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(4);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.ironworks), sco);
    }

    @Override
    public Card masquerade_cardToPass(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_masquerade_cardToPass(context)) {
            return super.masquerade_cardToPass(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.GIVE);
        return getCardFromHand(context, getString(R.string.masquerade_part), sco);
    }

    @Override
    public VictoryCard bureaucrat_cardToReplace(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_bureaucrat_cardToReplace(context)) {
            return super.bureaucrat_cardToReplace(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isVictory();
        return (VictoryCard) getCardFromHand(context, getString(R.string.bureaucrat_part), sco);
    }

    @Override
    public Card masquerade_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_masquerade_cardToTrash(context)) {
            return super.masquerade_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.masquerade), sco);
    }

    @Override
    public boolean miningVillage_shouldTrashMiningVillage(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_miningVillage_shouldTrashMiningVillage(context)) {
            return super.miningVillage_shouldTrashMiningVillage(context);
        }
        return selectBoolean(context, getCardName(Cards.miningVillage), getString(R.string.mining_village_option_one), getString(R.string.keep));
    }

    @Override
    public Card saboteur_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_saboteur_cardToObtain(context, maxCost, potion)) {
            return super.saboteur_cardToObtain(context, maxCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getString(R.string.saboteur_part), sco);
    }

    @Override
    public Card[] scout_orderCards(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_scout_orderCards(context, cards)) {
            return super.scout_orderCards(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards));
        for (int i : order)
            orderedCards.add(cards[i]);
        return orderedCards.toArray(new Card[0]);
    }

    @Override
    public NoblesOption nobles_chooseOptions(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_nobles_chooseOptions(context)) {
            return super.nobles_chooseOptions(context);
        }
        LinkedHashMap<String, NoblesOption> h = new LinkedHashMap<String, NoblesOption>();

        h.put(getString(R.string.nobles_option_one), NoblesOption.AddCards);
        h.put(getString(R.string.nobles_option_two), NoblesOption.AddActions);

        return h.get(selectString(context, Cards.nobles, h.keySet().toArray(new String[0])));
    }

    // Either return two cards, or null if you do not want to trash any cards.
    @Override
    public Card[] tradingPost_cardsToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_tradingPost_cardsToTrash(context)) {
            return super.tradingPost_cardsToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.TRASH);
        return getFromHand(context, getActionString(ActionType.TRASH, Cards.tradingPost), sco);
    }

    @Override
    public Card wishingWell_cardGuess(MoveContext context, ArrayList<Card> cardList) {
        if(context.isQuickPlay() && shouldAutoPlay_wishingWell_cardGuess(context)) {
            return super.wishingWell_cardGuess(context, cardList);
        }
        
        LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();

        // Add option to skip the guess
        h.put("None", null);
        
        for (Card c : cardList) {
            h.put(c.getName(), c);
        }
        
        String choice = selectString(context, getActionString(ActionType.NAMECARD, Cards.wishingWell), h.keySet().toArray(new String[0])); 
    
        return h.get(choice);
    }

    @Override
    public Card upgrade_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_upgrade_cardToTrash(context)) {
            return super.upgrade_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.upgrade), sco);
    }

    @Override
    public Card upgrade_cardToObtain(MoveContext context, int exactCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_upgrade_cardToObtain(context, exactCost, potion)) {
            return super.upgrade_cardToObtain(context, exactCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().exactCost(exactCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.upgrade), sco);
    }

    @Override
    public MinionOption minion_chooseOption(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_minion_chooseOption(context)) {
            return super.minion_chooseOption(context);
        }
        LinkedHashMap<String, MinionOption> h = new LinkedHashMap<String, MinionOption>();

        h.put(getString(R.string.minion_option_one), MinionOption.AddGold);
        h.put(getString(R.string.minion_option_two), MinionOption.RolloverCards);

        return h.get(selectString(context, Cards.minion, h.keySet().toArray(new String[0])));
    }

    @Override
    public Card[] secretChamber_cardsToPutOnDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_secretChamber_cardsToPutOnDeck(context)) {
            return super.secretChamber_cardsToPutOnDeck(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().ordered();
        return getFromHand(context, getString(R.string.secretchamber_part), sco);
    }

    // ////////////////////////////////////////////
    // Card interactions - cards from the Seaside
    // ////////////////////////////////////////////
    @Override
    public Card[] ghostShip_attack_cardsToPutBackOnDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_ghostShip_attack_cardsToPutBackOnDeck(context)) {
            return super.ghostShip_attack_cardsToPutBackOnDeck(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(-3).ordered();
        return getFromHand(context, getString(R.string.ghostship_part), sco);
        //return getOrderedFromHand(context, getString(R.string.ghostship_part), NOTPASSABLE, getHand().size() - 3, true);
    }

    @Override
    public Card salvager_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_salvager_cardToTrash(context)) {
            return super.salvager_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.salvager), sco);
    }

    @Override 
    public Card[] warehouse_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_warehouse_cardsToDiscard(context)) {
            return super.warehouse_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.warehouse), sco);
    }

    @Override
    public boolean pirateShip_takeTreasure(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_pirateShip_takeTreasure(context)) {
            return super.pirateShip_takeTreasure(context);
        }
        int t = this.getPirateShipTreasure();
        return selectBoolean(context, getCardName(Cards.pirateShip), Strings.format(R.string.pirate_ship_option_one, "" + t), getString(R.string.pirate_ship_option_two));
    }

    @Override
    public boolean nativeVillage_takeCards(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_nativeVillage_takeCards(context)) {
            return super.nativeVillage_takeCards(context);
        }
        return selectBoolean(context, getCardName(Cards.nativeVillage), getString(R.string.native_village_option_one), getString(R.string.native_village_option_two));
    }

    @Override
    public Card smugglers_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_smugglers_cardToObtain(context)) {
            return super.smugglers_cardToObtain(context);
        }
        ArrayList<String> options = new ArrayList<String>();
        Card[] cards = context.getCardsObtainedByLastPlayer().toArray(new Card[0]);
        for (Card c : cards)
            if (c.getCost(context) <= 6 && !c.isPrize())
                options.add(Strings.getCardName(c));

        if (options.size() > 0) {
            String o = selectString(context, getString(R.string.smuggle_query), options.toArray(new String[0]));
            return localNameToCard(o, cards);
        } else
            return null;
    }

    @Override
    public Card island_cardToSetAside(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_island_cardToSetAside(context)) {
            return super.island_cardToSetAside(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, getCardName(Cards.island), sco);
    }

    @Override
    public Card haven_cardToSetAside(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_haven_cardToSetAside(context)) {
            return super.haven_cardToSetAside(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, getCardName(Cards.haven), sco);
    }

    @Override
    public boolean navigator_shouldDiscardTopCards(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_navigator_shouldDiscardTopCards(context, cards)) {
            return super.navigator_shouldDiscardTopCards(context, cards);
        }
        String header = "";
        for (Card c : cards)
            header += getCardName(c) + ", ";
        header += "--";
        header = header.replace(", --", "");
        header = Strings.format(R.string.navigator_header, header);

        String option1 = getString(R.string.discard);
        String option2 = getString(R.string.navigator_option_two);

        return selectBoolean(context, header, option1, option2);
    }

    @Override
    public Card[] navigator_cardOrder(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_navigator_cardOrder(context, cards)) {
            return super.navigator_cardOrder(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards));
        for (int i : order) {
            orderedCards.add(cards[i]);
        }
        return orderedCards.toArray(new Card[0]);
    }

    @Override
    public Card embargo_supplyToEmbargo(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_embargo_supplyToEmbargo(context)) {
            return super.embargo_supplyToEmbargo(context);
        }
        SelectCardOptions sco = new SelectCardOptions().allowEmpty();
        return getFromTable(context, getCardName(Cards.embargo), sco);
    }

    // Will be passed all three cards
    @Override
    public Card lookout_cardToTrash(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_lookout_cardToTrash(context, cards)) {
            return super.lookout_cardToTrash(context, cards);
        }
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));

        String o = selectString(context, R.string.lookout_query_trash, Cards.lookout, options.toArray(new String[0]));
        return localNameToCard(o, cards);
    }

    // Will be passed the two cards leftover after trashing one
    @Override
    public Card lookout_cardToDiscard(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_lookout_cardToDiscard(context, cards)) {
            return super.lookout_cardToDiscard(context, cards);
        }
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));

        String o = selectString(context, R.string.lookout_query_discard, Cards.lookout, options.toArray(new String[0]));
        return localNameToCard(o, cards);
    }

    @Override
    public Card ambassador_revealedCard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_ambassador_revealedCard(context)) {
            return super.ambassador_revealedCard(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, getActionString(ActionType.REVEAL, Cards.ambassador), sco);
    }

    @Override
    public int ambassador_returnToSupplyFromHand(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_ambassador_returnToSupplyFromHand(context, card)) {
            return super.ambassador_returnToSupplyFromHand(context, card);
        }
        int numCards = 0;
        for (Card c : (context.player.isPossessed()) ? context.player.getHand() : getHand())
            if (c.equals(card))
                numCards++;

        return selectInt(context, Strings.format(R.string.ambassador_query, getCardName(card)), Math.min(2, numCards), 0);
    }

    @Override
    public boolean pearlDiver_shouldMoveToTop(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_pearlDiver_shouldMoveToTop(context, card)) {
            return super.pearlDiver_shouldMoveToTop(context, card);
        }

        String option1 = getString(R.string.pearldiver_option_one);
        String option2 = getString(R.string.pearldiver_option_two);
        return selectBooleanCardRevealed(context, Cards.pearlDiver, card, option1, option2);
    }

    @Override
    public boolean explorer_shouldRevealProvince(MoveContext context) {
        if (context.isQuickPlay() && shouldAutoPlay_explorer_shouldRevealProvince(context)) {
            super.explorer_shouldRevealProvince(context);
        }
        return selectBoolean(context, Cards.explorer, Strings.getString(R.string.explorer_reveal), Strings.getString(R.string.pass));
    }

    @Override
    public Card transmute_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_transmute_cardToTrash(context)) {
            return super.transmute_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.transmute), sco);
    }

    @Override
    public ArrayList<Card> apothecary_cardsForDeck(MoveContext context, ArrayList<Card> cards) {
        if(context.isQuickPlay() && shouldAutoPlay_apothecary_cardsForDeck(context, cards)) {
            return super.apothecary_cardsForDeck(context, cards);
        }

        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards.toArray(new Card[0])));
        for (int i : order)
            orderedCards.add(cards.get(i));
        return orderedCards;
    }

    @Override
    public boolean alchemist_backOnDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_alchemist_backOnDeck(context)) {
            return super.alchemist_backOnDeck(context);
        }
        String option1 = getString(R.string.alchemist_option_one);
        String option2 = getString(R.string.alchemist_option_two);
        return selectBoolean(context, Cards.alchemist, option1, option2);
    }

    @Override
    public TreasureCard herbalist_backOnDeck(MoveContext context, TreasureCard[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_herbalist_backOnDeck(context, cards)) {
            return super.herbalist_backOnDeck(context, cards);
        }
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));

//        String none = getString(R.string.none);
//        options.add(none);
        String o = selectString(context, R.string.herbalist_query, Cards.herbalist, options.toArray(new String[0]));
//        if(o.equals(none)) {
//            return null;
//        }
        return (TreasureCard) localNameToCard(o, cards);
    }

    @Override
    public Card apprentice_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_apprentice_cardToTrash(context)) {
            return super.apprentice_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.apprentice), sco);
    }

    @Override
    public ActionCard university_actionCardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_university_actionCardToObtain(context)) {
            return super.university_actionCardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(5).isAction().setPassable(getString(R.string.none));
        return (ActionCard) getFromTable(context, getString(R.string.university_part), sco);
    }

    @Override
    public boolean scryingPool_shouldDiscard(MoveContext context, Player targetPlayer, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_scryingPool_shouldDiscard(context, targetPlayer, card)) {
            return super.scryingPool_shouldDiscard(context, targetPlayer, card);
        }
        return selectBooleanCardRevealedAndPlayer(context, Cards.scryingPool, card, targetPlayer, getString(R.string.discard), getString(R.string.replace));
    }

    @Override
    public ActionCard[] golem_cardOrder(MoveContext context, ActionCard[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_golem_cardOrder(context, cards)) {
            return super.golem_cardOrder(context, cards);
        }

        if(cards == null || cards.length < 2) {
            return cards;
        }

        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));

        String o = selectString(context, R.string.golem_first_action, Cards.golem, options.toArray(new String[0]));
        Card c = localNameToCard(o, cards);
        if(c.equals(cards[0])) {
            return cards;
        }
        return new ActionCard[]{ cards[1], cards[0] };
    }

    @Override
    public Card bishop_cardToTrashForVictoryTokens(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_bishop_cardToTrashForVictoryTokens(context)) {
            return super.bishop_cardToTrashForVictoryTokens(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getString(R.string.bishop_part), sco);
    }

    @Override
    public Card bishop_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_bishop_cardToTrash(context)) {
            return super.bishop_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.bishop), sco);
    }

    @Override
    public Card contraband_cardPlayerCantBuy(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_contraband_cardPlayerCantBuy(context)) {
            return super.contraband_cardPlayerCantBuy(context);
        }
        SelectCardOptions sco = new SelectCardOptions().allowEmpty();
        return getFromTable(context, getCardName(Cards.contraband), sco);
    }

    @Override
    public Card expand_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_expand_cardToTrash(context)) {
            return super.expand_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.expand), sco);
    }

    @Override
    public Card expand_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_expand_cardToObtain(context, maxCost, potion)) {
            return super.expand_cardToObtain(context, maxCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.expand), sco);
    }

    @Override
    public Card[] forge_cardsToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_forge_cardsToTrash(context)) {
            return super.forge_cardsToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getFromHand(context, getActionString(ActionType.TRASH, Cards.forge), sco);
    }

    @Override
    public Card forge_cardToObtain(MoveContext context, int exactCost) {
        if(context.isQuickPlay() && shouldAutoPlay_forge_cardToObtain(context, exactCost)) {
            return super.forge_cardToObtain(context, exactCost);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).exactCost(exactCost);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.forge), sco);
    }

    @Override
    public Card[] goons_attack_cardsToKeep(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_goons_attack_cardsToKeep(context)) {
            return super.goons_attack_cardsToKeep(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.KEEP);
        return getFromHand(context, getString(R.string.goons_part), sco);
    }

    @Override
    public ActionCard kingsCourt_cardToPlay(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_kingsCourt_cardToPlay(context)) {
            return super.kingsCourt_cardToPlay(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isAction().setPassable(getString(R.string.none)).setPickType(PickType.PLAY);
        return (ActionCard) getCardFromHand(context, getCardName(Cards.kingsCourt), sco);
    }

    @Override
    public ActionCard throneRoom_cardToPlay(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_throneRoom_cardToPlay(context)) {
            return super.throneRoom_cardToPlay(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isAction().setPassable(getString(R.string.none)).setPickType(PickType.PLAY);
        return (ActionCard) getCardFromHand(context, getCardName(Cards.throneRoom), sco);
    }

    @Override
    public boolean loan_shouldTrashTreasure(MoveContext context, TreasureCard treasure) {
        if(context.isQuickPlay() && shouldAutoPlay_loan_shouldTrashTreasure(context, treasure)) {
            return super.loan_shouldTrashTreasure(context, treasure);
        }
        return selectBooleanCardRevealed(context, Cards.loan, treasure, getString(R.string.trash), getString(R.string.discard));
    }

    @Override
    public TreasureCard mint_treasureToMint(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_mint_treasureToMint(context)) {
            return super.mint_treasureToMint(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPassable(getString(R.string.pass)).setPickType(PickType.MINT);
        return (TreasureCard) getCardFromHand(context, getCardName(Cards.mint), sco);
    }

    @Override
    public boolean mountebank_attack_shouldDiscardCurse(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_mountebank_attack_shouldDiscardCurse(context)) {
            return super.mountebank_attack_shouldDiscardCurse(context);
        }
        return selectBoolean(context, getString(R.string.mountebank_query), getString(R.string.mountebank_option_one), getString(R.string.mountebank_option_two));
    }

    @Override
    public Card[] rabble_attack_cardOrder(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_rabble_attack_cardOrder(context, cards)) {
            return super.rabble_attack_cardOrder(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards));
        for (int i : order)
            orderedCards.add(cards[i]);
        return orderedCards.toArray(new Card[0]);
    }

    @Override
    public boolean royalSeal_shouldPutCardOnDeck(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_royalSeal_shouldPutCardOnDeck(context, card)) {
            return super.royalSeal_shouldPutCardOnDeck(context, card);
        }
        return selectBooleanCardRevealed(context, Cards.royalSeal, card, getString(R.string.top_of_deck), getString(R.string.take_normal));
    }

    @Override
    public Card tradeRoute_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_tradeRoute_cardToTrash(context)) {
            return super.tradeRoute_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.tradeRoute), sco);
    }

    @Override
    public Card[] vault_cardsToDiscardForGold(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_vault_cardsToDiscardForGold(context)) {
            return super.vault_cardsToDiscardForGold(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getString(R.string.vault_part_discard_for_gold), sco);
    }

    @Override
    public Card[] vault_cardsToDiscardForCard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_vault_cardsToDiscardForCard(context)) {
            return super.vault_cardsToDiscardForCard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getString(R.string.vault_part_discard_for_card), sco);
    }

    @Override
    public WatchTowerOption watchTower_chooseOption(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_watchTower_chooseOption(context, card)) {
            return super.watchTower_chooseOption(context, card);
        }
        LinkedHashMap<String, WatchTowerOption> h = new LinkedHashMap<String, WatchTowerOption>();

        h.put(getString(R.string.watch_tower_option_one), WatchTowerOption.Normal);
        h.put(getString(R.string.trash), WatchTowerOption.Trash);
        h.put(getString(R.string.watch_tower_option_three), WatchTowerOption.TopOfDeck);

        return h.get(selectString(context, Strings.format(R.string.watch_tower_query, getCardName(card)), h.keySet().toArray(new String[0])));
    }

    @Override
    public Card hamlet_cardToDiscardForAction(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_hamlet_cardToDiscardForAction(context)) {
            return super.hamlet_cardToDiscardForAction(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getCardFromHand(context, getString(R.string.hamlet_part_discard_for_action), sco);
    }

    @Override
    public Card hamlet_cardToDiscardForBuy(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_hamlet_cardToDiscardForBuy(context)) {
            return super.hamlet_cardToDiscardForBuy(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getCardFromHand(context, getString(R.string.hamlet_part_discard_for_buy), sco);
    }

    @Override
    public Card hornOfPlenty_cardToObtain(MoveContext context, int maxCost) {
        if(context.isQuickPlay() && shouldAutoPlay_hornOfPlenty_cardToObtain(context, maxCost)) {
            return super.hornOfPlenty_cardToObtain(context, maxCost);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(maxCost);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.hornOfPlenty), sco);
    }

    @Override
    public Card[] horseTraders_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_horseTraders_cardsToDiscard(context)) {
            return super.horseTraders_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.horseTraders), sco);
    }

    @Override
    public JesterOption jester_chooseOption(MoveContext context, Player targetPlayer, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_jester_chooseOption(context, targetPlayer, card)) {
            return super.jester_chooseOption(context, targetPlayer, card);
        }

        LinkedHashMap<String, JesterOption> h = new LinkedHashMap<String, JesterOption>();

        h.put(getString(R.string.jester_option_one), JesterOption.GainCopy);
        h.put(Strings.format(R.string.jester_option_two, targetPlayer.getPlayerName()), JesterOption.GiveCopy);

        String header = Strings.format(R.string.card_revealed, getCardName(Cards.jester), getCardName(card));
        return h.get(selectString(context, header, h.keySet().toArray(new String[0])));
    }

    @Override
    public Card remake_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_remake_cardToTrash(context)) {
            return super.remake_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.remake), sco);
    }

    @Override
    public Card remake_cardToObtain(MoveContext context, int exactCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_remake_cardToObtain(context, exactCost, potion)) {
            return super.remake_cardToObtain(context, exactCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().exactCost(exactCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.remake), sco);
    }

    @Override
    public boolean tournament_shouldRevealProvince(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_tournament_shouldRevealProvince(context)) {
            return super.tournament_shouldRevealProvince(context);
        }
        return selectBoolean(context, Cards.tournament, Strings.getString(R.string.tournament_reveal), Strings.getString(R.string.tournament_option_one));

    }

    @Override
    public TournamentOption tournament_chooseOption(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_tournament_chooseOption(context)) {
            return super.tournament_chooseOption(context);
        }
        LinkedHashMap<String, TournamentOption> h = new LinkedHashMap<String, TournamentOption>();

        h.put(getString(R.string.tournament_option_two), TournamentOption.GainPrize);
        h.put(getString(R.string.tournament_option_three), TournamentOption.GainDuchy);

        return h.get(selectString(context, Cards.tournament, h.keySet().toArray(new String[0])));
    }

    @Override
    public Card tournament_choosePrize(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_tournament_choosePrize(context)) {
            return super.tournament_choosePrize(context);
        }
        SelectCardOptions sco = new SelectCardOptions().fromPrizes();
        return getFromTable(context, getString(R.string.select_prize), sco);
    }

    @Override
    public Card[] youngWitch_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_youngWitch_cardsToDiscard(context)) {
            return super.youngWitch_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.youngWitch), sco);
    }

    @Override
    public Card[] followers_attack_cardsToKeep(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_followers_attack_cardsToKeep(context)) {
            return super.followers_attack_cardsToKeep(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.KEEP);
        return getFromHand(context, getString(R.string.followers_part), sco);
    }

    @Override
    public TrustySteedOption[] trustySteed_chooseOptions(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_trustySteed_chooseOptions(context)) {
            return super.trustySteed_chooseOptions(context);
        }
        LinkedHashMap<String, TrustySteedOption> h = new LinkedHashMap<String, TrustySteedOption>();

        h.put(getString(R.string.trusty_steed_option_two), TrustySteedOption.AddCards);
        h.put(getString(R.string.trusty_steed_option_one), TrustySteedOption.AddActions);
        h.put(getString(R.string.trusty_steed_option_three), TrustySteedOption.AddGold);
        h.put(getString(R.string.trusty_steed_option_four), TrustySteedOption.GainSilvers);

        TrustySteedOption[] choices = new TrustySteedOption[2];
        choices[0] = h.remove(selectString(context, Cards.trustySteed, h.keySet().toArray(new String[0])));
        choices[1] = h.remove(selectString(context, Cards.trustySteed, h.keySet().toArray(new String[0])));
        return choices;
    }

    @Override
    public TreasureCard thief_treasureToTrash(MoveContext context, TreasureCard[] treasures) {
        if(context.isQuickPlay() && shouldAutoPlay_thief_treasureToTrash(context, treasures)) {
            return super.thief_treasureToTrash(context, treasures);
        }
        ArrayList<String> options = new ArrayList<String>();
        for (TreasureCard c : treasures)
            options.add(Strings.getCardName(c));

        if (options.size() > 0) {
            String o = selectString(context, R.string.treasure_to_trash, Cards.thief, options.toArray(new String[0]));
            return (TreasureCard) localNameToCard(o, treasures);
        } else {
            return null;
        }
    }

    @Override
    public TreasureCard[] thief_treasuresToGain(MoveContext context, TreasureCard[] treasures) {
        if(context.isQuickPlay() && shouldAutoPlay_thief_treasuresToGain(context, treasures)) {
            return super.thief_treasuresToGain(context, treasures);
        }
        ArrayList<String> options = new ArrayList<String>();
        options.add(getString(R.string.none));
        for (TreasureCard c : treasures)
            options.add(Strings.getCardName(c));

        if (options.size() > 0) {
            ArrayList<TreasureCard> toGain = new ArrayList<TreasureCard>();
            String o = null;

            while (options.size() > 1 && !getString(R.string.none).equals(o = selectString(context, R.string.thief_query, Cards.thief, options.toArray(new String[0])))) {
                toGain.add((TreasureCard) localNameToCard(o, treasures));
                options.remove(o);
            }

            return toGain.toArray(new TreasureCard[0]);
        } else {
            return null;
        }
    }

    @Override
    public TreasureCard pirateShip_treasureToTrash(MoveContext context, TreasureCard[] treasures) {
        if(context.isQuickPlay() && shouldAutoPlay_pirateShip_treasureToTrash(context, treasures)) {
            return super.pirateShip_treasureToTrash(context, treasures);
        }
        ArrayList<String> options = new ArrayList<String>();
        for (TreasureCard c : treasures)
            options.add(Strings.getCardName(c));

        if (options.size() > 0) {
            String o = selectString(context, R.string.treasure_to_trash, Cards.pirateShip, options.toArray(new String[0]));
            return (TreasureCard) localNameToCard(o, treasures);
        } else {
            return null;
        }
    }

    @Override
    public boolean tunnel_shouldReveal(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_tunnel_shouldReveal(context)) {
            return super.tunnel_shouldReveal(context);
        }
        return selectBoolean(context, getString(R.string.tunnel_query), getString(R.string.tunnel_option_one), getString(R.string.pass));
    }

    @Override
    public boolean duchess_shouldGainBecauseOfDuchy(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_duchess_shouldGainBecauseOfDuchy(context)) {
            return super.duchess_shouldGainBecauseOfDuchy(context);
        }
        return selectBoolean(context, getString(R.string.duchess_query), getString(R.string.duchess_option_one), getString(R.string.pass));
    }

    @Override
    public boolean duchess_shouldDiscardCardFromTopOfDeck(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_duchess_shouldDiscardCardFromTopOfDeck(context, card)) {
            return super.duchess_shouldDiscardCardFromTopOfDeck(context, card);
        }
        return !selectBooleanCardRevealed(context, Cards.duchess, card, getString(R.string.duchess_play_option_one), getString(R.string.discard));
    }

    @Override
    public boolean foolsGold_shouldTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_foolsGold_shouldTrash(context)) {
            return super.foolsGold_shouldTrash(context);
        }
        return selectBoolean(context, Cards.foolsGold, getString(R.string.fools_gold_option_one), getString(R.string.pass));
    }

    @Override
    public boolean trader_shouldGainSilverInstead(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_trader_shouldGainSilverInstead(context, card)) {
            return super.trader_shouldGainSilverInstead(context, card);
        }
        return !selectBoolean(context, Cards.trader, Strings.format(R.string.trader_gain, getCardName(card)), Strings.format(R.string.trader_gain_instead_of, getCardName(Cards.silver), getCardName(card)));
    }

    @Override
    public Card trader_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_trader_cardToTrash(context)) {
            return super.trader_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.trader), sco);
    }

    @Override
    public Card oasis_cardToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_oasis_cardToDiscard(context)) {
            return super.oasis_cardToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.DISCARD);
        return getCardFromHand(context, getActionString(ActionType.DISCARD, Cards.oasis), sco);
    }

    @Override
    public Card develop_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_develop_cardToTrash(context)) {
            return super.develop_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.develop), sco);
    }

    @Override
    public Card develop_lowCardToGain(MoveContext context, int cost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_develop_lowCardToGain(context, cost, potion)) {
            return super.develop_lowCardToGain(context, cost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().exactCost(cost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.develop), sco);
    }

    @Override
    public Card develop_highCardToGain(MoveContext context, int cost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_develop_highCardToGain(context, cost, potion)) {
            return super.develop_highCardToGain(context, cost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().exactCost(cost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.develop), sco);
    }

    @Override
    public Card[] develop_orderCards(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_develop_orderCards(context, cards)) {
            return super.develop_orderCards(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards), getString(R.string.card_order_on_deck));
        for (int i : order)
            orderedCards.add(cards[i]);
        return orderedCards.toArray(new Card[0]);
    }

    @Override
    public TreasureCard nobleBrigand_silverOrGoldToTrash(MoveContext context, TreasureCard[] silverOrGoldCards) {
        if(context.isQuickPlay() && shouldAutoPlay_nobleBrigand_silverOrGoldToTrash(context, silverOrGoldCards)) {
            return super.nobleBrigand_silverOrGoldToTrash(context, silverOrGoldCards);
        }

        int highIndex;
        int lowIndex;
        if(silverOrGoldCards[0].getCost(context) >= silverOrGoldCards[1].getCost(context)) {
            highIndex = 0;
            lowIndex = 1;
        }
        else {
            highIndex = 1;
            lowIndex = 0;
        }

        if(selectBoolean(context, Strings.format(R.string.noble_brigand_query, context.getAttackedPlayer()), Strings.getCardName(silverOrGoldCards[lowIndex]), Strings.getCardName(silverOrGoldCards[highIndex]))) {
            return silverOrGoldCards[lowIndex];
        }
        else {
            return silverOrGoldCards[highIndex];
        }
    }

    @Override
    public boolean jackOfAllTrades_shouldDiscardCardFromTopOfDeck(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_jackOfAllTrades_shouldDiscardCardFromTopOfDeck(context, card)) {
            super.jackOfAllTrades_shouldDiscardCardFromTopOfDeck(context, card);
        }
        return !selectBooleanCardRevealed(context, Cards.jackOfAllTrades, card, getString(R.string.jack_of_all_trades_option_one), getString(R.string.discard));
    }

    @Override
    public Card jackOfAllTrades_nonTreasureToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_jackOfAllTrades_nonTreasureToTrash(context)) {
            super.jackOfAllTrades_nonTreasureToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isNonTreasure().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.jackOfAllTrades), sco);
    }

    @Override
    public TreasureCard spiceMerchant_treasureToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_spiceMerchant_treasureToTrash(context)) {
            return super.spiceMerchant_treasureToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return (TreasureCard) getCardFromHand(context, getActionString(ActionType.TRASH, Cards.spiceMerchant), sco);
    }

    @Override
    public SpiceMerchantOption spiceMerchant_chooseOption(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_spiceMerchant_chooseOption(context)) {
            return super.spiceMerchant_chooseOption(context);
        }
        LinkedHashMap<String, SpiceMerchantOption> h = new LinkedHashMap<String, SpiceMerchantOption>();

        h.put(getString(R.string.spice_merchant_option_one), SpiceMerchantOption.AddCardsAndAction);
        h.put(getString(R.string.spice_merchant_option_two), SpiceMerchantOption.AddGoldAndBuy);

        String o = selectString(context, getCardName(Cards.spiceMerchant), h.keySet().toArray(new String[0]));
        return h.get(o);
    }

    @Override
    public Card[] embassy_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_embassy_cardsToDiscard(context)) {
            return super.embassy_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.embassy), sco);
    }

    @Override
    public Card[] cartographer_cardsFromTopOfDeckToDiscard(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_cartographer_cardsFromTopOfDeckToDiscard(context, cards)) {
            return super.cartographer_cardsFromTopOfDeckToDiscard(context, cards);
        }

        if(cards == null || cards.length == 0) {
            return cards;
        }

        ArrayList<Card> cardsToDiscard = new ArrayList<Card>();

        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);

        do {
            String o = selectString(context, R.string.Cartographer_query, Cards.cartographer, options.toArray(new String[0]));
            if (o.equals(none)) {
                break;
            }
            cardsToDiscard.add(localNameToCard(o, cards));
            options.remove(o);
        } while (options.size() > 1);

        return cardsToDiscard.toArray(new Card[0]);
    }

    @Override
    public Card[] cartographer_cardOrder(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_cartographer_cardOrder(context, cards)) {
            return super.cartographer_cardOrder(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards));
        for (int i : order)
            orderedCards.add(cards[i]);
        return orderedCards.toArray(new Card[0]);
    }

    @Override
    public ActionCard scheme_actionToPutOnTopOfDeck(MoveContext context, ActionCard[] actions) {
        if(context.isQuickPlay() && shouldAutoPlay_scheme_actionToPutOnTopOfDeck(context, actions)) {
            return super.scheme_actionToPutOnTopOfDeck(context, actions);
        }
        ArrayList<String> options = new ArrayList<String>();
        for (ActionCard c : actions)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);
        String o = selectString(context, R.string.scheme_query, Cards.scheme, options.toArray(new String[0]));
        if(o.equals(none)) {
            return null;
        }
        return (ActionCard) localNameToCard(o, actions);
    }

    @Override
    public boolean oracle_shouldDiscard(MoveContext context, Player player, ArrayList<Card> cards) {
        if(context.isQuickPlay() && shouldAutoPlay_oracle_shouldDiscard(context, player, cards)) {
            return super.oracle_shouldDiscard(context, player, cards);
        }
        String cardNames = "";
        boolean first = true;
        for(Card c : cards) {
            if(first)
                first = false;
            else
                cardNames += ", ";
            cardNames += Strings.getCardName(c);
        }
        String s = Strings.format(R.string.card_revealed, player.getPlayerName(), cardNames);
        return !selectBoolean(context, s, getString(R.string.top_of_deck), getString(R.string.discard));
    }

    @Override
    public Card[] oracle_orderCards(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_oracle_orderCards(context, cards)) {
            return super.oracle_orderCards(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards));
        for (int i : order)
            orderedCards.add(cards[i]);
        return orderedCards.toArray(new Card[0]);
    }

    @Override
    public boolean illGottenGains_gainCopper(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_illGottenGains_gainCopper(context)) {
            return super.illGottenGains_gainCopper(context);
        }
        return selectBoolean(context, Cards.illGottenGains, getString(R.string.ill_gotten_gains_option_one), getString(R.string.pass));
    }

    @Override
    public Card haggler_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_haggler_cardToObtain(context, maxCost, potion)) {
            return super.haggler_cardToObtain(context, maxCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(potion ? 1 : 0).debtCost(debt).maxCost(maxCost).maxCostWithoutPotion().isNonVictory();
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.haggler), sco);
    }

    @Override
    public Card[] inn_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_inn_cardsToDiscard(context)) {
            return super.inn_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.inn), sco);
    }

    @Override
    public boolean inn_shuffleCardBackIntoDeck(MoveContext context, ActionCard card) {
        if(context.isQuickPlay() && shouldAutoPlay_inn_shuffleCardBackIntoDeck(context, card)) {
            return super.inn_shuffleCardBackIntoDeck(context, card);
        }

        String option1 = getString(R.string.inn_option_one);
        String option2 = getString(R.string.inn_option_two);
        return selectBooleanCardRevealed(context, Cards.inn, card, option1, option2);
    }

    @Override
    public Card borderVillage_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_borderVillage_cardToObtain(context)) {
            return super.borderVillage_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(Cards.borderVillage.getCost(context) - 1);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.borderVillage), sco);
    }

    @Override
    public Card farmland_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_farmland_cardToTrash(context)) {
            return super.farmland_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.farmland), sco);
    }

    @Override
    public Card farmland_cardToObtain(MoveContext context, int exactCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_remodel_cardToObtain(context, exactCost, potion)) {
            return super.remodel_cardToObtain(context, exactCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().exactCost(exactCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.farmland), sco);
    }

    @Override
    public TreasureCard stables_treasureToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_stables_treasureToDiscard(context)) {
            return super.stables_treasureToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return (TreasureCard) getCardFromHand(context, getActionString(ActionType.DISCARD, Cards.stables), sco);
    }

    @Override
    public Card mandarin_cardToReplace(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_mandarin_cardToReplace(context)) {
            return super.mandarin_cardToReplace(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, getString(R.string.mandarin_part), sco);
    }

    @Override
    public Card[] mandarin_orderCards(MoveContext context, Card[] cards) {
        if (context.isQuickPlay() && shouldAutoPlay_mandarin_orderCards(context, cards)) {
            return super.mandarin_orderCards(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards));
        for (int i : order)
            orderedCards.add(cards[i]);
        return orderedCards.toArray(new Card[0]);
    }

    @Override
    public Card[] margrave_attack_cardsToKeep(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_margrave_attack_cardsToKeep(context)) {
            return super.margrave_attack_cardsToKeep(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.KEEP);
        return getFromHand(context, getString(R.string.margrave_part), sco);
    }

    @Override
    public Card getAttackReaction(MoveContext context, Card responsible, boolean defended, Card lastCard) {
        ArrayList<Card> reactionCards = new ArrayList<Card>();
        for (Card c : getReactionCards(defended)) {
            if (!c.equals(Cards.marketSquare) && !c.equals(Cards.watchTower)) 
            {
                reactionCards.add(c);
            }
        }
        if (reactionCards.size() > 0) {
            ArrayList<String> options = new ArrayList<String>();
            for (Card c : reactionCards)
                if (lastCard == null || !Game.suppressRedundantReactions || c.getName() != lastCard.getName() || c.equals(Cards.horseTraders) || c.equals(Cards.beggar) || c.equals(Cards.caravanGuard) || c.equals(Cards.diplomat))
                   options.add(Strings.getCardName(c));
            if (options.size() > 0) {
                String none = getString(R.string.none);
                options.add(none);
                String o = selectString(context, R.string.reaction_query, responsible, options.toArray(new String[0]));
                if(o.equals(none)) return null;
                return localNameToCard(o, reactionCards.toArray(new Card[0]));
            }
        }
        return null;
    }

    @Override
    public boolean revealBane(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_revealBane(context)) {
            return super.revealBane(context);
        }
        return selectBoolean(context, Cards.youngWitch, Strings.format(R.string.bane_option_one, Strings.getCardName(game.baneCard)), getString(R.string.pass));
    }

    @Override
    public PutBackOption selectPutBackOption(MoveContext context, List<PutBackOption> putBacks) {
        if(context.isQuickPlay() && shouldAutoPlay_selectPutBackOption(context, putBacks)) {
            return super.selectPutBackOption(context, putBacks);
        }
        Collections.sort(putBacks);
        LinkedHashMap<String, PutBackOption> h = new LinkedHashMap<String, PutBackOption>();
        h.put(getCardName(Cards.treasury), PutBackOption.Treasury);
        h.put(getCardName(Cards.alchemist), PutBackOption.Alchemist);
        h.put(getCardName(Cards.walledVillage), PutBackOption.WalledVillage);
        h.put(getString(R.string.putback_option_one), PutBackOption.Coin);
        h.put(getString(R.string.putback_option_two), PutBackOption.Action);
        h.put(getString(R.string.none), PutBackOption.None);
        List<String> options = new ArrayList<String>();
        for (PutBackOption putBack : putBacks) {
            switch (putBack) {
            case Treasury:
                options.add(getCardName(Cards.treasury));
                break;
            case Alchemist:
                options.add(getCardName(Cards.alchemist));
                break;
            case WalledVillage:
                options.add(getCardName(Cards.walledVillage));
                break;
            case Coin:
                options.add(getString(R.string.putback_option_one));
                break;
            case Action:
                options.add(getString(R.string.putback_option_two));
                break;
            case None:
                break;
            default:
                break;
            }
        }
        options.add(getString(R.string.none));

        return h.get(selectString(context, getString(R.string.putback_query), options.toArray(new String[0])));
    }
    
    @Override
    public SquireOption squire_chooseOption(MoveContext context) {
//      if(context.isQuickPlay() && shouldAutoPlay_steward_chooseOption(context)) {
//          return super.steward_chooseOption(context);
//      }
        LinkedHashMap<String, SquireOption> h = new LinkedHashMap<String, SquireOption>();
        
        h.put(getString(R.string.squire_option_one), SquireOption.AddActions);
        h.put(getString(R.string.squire_option_two), SquireOption.AddBuys);
        h.put(getString(R.string.squire_option_three), SquireOption.GainSilver);
    
        return h.get(selectString(context, Cards.squire, h.keySet().toArray(new String[0])));
    }

    @Override
    public Card armory_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.armory_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(4);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.armory), sco);
    }

    @Override
    public Card altar_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_apprentice_cardToTrash(context)) {
            return super.altar_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.altar), sco);
    }
    
    @Override
    public Card altar_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.altar_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(5);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.altar), sco);
    }
    
    @Override
    public Card squire_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_feast_cardToObtain(context)) {
            return super.squire_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isAttack();
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.squire), sco);
    }
    
    @Override
    public Card rats_cardToTrash(MoveContext context) {
        if (context.isQuickPlay() && shouldAutoPlay_rats_cardToTrash(context)) {
            return super.rats_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isNonRats().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.rats), sco);
    }
    
    @Override
    public boolean catacombs_shouldDiscardTopCards(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_navigator_shouldDiscardTopCards(context, cards)) {
            return super.catacombs_shouldDiscardTopCards(context, cards);
        }
        String header = "";
        for (Card c : cards)
            header += getCardName(c) + ", ";
        header += "--";
        header = header.replace(", --", "");
        header = Strings.format(R.string.catacombs_header, header);

        String option1 = getString(R.string.catacombs_option_one);
        String option2 = getString(R.string.catacombs_option_two);

        return !selectBoolean(context, header, option1, option2);
    }
    
    @Override
    public Card catacombs_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_feast_cardToObtain(context)) {
            return super.catacombs_cardToObtain(context);
        }
        int maxPrice = Math.max(0, game.getPile(Cards.catacombs).card().getCost(context) - 1);
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(maxPrice);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.catacombs), sco);
    }
    
    @Override
    public CountFirstOption count_chooseFirstOption(MoveContext context) {
        LinkedHashMap<String, CountFirstOption> h = new LinkedHashMap<String, CountFirstOption>();
        
        h.put(getString(R.string.count_firstoption_one), CountFirstOption.Discard);
        h.put(getString(R.string.count_firstoption_two), CountFirstOption.PutOnDeck);
        h.put(getString(R.string.count_firstoption_three), CountFirstOption.GainCopper);
    
        return h.get(selectString(context, Cards.count, h.keySet().toArray(new String[0])));
    }
    
    @Override
    public CountSecondOption count_chooseSecondOption(MoveContext context) {
        LinkedHashMap<String, CountSecondOption> h = new LinkedHashMap<String, CountSecondOption>();
        
        h.put(getString(R.string.count_secondoption_one), CountSecondOption.Coins);
        h.put(getString(R.string.count_secondoption_two), CountSecondOption.TrashHand);
        h.put(getString(R.string.count_secondoption_three), CountSecondOption.GainDuchy);
    
        return h.get(selectString(context, Cards.count, h.keySet().toArray(new String[0])));
    }
    
    @Override
    public Card[] count_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_torturer_attack_cardsToDiscard(context)) {
            return super.count_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.count), sco);
    }
    
    @Override
    public Card count_cardToPutBackOnDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_courtyard_cardToPutBackOnDeck(context)) {
            return super.count_cardToPutBackOnDeck(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, Strings.format(R.string.count_part_top_of_deck, getCardName(Cards.count)), sco);
    }
    @Override
    public Card deathCart_actionToTrash(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isAction().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.deathCart), sco);
    }
    
    @Override
    public Card forager_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_apprentice_cardToTrash(context)) {
            return super.forager_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.forager), sco);

    }
    
    @Override
    public GraverobberOption graverobber_chooseOption(MoveContext context) {
        LinkedHashMap<String, GraverobberOption> h = new LinkedHashMap<String, GraverobberOption>();
        
        h.put(getString(R.string.graverobber_option_one), GraverobberOption.GainFromTrash);
        h.put(getString(R.string.graverobber_option_two), GraverobberOption.TrashActionCard);
    
        return h.get(selectString(context, Cards.graverobber, h.keySet().toArray(new String[0])));
    }
    
    @Override
    public Card graverobber_cardToGainFromTrash(MoveContext context) {
        LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();
        ArrayList<Card> options = new ArrayList<Card>(); 
        
        for (Card c : game.trashPile) {
            if (c.getCost(context) >= 3 && c.getCost(context) <= 6)
                options.add(c);
        }
        
        if (options.isEmpty()) {
            return null;
        }
        
        for (Card c : options) {
            h.put(c.getName(), c);
        }
    
        return h.get(selectString(context, Cards.graverobber, h.keySet().toArray(new String[0])));
    }
    
    @Override
    public Card graverobber_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_apprentice_cardToTrash(context)) {
            return super.graverobber_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isAction().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.graverobber), sco);
    }
    
    @Override
    public Card graverobber_cardToReplace(MoveContext context, int maxCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_expand_cardToObtain(context, maxCost, potion)) {
            return super.graverobber_cardToReplace(context, maxCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.graverobber), sco);
    }
    
    @Override
    public HuntingGroundsOption huntingGrounds_chooseOption(MoveContext context) {
        LinkedHashMap<String, HuntingGroundsOption> h = new LinkedHashMap<String, HuntingGroundsOption>();
        
        h.put(getString(R.string.hunting_grounds_option_one), HuntingGroundsOption.GainDuchy);
        h.put(getString(R.string.hunting_grounds_option_two), HuntingGroundsOption.GainEstates);
    
        return h.get(selectString(context, Cards.huntingGrounds, h.keySet().toArray(new String[0])));
    }
    
    @Override
    public boolean ironmonger_shouldDiscard(MoveContext context, Card card) {
        return !selectBooleanCardRevealed(context, Cards.ironmonger, card, getString(R.string.ironmonger_option_one), getString(R.string.discard));
    }
    
    @Override
    public Card junkDealer_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_trader_cardToTrash(context)) {
            return super.junkDealer_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.junkDealer), sco);
    }
    
    @Override
    public boolean marketSquare_shouldDiscard(MoveContext context) {
        return selectBoolean(context, Cards.marketSquare, getString(R.string.discard), getString(R.string.keep));
    }
    
    @Override
    public Card mystic_cardGuess(MoveContext context, ArrayList<Card> cardList) {
        
        if(context.isQuickPlay() && shouldAutoPlay_wishingWell_cardGuess(context)) {
            return super.mystic_cardGuess(context, cardList);
        }
      
        LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();

        // Add option to skip the guess
        h.put("None", null);
        
        for (Card c : cardList) {
            h.put(c.getName(), c);
        }
        
        String choice = selectString(context, getActionString(ActionType.NAMECARD, Cards.mystic), h.keySet().toArray(new String[0])); 
    
        return h.get(choice);
    }
    
    @Override
    public boolean scavenger_shouldDiscardDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_chancellor_shouldDiscardDeck(context)) {
            return super.scavenger_shouldDiscardDeck(context);
        }
        return selectBoolean(context, getCardName(Cards.scavenger), getString(R.string.chancellor_query), getString(R.string.pass));
    }
    
    @Override
	public Card scavenger_cardToPutBackOnDeck(MoveContext context) {
        CardList localDiscard = (context.player.isPossessed()) ? context.player.getDiscard() : getDiscard();
	    if (localDiscard.isEmpty())
	    	return null;
		
	    // oh, how simple it looks, and how ugly it will be (again)
	    LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();
	    for (Card c : localDiscard) {
	    	h.put(c.getName(), c);
	    }
	    return h.get(selectString(context, Cards.scavenger, h.keySet().toArray(new String[0])));
	}
    
    @Override
    public Card[] storeroom_cardsToDiscardForCards(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_cellar_cardsToDiscard(context)) {
            return super.storeroom_cardsToDiscardForCards(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARDFORCARD, Cards.storeroom), sco);
    }
    
    @Override
    public Card[] storeroom_cardsToDiscardForCoins(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_cellar_cardsToDiscard(context)) {
            return super.storeroom_cardsToDiscardForCoins(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARDFORCOIN, Cards.storeroom), sco);
    }
    
    @Override
    public ActionCard procession_cardToPlay(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_throneRoom_cardToPlay(context)) {
            return super.procession_cardToPlay(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isAction().setPassable(getString(R.string.none)).setPickType(PickType.PLAY);
        return (ActionCard) getCardFromHand(context, getCardName(Cards.procession), sco);
    }
    
    @Override
    public Card procession_cardToGain(MoveContext context, int exactCost,   boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_procession_cardToObtain(context, exactCost, potion)) {
            return super.procession_cardToGain(context, exactCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().isAction().exactCost(exactCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.procession), sco);
    }
    
    @Override
    public Card rebuild_cardToPick(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_feast_cardToObtain(context)) {
            return super.rebuild_cardToPick(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isVictory().allowEmpty();
        return getFromTable(context, getActionString(ActionType.NAMECARD, Cards.rebuild), sco);
    }
    
    @Override
    public Card rebuild_cardToGain(MoveContext context, int maxCost, boolean costPotion) {
        if(context.isQuickPlay() && shouldAutoPlay_remodel_cardToObtain(context, maxCost, costPotion)) {
            return super.rebuild_cardToGain(context, maxCost, costPotion);
        }
        SelectCardOptions sco = new SelectCardOptions().isVictory().maxCost(maxCost).potionCost(costPotion ? 1 : 0);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.rebuild), sco);
    }
    
    @Override
    public Card rogue_cardToGain(MoveContext context) {
        LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();
        ArrayList<Card> options = new ArrayList<Card>(); 
        
        for (Card c : game.trashPile) {
            if (c.getCost(context) >= 3 && c.getCost(context) <= 6)
                options.add(c);
        }
        
        if (options.isEmpty()) {
            return null;
        }
        
        for (Card c : options) {
            h.put(c.getName(), c);
        }
    
        return h.get(selectString(context, Cards.rogue, h.keySet().toArray(new String[0])));
    }
    
    @Override
    public Card rogue_cardToTrash(MoveContext context, ArrayList<Card> canTrash) {
        LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();
        for (Card c : canTrash) {
            h.put(c.getName(), c);
        }
    
        return h.get(selectString(context, Cards.rogue, h.keySet().toArray(new String[0])));
    }
    
    @Override
    public TreasureCard counterfeit_cardToPlay(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_masquerade_cardToTrash(context)) {
            return super.counterfeit_cardToPlay(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return (TreasureCard) getCardFromHand(context, getActionString(ActionType.TRASH, Cards.counterfeit), sco);
    }
    
    @Override
    public Card pillage_opponentCardToDiscard(MoveContext context, ArrayList<Card> handCards)
    {
        if(context.isQuickPlay() && shouldAutoPlay_pillage_opponentCardToDiscard(context)) 
        {
            return super.pillage_opponentCardToDiscard(context, handCards);
        }
        
        ArrayList<String> options = new ArrayList<String>();
        
        for (Card c : handCards)
        {
            options.add(Strings.getCardName(c));
        }

        if (!options.isEmpty()) 
        {
            String o = selectString(context, getActionString(ActionType.OPPONENTDISCARD, Cards.pillage, context.attackedPlayer.getPlayerName()), options.toArray(new String[0]));
            return (Card) localNameToCard(o, handCards.toArray(new Card[0]));
        } 
        else 
        {
            return null;
        }
    }
    
    @Override
    public boolean hovel_shouldTrash(MoveContext context)
    {
        if(context.isQuickPlay())
        {
            return true;
        }
        else
        {
            return selectBoolean(context, getCardName(Cards.hovel), getString(R.string.hovel_option), getString(R.string.pass));
        }
    }
    
    @Override
    public boolean walledVillage_backOnDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_walledVillage_backOnDeck(context)) {
            return super.walledVillage_backOnDeck(context);
        }
        String option1 = getString(R.string.walledVillage_option_one);
        String option2 = getString(R.string.walledVillage_option_two);
        return selectBoolean(context, Cards.walledVillage, option1, option2);
    }
    
    @Override
    public GovernorOption governor_chooseOption(MoveContext context) {

        LinkedHashMap<String, GovernorOption> h = new LinkedHashMap<String, GovernorOption>();
        
        h.put(getString(R.string.governor_option_one), GovernorOption.AddCards);
        h.put(getString(R.string.governor_option_two), GovernorOption.GainTreasure);
        h.put(getString(R.string.governor_option_three), GovernorOption.Upgrade);
    
        return h.get(selectString(context, Cards.governor, h.keySet().toArray(new String[0])));
    }
    
    @Override
    public Card envoy_cardToDiscard(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_envoy_opponentCardToDiscard(context)) {
            return super.envoy_cardToDiscard(context, cards);
        }
        
        ArrayList<String> options = new ArrayList<String>();
        
        for (Card c : cards) {
            options.add(Strings.getCardName(c));
        }

        if (!options.isEmpty()) {
            String o = selectString(context, getActionString(ActionType.OPPONENTDISCARD, Cards.envoy, context.getPlayer().getPlayerName()), options.toArray(new String[0]));
            return (Card) localNameToCard(o, cards);
        } else {
            return null;
        }
    }
    
    @Override
    public boolean survivors_shouldDiscardTopCards(MoveContext context, Card[] array) {
        if(context.isQuickPlay() && shouldAutoPlay_navigator_shouldDiscardTopCards(context, array)) {
            return super.survivors_shouldDiscardTopCards(context, array);
        }
        String header = "";
        for (Card c : array)
            header += getCardName(c) + ", ";
        header += "--";
        header = header.replace(", --", "");
        header = Strings.format(R.string.survivors_header, header);

        String option1 = getString(R.string.discard);
        String option2 = getString(R.string.navigator_option_two);

        return selectBoolean(context, header, option1, option2);
    }
    @Override
    public Card[] survivors_cardOrder(MoveContext context, Card[] array) {
        if(context.isQuickPlay() && shouldAutoPlay_navigator_cardOrder(context, array)) {
            return super.survivors_cardOrder(context, array);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(array));
        for (int i : order) {
            orderedCards.add(array[i]);
        }
        return orderedCards.toArray(new Card[0]);
    }
    @Override
    public boolean cultist_shouldPlayNext(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_cultist_shouldPlayNext(context)) {
            return super.cultist_shouldPlayNext(context);
        }

        String option1 = getString(R.string.cultist_play_next);
        String option2 = getString(R.string.pass);

        return selectBoolean(context, Cards.cultist.getName(), option1, option2);
    }
    
    @Override
    public Card[] urchin_attack_cardsToKeep(MoveContext context) {
        //if(context.isQuickPlay() && shouldAutoPlay_urchin_attack_cardsToKeep(context)) {
        //    return super.urchin_attack_cardsToKeep(context);
        //}
        
        SelectCardOptions sco = new SelectCardOptions().setCount(4).exactCount().setPickType(PickType.KEEP);
        return getFromHand(context, getString(R.string.urchin_keep), sco);
    }
    
    @Override
    public boolean urchin_shouldTrashForMercenary(MoveContext context)
    {
        if(context.isQuickPlay() && shouldAutoPlay_urchin_shouldTrashForMercenary(context)) {
            return super.urchin_shouldTrashForMercenary(context);
        }

        String option1 = getString(R.string.urchin_trash_for_mercenary);
        String option2 = getString(R.string.pass);

        return selectBoolean(context, Cards.urchin.getName(), option1, option2);
    }
    
    @Override
    public Card[] mercenary_cardsToTrash(MoveContext context) {
        //if(context.isQuickPlay() && shouldAutoPlay_mercenary_cardsToTrash(context)) {
        //    return super.mercenary_cardsToTrash(context);
        //}
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getFromHand(context, getActionString(ActionType.TRASH, Cards.mercenary), sco);
    }
    
    @Override
    public Card[] mercenary_attack_cardsToKeep(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_militia_attack_cardsToKeep(context)) {
            return super.mercenary_attack_cardsToKeep(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.KEEP);
        return getFromHand(context, getString(R.string.mercenary_part), sco);
    }

    @Override
    public boolean madman_shouldReturnToPile(MoveContext context)
    {
        if(context.isQuickPlay() && shouldAutoPlay_madman_shouldReturnToPile(context)) {
            return super.madman_shouldReturnToPile(context);
        }

        String option1 = getString(R.string.madman_option);
        String option2 = getString(R.string.pass);

        return selectBoolean(context, Cards.madman.getName(), option1, option2);
    }
    
    @Override
    public Card hermit_cardToTrash(MoveContext context, ArrayList<Card> cardList, int nonTreasureCountInDiscard)
    {
        LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();
        
        int cardCount = 0;
        
        // Add option to skip the trashing
        h.put("None", null);
        
        for (Card c : cardList) {
            if (cardCount < nonTreasureCountInDiscard) {
                h.put(c.getName() + " (discard pile)", c);
            } else {
                h.put(c.getName() + " (hand)", c);
            }
            
            ++cardCount;
        }
        
        String choice = selectString(context, getActionString(ActionType.TRASH, Cards.hermit), h.keySet().toArray(new String[0])); 
        
        if (choice.contains("discard pile")) {
            context.hermitTrashCardPile = PileSelection.DISCARD;
        } else if (choice.contains("hand")) {
            context.hermitTrashCardPile = PileSelection.HAND;
        } 
        
        return h.get(choice);
    }
    
    @Override
    public Card hermit_cardToGain(MoveContext context)  {
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(3);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.hermit), sco);
    }
        
    @Override
    public Card[] dameAnna_cardsToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_chapel_cardsToTrash(context)) {
            return super.dameAnna_cardsToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getFromHand(context, getActionString(ActionType.TRASH, Cards.dameAnna), sco);
    }
    @Override
    public Card knight_cardToTrash(MoveContext context, ArrayList<Card> canTrash) {
        LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();
        for (Card c : canTrash) {
            h.put(c.getName(), c);
        }
        return h.get(selectString(context, Cards.virtualKnight, h.keySet().toArray(new String[0])));
    }
    @Override
    public Card[] sirMichael_attack_cardsToKeep(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_militia_attack_cardsToKeep(context)) {
            return super.sirMichael_attack_cardsToKeep(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.KEEP);
        return getFromHand(context, getString(R.string.sir_michael_part), sco);
    }
    @Override
    public Card dameNatalie_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.dameNatalie_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(3).setPassable(getString(R.string.none));
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.dameNatalie), sco);
    }

    @Override
    public ActionCard bandOfMisfits_actionCardToImpersonate(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_bandOfMisfits_actionCardToImpersonate(context)) {
            return super.bandOfMisfits_actionCardToImpersonate(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(Cards.bandOfMisfits.getCost(context) - 1).isAction().setPassable(getString(R.string.none));
        return (ActionCardImpl) getFromTable(context, getString(R.string.part_play), sco);
    }
    
    @Override
    public TreasureCard taxman_treasureToTrash(MoveContext context) 
    {    
        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return (TreasureCard) getCardFromHand(context, getActionString(ActionType.TRASH, Cards.taxman), sco);
    }
    
    @Override
    public TreasureCard taxman_treasureToObtain(MoveContext context, int maxCost) {
        if(context.isQuickPlay() && shouldAutoPlay_taxman_treasureToObtain(context, maxCost)) {
            return super.taxman_treasureToObtain(context, maxCost);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure().maxCost(maxCost);
        return (TreasureCard) getFromTable(context, getString(R.string.taxman_part), sco);
    }
    
    @Override
    public TreasureCard plaza_treasureToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_stables_treasureToDiscard(context)) {
            return super.stables_treasureToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return (TreasureCard) getCardFromHand(context, getActionString(ActionType.DISCARD, Cards.plaza), sco);
    }
    
    @Override
    public int numGuildsCoinTokensToSpend(MoveContext context)
    {
        return selectInt(context, "Spend Guilds Coin Tokens", getGuildsCoinTokenCount(), 0);
    }
    
    @Override
    public int amountToOverpay(MoveContext context, int cardCost)
    {
        int availableAmount = context.getCoinAvailableForBuy() - cardCost;
        
        // If at least one potion is available, it can be used to overpay
        int potion = context.potions;
        
        if (availableAmount <= 0)
        {
            return 0;
        }
        else
        {
            return selectInt(context, "Overpay?", availableAmount, 0);
        }
    }
    
    @Override
    public int overpayByPotions(MoveContext context, int availablePotions)
    {
        if (availablePotions > 0)
        {
            return selectInt(context, "Overpay by Potion(s)?", availablePotions, 0);
        }
        else
        {
            return 0;
        }
    }
    
    @Override
    public Card butcher_cardToTrash(MoveContext context) 
    {
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH).setPassable(getString(R.string.none));
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.butcher), sco);
    }
    
    @Override
    public Card butcher_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt)
    {        
        SelectCardOptions sco = new SelectCardOptions().maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.butcher), sco);
    }
    
    @Override
    public Card advisor_cardToDiscard(MoveContext context, Card[] cards)
    {
        ArrayList<String> options = new ArrayList<String>();
        
        for (Card c : cards) 
        {
            options.add(Strings.getCardName(c));
        }

        if (!options.isEmpty()) 
        {
            String o = selectString(context, getActionString(ActionType.OPPONENTDISCARD, Cards.advisor, context.getPlayer().getPlayerName()), options.toArray(new String[0]));
            return (Card) localNameToCard(o, cards);
        } 
        else 
        {
            return null;
        }
    }
    
    @Override
    public Card journeyman_cardToPick(MoveContext context) 
    {
        SelectCardOptions sco = new SelectCardOptions().allowEmpty();
        return getFromTable(context, getActionString(ActionType.NAMECARD, Cards.journeyman), sco);
    }
    
    @Override
    public Card stonemason_cardToTrash(MoveContext context)
    {
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH).allowEmpty();
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.stonemason), sco);
    }
    
    @Override
    public Card stonemason_cardToGain(MoveContext context, int maxCost, boolean potion,int debt)
    {
        SelectCardOptions sco = new SelectCardOptions().allowEmpty().maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.stonemason), sco);
    }
    
    @Override
    public Card stonemason_cardToGainOverpay(MoveContext context, int overpayAmount, boolean potion,int debt)
    {
        SelectCardOptions sco = new SelectCardOptions().allowEmpty().exactCost(overpayAmount).isAction().potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.stonemason), sco);
    }
    
    @Override
    public Card doctor_cardToPick(MoveContext context)
    {
        SelectCardOptions sco = new SelectCardOptions().allowEmpty();
        return getFromTable(context, getActionString(ActionType.NAMECARD, Cards.doctor), sco);
    }
    
    @Override
    public ArrayList<Card> doctor_cardsForDeck(MoveContext context, ArrayList<Card> cards) 
    {
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        
        int[] order = orderCards(context, cardArrToIntArr(cards.toArray(new Card[0])));
        
        for (int i : order)
        {
            orderedCards.add(cards.get(i));
        }
        
        return orderedCards;
    }
    
    @Override
    public DoctorOverpayOption doctor_chooseOption(MoveContext context, Card card) 
    {
        LinkedHashMap<String, DoctorOverpayOption> optionMap = new LinkedHashMap<String, DoctorOverpayOption>();
        
        optionMap.put(getString(R.string.doctor_overpay_option_one),   DoctorOverpayOption.TrashIt);
        optionMap.put(getString(R.string.doctor_overpay_option_two),   DoctorOverpayOption.DiscardIt);
        optionMap.put(getString(R.string.doctor_overpay_option_three), DoctorOverpayOption.PutItBack);
    
        return optionMap.get(selectString(context, "Doctor revealed " + getCardName(card), optionMap.keySet().toArray(new String[0])));
    }
    
    @Override
    public Card herald_cardTopDeck(MoveContext context, Card[] cardList)
    {
        ArrayList<String> options = new ArrayList<String>();
        
        // Remove first Herald from this list (representing the most recent one bought)
        boolean heraldRemoved = false;
        
        for (Card c : cardList) 
        {
            if (!heraldRemoved && c.getName().equalsIgnoreCase("herald"))
            {
                heraldRemoved = true;
            }
            else
            {
                options.add(Strings.getCardName(c));
            }
        }

        if (!options.isEmpty()) 
        {
            String o = selectString(context, R.string.herald_overpay_query, Cards.herald, options.toArray(new String[0]));
            return (Card) localNameToCard(o, cardList);
        } 
        else 
        {
            return null;
        }
    }

    public Card raze_cardToTrash(MoveContext context) 
    {
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH).setPassable(getString(R.string.none));
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.raze), sco);
    }
    
    public Card raze_cardToDraw(MoveContext context, Card[] cardList) 
    {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
        String o = selectString(context, R.string.raze_query, Cards.raze, options.toArray(new String[0]));
        return (Card) localNameToCard(o, cardList);
    }
    
	public Card amulet_cardToTrash(MoveContext context)
	{
        if(context.isQuickPlay() && shouldAutoPlay_trader_cardToTrash(context)) {
            return super.amulet_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.amulet), sco);
	}

    public AmuletOption amulet_chooseOption(MoveContext context) {
        LinkedHashMap<String, AmuletOption> h = new LinkedHashMap<String, AmuletOption>();
        h.put(getString(R.string.amulet_option_one), AmuletOption.AddGold);
        h.put(getString(R.string.amulet_option_two), AmuletOption.TrashCard);
        h.put(getString(R.string.amulet_option_three), AmuletOption.GainSilver);
        return h.get(selectString(context, Cards.amulet, h.keySet().toArray(new String[0])));
    }

    public Card[] dungeon_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_warehouse_cardsToDiscard(context)) {
            return super.dungeon_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.dungeon), sco);
    }

    public Card[] gear_cardsToSetAside(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.gear_cardsToSetAside(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).setPassable(getString(R.string.none)).setPickType(PickType.SELECT);
        return getFromHand(context, getCardName(Cards.gear), sco);
    }

    public boolean shouldExchangeTraveller(MoveContext context,Card source, Card target)
    {
        String option1 = source.getName() + " " + getString(R.string.exchange_traveller) + " " + target.getName();
        String option2 = getString(R.string.pass);

        return selectBoolean(context, source.getName(), option1, option2);
    }
    
	public Card fugitive_cardToDiscard(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.fugitive_cardToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.DISCARD);
        return getCardFromHand(context, getActionString(ActionType.DISCARD, Cards.fugitive), sco);
	}

    public Card soldier_attack_cardToDiscard(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.soldier_attack_cardToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.DISCARD);
        return getCardFromHand(context, getActionString(ActionType.DISCARD,Cards.soldier), sco);
	}

    public Card hero_cardToObtain(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.hero_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure();
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.hero), sco);
    }
    
    public ActionCard disciple_cardToPlay(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_kingsCourt_cardToPlay(context)) {
            return super.kingsCourt_cardToPlay(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isAction().setPassable(getString(R.string.none)).setPickType(PickType.PLAY);
        return (ActionCard) getCardFromHand(context, getCardName(Cards.disciple), sco);
    }

    public boolean messenger_shouldDiscardDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_chancellor_shouldDiscardDeck(context)) {
            return super.messenger_shouldDiscardDeck(context);
        }
        return selectBoolean(context, getCardName(Cards.messenger), getString(R.string.chancellor_query), getString(R.string.pass));
    }

    public Card messenger_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.messenger_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(4).potionCost(0);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.messenger), sco);
    }

    public boolean miser_takeCoin(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.miser_takeCoin(context);
        }
        return !selectBoolean(context, getCardName(Cards.miser), getString(R.string.miser_option_one), getString(R.string.miser_option_two));
    }

    public Card[] artificer_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_cellar_cardsToDiscard(context)) {
            return super.artificer_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.artificer), sco);
    }

    public Card artificer_cardToObtain(MoveContext context, int maxCost) {
        if(context.isQuickPlay()) {
            return super.artificer_cardToObtain(context, maxCost);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).exactCost(maxCost).setPassable(getString(R.string.none));
        return getFromTable(context, getActionString(ActionType.GAIN,Cards.artificer), sco);
    }

    public Card alms_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.alms_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(4).potionCost(0);
        return getFromTable(context, getActionString(ActionType.GAIN, Event.alms, ""), sco);
    }

    public QuestOption quest_chooseOption(MoveContext context) {
        LinkedHashMap<String, QuestOption> h = new LinkedHashMap<String, QuestOption>();
        h.put(getString(R.string.quest_option_one), QuestOption.Attack);
        h.put(getString(R.string.quest_option_two), QuestOption.Curses);
        h.put(getString(R.string.quest_option_three), QuestOption.Cards);
        return h.get(selectString(context, Cards.eventCard, h.keySet().toArray(new String[0])));
    }

	public Card quest_attackToDiscard(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isAttack();
        return getCardFromHand(context, getActionString(ActionType.DISCARD,Event.quest,""), sco);
	}

	public Card[] quest_cardsToDiscard(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().setCount(6).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD,Event.quest,""), sco);
	}

	public Card save_cardToSetAside(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_haven_cardToSetAside(context)) {
            return super.save_cardToSetAside(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, Event.save.name, sco);
	}

    public Card[] scoutingParty_cardsFromTopOfDeckToDiscard(MoveContext context, Card[] cards) {
        ArrayList<Card> cardsToDiscard = new ArrayList<Card>();
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));
        do {
            String o = selectString(context, R.string.Cartographer_query, Cards.eventCard, options.toArray(new String[0]));
            cardsToDiscard.add(localNameToCard(o, cards));
            options.remove(o);
        } while (cardsToDiscard.size() < 3);
        return cardsToDiscard.toArray(new Card[0]);
    }

    public Card[] scoutingParty_cardOrder(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_cartographer_cardOrder(context, cards)) {
            return super.scoutingParty_cardOrder(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards));
        for (int i : order)
            orderedCards.add(cards[i]);
        return orderedCards.toArray(new Card[0]);
	}

    public boolean travellingFair_shouldPutCardOnDeck(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_royalSeal_shouldPutCardOnDeck(context, card)) {
            return super.travellingFair_shouldPutCardOnDeck(context, card);
        }
        return selectBooleanCardRevealed(context, Cards.eventCard, card, getString(R.string.top_of_deck), getString(R.string.take_normal));
    }

    public Card[] bonfire_cardsToTrash(MoveContext context, Card[] cards)
	{
        ArrayList<Card> cardsToTrash = new ArrayList<Card>();
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);
        do {
            String o = selectString(context, Cards.eventCard, options.toArray(new String[0]));
			if (o.equals(none))
				break;
            cardsToTrash.add(localNameToCard(o, cards));
            options.remove(o);
        } while (cardsToTrash.size() < 2);
        return cardsToTrash.toArray(new Card[0]);
    }

    public Card ball_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.ball_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(4).potionCost(0);
        return getFromTable(context, getActionString(ActionType.GAIN, Event.ball, ""), sco);
    }

    public Card seaway_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_university_actionCardToObtain(context)) {
            return super.seaway_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(4).isAction();
        return getFromTable(context, getActionString(ActionType.GAIN,Event.seaway,""), sco);
    }

    public Card[] trade_cardsToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_chapel_cardsToTrash(context)) {
            return super.trade_cardsToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getFromHand(context, getActionString(ActionType.TRASH, Event.trade,""), sco);
    }

	public boolean callReserveCard(MoveContext context, Card target) {
        if(context.isQuickPlay()) {
			return true;
        }
        return selectBoolean(context, getCardName(target), getString(R.string.call_query), getString(R.string.pass));
	}

    public Card ratcatcher_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_trader_cardToTrash(context)) {
            return super.ratcatcher_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.ratcatcher), sco);
    }
    
    public Card transmogrify_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_trader_cardToTrash(context)) {
            return super.transmogrify_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.transmogrify), sco);
    }
    
    public Card transmogrify_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_remodel_cardToObtain(context, maxCost, potion)) {
            return super.transmogrify_cardToObtain(context, maxCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.transmogrify), sco);
    }

    public Card[] pilgrimage_cardsToObtain(MoveContext context, Card[] cards) {
		ArrayList<Card> choices = new ArrayList<Card>(Arrays.asList(cards));
		Collections.sort(choices, new CardCostComparator());
        ArrayList<Card> cardsToObtain = new ArrayList<Card>();
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : choices)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);
        do {
            String o = selectString(context, Cards.eventCard, options.toArray(new String[0]));
			if (o.equals(none))
				break;
            cardsToObtain.add(localNameToCard(o, cards));
            options.remove(o);
        } while (cardsToObtain.size() < 3);
        return cardsToObtain.toArray(new Card[0]);
    }

    public Card plan_placeToken(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isAction().allowEmpty();
        return getFromTable(context, Event.plan.name , sco);
    }

    public Card ferry_placeToken(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isAction();
        return getFromTable(context, Event.ferry.name , sco);
    }

    public Card lostArts_placeToken(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isAction().allowEmpty();
        return getFromTable(context, Event.lostArts.name , sco);
    }

    public Card training_placeToken(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isAction().allowEmpty();
        return getFromTable(context, Event.training.name , sco);
    }

    public Card pathfinding_placeToken(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isAction().allowEmpty();
        return getFromTable(context, Event.pathfinding.name , sco);
    }

    public Card inheritance_placeToken(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isAction().isNonVictory().potionCost(0).maxCost(4).allowEmpty();
        return getFromTable(context, Event.inheritance.name , sco);
    }

    public TeacherOption teacher_chooseOption(MoveContext context) {
        LinkedHashMap<String, TeacherOption> h = new LinkedHashMap<String, TeacherOption>();
        h.put(getString(R.string.teacher_option_one), TeacherOption.PlusCard);
        h.put(getString(R.string.teacher_option_two), TeacherOption.PlusAction);
        h.put(getString(R.string.teacher_option_three), TeacherOption.PlusCoin);
        h.put(getString(R.string.teacher_option_four), TeacherOption.PlusBuy);
        return h.get(selectString(context, Cards.eventCard, h.keySet().toArray(new String[0])));
	}

	public Card teacher_placeToken(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isAction().allowEmpty();
        sco.fromTable();
        Card[] cards = context.getCardsInGame();
        for (Card card : cards) {
            if ((card instanceof ActionCard) &&
				card != context.player.trashingToken &&
				card != context.player.minusCostToken &&
				card != context.player.plusCardToken &&
				card != context.player.plusActionToken &&
				card != context.player.plusCoinToken &&
				card != context.player.plusBuyToken) {
                    sco.addValidCard(cardToInt(card));
            }
        }
        if (sco.getAllowedCardCount() == 0) {
            return null;
        }
        String selectString = Strings.format(R.string.select_from_table, Cards.teacher.getName());
        return (Card) pickACard(context, selectString, sco);
    }

	public Card trashingToken_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_trader_cardToTrash(context)) {
            return super.trashingToken_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH).setPassable(getString(R.string.none));
        return getCardFromHand(context, getActionString(ActionType.TRASH, Event.plan,""), sco);
	}

	public Card[] storyteller_cardsToPlay(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPassable(getString(R.string.none)).setCount(3).ordered().setPickType(PickType.SELECT);
        return getFromHand(context, Cards.storyteller.getName(), sco);
	}

	public int payDebt(MoveContext context, int debt) {
		int coin = context.getCoinAvailableForBuy();
        return selectInt(context, "Pay Debt", coin<debt?coin:debt, 0);
	}

    public Card engineer_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.engineer_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(4).potionCost(0);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.engineer), sco);
    }

    public boolean engineer_shouldTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_miningVillage_shouldTrashMiningVillage(context)) {
            return super.engineer_shouldTrash(context);
        }
        return selectBoolean(context, getCardName(Cards.engineer), getString(R.string.trash), getString(R.string.keep));
    }

    public ActionCard overlord_actionCardToImpersonate(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_bandOfMisfits_actionCardToImpersonate(context)) {
            return super.overlord_actionCardToImpersonate(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(5).isAction().setPassable(getString(R.string.none));
        return (ActionCardImpl) getFromTable(context, getString(R.string.part_play), sco);
    }
    
	public boolean settlers_shouldReveal(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.settlers_shouldReveal(context);
        }
        return selectBoolean(context, getCardName(Cards.settlers), getActionString(ActionType.REVEAL,Cards.copper,null), getString(R.string.pass));

	}

	public boolean bustlingVillage_shouldReveal(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.bustlingVillage_shouldReveal(context);
        }
        return selectBoolean(context, getCardName(Cards.bustlingVillage), getActionString(ActionType.REVEAL,Cards.settlers,null), getString(R.string.pass));
	}

    public Card[] catapult_attack_cardsToKeep(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_militia_attack_cardsToKeep(context)) {
            return super.catapult_attack_cardsToKeep(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.KEEP);
        return getFromHand(context, getString(R.string.keep), sco);
    }

    public Card catapult_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_salvager_cardToTrash(context)) {
            return super.catapult_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.catapult), sco);
    }

    public Card sacrifice_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_salvager_cardToTrash(context)) {
            return super.sacrifice_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.sacrifice), sco);
    }

    public Card[] forum_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_warehouse_cardsToDiscard(context)) {
            return super.forum_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.forum), sco);
    }

    public boolean legionary_shouldRevealGold(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.legionary_shouldRevealGold(context);
        }
        return selectBoolean(context, Cards.legionary, getActionString(ActionType.REVEAL,Cards.gold,null), getString(R.string.pass));

    }

    public Card[] legionary_attack_cardsToKeep(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_militia_attack_cardsToKeep(context)) {
            return super.legionary_attack_cardsToKeep(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.KEEP);
        return getFromHand(context, getString(R.string.keep), sco);
    }

    public Card[] annex_cardsToKeepInDiscard(MoveContext context, Card[] cards) {
        ArrayList<Card> cardsToDiscard = new ArrayList<Card>();
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);
        do {
            String o = selectString(context, R.string.Cartographer_query, Cards.eventCard, options.toArray(new String[0]));
			if (o.equals(none))
				break;
            cardsToDiscard.add(localNameToCard(o, cards));
            options.remove(o);
        } while (cardsToDiscard.size() < 5);
        return cardsToDiscard.toArray(new Card[0]);
    }

    public Card advance_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_apprentice_cardToTrash(context)) {
            return super.advance_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isAction().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.eventCard), sco);
    }
    
    public ActionCard advance_actionCardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_university_actionCardToObtain(context)) {
            return super.advance_actionCardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(6).isAction().setPassable(getString(R.string.none));
        return (ActionCard) getFromTable(context, getActionString(ActionType.GAIN, Cards.eventCard), sco);
    }

    public Card banquet_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.banquet_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(5).isNonVictory();
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.eventCard), sco);
	}

    public Card ritual_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_salvager_cardToTrash(context)) {
            return super.ritual_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.eventCard), sco);
    }

	public Card saltTheEarth_supplyCardToTrash(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.saltTheEarth_supplyCardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isVictory();
        return getFromTable(context, getActionString(ActionType.TRASH, Cards.eventCard), sco);
	}

    public Card[] donate_cardsToTrash(MoveContext context, Card[] cards) {
        ArrayList<Card> cardsToTrash = new ArrayList<Card>();
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);
        do {
            String o = selectString(context,getActionString(ActionType.TRASH, Cards.eventCard), options.toArray(new String[0]));
			if (o.equals(none))
				break;
            cardsToTrash.add(localNameToCard(o, cards));
            options.remove(o);
        } while ( true);
        return cardsToTrash.toArray(new Card[0]);
    }

    public Card tax_supplyToTax(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_embargo_supplyToEmbargo(context)) {
            return super.tax_supplyToTax(context);
        }
        SelectCardOptions sco = new SelectCardOptions().allowEmpty();
        return getFromTable(context, Event.tax.displayName, sco);
    }

	public Card smallCastle_cardToTrash(MoveContext context, Card[] cardList) {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
        String o = selectString(context, getActionString(ActionType.TRASH,Cards.smallCastle), options.toArray(new String[0]));
        return (Card) localNameToCard(o, cardList);
    }
    
    public Card[] hauntedCastle_cardsToPutBackOnDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_ghostShip_attack_cardsToPutBackOnDeck(context)) {
            return super.hauntedCastle_cardsToPutBackOnDeck(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).ordered();
        return getFromHand(context, getString(R.string.ironmonger_option_one), sco);
    }

    public Card[] opulentCastle_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_warehouse_cardsToDiscard(context)) {
            return super.opulentCastle_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isVictory().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.opulentCastle), sco);
    }

    public HuntingGroundsOption sprawlingCastle_chooseOption(MoveContext context) {
        LinkedHashMap<String, HuntingGroundsOption> h = new LinkedHashMap<String, HuntingGroundsOption>();   
        h.put(getString(R.string.hunting_grounds_option_one), HuntingGroundsOption.GainDuchy);
        h.put(getString(R.string.hunting_grounds_option_two), HuntingGroundsOption.GainEstates);
        return h.get(selectString(context, Cards.sprawlingCastle, h.keySet().toArray(new String[0])));
    }
    
    public boolean encampment_shouldReveal(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.encampment_shouldReveal(context);
        }
        return selectBoolean(context, Cards.encampment, getString(R.string.encampment_option), getString(R.string.pass));
    }

    public boolean gladiator_shouldReveal(MoveContext context,Card card) {
        if(context.isQuickPlay()) {
            return super.gladiator_shouldReveal(context,card);
        }
        return selectBoolean(context, Cards.gladiator, getActionString(ActionType.REVEAL,card,null), getString(R.string.pass));

    }

    public Card gladiator_revealedCard(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.gladiator_revealedCard(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, getActionString(ActionType.REVEAL, Cards.gladiator), sco);
    }

    public Card[] temple_cardsToTrash(MoveContext context, Card[] cards)
	{
        ArrayList<Card> cardsToTrash = new ArrayList<Card>();
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        do {
            String o = selectString(context, Cards.temple, options.toArray(new String[0]));
			if (o.equals(none))
				break;
            cardsToTrash.add(localNameToCard(o, cards));
            options.remove(o);
			if (cardsToTrash.size()==1)
				options.add(none);
        } while (cardsToTrash.size() < 3);
        return cardsToTrash.toArray(new Card[0]);
    }

    public WildHuntOption wildHunt_chooseOption(MoveContext context) {
        LinkedHashMap<String, WildHuntOption> h = new LinkedHashMap<String, WildHuntOption>();
        h.put(getString(R.string.wild_hunt_option_one), WildHuntOption.AddCards);
        h.put(getString(R.string.wild_hunt_option_two), WildHuntOption.GainEstate);
        return h.get(selectString(context, Cards.wildHunt, h.keySet().toArray(new String[0])));
    }
    
    public Card archive_cardToDraw(MoveContext context, Card[] cardList) 
    {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
        String o = selectString(context, R.string.raze_query, Cards.archive, options.toArray(new String[0]));
        return (Card) localNameToCard(o, cardList);
    }
    
    public CharmOption charm_chooseOption(MoveContext context) {
        LinkedHashMap<String, CharmOption> h = new LinkedHashMap<String, CharmOption>();
        h.put(getString(R.string.charm_option_one), CharmOption.AddCoin);
        h.put(getString(R.string.charm_option_two), CharmOption.GainCard);
        return h.get(selectString(context, Cards.charm, h.keySet().toArray(new String[0])));
    }
    
    public Card charm_cardToObtain(MoveContext context, Card[] cardList) {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
		String none = getString(R.string.none);
		options.add(none);
        String o = selectString(context,getActionString(ActionType.GAIN, Cards.charm), options.toArray(new String[0]));
		if(o.equals(none)) return null;
        return (Card) localNameToCard(o, cardList);
    }

    public ActionCard crown_actionCardToPlay(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_throneRoom_cardToPlay(context)) {
            return super.crown_actionCardToPlay(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isAction().setPassable(getString(R.string.none)).setPickType(PickType.PLAY);
        return (ActionCard) getCardFromHand(context, getCardName(Cards.crown), sco);
    }

    public TreasureCard crown_treasureCardToPlay(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.crown_treasureCardToPlay(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPassable(getString(R.string.none)).setPickType(PickType.PLAY);
        return (TreasureCard) getCardFromHand(context, getActionString(ActionType.REVEAL, Cards.crown), sco);
    }
    
	public Card arena_actionToDiscard(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().isAction().setPassable(getString(R.string.none));
        return getCardFromHand(context, getActionString(ActionType.DISCARD,Landmarks.arena,""), sco);
	}

    public int mountainPass_amountToBid(MoveContext context) {
        return selectInt(context, "Mountain Pass: Bid",40, 0);
    }
    
	public int stash_positionInDeck(MoveContext context,int deckSize) {
        return selectInt(context, "Stash: Position in Deck",deckSize, 0);
	}
	
	public boolean vassal_shouldPlayAction(MoveContext context, Card card) {
        if(context.isQuickPlay()) {
            return super.vassal_shouldPlayAction(context,card);
        }
        return selectBoolean(context, Cards.vassal, getActionString(ActionType.PLAY,card,""), getString(R.string.pass));
	}

	public Card harbinger_cardToTopdeck(MoveContext context, HashSet<Card> options) {
        LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();
		for (Card card : options)
			h.put(card.getName(),card);
		h.put("None",null);
        String choice = selectString(context,Cards.harbinger, h.keySet().toArray(new String[0])); 
		return h.get(choice);
	}

    public Card[] poacher_cardsToDiscard(MoveContext context, int num) {
        if(context.isQuickPlay() && shouldAutoPlay_warehouse_cardsToDiscard(context)) {
            return super.poacher_cardsToDiscard(context,num);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(num).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.poacher), sco);
    }

    public TreasureCard bandit_treasureToTrash(MoveContext context, TreasureCard[] treasures) {
        if(context.isQuickPlay() && shouldAutoPlay_thief_treasureToTrash(context, treasures)) {
            return super.bandit_treasureToTrash(context, treasures);
        }
        ArrayList<String> options = new ArrayList<String>();
        for (TreasureCard c : treasures)
            options.add(Strings.getCardName(c));

        if (options.size() > 0) {
            String o = selectString(context, R.string.treasure_to_trash, Cards.bandit, options.toArray(new String[0]));
            return (TreasureCard) localNameToCard(o, treasures);
        } else {
            return null;
        }
    }

 	public SentryOption sentry_chooseOption(MoveContext context, Card card) {
        LinkedHashMap<String, SentryOption> optionMap = new LinkedHashMap<String, SentryOption>();
        optionMap.put(getString(R.string.doctor_overpay_option_one),   SentryOption.TrashIt);
        optionMap.put(getString(R.string.doctor_overpay_option_two),   SentryOption.DiscardIt);
        optionMap.put(getString(R.string.doctor_overpay_option_three), SentryOption.KeepIt);
        return optionMap.get(selectString(context, "Sentry revealed " + getCardName(card), optionMap.keySet().toArray(new String[0])));
	}

    public Card artisan_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.artisan_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(5).potionCost(0);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.artisan), sco);
	}

    public Card artisan_cardToPutBackOnDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_courtyard_cardToPutBackOnDeck(context)) {
            return super.artisan_cardToPutBackOnDeck(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, Strings.format(R.string.courtyard_part_top_of_deck, getCardName(Cards.artisan)), sco);
	}

    public LurkerOption lurker_chooseOption(MoveContext context){
        LinkedHashMap<String, LurkerOption> h = new LinkedHashMap<String, LurkerOption>();
        h.put(getString(R.string.graverobber_option_one), LurkerOption.GainFromTrash);
        h.put(getString(R.string.graverobber_option_two), LurkerOption.TrashActionCard);
        return h.get(selectString(context, Cards.lurker, h.keySet().toArray(new String[0])));
	}

	public Card lurker_cardToGainFromTrash(MoveContext context) {
        LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();
        ArrayList<Card> options = new ArrayList<Card>(); 
        for (Card c : game.trashPile) {
            if (c instanceof ActionCard)
                options.add(c);
        }
        if (options.isEmpty())
            return null;
        for (Card c : options)
            h.put(c.getName(), c);
        return h.get(selectString(context, Cards.lurker, h.keySet().toArray(new String[0])));
	}

	public Card lurker_cardToTrash(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.lurker_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isAction();
        return getFromTable(context, getActionString(ActionType.TRASH, Cards.lurker), sco);
	}

	public Card secretPassage_cardToPutInDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_courtyard_cardToPutBackOnDeck(context)) {
            return super.secretPassage_cardToPutInDeck(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, Strings.format(R.string.courtyard_part_top_of_deck, getCardName(Cards.secretPassage)), sco);
	}

	public int secretPassage_positionInDeck(MoveContext context, int deckSize) {
        return selectInt(context, "Secret Passage: Position in Deck",deckSize, 0);
	}

    public Card[] diplomat_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_warehouse_cardsToDiscard(context)) {
            return super.diplomat_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.diplomat), sco);
    }

    public Card[] mill_cardsToDiscard(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.mill), sco);
    }

    public CourtierOption[] courtier_chooseOptions(MoveContext context, int num) {
        CourtierOption[] os = new CourtierOption[num];
        LinkedHashMap<String, CourtierOption> h = new LinkedHashMap<String, CourtierOption>();
        h.put(getString(R.string.courtier_one), CourtierOption.AddAction);
        h.put(getString(R.string.courtier_two), CourtierOption.AddBuy);
        h.put(getString(R.string.courtier_three), CourtierOption.AddGold);
        h.put(getString(R.string.courtier_four), CourtierOption.GainGold);
		for (int i=0;i<num;i++) {
			String o1 = selectString(context, Cards.courtier, h.keySet().toArray(new String[0]));
			os[i] = h.get(o1);
			h.remove(o1);
		}
        return os;
	}

    public Card courtier_revealedCard(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.courtier_revealedCard(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, getActionString(ActionType.REVEAL, Cards.courtier), sco);
    }

    public Card replace_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_trader_cardToTrash(context)) {
            return super.replace_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.replace), sco);
    }
    
    public Card replace_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_remodel_cardToObtain(context, maxCost, potion)) {
            return super.replace_cardToObtain(context, maxCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.replace), sco);
    }

    public Card devilsWorkshop_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.devilsWorkshop_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(4).potionCost(0);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.devilsWorkshop), sco);
    }

    public Card vampire_cardToObtain(MoveContext context, Card[] cardList) {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
        String o = selectString(context,getActionString(ActionType.GAIN, Cards.vampire), options.toArray(new String[0]));
        return (Card) localNameToCard(o, cardList);
    }

    public Card[] bat_cardsToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_chapel_cardsToTrash(context)) {
            return super.trade_cardsToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(2).setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getFromHand(context, getActionString(ActionType.TRASH, Cards.bat), sco);
    }

	public Card raider_discard_chooseOption(MoveContext context, Card[] cardList) {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList) {
            options.add(Strings.getCardName(c));
        }
		String o = selectString(context, getActionString(ActionType.DISCARD, Cards.raider, context.attackedPlayer.getPlayerName()), options.toArray(new String[0]));
		return (Card) localNameToCard(o, cardList);
	}

	public Card[] crypt_cardsToSetAside(MoveContext context, Card[] cardList) {
		ArrayList<Card> choices = new ArrayList<Card>(Arrays.asList(cardList));
		Collections.sort(choices, new CardCostComparator());
        ArrayList<Card> cardsToSetAside = new ArrayList<Card>();
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : choices)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);
        do {
            String o = selectString(context, Cards.crypt, options.toArray(new String[0]));
			if (o.equals(none))
				break;
            cardsToSetAside.add(localNameToCard(o, cardList));
            options.remove(o);
        } while (true);
        return cardsToSetAside.toArray(new Card[0]);
	}

    public Card crypt_cardToDraw(MoveContext context, Card[] cardList) {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
        String o = selectString(context, R.string.raze_query, Cards.crypt, options.toArray(new String[0]));
        return (Card) localNameToCard(o, cardList);
    }
    
    public Card goat_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_masquerade_cardToTrash(context)) {
            return super.goat_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.goat), sco);
    }

    public Card hauntedMirror_cardToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_warehouse_cardsToDiscard(context)) {
            return super.hauntedMirror_cardToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isAction().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getCardFromHand(context, getActionString(ActionType.DISCARD, Cards.hauntedMirror), sco);
    }

    public MonasteryOption monastery_chooseOption(MoveContext context){
        LinkedHashMap<String, MonasteryOption> h = new LinkedHashMap<String, MonasteryOption>();
        h.put(getString(R.string.monastery_option_one), MonasteryOption.TrashFromHand);
        h.put(getString(R.string.monastery_option_two), MonasteryOption.TrashCopper);
        h.put(getString(R.string.none), MonasteryOption.None);
        return h.get(selectString(context, Cards.monastery, h.keySet().toArray(new String[0])));
	}

    public Card monastery_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_masquerade_cardToTrash(context)) {
            return super.monastery_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.monastery), sco);
    }

    public Card[] nightWatchman_cardsFromTopOfDeckToDiscard(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_cartographer_cardsFromTopOfDeckToDiscard(context, cards)) {
            return super.nightWatchman_cardsFromTopOfDeckToDiscard(context, cards);
        }

        if(cards == null || cards.length == 0) {
            return cards;
        }

        ArrayList<Card> cardsToDiscard = new ArrayList<Card>();

        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);

        do {
            String o = selectString(context, R.string.Cartographer_query, Cards.nightWatchman, options.toArray(new String[0]));
            if (o.equals(none)) {
                break;
            }
            cardsToDiscard.add(localNameToCard(o, cards));
            options.remove(o);
        } while (options.size() > 1);

        return cardsToDiscard.toArray(new Card[0]);
    }

    public Card[] nightWatchman_cardOrder(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_cartographer_cardOrder(context, cards)) {
            return super.nightWatchman_cardOrder(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards));
        for (int i : order)
            orderedCards.add(cards[i]);
        return orderedCards.toArray(new Card[0]);
    }

    public Card[] secretCave_cardsToDiscard(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.secretCave), sco);
    }

    public Card changeling_cardToObtain(MoveContext context, Card[] cardList) {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
        String o = selectString(context,getActionString(ActionType.GAIN, Cards.changeling), options.toArray(new String[0]));
        return (Card) localNameToCard(o, cardList);
    }

    public boolean changeling_shouldGain(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_trader_shouldGainSilverInstead(context, card)) {
            return super.changeling_shouldGain(context, card);
        }
        return !selectBoolean(context, Cards.changeling, Strings.format(R.string.trader_gain, getCardName(card)), Strings.format(R.string.trader_gain_instead_of, getCardName(Cards.changeling), getCardName(card)));
    }

    public Card[] cemetery_cardsToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_chapel_cardsToTrash(context)) {
            return super.cemetery_cardsToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(4).setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getFromHand(context, getActionString(ActionType.TRASH, Cards.cemetery), sco);
    }

    public ActionCard conclave_cardToPlay(MoveContext context, ActionCard[] actions) {
        if(context.isQuickPlay() && shouldAutoPlay_scheme_actionToPutOnTopOfDeck(context, actions)) {
            return super.conclave_cardToPlay(context, actions);
        }
        ArrayList<String> options = new ArrayList<String>();
        for (ActionCard c : actions)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);
        String o = selectString(context, R.string.golem_first_action, Cards.conclave, options.toArray(new String[0]));
        if(o.equals(none)) {
            return null;
        }
        return (ActionCard) localNameToCard(o, actions);
    }

    public ActionCard imp_cardToPlay(MoveContext context, ActionCard[] actions) {
        if(context.isQuickPlay() && shouldAutoPlay_scheme_actionToPutOnTopOfDeck(context, actions)) {
            return super.imp_cardToPlay(context, actions);
        }
        ArrayList<String> options = new ArrayList<String>();
        for (ActionCard c : actions)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);
        String o = selectString(context, R.string.golem_first_action, Cards.imp, options.toArray(new String[0]));
        if(o.equals(none)) {
            return null;
        }
        return (ActionCard) localNameToCard(o, actions);
    }

    public Card exorcist_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_masquerade_cardToTrash(context)) {
            return super.exorcist_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.exorcist), sco);
    }

    public Card exorcist_cardToObtain(MoveContext context, Card[] cardList) {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
        String o = selectString(context,getActionString(ActionType.GAIN, Cards.exorcist), options.toArray(new String[0]));
        return (Card) localNameToCard(o, cardList);
    }

    public Card[] shepherd_cardsToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_warehouse_cardsToDiscard(context)) {
            return super.shepherd_cardsToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isVictory().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.shepherd), sco);
    }

    public Card cobbler_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.cobbler_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(4).potionCost(0);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.cobbler), sco);
    }

    public ActionCard necromancer_cardToPlay(MoveContext context, ActionCard[] actions) {
        if(context.isQuickPlay() && shouldAutoPlay_scheme_actionToPutOnTopOfDeck(context, actions)) {
            return super.necromancer_cardToPlay(context, actions);
        }
        ArrayList<String> options = new ArrayList<String>();
        for (ActionCard c : actions)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);
        String o = selectString(context, R.string.golem_first_action, Cards.necromancer, options.toArray(new String[0]));
        if(o.equals(none)) {
            return null;
        }
        return (ActionCard) localNameToCard(o, actions);
    }

    public boolean zombieSpy_shouldDiscard(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_jackOfAllTrades_shouldDiscardCardFromTopOfDeck(context, card)) {
            super.zombieSpy_shouldDiscard(context, card);
        }
        return !selectBooleanCardRevealed(context, Cards.zombieSpy, card, getString(R.string.jack_of_all_trades_option_one), getString(R.string.discard));
    }

    public Card zombieApprentice_actionToTrash(MoveContext context) {
		if (context.isQuickPlay())
			return super.zombieApprentice_actionToTrash(context);
        SelectCardOptions sco = new SelectCardOptions().isAction().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.zombieApprentice), sco);
    }
    
    public Card zombieMason_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_remodel_cardToObtain(context, maxCost, potion)) {
            return super.zombieMason_cardToObtain(context, maxCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.zombieMason), sco);
    }

	public Card pooka_cardToTrash(MoveContext context, Card[] cardList) {
		if (context.isQuickPlay())
			return super.pooka_cardToTrash(context, cardList);
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
        String o = selectString(context, getActionString(ActionType.TRASH,Cards.pooka), options.toArray(new String[0]));
        return (Card) localNameToCard(o, cardList);
    }
    
    public Card tragicHero_cardToObtain(MoveContext context) {
        if(context.isQuickPlay()) {
            return super.tragicHero_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure();
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.tragicHero), sco);
    }
    
    public TreasureCard earthsgift_treasureToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_stables_treasureToDiscard(context)) {
            return super.earthsgift_treasureToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().isTreasure().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return (TreasureCard) getCardFromHand(context, getActionString(ActionType.DISCARD, Cards.boonCard), sco);
    }

    public Card earthsgift_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.earthsgift_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(4).potionCost(0);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.boonCard), sco);
    }

    public Card flamesgift_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_masquerade_cardToTrash(context)) {
            return super.flamesgift_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.boonCard), sco);
    }

	public Card moonsgift_cardToTopdeck(MoveContext context, HashSet<Card> options) {
        LinkedHashMap<String, Card> h = new LinkedHashMap<String, Card>();
		for (Card card : options)
			h.put(card.getName(),card);
		h.put("None",null);
        String choice = selectString(context,Cards.boonCard, h.keySet().toArray(new String[0])); 
		return h.get(choice);
	}

    public Card[] skysgift_cardsToDiscard(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.boonCard), sco);
    }

    public Card[] sunsgift_cardsFromTopOfDeckToDiscard(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_cartographer_cardsFromTopOfDeckToDiscard(context, cards)) {
            return super.sunsgift_cardsFromTopOfDeckToDiscard(context, cards);
        }

        if(cards == null || cards.length == 0) {
            return cards;
        }

        ArrayList<Card> cardsToDiscard = new ArrayList<Card>();

        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));
        String none = getString(R.string.none);
        options.add(none);

        do {
            String o = selectString(context, R.string.Cartographer_query, Cards.boonCard, options.toArray(new String[0]));
            if (o.equals(none)) {
                break;
            }
            cardsToDiscard.add(localNameToCard(o, cards));
            options.remove(o);
        } while (options.size() > 1);

        return cardsToDiscard.toArray(new Card[0]);
    }

    public Card[] sunsgift_cardOrder(MoveContext context, Card[] cards) {
        if(context.isQuickPlay() && shouldAutoPlay_cartographer_cardOrder(context, cards)) {
            return super.sunsgift_cardOrder(context, cards);
        }
        ArrayList<Card> orderedCards = new ArrayList<Card>();
        int[] order = orderCards(context, cardArrToIntArr(cards));
        for (int i : order)
            orderedCards.add(cards[i]);
        return orderedCards.toArray(new Card[0]);
    }

    public Card[] windsgift_cardsToDiscard(MoveContext context) {
        SelectCardOptions sco = new SelectCardOptions().setCount(2).exactCount().setPickType(PickType.DISCARD);
        return getFromHand(context, getActionString(ActionType.DISCARD, Cards.boonCard), sco);
    }

    public boolean pixie_shouldTrash(MoveContext context, Boons boon) {
        if(context.isQuickPlay() && shouldAutoPlay_miningVillage_shouldTrashMiningVillage(context)) {
            return super.pixie_shouldTrash(context, boon);
        }
        return selectBoolean(context, getCardName(Cards.pixie), boon.description+" twice", getString(R.string.keep));
    }

    public boolean sacredGrove_receiveBoons(MoveContext context, Boons boon) {
        return selectBoolean(context, getCardName(Cards.sacredGrove), boon.description, getString(R.string.discard));
    }

    public Card fear_cardToDiscard(MoveContext context, Card[] cards) {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cards)
            options.add(Strings.getCardName(c));
        String o = selectString(context, R.string.lookout_query_discard, Cards.hexCard, options.toArray(new String[0]));
        return localNameToCard(o, cards);
    }

    public Card haunting_cardToPutBackOnDeck(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_courtyard_cardToPutBackOnDeck(context)) {
            return super.haunting_cardToPutBackOnDeck(context);
        }
        SelectCardOptions sco = new SelectCardOptions();
        return getCardFromHand(context, Strings.format(R.string.courtyard_part_top_of_deck, getCardName(Cards.hexCard)), sco);
    }

    public Card[] poverty_cardsToKeep(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_militia_attack_cardsToKeep(context)) {
            return super.poverty_cardsToKeep(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setCount(3).exactCount().setPickType(PickType.KEEP);
        return getFromHand(context, getString(R.string.keep), sco);
    }

    public Card locusts_cardToObtain(MoveContext context, Card[] cardList) {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
        String o = selectString(context,getActionString(ActionType.GAIN, Cards.hexCard), options.toArray(new String[0]));
        return (Card) localNameToCard(o, cardList);
    }

    public boolean blessedVillage_receiveBoons(MoveContext context, Boons boon) {
        return selectBoolean(context, getCardName(Cards.blessedVillage), boon.description, getString(R.string.keep));
    }

    public Card lostInTheWoods_cardToDiscard(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_hamlet_cardToDiscardForAction(context)) {
            return super.lostInTheWoods_cardToDiscard(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.DISCARD);
        return getCardFromHand(context, getActionString(ActionType.DISCARD, Cards.fool), sco);
    }

	public Boons druid_boonToPlay(MoveContext context, Boons[] boons) {
        ArrayList<String> options = new ArrayList<String>();
        for (Boons b : boons)
            options.add(b.description);
        String o = selectString(context,getActionString(ActionType.PLAY, Cards.druid), options.toArray(new String[0]));
		if (o.equals(options.get(0)))
			return boons[0];
		if (o.equals(options.get(1)))
			return boons[1];
		return boons[2];
	}

    public boolean tracker_shouldPutCardOnDeck(MoveContext context, Card card) {
        if(context.isQuickPlay() && shouldAutoPlay_royalSeal_shouldPutCardOnDeck(context, card)) {
            return super.tracker_shouldPutCardOnDeck(context, card);
        }
        return selectBooleanCardRevealed(context, Cards.tracker, card, getString(R.string.top_of_deck), getString(R.string.take_normal));
    }

    public Card wish_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_workshop_cardToObtain(context)) {
            return super.wish_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(6).potionCost(0);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.wish), sco);
    }

    public Card dismantle_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_trader_cardToTrash(context)) {
            return super.dismantle_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.dismantle), sco);
    }
    
    public Card dismantle_cardToObtain(MoveContext context, int maxCost, boolean potion,int debt) {
        if(context.isQuickPlay() && shouldAutoPlay_remodel_cardToObtain(context, maxCost, potion)) {
            return super.dismantle_cardToObtain(context, maxCost, potion,debt);
        }
        SelectCardOptions sco = new SelectCardOptions().maxCost(maxCost).potionCost(potion ? 1 : 0).debtCost(debt);
        return getFromTable(context, getActionString(ActionType.GAIN, Cards.dismantle), sco);
    }

    public boolean sauna_shouldPlayNext(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_cultist_shouldPlayNext(context)) {
            return super.sauna_shouldPlayNext(context);
        }
        String option1 = getActionString(ActionType.PLAY, Cards.avanto);
        String option2 = getString(R.string.pass);
        return selectBoolean(context, Cards.sauna.getName(), option1, option2);
    }
    
    public boolean avanto_shouldPlayNext(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_cultist_shouldPlayNext(context)) {
            return super.avanto_shouldPlayNext(context);
        }
        String option1 = getActionString(ActionType.PLAY, Cards.sauna);
        String option2 = getString(R.string.pass);
        return selectBoolean(context, Cards.avanto.getName(), option1, option2);
    }
    
    public Card sauna_cardToTrash(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_masquerade_cardToTrash(context)) {
            return super.sauna_cardToTrash(context);
        }
        SelectCardOptions sco = new SelectCardOptions().setPassable(getString(R.string.none)).setPickType(PickType.TRASH);
        return getCardFromHand(context, getActionString(ActionType.TRASH, Cards.sauna), sco);
    }

    public Card summon_cardToObtain(MoveContext context) {
        if(context.isQuickPlay() && shouldAutoPlay_university_actionCardToObtain(context)) {
            return super.summon_cardToObtain(context);
        }
        SelectCardOptions sco = new SelectCardOptions().potionCost(0).maxCost(4).isAction();
        return getFromTable(context, getActionString(ActionType.GAIN,Event.summon,""), sco);
    }

    public Card prince_cardToSetAside(MoveContext context, Card[] cardList) {
        ArrayList<String> options = new ArrayList<String>();
        for (Card c : cardList)
            options.add(Strings.getCardName(c));
        String o = selectString(context,getActionString(ActionType.REVEAL, Cards.prince), options.toArray(new String[0]));
        return (Card) localNameToCard(o, cardList);
    }

}
