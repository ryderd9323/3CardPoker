import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class DealerTest {
  Dealer dealer = new Dealer();
  ArrayList<Card> deck;

  @BeforeEach
  void setup() {
    dealer.generateDeck();
    deck = dealer.getDeck();
  }

  @Test
  void generateDeckTest() {
    ArrayList<String> suites = new ArrayList<String>() {
      {
        add("Diamonds");
        add("Hearts");
        add("Clubs");
        add("Spades");
      }
    };
    int i, j;
    for (i = 0; i < 4; i++) {
      // Should draw in the same order they were added
      
      Card drawn;
      for (j = 2; j < 15; j++) {
        drawn = deck.remove(0);
        assertEquals(suites.get(i), drawn.suit, "generateDeck failed, suite returned incorrect value");
        assertEquals(j, drawn.value, "generateDeck failed. drawn.value returned incorrect value");
      }
    }
  }

  @Test
  void shuffleDeckTest() {
    HashSet<Card> cardSet = new HashSet<>();
    Card drawn;
    deck = dealer.getDeck();

    dealer.shuffleDeck();

    assertNotEquals(deck, dealer.getDeck(), "deck and dealer.getDeck() should not be equal");

    while (deck.size() > 0) {
      drawn = deck.remove(0);
      assertTrue(cardSet.add(drawn), "shuffleDeck generated a duplicate of a card");
    }
  }

  @Test
  void shuffleRandomnessTest() {
    HashSet<ArrayList<Card>> deckSet = new HashSet<>();

    dealer.shuffleDeck();

    while (deckSet.size() < 1000) {
      assertTrue(deckSet.add(dealer.getDeck()), "shuffleDeck produced duplicate deck");
      dealer.shuffleDeck();
    }    
  }

  @Test
  void dealHandTest() {
    ArrayList<Card> hand = new ArrayList<>();
    HashSet<ArrayList<Card>> handSet = new HashSet<>();

    dealer.shuffleDeck();
    deck = dealer.getDeck();

    for (int i = 0; i < 17; i++) {
      hand = dealer.dealHand();
      assertEquals(3, hand.size(), "Wrong hand size");
      assertEquals(52-(3*(i+1)), deck.size(), "Deck size tracked incorrectly");
      assertTrue(handSet.add(hand), "Duplicate hand");
    }
  }

  @Test
  void handRandomnessTest() {
    ArrayList<Card> hand = new ArrayList<>();
    HashSet<ArrayList<Card>> handSet = new HashSet<>();
    int i;

    while (handSet.size() < 20000) {
      dealer.generateDeck();
      dealer.shuffleDeck();
      deck = dealer.getDeck();
      for (i = 0; i < 17; i++) {
        hand = dealer.dealHand();
        assertTrue(handSet.add(hand), "Duplicate hand");
      }
    }
  }
  
}
