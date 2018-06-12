package minesweeper.view.exceptions;

/**
 * Raised when an exception occurs in counter object.
 */
public class CounterException extends RuntimeException {
    /**
     * Initializes an exception.
     */
    public CounterException() { super(); }

    /**
     * Initializes an exception given string message.
     * @param message Message to put in an exception.
     */
    public CounterException(String message) { super(message); }
}
