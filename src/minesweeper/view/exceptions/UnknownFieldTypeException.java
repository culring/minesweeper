package minesweeper.view.exceptions;

public class UnknownFieldTypeException extends RuntimeException {
    public UnknownFieldTypeException() { super(); }
    public UnknownFieldTypeException(String message) { super(message); }
}
