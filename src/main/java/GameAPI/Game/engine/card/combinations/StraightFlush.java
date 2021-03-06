package GameAPI.Game.engine.card.combinations;

import GameAPI.Game.engine.card.Card;
import GameAPI.Game.engine.card.Cards;
import GameAPI.Game.engine.card.Rank;
import GameAPI.Game.engine.card.combinations.exceptions.CombinationNotPresentException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class StraightFlush extends Combination {

  private static final Integer value = FourOfAKind.getNextValue();
  private final Rank bestRank;

  public static Integer getNextValue() {
    return value + 1;
  }

  StraightFlush(Rank bestRank) {
    super(value);
    this.bestRank = bestRank;
  }

  public StraightFlush(Cards cards) {
    super(value);
    // Keep card whose suit is present at least 5 times
    Cards fiveTimesSuitCards = new Cards();
    fiveTimesSuitCards.addAll(cards.stream()
        .filter(card -> cards.stream().map(Card::getSuit).filter(suit -> suit == card.getSuit()).count() >= 5)
        .collect(Collectors.toList()));
    try {
      Straight bestStraight = new Straight(fiveTimesSuitCards);
      bestRank = bestStraight.getBestRank();
    } catch (CombinationNotPresentException | IndexOutOfBoundsException e) {
      throw new CombinationNotPresentException("No straight flush");
    }
  }

  public Rank getBestRank() {
    return bestRank;
  }

  @Override
  protected Integer comparesWithSame(Combination combination) {
    StraightFlush straightFlush = (StraightFlush) combination;
    return bestRank.compares(straightFlush.bestRank);
  }

  @Override
  public String toString() {
    return "Quinte flush par le " + bestRank;
  }
}
