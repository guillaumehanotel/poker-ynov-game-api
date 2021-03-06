package GameAPI.Game.engine.card.combinations;

import GameAPI.Game.engine.card.Card;
import GameAPI.Game.engine.card.Cards;
import GameAPI.Game.engine.card.Rank;
import GameAPI.Game.engine.card.combinations.exceptions.CombinationNotPresentException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class FourOfAKind extends Combination {

  private static final Integer value = FullHouse.getNextValue();
  private final Rank fourRank;
  private final Rank kicker;

  public static Integer getNextValue() {
    return value + 1;
  }

  FourOfAKind(Rank fourRank, Rank kicker) {
    super(value);
    this.fourRank = fourRank;
    this.kicker = kicker;
  }

  public FourOfAKind(Cards cards) {
    super(value);
    CombinationNotPresentException exception = new CombinationNotPresentException("No four of a kind");
    List<Rank> ranks = cards.stream().map(Card::getRank).collect(Collectors.toList());
    fourRank = ranks.stream()
        .filter(rank -> ranks.stream().filter(rank1 -> rank1 == rank).count() == 4)
        .max(Rank::compares)
        .orElseThrow(() -> exception);
    kicker = ranks.stream()
        .filter(rank -> rank != fourRank)
        .max(Rank::compares)
        .orElseThrow(() -> exception);
  }

  @Override
  protected Integer comparesWithSame(Combination combination) {
    FourOfAKind fourOfAKind = (FourOfAKind) combination;
    Integer compares = fourRank.compares(fourOfAKind.fourRank);
    return compares == 0 ? kicker.compares(fourOfAKind.kicker) : compares;
  }

  @Override
  public String toString() {
    return "Carré de " + fourRank + " par le " + kicker;
  }
}
