package GameAPI.entities;

import GameAPI.entities.cards.Card;
import GameAPI.entities.cards.Cards;
import GameAPI.entities.cards.combinations.*;
import GameAPI.entities.cards.combinations.exceptions.CombinationNotPresentException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class Player {

    private User user;
    @JsonIgnore
    private Game game;
    private Integer chips;
    private Boolean isEliminated;
    private Boolean hasDropped;
    private Integer currentBet;
    private Boolean hasPlayTurn;
    @JsonIgnore
    private List<Card> downCards;
    @JsonIgnore
    private List<Card> previousDownCards;
    private String combination;
    private Integer earnedMoney;
    @JsonIgnore
    private Combination _combination; //store combination without showing it in json

    public Player(User user, Integer startingChips, Game game) {
        this.user = user;
        this.game = game;

        // lié à une partie
        this.isEliminated = false;
        this.chips = startingChips;

        // lié à un round
        this.hasDropped = false;
        this.downCards = new ArrayList<>();
        this.previousDownCards = new ArrayList<>();
        this.currentBet = 0;

        // lié à un tour
        this.hasPlayTurn = false;
    }

    public void resetRound(){
        this.hasDropped = false;
        this.downCards.clear();
        this.currentBet = 0;
    }

    public void resetTurn(){
        this.hasPlayTurn = false;
    }

    public void addCardToHand(Card card) {
        this.downCards.add(card);
    }

    public void bets(Integer amount) {
        log.info(this.user.getUsername() + " bets " + amount);
        this.currentBet += amount;
        this.chips -= amount;
        game.setPot(game.getPot() + amount);
        this.hasPlayTurn = true;
    }

    public void fold() {
        log.info(this.user.getUsername() + " fold");
        this.hasDropped = true;
        this.hasPlayTurn = true;
    }

    public void call(Integer biggestBet) {
        log.info(this.user.getUsername() + " call");
        this.bets(biggestBet - currentBet);
        this.hasPlayTurn = true;
    }

    public Boolean hasAllIn(){
        return currentBet.equals(chips + currentBet);
    }

    /**
     * Un joueur est considéré comme ignoré pour une manche si :
     * - il est éliminé
     * - il s'est couché
     * - il a misé tout ses jetons (all-in)
     */
    @JsonIgnore
    public Boolean isIgnoredForRound(){
        if(!isEliminated){
            return hasAllIn() || hasDropped;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(user) + "\n";
    }

    public Integer comparesCards(Player player) {
        Combination bestCombination = getBestCombination();
        Combination playerBestCombination = player.getBestCombination();
        if (bestCombination == null && playerBestCombination == null) return 0;
        else if (bestCombination == null) return -1;
        else if (playerBestCombination == null) return 1;
        return bestCombination.compares(playerBestCombination);
    }

    @JsonIgnore
    public Combination getBestCombination() {
        List<Combination> combinations = new ArrayList<>();
        Cards allCards = getAllCards();
        try {
            combinations.add(new Hauteur(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        try {
            combinations.add(new Paire(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        try {
            combinations.add(new DoublePaire(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        try {
            combinations.add(new Brelan(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        try {
            combinations.add(new Quinte(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        try {
            combinations.add(new Couleur(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        try {
            combinations.add(new FourOfAKind(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        try {
            combinations.add(new Full(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        try {
            combinations.add(new FourOfAKind(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        try {
            combinations.add(new StraightFlush(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        try {
            combinations.add(new RoyalFlush(allCards));
        } catch (CombinationNotPresentException ignored) {
        }
        Combination bestCombination = combinations.stream().max(Combination::compares).orElse(null);
        System.out.println(toString() + " : " + bestCombination);
        this._combination = bestCombination;
        return bestCombination;
    }

    private Cards getAllCards() {
        Cards allCards = new Cards();
        allCards.addAll(downCards);
        allCards.addAll(game.getLastCommunityCards());
        return allCards;
    }

    // TODO call API update user money
    public void win(Integer earnedMoney) {
        log.info(this.user.getUsername() + " WINS " + earnedMoney);
        this.chips += earnedMoney;
        this.combination = _combination.toString();
        System.out.println(combination);
        this.earnedMoney = earnedMoney;
    }

    // TODO call API update user money
    void loose() {
        log.info(this.user.getUsername() + " LOOSES " + this.currentBet);
        if(this.chips == 0){
            this.isEliminated = true;
        }
    }

    public void syncMoneyWithChips() {
        user.setMoney(this.chips);
    }

    public void savePreviousDownCards() {
        this.previousDownCards.clear();
        this.previousDownCards.addAll(downCards);
    }
}
