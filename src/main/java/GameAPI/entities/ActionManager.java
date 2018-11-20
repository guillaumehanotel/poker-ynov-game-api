package GameAPI.entities;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Classe appelée lorsqu'une action est reçue et acceptée pour une partie donnée
 * Elle va générer une méthode à faire en fonction du type d'action reçu
 * C'est cette classe qui va éxecuter l'action d'un joueur lorsqu'une action est attendu
 */
@Slf4j
public class ActionManager {

    private final HashMap<ActionType, Method> actionMap;

    private Game game;

    private Method playerAction;
    private Integer actionParameter;

    public ActionManager(Game game) {
        this.game = game;
        this.actionMap = new HashMap<>();
        initActionMap();
        this.playerAction = null;
        this.actionParameter = null;
    }

    private void initActionMap() {
        try {
            actionMap.put(ActionType.FOLD, Player.class.getMethod("fold"));
            actionMap.put(ActionType.CALL, Player.class.getMethod("call", Integer.class));
            actionMap.put(ActionType.RAISE, Player.class.getMethod("raise", Integer.class));
            actionMap.put(ActionType.BET, Player.class.getMethod("bets", Integer.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Quand une action est attendu, méthode appelé continuellement pour vérifier joinQueue'il y a une action à faire
     */
    public boolean checkPlayerAction(Round round) {
        if (playerAction != null) {
            this.playAction(round);
        }
        return true;
    }

    /**
     * Execution de la méthode provenant d'une requete
     */
    private void playAction(Round round) {
        Player player = round.getPlayers().getPlayingPlayer();
        try {
            if(actionParameter != null){
                playerAction.invoke(player, actionParameter);
            } else {
                if(playerAction.getName().equals("call")){
                    playerAction.invoke(player, round.getBiggestBet());
                } else {
                    playerAction.invoke(player);
                }
            }

            // on peut enregistrer le jeu actuel dans la actionQueue après l'action jouée pour pouvoir retourner le nouvel état du jeu dans la réponse de la requête
            if(round.turnNotFinishCondition()) {
                try {
                    game.actionQueue.put(game);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Une fois qu'un player a joué, on met une protection pour le prochain player
            game.getActionGuard().expectActionFrom(round.getPlayers().getNextPlayingPlayer());
            game.setPlayingPlayerId(round.getPlayers().getPlayingPlayer().getUser().getId());

        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        this.resetAction();
    }

    /**
     * Lorsqu'une requete arrive, on prend l'action et on en retire une méthode et optionellement un paramètre Integer
     */
    public void saveAction(Action action) {
        log.info("[ACTION ACCEPTED]");
        this.playerAction = actionMap.get(action.getActionType());
        this.actionParameter = action.getValue();
    }

    /**
     * Clear de la méthode et du paramètre
     */
    private void resetAction() {
        this.playerAction = null;
        this.actionParameter = null;
    }

}
