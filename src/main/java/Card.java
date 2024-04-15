import java.io.Serializable;

public class Card implements Serializable {
  String suit;
  int value;

  Card(String suit, int value) {
    this.suit = suit;
    this.value = value;
  }

  String cardString() {
    String valString;

    switch (value) {
      case 14:
        valString = "a";
        break;
      case 11:
        valString = "j";
        break;
      case 12:
        valString = "q";
        break;
      case 13:
        valString = "k";
        break; 
      default:
        valString = String.valueOf(value);
        break;
    }

    return (valString + suit);
  }
}
