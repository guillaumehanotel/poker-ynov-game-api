package GameAPI.Game.engine.card.combinations;


import GameAPI.Game.engine.card.Cards;
import GameAPI.Game.engine.card.Rank;
import GameAPI.Game.engine.card.combinations.exceptions.CombinationNotPresentException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Comparator;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThreeOfAKind extends Combination {

  private static final Integer value = DoublePair.getNextValue();
  private final Rank rank;

  static Integer getNextValue() {
    return value + 1;
  }

  ThreeOfAKind(Rank rank) {
    super(value);
    this.rank = rank;
  }

  public ThreeOfAKind(Cards cards) {
    super(value);
    rank = cards.getRanksByMinimumNbr(3)
        .stream()
        .max(Comparator.comparingInt(Rank::getValue))
        .orElseThrow(() -> new CombinationNotPresentException("ENo three of a kind"));
  }

  @Override
  protected Integer comparesWithSame(Combination combination) {
    ThreeOfAKind threeOfAKind = (ThreeOfAKind) combination;
    return rank.compares(threeOfAKind.rank);
  }

  @Override
  public String toString() {
    return "Brelan de " + rank;
  }
}
