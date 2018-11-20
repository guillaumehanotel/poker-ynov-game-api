package GameAPI.entities.cards.combinations;

import GameAPI.entities.cards.Card;
import GameAPI.entities.cards.Cards;
import GameAPI.entities.cards.Rank;
import GameAPI.entities.cards.combinations.exceptions.CombinationNotPresentException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Quinte extends Combination {

  private static final Integer value = Brelan.getNextValue();
  private final Rank lastRank;

  static Integer getNextValue() {
    return value + 1;
  }

  public Quinte(Rank lastRank) {
    super(value);
    this.lastRank = lastRank;
  }

  public Quinte(Cards cards) {
    super(value);
    List<Rank> collect = cards.stream()
        .map(Card::getRank)
        .sorted(Collections.reverseOrder(Comparator.comparingInt(Rank::getValue)))
        .distinct()
        .collect(Collectors.toList());
    Rank potentialRank = collect.get(0);
    int nbrCorrect = 1;
    for (int i = 1; i < collect.size(); i++) {
      Rank rank1 = collect.get(i - 1);
      Rank rank2 = collect.get(i);
      boolean areSequentials = rank1.getValue() - rank2.getValue() == 1;
      boolean twoAndAce = rank2 == Rank.Two && collect.get(0) == Rank.Ace;
      if (areSequentials || twoAndAce) {
        if (twoAndAce) nbrCorrect++;
        nbrCorrect += 1;
      } else {
        potentialRank = rank2;
        nbrCorrect = 1;
      }
      if (nbrCorrect == 5) {
        lastRank = potentialRank;
        return;
      }
    }
    throw new CombinationNotPresentException("No Quinte");
  }

  @Override
  protected Integer comparesWithSame(Combination combination) {
    Quinte quinte = (Quinte) combination;
    return this.lastRank.compares(quinte.lastRank);
  }

  @Override
  public String toString() {
    return "Quinte starting at " + lastRank;
  }
}
