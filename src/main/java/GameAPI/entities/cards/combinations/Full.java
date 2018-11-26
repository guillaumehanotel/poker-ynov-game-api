package GameAPI.entities.cards.combinations;

import GameAPI.entities.cards.Cards;
import GameAPI.entities.cards.Rank;
import GameAPI.entities.cards.combinations.exceptions.CombinationNotPresentException;
import lombok.Data;

import java.util.Comparator;

@Data
public class Full extends Combination {

  private static Integer value = Couleur.getNextValue();
  private Rank doubleRank;
  private Rank tripletRank;

  public static Integer getNextValue() {
    return value;
  }

  Full(Rank tripletRank, Rank doubleRank) {
    super(value);
    this.tripletRank = tripletRank;
    this.doubleRank = doubleRank;
  }

  public Full(Cards cards) throws CombinationNotPresentException {
    super(value);
    tripletRank = cards.getRanksByMinimumNbr(3)
        .stream()
        .max(Comparator.comparingInt(Rank::getValue))
        .orElseThrow(() -> new CombinationNotPresentException("Full triplet is null"));
    doubleRank = cards.getRanksByMinimumNbr(2)
        .stream()
        .filter(rank -> rank != tripletRank)
        .max(Comparator.comparingInt(Rank::getValue))
        .orElseThrow(() -> new CombinationNotPresentException("Full double is null"));
  }

  @Override
  protected Integer comparesWithSame(Combination combination) {
    Full full = (Full) combination;
    Integer compares = tripletRank.compares(full.tripletRank);
    if (compares == 0) compares = doubleRank.compares(full.doubleRank);
    return compares;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " of " + tripletRank + " by the " + doubleRank;
  }
}
