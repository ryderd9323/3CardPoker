import java.util.ArrayList;
import java.util.Random;

// Responsible for maintaining deck (creating, storing, shuffling, etc) and for 'dealing' cards

public class Dealer {
  private Random rng = new Random();
  private ArrayList<Card> deck;
  private ArrayList<String> suits = new ArrayList<String>() {
    {
      add("Diamonds");
      add("Hearts");
      add("Clubs");
      add("Spades");
    }
  };

  //
  // Generates standard 52-card deck (2-10, A, J, Q, K for 4 suites)
  // Deck is an ArrayList of Card objects
  //
  public void generateDeck() {
    deck = new ArrayList<>(52);
    for (String s : suits) {
      // Number cards
      for (int i = 2; i < 15; i++) {
        deck.add(new Card(s, i));
      }
    }
  }

  //
  // Randomize the deck. Calls generateDeck() if no deck has been generated.
  // Essentially removes a random card from the current deck and places it on
  // the bottom of the new deck, repeating until no cards remain in old deck.
  //
  public void shuffleDeck() {
    int size = deck.size();
    ArrayList<Card> newDeck = new ArrayList<>(size);

    while (!deck.isEmpty()) {
      size = deck.size();
      Card c = deck.remove(rng.nextInt(size));
      newDeck.add(c);
    }
    deck = newDeck;
  }

  //
  // Deal three cards from the top of the deck 
  //

  // TODO: Throw an error if deck has < 3 cards remaining
  public ArrayList<Card> dealHand() {
    ArrayList<Card> hand = new ArrayList<>(3);
    
    for (int i = 0; i < 3; i++) {
      hand.add(deck.remove(0));
    }

    return hand;
  }

  public ArrayList<Card> getDeck() {
    return deck;
  }
}