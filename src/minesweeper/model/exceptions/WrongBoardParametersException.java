package minesweeper.model.exceptions;

/**
 * Raised when parameters passed to the board are invalid.
 */
public class WrongBoardParametersException extends RuntimeException{
    /**
     * Calls RuntimeException constructor.
     * @param message Message to include in an exception.
     */
    public WrongBoardParametersException(String message){ super(message); }
}
