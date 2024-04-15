import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameLogicTest {
	GameLogic logic = new GameLogic();
	Dealer dealer = new Dealer();
	ArrayList<Card> deck;
	ArrayList<Card> myHand;
	ArrayList<Card> dealerHand;

	// Game setup
	@BeforeEach
	void setup() {
		dealer.generateDeck();
		dealer.shuffleDeck();
		deck = dealer.getDeck();
	}

	@Test
	void sortHandTest() {
		myHand = new ArrayList<Card>() {
			{
				add(new Card("Diamonds", 5));
				add(new Card("Hearts", 5));
				add(new Card("Clubs", 14));
			}
		};

		ArrayList<Card> sorted = new ArrayList<Card>() {
			{
				add(new Card("Clubs", 14));
				add(new Card("Diamonds", 5));
				add(new Card("Hearts", 5));
			}
		};

		myHand = logic.sortMyHand(myHand);

		for (int i = 0; i < 3; i++) {
			assertEquals(sorted.get(i).cardString(), myHand.get(i).cardString(), "Cards not sorted correctly");
		}

	}

	@Test
	void evalHandTest() {
		// Pair
		myHand = new ArrayList<Card>() {
			{
				add(new Card("Diamonds", 13));
				add(new Card("Hearts", 13));
				add(new Card("Diamonds", 5));
			}
		};
		
		assertEquals("Pair", logic.handRanks.get(logic.evalHand(myHand)), "Evaluated hand incorrectly");

		// 3 of a kind
		myHand.set(2, new Card("Clubs", 13));

		assertEquals("Three of a Kind", logic.handRanks.get(logic.evalHand(myHand)), "Evaluated hand incorrectly");

		// Straights
		// Normal straight
		myHand = new ArrayList<Card>() {
			{
				add(new Card("Clubs", 2));
				add(new Card("Hearts", 3));
				add(new Card("Diamonds", 4));
			}
		};

		assertEquals("Straight", logic.handRanks.get(logic.evalHand(myHand)), "Evaluated hand incorrectly");

		// King, Ace, 2
		myHand = new ArrayList<Card>() {
			{
				add(new Card("Hearts", 13));
				add(new Card("Diamonds", 1));
				add(new Card("Spades", 2));
			}
		};

		assertEquals("Straight", logic.handRanks.get(logic.evalHand(myHand)), "Evaluated hand incorrectly");

		// Queen, King, Ace
		myHand = new ArrayList<Card>() {
			{
				add(new Card("Diamonds", 12));
				add(new Card("Spades", 13));
				add(new Card("Hearts", 1));
			}
		};

		assertEquals("Straight", logic.handRanks.get(logic.evalHand(myHand)), "Evaluated hand incorrectly");

		// Flush
		myHand = new ArrayList<Card>() {
			{
				add(new Card("Spades", 5));
				add(new Card("Spades", 2));
				add(new Card("Spades", 12));
			}
		};

		assertEquals("Flush", logic.handRanks.get(logic.evalHand(myHand)), "Evaluated hand incorrectly");

		// Straight Flush
		myHand = new ArrayList<Card>() {
			{
				add(new Card("Hearts", 10));
				add(new Card("Hearts", 11));
				add(new Card("Hearts", 12));
			}
		};

		assertEquals("Straight Flush", logic.handRanks.get(logic.evalHand(myHand)), "Evaluated hand incorrectly");

		myHand = new ArrayList<Card>() {
			{
				add(new Card("Hearts", 2));
				add(new Card("Diamonds", 3));
				add(new Card("Clubs", 5));
			}
		};

		assertEquals("High", logic.handRanks.get(logic.evalHand(myHand)), "Evaluated hand incorrectly");

		for (int i = 6; i < 14; i++) {
			myHand.set(0, new Card("Clubs", i));

			assertEquals("High", logic.handRanks.get(logic.evalHand(myHand)), "Evaluated hand incorrectly");
		}
	}

	

}
