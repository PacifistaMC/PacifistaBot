package fr.pacifista.bot.core.exceptions;

/**
 * Utilisé pour les erreurs liées à l'utilisateur
 */
public class UserBotException extends Exception {

    public UserBotException(String message) {
        super(message);
    }

    public UserBotException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserBotException(Throwable cause) {
        super(cause);
    }

}
