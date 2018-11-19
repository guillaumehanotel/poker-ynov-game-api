package GameAPI.entities;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class Players extends ArrayList<Player> {

    private Integer currentOrderIndex = 0;

    public Player getPlayingPlayer(){
        return get(currentOrderIndex);
    }

    public void passToNextPlayer(){
        if (currentOrderIndex+1 == size()) {
            currentOrderIndex = 0;
        } else {
            currentOrderIndex++;
        }
    }

    public Player getNextPlayingPlayer(){
        passToNextPlayer();
        Player player = getPlayingPlayer();
        if (player.getIsEliminated() || player.getHasDropped()) {
            return getNextPlayingPlayer();
        }
        log.info("turn passes to " + player.getUser().getUsername());
        return player;
    }





    /**
     * Récupère le joueur suivant dans l'ordre des joueurs d'un jeu
     */
    public Player getNextPlayer(){
        if (currentOrderIndex == size()) {
            currentOrderIndex = 0;
        }
        Player player = get(currentOrderIndex);
        currentOrderIndex++;
        return player;
    }

    /**
     * Récupère le joueur suivant qui n'est pas éliminé et qui n'est pas couché
     */
    public Player getNextPlaying(){
        Player player = getNextPlayer();
        if (player.getIsEliminated() || player.getHasDropped()) {
            return getNextPlaying();
        }
        return player;
    }



    /**
     * Modifie l'index de l'ordre de jeu des joueurs
     */
    public void setCurrentOrderIndex(Integer currentOrderIndex) {
        log.info(String.valueOf(currentOrderIndex));
        log.info(String.valueOf(this.size()));
        if (currentOrderIndex >= this.size()) {
            throw new RuntimeException("CurrentIndex too big");
        }
        this.currentOrderIndex = currentOrderIndex;
    }

}
