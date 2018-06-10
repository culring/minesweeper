package minesweeper.logger;

public class Logger {
    public Logger(String who){
        this.who = who;
    }

    public void log(String message){
        if(isDebug) System.out.println("[" + who + "]: " + message);
    }

    private String who;
    public static boolean isDebug = false;
}
