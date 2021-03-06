package com.vdom.core;

import com.vdom.api.Card;
import com.vdom.api.GameType;

import java.util.*;

/**
 * This class contains a set of cards (for a given GameType) that can be used.
 * If the GameType value is one of the random sets, a random CardSet is generated
 * using the available cards specified.
 *
 * Note that the sets defined in the actual Dominion rule books are created when the application loads.
 * These are created in a static block at the end of the class.
 *
 * @author Michael Fazio
 *
 */
public class CardSet {

	public static Random rand = new Random(System.currentTimeMillis());
	private static final Map<GameType, CardSet> CardSetMap = new HashMap<GameType, CardSet>();

	public static final GameType defaultGameType = GameType.Random;

	private final List<Card> cards;
	private final Card baneCard;
	private final boolean isRandom;

	private CardSet(final List<Card> cards, final Card baneCard) {
		this.cards = cards;
		this.baneCard = baneCard;
		this.isRandom = false;
	}

	private CardSet(final Card[] cardsArray, final Card baneCard) {
		this.cards = Arrays.asList(cardsArray);
		this.baneCard = baneCard;
		this.isRandom = false;
	}

	private CardSet(final List<Card> cards, final boolean isRandom) {
		this.cards = cards;
		this.baneCard = null;
		this.isRandom = isRandom;
	}

	public static CardSet getCardSet(final GameType type) {
		CardSet set = CardSet.CardSetMap.get(type);

		if(set == null) {
			set = CardSet.getCardSet(CardSet.defaultGameType);
		}

		if(set.isRandom) {
			set = CardSet.getRandomCardSet(set.getCards());
		}

		return set;
	}

	/**
	 * This returns a random CardSet using a subset of the cards entered as a parameter.
	 * Note that a bane card is set aside every time to avoid having to swap out a card after
	 * the entire set is selected.  If a bane card is not required, that value is simply cleared away.
	 *
	 * @param possibleCards The set of cards that can be included in the random CardSet
	 * @return A random CardSet selected from the list of entered cards.
	 */
	private static CardSet getRandomCardSet(final List<Card> possibleCards) {
		final List<Card> cardSetList = new ArrayList<Card>();
		//Save off a possible bane card initially to avoid having to add another card later.
		Card baneCard = CardSet.getRandomBaneCard(possibleCards);

		int added = 0;
		while (added < 10) {
			Card card;
			do {
				card = possibleCards.get(rand.nextInt(possibleCards.size()));
				if (card.equals(baneCard) || cardSetList.contains(card)) {
					card = null;
				}
			} while (card == null);

			cardSetList.add(card);
			added++;
		}

		//If we don't need the bane card, just discard it.
		if(!cardSetList.contains(Cards.youngWitch)) {
			baneCard = null;
		}

		return new CardSet(cardSetList, baneCard);
	}

	private static Card getRandomBaneCard(final List<Card> possibleCards) {
		Card baneCard = null;

		Card card = null;
		do {
			card = possibleCards.get(rand.nextInt(possibleCards.size()));
			final int cost = card.getCost(null);
            if (!card.costPotion() && (cost == 2 || cost == 3)) {
				baneCard = card;
			}
		} while(baneCard == null);

		return baneCard;
	}

	public Card getBaneCard() {
		return baneCard;
	}

	public List<Card> getCards() {
		return cards;
	}

	public static Map<GameType, CardSet> getCardSetMap() {
		return CardSetMap;
	}

	public boolean isRandom() {
		return isRandom;
	}

	static {
		CardSetMap.put(GameType.Random, new CardSet(Cards.actionCards, true));
		CardSetMap.put(GameType.RandomBaseGame, new CardSet(Cards.actionCardsBaseGame, true));
		CardSetMap.put(GameType.RandomIntrigue, new CardSet(Cards.actionCardsIntrigue, true));
		CardSetMap.put(GameType.RandomSeaside, new CardSet(Cards.actionCardsSeaside, true));
		CardSetMap.put(GameType.RandomAlchemy, new CardSet(Cards.actionCardsAlchemy, true));
		CardSetMap.put(GameType.RandomProsperity, new CardSet(Cards.actionCardsProsperity, true));
		CardSetMap.put(GameType.RandomCornucopia, new CardSet(Cards.actionCardsCornucopia, true));
		CardSetMap.put(GameType.RandomHinterlands, new CardSet(Cards.actionCardsHinterlands, true));
		CardSetMap.put(GameType.RandomDarkAges, new CardSet(Cards.actionCardsDarkAges, true));
		CardSetMap.put(GameType.RandomGuilds, new CardSet(Cards.actionCardsGuilds, true));
		CardSetMap.put(GameType.RandomAdventures, new CardSet(Cards.actionCardsAdventures, true));
		CardSetMap.put(GameType.RandomEmpires, new CardSet(Cards.actionCardsEmpires, true));
		CardSetMap.put(GameType.RandomNocturne, new CardSet(Cards.actionCardsNocturne, true));

        CardSetMap.put(GameType.ForbiddenArts, new CardSet(new Card[] { Cards.apprentice, Cards.familiar, Cards.possession, Cards.university, Cards.cellar, Cards.councilRoom, Cards.gardens, Cards.laboratory, Cards.thief, Cards.throneRoom }, null));
		CardSetMap.put(GameType.PotionMixers, new CardSet(new Card[]{Cards.alchemist, Cards.apothecary, Cards.golem, Cards.herbalist, Cards.transmute, Cards.cellar, Cards.chancellor, Cards.festival, Cards.militia, Cards.smithy}, null));
		CardSetMap.put(GameType.ChemistryLesson, new CardSet(new Card[]{Cards.alchemist, Cards.golem, Cards.philosophersStone, Cards.university, Cards.bureaucrat, Cards.market, Cards.moat, Cards.remodel, Cards.witch, Cards.woodcutter}, null));
        CardSetMap.put(GameType.Servants, new CardSet(new Card[] { Cards.golem, Cards.possession, Cards.scryingPool, Cards.transmute, Cards.vineyard, Cards.conspirator, Cards.greatHall, Cards.minion, Cards.pawn, Cards.steward }, null));
		CardSetMap.put(GameType.SecretResearch, new CardSet(new Card[]{Cards.familiar, Cards.herbalist, Cards.philosophersStone, Cards.university, Cards.bridge, Cards.masquerade, Cards.minion, Cards.nobles, Cards.shantyTown, Cards.torturer}, null));
		CardSetMap.put(GameType.PoolsToolsAndFools, new CardSet(new Card[]{Cards.apothecary, Cards.apprentice, Cards.golem, Cards.scryingPool, Cards.baron, Cards.coppersmith, Cards.ironworks, Cards.nobles, Cards.tradingPost, Cards.wishingWell}, null));
		CardSetMap.put(GameType.FirstGame, new CardSet(new Card[]{Cards.cellar, Cards.market, Cards.militia, Cards.mine, Cards.moat, Cards.remodel, Cards.smithy, Cards.village, Cards.woodcutter, Cards.workshop}, null));
		CardSetMap.put(GameType.BigMoney, new CardSet(new Card[]{Cards.adventurer, Cards.bureaucrat, Cards.chancellor, Cards.chapel, Cards.feast, Cards.laboratory, Cards.market, Cards.mine, Cards.moneyLender, Cards.throneRoom}, null));
		CardSetMap.put(GameType.Interaction, new CardSet(new Card[]{Cards.bureaucrat, Cards.chancellor, Cards.councilRoom, Cards.festival, Cards.library, Cards.militia, Cards.moat, Cards.spy, Cards.thief, Cards.village}, null));
		CardSetMap.put(GameType.SizeDistortion, new CardSet(new Card[]{Cards.cellar, Cards.chapel, Cards.feast, Cards.gardens, Cards.laboratory, Cards.thief, Cards.village, Cards.witch, Cards.woodcutter, Cards.workshop}, null));
		CardSetMap.put(GameType.VillageSquare, new CardSet(new Card[]{Cards.bureaucrat, Cards.cellar, Cards.festival, Cards.library, Cards.market, Cards.remodel, Cards.smithy, Cards.throneRoom, Cards.village, Cards.woodcutter}, null));
		CardSetMap.put(GameType.VictoryDance, new CardSet(new Card[]{Cards.bridge, Cards.duke, Cards.greatHall, Cards.harem, Cards.ironworks, Cards.masquerade, Cards.nobles, Cards.pawn, Cards.scout, Cards.upgrade}, null));
		CardSetMap.put(GameType.SecretSchemes, new CardSet(new Card[]{Cards.conspirator, Cards.harem, Cards.ironworks, Cards.pawn, Cards.saboteur, Cards.shantyTown, Cards.steward, Cards.swindler, Cards.tradingPost, Cards.tribute}, null));
		CardSetMap.put(GameType.BestWishes, new CardSet(new Card[]{Cards.coppersmith, Cards.courtyard, Cards.masquerade, Cards.scout, Cards.shantyTown, Cards.steward, Cards.torturer, Cards.tradingPost, Cards.upgrade, Cards.wishingWell}, null));
		CardSetMap.put(GameType.Deconstruction, new CardSet(new Card[]{Cards.bridge, Cards.miningVillage, Cards.remodel, Cards.saboteur, Cards.secretChamber, Cards.spy, Cards.swindler, Cards.thief, Cards.throneRoom, Cards.torturer}, null));
		CardSetMap.put(GameType.HandMadness, new CardSet(new Card[]{Cards.bureaucrat, Cards.chancellor, Cards.councilRoom, Cards.courtyard, Cards.mine, Cards.militia, Cards.minion, Cards.nobles, Cards.steward, Cards.torturer}, null));
		CardSetMap.put(GameType.Underlings, new CardSet(new Card[]{Cards.baron, Cards.cellar, Cards.festival, Cards.library, Cards.masquerade, Cards.minion, Cards.nobles, Cards.pawn, Cards.steward, Cards.witch}, null));
		CardSetMap.put(GameType.HighSeas, new CardSet(new Card[]{Cards.bazaar, Cards.caravan, Cards.embargo, Cards.explorer, Cards.haven, Cards.island, Cards.lookout, Cards.pirateShip, Cards.smugglers, Cards.wharf}, null));
		CardSetMap.put(GameType.BuriedTreasure, new CardSet(new Card[]{Cards.ambassador, Cards.cutpurse, Cards.fishingVillage, Cards.lighthouse, Cards.outpost, Cards.pearlDiver, Cards.tactician, Cards.treasureMap, Cards.warehouse, Cards.wharf}, null));
		CardSetMap.put(GameType.Shipwrecks, new CardSet(new Card[]{Cards.ghostShip, Cards.merchantShip, Cards.nativeVillage, Cards.navigator, Cards.pearlDiver, Cards.salvager, Cards.seaHag, Cards.smugglers, Cards.treasury, Cards.warehouse}, null));
		CardSetMap.put(GameType.ReachForTomorrow, new CardSet(new Card[]{Cards.adventurer, Cards.cellar, Cards.councilRoom, Cards.cutpurse, Cards.ghostShip, Cards.lookout, Cards.seaHag, Cards.spy, Cards.treasureMap, Cards.village}, null));
		CardSetMap.put(GameType.Repetition, new CardSet(new Card[]{Cards.caravan, Cards.chancellor, Cards.explorer, Cards.festival, Cards.militia, Cards.outpost, Cards.pearlDiver, Cards.pirateShip, Cards.treasury, Cards.workshop}, null));
		CardSetMap.put(GameType.GiveAndTake, new CardSet(new Card[]{Cards.ambassador, Cards.fishingVillage, Cards.haven, Cards.island, Cards.library, Cards.market, Cards.moneyLender, Cards.salvager, Cards.smugglers, Cards.witch}, null));
		CardSetMap.put(GameType.Beginners, new CardSet(new Card[]{Cards.bank, Cards.countingHouse, Cards.expand, Cards.goons, Cards.monument, Cards.rabble, Cards.royalSeal, Cards.venture, Cards.watchTower, Cards.workersVillage}, null));
		CardSetMap.put(GameType.FriendlyInteractive, new CardSet(new Card[]{Cards.bishop, Cards.city, Cards.contraband, Cards.forge, Cards.hoard, Cards.peddler, Cards.royalSeal, Cards.tradeRoute, Cards.vault, Cards.workersVillage}, null));
		CardSetMap.put(GameType.BigActions, new CardSet(new Card[]{Cards.city, Cards.expand, Cards.grandMarket, Cards.kingsCourt, Cards.loan, Cards.mint, Cards.quarry, Cards.rabble, Cards.talisman, Cards.vault}, null));
		CardSetMap.put(GameType.BiggestMoney, new CardSet(new Card[]{Cards.bank, Cards.grandMarket, Cards.mint, Cards.royalSeal, Cards.venture, Cards.adventurer, Cards.laboratory, Cards.mine, Cards.moneyLender, Cards.spy}, null));
		CardSetMap.put(GameType.TheKingsArmy, new CardSet(new Card[]{Cards.expand, Cards.goons, Cards.kingsCourt, Cards.rabble, Cards.vault, Cards.bureaucrat, Cards.councilRoom, Cards.moat, Cards.spy, Cards.village}, null));
		CardSetMap.put(GameType.TheGoodLife, new CardSet(new Card[]{Cards.contraband, Cards.countingHouse, Cards.hoard, Cards.monument, Cards.mountebank, Cards.bureaucrat, Cards.cellar, Cards.chancellor, Cards.gardens, Cards.village}, null));
		CardSetMap.put(GameType.PathToVictory, new CardSet(new Card[]{Cards.bishop, Cards.countingHouse, Cards.goons, Cards.monument, Cards.peddler, Cards.baron, Cards.harem, Cards.pawn, Cards.shantyTown, Cards.upgrade}, null));
		CardSetMap.put(GameType.AllAlongTheWatchtower, new CardSet(new Card[]{Cards.hoard, Cards.talisman, Cards.tradeRoute, Cards.vault, Cards.watchTower, Cards.bridge, Cards.greatHall, Cards.miningVillage, Cards.pawn, Cards.torturer}, null));
		CardSetMap.put(GameType.LuckySeven, new CardSet(new Card[]{Cards.bank, Cards.expand, Cards.forge, Cards.kingsCourt, Cards.vault, Cards.bridge, Cards.coppersmith, Cards.swindler, Cards.tribute, Cards.wishingWell}, null));
		CardSetMap.put(GameType.BountyOfTheHunt, new CardSet(new Card[]{Cards.harvest, Cards.hornOfPlenty, Cards.huntingParty, Cards.menagerie, Cards.tournament, Cards.cellar, Cards.festival, Cards.militia, Cards.moneyLender, Cards.smithy}, null));
		CardSetMap.put(GameType.BadOmens, new CardSet(new Card[]{Cards.fortuneTeller, Cards.hamlet, Cards.hornOfPlenty, Cards.jester, Cards.remake, Cards.adventurer, Cards.bureaucrat, Cards.laboratory, Cards.spy, Cards.throneRoom}, null));
		CardSetMap.put(GameType.TheJestersWorkshop, new CardSet(new Card[]{Cards.fairgrounds, Cards.farmingVillage, Cards.horseTraders, Cards.jester, Cards.youngWitch, Cards.feast, Cards.laboratory, Cards.market, Cards.remodel, Cards.workshop}, Cards.chancellor));
		CardSetMap.put(GameType.LastLaughs, new CardSet(new Card[]{Cards.farmingVillage, Cards.harvest, Cards.horseTraders, Cards.huntingParty, Cards.jester, Cards.minion, Cards.nobles, Cards.pawn, Cards.steward, Cards.swindler}, null));
		CardSetMap.put(GameType.TheSpiceOfLife, new CardSet(new Card[]{Cards.fairgrounds, Cards.hornOfPlenty, Cards.remake, Cards.tournament, Cards.youngWitch, Cards.coppersmith, Cards.courtyard, Cards.greatHall, Cards.miningVillage, Cards.tribute }, Cards.wishingWell));
		CardSetMap.put(GameType.SmallVictories, new CardSet(new Card[]{Cards.fortuneTeller, Cards.hamlet, Cards.huntingParty, Cards.remake, Cards.tournament, Cards.conspirator, Cards.duke, Cards.greatHall, Cards.harem, Cards.pawn}, null));
		CardSetMap.put(GameType.HinterlandsIntro, new CardSet(new Card[]{Cards.cache, Cards.crossroads, Cards.develop, Cards.haggler, Cards.jackOfAllTrades, Cards.margrave, Cards.nomadCamp, Cards.oasis, Cards.spiceMerchant, Cards.stables}, null));
		CardSetMap.put(GameType.FairTrades, new CardSet(new Card[]{Cards.borderVillage, Cards.cartographer, Cards.develop, Cards.duchess, Cards.farmland, Cards.illGottenGains, Cards.nobleBrigand, Cards.silkRoad, Cards.stables, Cards.trader}, null));
		CardSetMap.put(GameType.Bargains, new CardSet(new Card[]{Cards.borderVillage, Cards.cache, Cards.duchess, Cards.foolsGold, Cards.haggler, Cards.highway, Cards.nomadCamp, Cards.scheme, Cards.spiceMerchant, Cards.trader}, null));
		CardSetMap.put(GameType.Gambits, new CardSet(new Card[]{Cards.cartographer, Cards.crossroads, Cards.embassy, Cards.inn, Cards.jackOfAllTrades, Cards.mandarin, Cards.nomadCamp, Cards.oasis, Cards.oracle, Cards.tunnel}, null));
		CardSetMap.put(GameType.HighwayRobbery, new CardSet(new Card[]{Cards.cellar, Cards.library, Cards.moneyLender, Cards.throneRoom, Cards.workshop, Cards.highway, Cards.inn, Cards.margrave, Cards.nobleBrigand, Cards.oasis}, null));
		CardSetMap.put(GameType.AdventuresAbroad, new CardSet(new Card[]{Cards.adventurer, Cards.chancellor, Cards.festival, Cards.laboratory, Cards.remodel, Cards.crossroads, Cards.farmland, Cards.foolsGold, Cards.oracle, Cards.spiceMerchant}, null));
		CardSetMap.put(GameType.MoneyForNothing, new CardSet(new Card[]{Cards.coppersmith, Cards.greatHall, Cards.pawn, Cards.shantyTown, Cards.torturer, Cards.cache, Cards.cartographer, Cards.jackOfAllTrades, Cards.silkRoad, Cards.tunnel}, null));
		CardSetMap.put(GameType.TheDukesBall, new CardSet(new Card[]{Cards.conspirator, Cards.duke, Cards.harem, Cards.masquerade, Cards.upgrade, Cards.duchess, Cards.haggler, Cards.inn, Cards.nobleBrigand, Cards.scheme}, null));
		CardSetMap.put(GameType.Travelers, new CardSet(new Card[]{Cards.cutpurse, Cards.island, Cards.lookout, Cards.merchantShip, Cards.warehouse, Cards.cartographer, Cards.crossroads, Cards.farmland, Cards.silkRoad, Cards.stables}, null));
		CardSetMap.put(GameType.Diplomacy, new CardSet(new Card[]{Cards.ambassador, Cards.bazaar, Cards.caravan, Cards.embargo, Cards.smugglers, Cards.embassy, Cards.farmland, Cards.illGottenGains, Cards.nobleBrigand, Cards.trader}, null));
		CardSetMap.put(GameType.SchemesAndDreams, new CardSet(new Card[]{Cards.apothecary, Cards.apprentice, Cards.herbalist, Cards.philosophersStone, Cards.transmute, Cards.duchess, Cards.foolsGold, Cards.illGottenGains, Cards.jackOfAllTrades, Cards.scheme}, null));
		CardSetMap.put(GameType.WineCountry, new CardSet(new Card[]{Cards.apprentice, Cards.familiar, Cards.golem, Cards.university, Cards.vineyard, Cards.crossroads, Cards.farmland, Cards.haggler, Cards.highway, Cards.nomadCamp}, null));
		CardSetMap.put(GameType.InstantGratification, new CardSet(new Card[]{Cards.bishop, Cards.expand, Cards.hoard, Cards.mint, Cards.watchTower, Cards.farmland, Cards.haggler, Cards.illGottenGains, Cards.nobleBrigand, Cards.trader}, null));
		CardSetMap.put(GameType.TreasureTrove, new CardSet(new Card[]{Cards.bank, Cards.monument, Cards.royalSeal, Cards.tradeRoute, Cards.venture, Cards.cache, Cards.develop, Cards.foolsGold, Cards.illGottenGains, Cards.mandarin}, null));
		CardSetMap.put(GameType.BlueHarvest, new CardSet(new Card[]{Cards.hamlet, Cards.hornOfPlenty, Cards.horseTraders, Cards.jester, Cards.tournament, Cards.foolsGold, Cards.mandarin, Cards.nobleBrigand, Cards.trader, Cards.tunnel}, null));
		CardSetMap.put(GameType.TravelingCircus, new CardSet(new Card[]{Cards.fairgrounds, Cards.farmingVillage, Cards.huntingParty, Cards.jester, Cards.menagerie, Cards.borderVillage, Cards.embassy, Cards.foolsGold, Cards.nomadCamp, Cards.oasis}, null));
		CardSetMap.put(GameType.PlayingChessWithDeath, new CardSet(new Card[]{Cards.banditCamp, Cards.graverobber, Cards.junkDealer, Cards.mystic, Cards.pillage, Cards.rats, Cards.sage, Cards.scavenger, Cards.storeroom, Cards.vagrant}, null));
		CardSetMap.put(GameType.GrimParade, new CardSet(new Card[]{Cards.armory, Cards.bandOfMisfits, Cards.catacombs, Cards.cultist, Cards.forager, Cards.fortress, Cards.virtualKnight, Cards.marketSquare, Cards.procession, Cards.huntingGrounds}, null));
		CardSetMap.put(GameType.HighAndLow, new CardSet(new Card[]{Cards.hermit, Cards.huntingGrounds, Cards.mystic, Cards.poorHouse, Cards.wanderingMinstrel, Cards.cellar, Cards.moneyLender, Cards.throneRoom, Cards.witch, Cards.workshop}, null));
		CardSetMap.put(GameType.ChivalryAndRevelry, new CardSet(new Card[]{Cards.altar, Cards.virtualKnight, Cards.rats, Cards.scavenger, Cards.squire, Cards.festival, Cards.gardens, Cards.laboratory, Cards.library, Cards.remodel}, null));
		CardSetMap.put(GameType.Prophecy, new CardSet(new Card[]{Cards.armory, Cards.ironmonger, Cards.mystic, Cards.rebuild, Cards.vagrant, Cards.baron, Cards.conspirator, Cards.greatHall, Cards.nobles, Cards.wishingWell}, null));
		CardSetMap.put(GameType.Invasion, new CardSet(new Card[]{Cards.beggar, Cards.marauder, Cards.rogue, Cards.squire, Cards.urchin, Cards.harem, Cards.miningVillage, Cards.swindler, Cards.torturer, Cards.upgrade}, null));
		CardSetMap.put(GameType.WateryGraves, new CardSet(new Card[]{Cards.count, Cards.graverobber, Cards.hermit, Cards.scavenger, Cards.urchin, Cards.nativeVillage, Cards.pirateShip, Cards.salvager, Cards.treasureMap, Cards.treasury}, null));
		CardSetMap.put(GameType.Peasants, new CardSet(new Card[]{Cards.deathCart, Cards.feodum, Cards.poorHouse, Cards.urchin, Cards.vagrant, Cards.fishingVillage, Cards.haven, Cards.island, Cards.lookout, Cards.warehouse}, null));
		CardSetMap.put(GameType.Infestations, new CardSet(new Card[]{Cards.armory, Cards.cultist, Cards.feodum, Cards.marketSquare, Cards.rats, Cards.wanderingMinstrel, Cards.apprentice, Cards.scryingPool, Cards.transmute, Cards.vineyard}, null));
		CardSetMap.put(GameType.OneMansTrash, new CardSet(new Card[]{Cards.counterfeit, Cards.forager, Cards.graverobber, Cards.marketSquare, Cards.rogue, Cards.city, Cards.grandMarket, Cards.monument, Cards.talisman, Cards.venture}, null));
		CardSetMap.put(GameType.HonorAmongThieves, new CardSet(new Card[]{Cards.banditCamp, Cards.procession, Cards.rebuild, Cards.rogue, Cards.squire, Cards.forge, Cards.hoard, Cards.peddler, Cards.quarry, Cards.watchTower}, null));
		CardSetMap.put(GameType.DarkCarnival, new CardSet(new Card[]{Cards.bandOfMisfits, Cards.cultist, Cards.fortress, Cards.hermit, Cards.junkDealer, Cards.virtualKnight, Cards.fairgrounds, Cards.hamlet, Cards.hornOfPlenty, Cards.menagerie}, null));
		CardSetMap.put(GameType.ToTheVictor, new CardSet(new Card[]{Cards.banditCamp, Cards.counterfeit, Cards.deathCart, Cards.marauder, Cards.pillage, Cards.sage, Cards.harvest, Cards.huntingParty, Cards.remake, Cards.tournament}, null));
		CardSetMap.put(GameType.FarFromHome, new CardSet(new Card[]{Cards.beggar, Cards.count, Cards.feodum, Cards.marauder, Cards.wanderingMinstrel, Cards.cartographer, Cards.develop, Cards.embassy, Cards.foolsGold, Cards.haggler}, null));
		CardSetMap.put(GameType.Expeditions, new CardSet(new Card[]{Cards.altar, Cards.catacombs, Cards.ironmonger, Cards.poorHouse, Cards.storeroom, Cards.crossroads, Cards.farmland, Cards.highway, Cards.spiceMerchant, Cards.tunnel}, null));
		CardSetMap.put(GameType.Lamentations, new CardSet(new Card[]{Cards.beggar, Cards.catacombs, Cards.counterfeit, Cards.forager, Cards.ironmonger, Cards.pillage, Cards.apothecary, Cards.golem, Cards.herbalist, Cards.university}, null));
		
		CardSetMap.put(GameType.ArtsAndCrafts,      new CardSet(new Card[]{Cards.stonemason, Cards.advisor, Cards.baker, Cards.journeyman, Cards.merchantGuild, Cards.laboratory, Cards.cellar, Cards.workshop, Cards.festival, Cards.moneyLender}, null));
		CardSetMap.put(GameType.CleanLiving,        new CardSet(new Card[]{Cards.butcher, Cards.baker, Cards.candlestickMaker, Cards.doctor, Cards.soothsayer, Cards.militia, Cards.thief, Cards.moneyLender, Cards.gardens, Cards.village}, null));
		CardSetMap.put(GameType.GildingTheLily,     new CardSet(new Card[]{Cards.plaza, Cards.masterpiece, Cards.candlestickMaker, Cards.taxman, Cards.herald, Cards.library, Cards.remodel, Cards.adventurer, Cards.market, Cards.chancellor}, null));
		CardSetMap.put(GameType.NameThatCard,       new CardSet(new Card[]{Cards.baker, Cards.doctor, Cards.plaza, Cards.advisor, Cards.masterpiece, Cards.courtyard, Cards.wishingWell, Cards.harem, Cards.tribute, Cards.nobles}, null));
		CardSetMap.put(GameType.TricksOfTheTrade,   new CardSet(new Card[]{Cards.stonemason, Cards.herald, Cards.soothsayer, Cards.journeyman, Cards.butcher, Cards.greatHall, Cards.nobles, Cards.conspirator, Cards.masquerade, Cards.coppersmith}, null));
		CardSetMap.put(GameType.DecisionsDecisions, new CardSet(new Card[]{Cards.merchantGuild, Cards.candlestickMaker, Cards.masterpiece, Cards.taxman, Cards.butcher, Cards.bridge, Cards.pawn, Cards.miningVillage, Cards.upgrade, Cards.duke}, null));

		CardSetMap.put(GameType.GentleIntro, new CardSet(new Card[]{Cards.amulet,Cards.distantLands,Cards.dungeon,Cards.duplicate,Cards.giant,Cards.hireling,Cards.port,Cards.ranger,Cards.ratcatcher,Cards.treasureTrove},null));
		CardSetMap.put(GameType.ExpertIntro, new CardSet(new Card[]{Cards.caravanGuard,Cards.coinOfTheRealm,Cards.hauntedWoods,Cards.lostCity,Cards.magpie,Cards.peasant,Cards.raze,Cards.swampHag,Cards.transmogrify,Cards.wineMerchant},null));
		CardSetMap.put(GameType.LevelUp, new CardSet(new Card[]{Cards.dungeon,Cards.gear,Cards.guide,Cards.lostCity,Cards.miser,Cards.market,Cards.militia,Cards.spy,Cards.throneRoom,Cards.workshop},null));
		CardSetMap.put(GameType.SonOfSizeDistortion, new CardSet(new Card[]{Cards.amulet,Cards.duplicate,Cards.giant,Cards.messenger,Cards.treasureTrove,Cards.bureaucrat,Cards.gardens,Cards.moneyLender,Cards.thief,Cards.witch},null));
		CardSetMap.put(GameType.RoyaltyFactory, new CardSet(new Card[]{Cards.bridgeTroll,Cards.duplicate,Cards.page,Cards.raze,Cards.royalCarriage,Cards.conspirator,Cards.harem,Cards.nobles,Cards.secretChamber,Cards.swindler},null));
		CardSetMap.put(GameType.MastersOfFinance, new CardSet(new Card[]{Cards.artificer,Cards.distantLands,Cards.gear,Cards.transmogrify,Cards.wineMerchant,Cards.bridge,Cards.pawn,Cards.shantyTown,Cards.steward,Cards.upgrade},null));
		CardSetMap.put(GameType.PrinceOfOrange, new CardSet(new Card[]{Cards.amulet,Cards.dungeon,Cards.hauntedWoods,Cards.page,Cards.swampHag,Cards.caravan,Cards.fishingVillage,Cards.merchantShip,Cards.tactician,Cards.treasureMap},null));
		CardSetMap.put(GameType.GiftsAndMathoms, new CardSet(new Card[]{Cards.bridgeTroll,Cards.caravanGuard,Cards.hireling,Cards.lostCity,Cards.messenger,Cards.ambassador,Cards.embargo,Cards.haven,Cards.salvager,Cards.smugglers},null));
		CardSetMap.put(GameType.HastePotion, new CardSet(new Card[]{Cards.magpie,Cards.messenger,Cards.port,Cards.royalCarriage,Cards.treasureTrove,Cards.apprentice,Cards.scryingPool,Cards.transmute,Cards.university,Cards.vineyard},null));
		CardSetMap.put(GameType.Cursecatchers, new CardSet(new Card[]{Cards.amulet,Cards.bridgeTroll,Cards.caravanGuard,Cards.peasant,Cards.ratcatcher,Cards.apothecary,Cards.familiar,Cards.golem,Cards.herbalist,Cards.philosophersStone},null));
		CardSetMap.put(GameType.LastWillAndMonument, new CardSet(new Card[]{Cards.coinOfTheRealm,Cards.dungeon,Cards.messenger,Cards.relic,Cards.treasureTrove,Cards.bishop,Cards.countingHouse,Cards.monument,Cards.rabble,Cards.vault},null));
		CardSetMap.put(GameType.ThinkBig, new CardSet(new Card[]{Cards.distantLands,Cards.giant,Cards.hireling,Cards.miser,Cards.storyteller,Cards.contraband,Cards.expand,Cards.hoard,Cards.kingsCourt,Cards.peddler},null));
		CardSetMap.put(GameType.TheHerosReturn, new CardSet(new Card[]{Cards.artificer,Cards.miser,Cards.page,Cards.ranger,Cards.relic,Cards.fairgrounds,Cards.farmingVillage,Cards.horseTraders,Cards.jester,Cards.menagerie},null));
		CardSetMap.put(GameType.SeacraftAndWitchcraft, new CardSet(new Card[]{Cards.peasant,Cards.storyteller,Cards.swampHag,Cards.transmogrify,Cards.wineMerchant,Cards.fortuneTeller,Cards.hamlet,Cards.hornOfPlenty,Cards.tournament,Cards.youngWitch},null));
		CardSetMap.put(GameType.TradersAndRaiders, new CardSet(new Card[]{Cards.hauntedWoods,Cards.lostCity,Cards.page,Cards.port,Cards.wineMerchant,Cards.develop,Cards.farmland,Cards.haggler,Cards.spiceMerchant,Cards.trader},null));
		CardSetMap.put(GameType.Journeys, new CardSet(new Card[]{Cards.bridgeTroll,Cards.distantLands,Cards.giant,Cards.guide,Cards.ranger,Cards.cartographer,Cards.crossroads,Cards.highway,Cards.inn,Cards.silkRoad},null));
		CardSetMap.put(GameType.CemeteryPolka, new CardSet(new Card[]{Cards.amulet,Cards.caravanGuard,Cards.hireling,Cards.peasant,Cards.relic,Cards.graverobber,Cards.marauder,Cards.procession,Cards.rogue,Cards.wanderingMinstrel},null));
		CardSetMap.put(GameType.GroovyDecay, new CardSet(new Card[]{Cards.dungeon,Cards.hauntedWoods,Cards.ratcatcher,Cards.raze,Cards.transmogrify,Cards.cultist,Cards.deathCart,Cards.fortress,Cards.virtualKnight,Cards.rats},null));
		CardSetMap.put(GameType.Spendthrift, new CardSet(new Card[]{Cards.artificer,Cards.gear,Cards.magpie,Cards.miser,Cards.storyteller,Cards.doctor,Cards.masterpiece,Cards.merchantGuild,Cards.soothsayer,Cards.stonemason},null));
		CardSetMap.put(GameType.QueenOfTan, new CardSet(new Card[]{Cards.coinOfTheRealm,Cards.duplicate,Cards.guide,Cards.ratcatcher,Cards.royalCarriage,Cards.advisor,Cards.butcher,Cards.candlestickMaker,Cards.herald,Cards.journeyman},null));

		CardSetMap.put(GameType.BasicIntro, new CardSet(new Card[]{Cards.virtualCastle,Cards.chariotRace,Cards.cityQuarter,Cards.engineer,Cards.farmersMarket,Cards.forum,Cards.legionary,Cards.patrician,Cards.sacrifice,Cards.villa},null));
		CardSetMap.put(GameType.AdvancedIntro, new CardSet(new Card[]{Cards.archive,Cards.capital,Cards.catapult,Cards.crown,Cards.enchantress,Cards.gladiator,Cards.groundskeeper,Cards.royalBlacksmith,Cards.settlers,Cards.temple},null));
		CardSetMap.put(GameType.EverythingInModeration, new CardSet(new Card[]{Cards.enchantress,Cards.forum,Cards.legionary,Cards.overlord,Cards.temple,Cards.cellar,Cards.library,Cards.remodel,Cards.village,Cards.workshop},null));
		CardSetMap.put(GameType.SilverBullets, new CardSet(new Card[]{Cards.catapult,Cards.charm,Cards.farmersMarket,Cards.groundskeeper,Cards.patrician,Cards.bureaucrat,Cards.gardens,Cards.laboratory,Cards.market,Cards.moneyLender},null));
		CardSetMap.put(GameType.DeliciousTorture, new CardSet(new Card[]{Cards.virtualCastle,Cards.crown,Cards.enchantress,Cards.sacrifice,Cards.settlers,Cards.baron,Cards.bridge,Cards.harem,Cards.ironworks,Cards.torturer},null));
		CardSetMap.put(GameType.BuddySystem, new CardSet(new Card[]{Cards.archive,Cards.capital,Cards.catapult,Cards.engineer,Cards.forum,Cards.masquerade,Cards.miningVillage,Cards.nobles,Cards.pawn,Cards.tradingPost},null));
		CardSetMap.put(GameType.BoxedIn, new CardSet(new Card[]{Cards.virtualCastle,Cards.chariotRace,Cards.encampment,Cards.enchantress,Cards.gladiator,Cards.salvager,Cards.smugglers,Cards.tactician,Cards.warehouse,Cards.wharf},null));
		CardSetMap.put(GameType.KingOfTheSea, new CardSet(new Card[]{Cards.archive,Cards.farmersMarket,Cards.overlord,Cards.temple,Cards.wildHunt,Cards.explorer,Cards.haven,Cards.nativeVillage,Cards.pirateShip,Cards.seaHag},null));
		CardSetMap.put(GameType.Collectors, new CardSet(new Card[]{Cards.cityQuarter,Cards.crown,Cards.encampment,Cards.enchantress,Cards.farmersMarket,Cards.apothecary,Cards.apprentice,Cards.herbalist,Cards.transmute,Cards.university},null));
		CardSetMap.put(GameType.BigTime, new CardSet(new Card[]{Cards.capital,Cards.gladiator,Cards.patrician,Cards.royalBlacksmith,Cards.villa,Cards.bank,Cards.forge,Cards.grandMarket,Cards.loan,Cards.royalSeal},null));
		CardSetMap.put(GameType.GildedGates, new CardSet(new Card[]{Cards.chariotRace,Cards.cityQuarter,Cards.encampment,Cards.groundskeeper,Cards.wildHunt,Cards.bishop,Cards.monument,Cards.mint,Cards.peddler,Cards.talisman},null));
		CardSetMap.put(GameType.Zookeepers, new CardSet(new Card[]{Cards.overlord,Cards.sacrifice,Cards.settlers,Cards.villa,Cards.wildHunt,Cards.fairgrounds,Cards.horseTraders,Cards.menagerie,Cards.jester,Cards.tournament},null));
		CardSetMap.put(GameType.SimplePlans, new CardSet(new Card[]{Cards.catapult,Cards.forum,Cards.patrician,Cards.temple,Cards.villa,Cards.borderVillage,Cards.develop,Cards.haggler,Cards.illGottenGains,Cards.stables},null));
		CardSetMap.put(GameType.Expansion, new CardSet(new Card[]{Cards.virtualCastle,Cards.charm,Cards.encampment,Cards.engineer,Cards.legionary,Cards.cache,Cards.farmland,Cards.highway,Cards.spiceMerchant,Cards.tunnel},null));
		CardSetMap.put(GameType.TombOfTheRatKing, new CardSet(new Card[]{Cards.virtualCastle,Cards.chariotRace,Cards.cityQuarter,Cards.legionary,Cards.sacrifice,Cards.deathCart,Cards.fortress,Cards.pillage,Cards.rats,Cards.storeroom},null));
		CardSetMap.put(GameType.TriumphOfTheBanditKing, new CardSet(new Card[]{Cards.capital,Cards.charm,Cards.engineer,Cards.groundskeeper,Cards.legionary,Cards.banditCamp,Cards.catacombs,Cards.huntingGrounds,Cards.marketSquare,Cards.procession},null));
		CardSetMap.put(GameType.TheSquiresRitual, new CardSet(new Card[]{Cards.archive,Cards.catapult,Cards.crown,Cards.patrician,Cards.settlers,Cards.feodum,Cards.hermit,Cards.ironmonger,Cards.rogue,Cards.squire},null));
		CardSetMap.put(GameType.CashFlow, new CardSet(new Card[]{Cards.virtualCastle,Cards.cityQuarter,Cards.engineer,Cards.gladiator,Cards.royalBlacksmith,Cards.baker,Cards.butcher,Cards.doctor,Cards.herald,Cards.soothsayer},null));
		CardSetMap.put(GameType.AreaControl, new CardSet(new Card[]{Cards.capital,Cards.catapult,Cards.charm,Cards.crown,Cards.farmersMarket,Cards.coinOfTheRealm,Cards.page,Cards.relic,Cards.treasureTrove,Cards.wineMerchant},null));
		CardSetMap.put(GameType.NoMoneyNoProblems, new CardSet(new Card[]{Cards.archive,Cards.encampment,Cards.royalBlacksmith,Cards.temple,Cards.villa,Cards.dungeon,Cards.duplicate,Cards.hireling,Cards.peasant,Cards.transmogrify},null));

		CardSetMap.put(GameType.Dusk, new CardSet(new Card[]{ Cards.blessedVillage, Cards.cobbler, Cards.denOfSin, Cards.faithfulHound, Cards.fool, Cards.monastery, Cards.nightWatchman, Cards.shepherd, Cards.tormentor, Cards.tragicHero, }, null));
		CardSetMap.put(GameType.Midnight, new CardSet(new Card[]{ Cards.conclave, Cards.crypt, Cards.cursedVillage, Cards.devilsWorkshop, Cards.druid, Cards.exorcist, Cards.leprechaun, Cards.pooka, Cards.raider, Cards.secretCave, }, null));
		CardSetMap.put(GameType.NightShift, new CardSet(new Card[]{ Cards.druid, Cards.exorcist, Cards.ghostTown, Cards.idol, Cards.nightWatchman, Cards.bandit, Cards.gardens, Cards.mine, Cards.poacher, Cards.smithy, }, null));
		CardSetMap.put(GameType.IdleHands, new CardSet(new Card[]{ Cards.bard, Cards.conclave, Cards.cursedVillage, Cards.devilsWorkshop, Cards.tragicHero, Cards.cellar, Cards.harbinger, Cards.market, Cards.merchant, Cards.moneyLender, }, null));
		CardSetMap.put(GameType.ShadowyFigures, new CardSet(new Card[]{ Cards.cobbler, Cards.conclave, Cards.faithfulHound, Cards.shepherd, Cards.tragicHero, Cards.bridge, Cards.conspirator, Cards.mill, Cards.nobles, Cards.secretPassage, }, null));
		CardSetMap.put(GameType.ImpendingDoom, new CardSet(new Card[]{ Cards.leprechaun, Cards.monastery, Cards.necromancer, Cards.tormentor, Cards.werewolf, Cards.courtier, Cards.lurker, Cards.miningVillage, Cards.swindler, Cards.upgrade, }, null));
		CardSetMap.put(GameType.TheNewBlack, new CardSet(new Card[]{ Cards.cobbler, Cards.denOfSin, Cards.ghostTown, Cards.raider, Cards.secretCave, Cards.caravan, Cards.haven, Cards.merchantShip, Cards.outpost, Cards.tactician, }, null));
		CardSetMap.put(GameType.ForbiddenIsle, new CardSet(new Card[]{ Cards.blessedVillage, Cards.cemetery, Cards.idol, Cards.tracker, Cards.tragicHero, Cards.fishingVillage, Cards.ghostShip, Cards.lookout, Cards.salvager, Cards.treasureMap, }, null));
		CardSetMap.put(GameType.NightmareFuel, new CardSet(new Card[]{ Cards.bard, Cards.blessedVillage, Cards.cemetery, Cards.sacredGrove, Cards.skulk, Cards.tracker, Cards.alchemist, Cards.apprentice, Cards.transmute, Cards.vineyard, }, null));
		CardSetMap.put(GameType.TreasuresOfTheNight, new CardSet(new Card[]{ Cards.crypt, Cards.guardian, Cards.nightWatchman, Cards.raider, Cards.vampire, Cards.bank, Cards.contraband, Cards.loan, Cards.royalSeal, Cards.venture, }, null));
		CardSetMap.put(GameType.DayAtTheRaces, new CardSet(new Card[]{ Cards.blessedVillage, Cards.cemetery, Cards.druid, Cards.tormentor, Cards.tragicHero, Cards.bishop, Cards.peddler, Cards.talisman, Cards.tradeRoute, Cards.watchTower, }, null));
		CardSetMap.put(GameType.TheEndlessFair, new CardSet(new Card[]{ Cards.devilsWorkshop, Cards.exorcist, Cards.monastery, Cards.pixie, Cards.shepherd, Cards.baker, Cards.fairgrounds, Cards.farmingVillage, Cards.fortuneTeller, Cards.merchantGuild, }, null));
		CardSetMap.put(GameType.HappyChaos, new CardSet(new Card[]{ Cards.blessedVillage, Cards.changeling, Cards.fool, Cards.faithfulHound, Cards.sacredGrove, Cards.doctor, Cards.harvest, Cards.herald, Cards.jester, Cards.masterpiece, }, null));
		CardSetMap.put(GameType.SearchParty, new CardSet(new Card[]{ Cards.cobbler, Cards.conclave, Cards.druid, Cards.faithfulHound, Cards.werewolf, Cards.cartographer, Cards.highway, Cards.inn, Cards.oasis, Cards.scheme, }, null));
		CardSetMap.put(GameType.CountingSheep, new CardSet(new Card[]{ Cards.bard, Cards.crypt, Cards.leprechaun, Cards.pooka, Cards.shepherd, Cards.crossroads, Cards.farmland, Cards.haggler, Cards.nobleBrigand, Cards.tunnel, }, null));
		CardSetMap.put(GameType.GraveMatters, new CardSet(new Card[]{ Cards.cemetery, Cards.cursedVillage, Cards.necromancer, Cards.skulk, Cards.tormentor, Cards.armory, Cards.forager, Cards.graverobber, Cards.marketSquare, Cards.squire, }, null));
		CardSetMap.put(GameType.RatsAndBats, new CardSet(new Card[]{ Cards.changeling, Cards.devilsWorkshop, Cards.sacredGrove, Cards.tracker, Cards.vampire, Cards.catacombs, Cards.count, Cards.fortress, Cards.hermit, Cards.rats, }, null));
		CardSetMap.put(GameType.MonsterMash, new CardSet(new Card[]{ Cards.conclave, Cards.guardian, Cards.pixie, Cards.vampire, Cards.werewolf, Cards.bridgeTroll, Cards.giant, Cards.messenger, Cards.ratcatcher, Cards.storyteller, }, null));
		CardSetMap.put(GameType.LostInTheWoods, new CardSet(new Card[]{ Cards.blessedVillage, Cards.druid, Cards.fool, Cards.sacredGrove, Cards.tracker, Cards.caravanGuard, Cards.guide, Cards.hauntedWoods, Cards.hireling, Cards.ranger, }, null));
		CardSetMap.put(GameType.Luftschloss, new CardSet(new Card[]{ Cards.cemetery, Cards.changeling, Cards.exorcist, Cards.fool, Cards.shepherd, Cards.archive, Cards.virtualCastle, Cards.catapult, Cards.engineer, Cards.temple, }, null));
		CardSetMap.put(GameType.PookaPranks, new CardSet(new Card[]{ Cards.faithfulHound, Cards.ghostTown, Cards.pixie, Cards.pooka, Cards.skulk, Cards.chariotRace, Cards.forum, Cards.groundskeeper, Cards.sacrifice, Cards.settlers, }, null));

	}

}
