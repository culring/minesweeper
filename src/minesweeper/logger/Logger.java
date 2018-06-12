package minesweeper.logger;

/**
 * The logger class used to printing logging
 * messages in unified format.
 */
public class Logger {
    /**
     * Logger constructor.
     * @param who This string will be displayed before
     *            every logged message.
     */
    public Logger(String who){
        this.who = who;
    }

    /**
     * Logs a message to the screen.
     * @param message A message to log.
     */
    public void log(String message){
        if(isDebug) System.out.println("[" + who + "]: " + message);
    }

    /**
     * This string will be displayed before every logged message.
     */
    private String who;
    /**
     * All logs are suspended if this is false.
     */
    public static boolean isDebug = false;
}
