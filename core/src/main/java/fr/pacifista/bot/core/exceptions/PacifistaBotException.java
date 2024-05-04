package fr.pacifista.bot.core.exceptions;

public class PacifistaBotException extends Exception {

    public PacifistaBotException(String message) {
        super(message);
    }

    public PacifistaBotException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacifistaBotException(Throwable cause) {
        super(cause);
    }

}
