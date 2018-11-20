package GameAPI.entities;

import GameAPI.entities.cards.Card;
import GameAPI.entities.cards.Rank;
import GameAPI.entities.cards.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayersTest {

  private Players players;

  @BeforeEach
  void setUp() throws Exception {
    players = new Players();
    Game game = Mockito.mock(Game.class);
    List<Card> cards = Arrays.asList(
        new Card(Suit.HEART, Rank.King),
        new Card(Suit.HEART, Rank.Queen),
        new Card(Suit.SPADE, Rank.King),
        new Card(Suit.DIAMOND, Rank.Queen),
        new Card(Suit.CLUB, Rank.Ace)
    );
    Mockito.when(game.getLastCommunityCards()).thenReturn(cards);
    players.add(new Player(new User("mail", "p1", 10000), 1000, game));
    players.add(new Player(new User("mail", "p2", 10000), 1000, game));
    players.add(new Player(new User("mail", "p3", 10000), 1000, game));
  }

  @Test
  void getNext() {
    final Player expectedPlayer = players.get(0);
    final Player actualPlayer = players.getNextPlayer();

    assertEquals(expectedPlayer, actualPlayer);
  }

//  @Test
//  void getNextPlaying() {
//    players.get(0).playPreFlop(1);
//    Player expected = players.get(1);
//    Player nextPlaying = players.getNextPlaying();
//    assertEquals(expected, nextPlaying);
//  }

  @Test
  void setCurrentIndex() {
    assertThrows(RuntimeException.class, () -> players.setCurrentOrderIndex(3));
  }

  @Test
  void getWinners1() {
    players.get(0).setDownCards(Arrays.asList(new Card(Suit.CLUB, Rank.Eight), new Card(Suit.HEART, Rank.Seven)));
    players.get(1).setDownCards(Arrays.asList(new Card(Suit.CLUB, Rank.Ace), new Card(Suit.HEART, Rank.Ace)));
    players.get(2).setDownCards(Arrays.asList(new Card(Suit.DIAMOND, Rank.Eight), new Card(Suit.SPADE, Rank.Seven)));
    Player actual = players.getPlayersByResult().get(PlayerStatus.WINNER).get(0);
    Player expected = players.get(1);
    assertEquals(expected, actual);
  }

  @Test
  void getWinners2() {
    players.get(0).setDownCards(Arrays.asList(new Card(Suit.CLUB, Rank.Eight), new Card(Suit.HEART, Rank.Seven)));
    players.get(1).setDownCards(Arrays.asList(new Card(Suit.CLUB, Rank.Ace), new Card(Suit.HEART, Rank.Ace)));
    players.get(2).setDownCards(Arrays.asList(new Card(Suit.SPADE, Rank.Ace), new Card(Suit.DIAMOND, Rank.Ace)));
    List<Player> actual = players.getPlayersByResult().get(PlayerStatus.WINNER);
    List<Player> expected = Arrays.asList(players.get(1), players.get(2));
    assertArrayEquals(expected.toArray(), actual.toArray());
  }
}
