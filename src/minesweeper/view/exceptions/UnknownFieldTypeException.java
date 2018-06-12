package minesweeper.view.exceptions;

/**
 * Raised when a field type is not recognised.
 */
public class UnknownFieldTypeException extends RuntimeException {
    /**
     * Initializes an exception.
     */
    public UnknownFieldTypeException() { super(); }

    /**
     * Initializes an exception given string message.
     * @param message Message to put in an exception.
     */
    public UnknownFieldTypeException(String message) { super(message); }
}
