import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
  ArrayList<Card> playerHand, dealerHand;
  int pairPlus, ante, currentFunds, antePayout, playPayout, pairPlusPayout;
  String phase, whoWon, resultString;
  boolean fold;

  GameState() {
    phase = "newHand";
    currentFunds = 1000;
  }

  // TODO: make data members private, write setters and getters
}
