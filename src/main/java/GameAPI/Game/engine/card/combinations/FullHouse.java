package GameAPI.Game.engine.card.combinations;

import GameAPI.Game.engine.card.Cards;
import GameAPI.Game.engine.card.Rank;
import GameAPI.Game.engine.card.combinations.exceptions.CombinationNotPresentException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Comparator;

@EqualsAndHashCode(callSuper = true)
@Data
public class FullHouse extends Combination {

  private static Integer value = Flush.getNextValue();
  private Rank doubleRank;
  private Rank tripletRank;

  public static Integer getNextValue() {
    return value;
  }

  FullHouse(Rank tripletRank, Rank doubleRank) {
    super(value);
    this.tripletRank = tripletRank;
    this.doubleRank = doubleRank;
  }

  public FullHouse(Cards cards) throws CombinationNotPresentException {
    super(value);
    tripletRank = cards.getRanksByMinimumNbr(3)
        .stream()
        .max(Comparator.comparingInt(Rank::getValue))
        .orElseThrow(() -> new CombinationNotPresentException("FullHouse triplet is null"));
    doubleRank = cards.getRanksByMinimumNbr(2)
        .stream()
        .filter(rank -> rank != tripletRank)
        .max(Comparator.comparingInt(Rank::getValue))
        .orElseThrow(() -> new CombinationNotPresentException("FullHouse double is null"));
  }

  @Override
  protected Integer comparesWithSame(Combination combination) {
    FullHouse fullHouse = (FullHouse) combination;
    Integer compares = tripletRank.compares(fullHouse.tripletRank);
    if (compares == 0) compares = doubleRank.compares(fullHouse.doubleRank);
    return compares;
  }

  @Override
  public String toString() {
    return "Full de " + tripletRank + " par les " + doubleRank;
  }
}
