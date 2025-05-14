package ru.cherry.itask.exception;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(final String message, Throwable cause) {
        super(message, cause);
    }
}