import java.util.ArrayList;
import java.util.Comparator;

//
// Handles all game logic, such as evaluating hands and payouts
//

public class GameLogic {

  ArrayList<String> handRanks;
    
  GameLogic() {
    handRanks = new ArrayList<String>() {
      {
        add("High");
        add("Pair");
        add("Flush");
        add("Straight");
        add("Three of a Kind");
        add("Straight Flush");
      }
    };
  }

  // Helper class that sorts Card objects according to value descending first, suit ascending second
  private class SortHand implements Comparator<Card> {
    public int compare(Card a, Card b) {
      int valComp = b.value - a.value;
      int suitComp = a.suit.compareTo(b.suit);

      return (valComp == 0) ? suitComp : valComp;
    }
  }

  private boolean isFlush(ArrayList<Card> hand) {
    if (hand.get(0).suit == hand.get(1).suit && hand.get(0).suit == hand.get(2).suit) {
      return true;
    }
    return false;
  }

  private boolean isStraight(ArrayList<Card> hand) {
    // Special cases when Straight involves King and Ace
    if (hand.get(0).value == 13 && hand.get(2).value == 1) {
      return (hand.get(1).value == 2 || hand.get(1).value == 12) ?  true : false;
    }

    if (hand.get(0).value - hand.get(1).value == 1 && hand.get(1).value - hand.get(2).value == 1) {
      return true;
    }
    return false;
  }

  public int evalHand(ArrayList<Card> hand) {
    // Sort hand first
    hand.sort(new SortHand());

    if (isFlush(hand)) {
      if (isStraight(hand))
        return 5;
      return 2;
    }

    if (isStraight(hand)) {
      return 3;
    }

    // Pair and 3 of a kind check
    if (hand.get(0).value == hand.get(1).value) {
      return (hand.get(0).value == hand.get(2).value) ? 4 : 1;
    }

    return 0;
  }

  public int pairPlusPayout(int handRank, int wager) {
    switch (handRank) {
      case 1: // Pair
        return (wager*2);
      case 2: // Flush
        return (wager*4);
      case 3: // Straight
        return (wager*7);
      case 4: // Three of a Kind
        return (wager*31);
      case 5: // Straight Flush
        return (wager*41);
      default:
        return 0;
    }
  }

  public ArrayList<Card> sortMyHand(ArrayList<Card> hand) {
    hand.sort(new SortHand());
    return hand;
  }
}
