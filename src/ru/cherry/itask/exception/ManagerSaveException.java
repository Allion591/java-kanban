package ru.cherry.itask.exception;

public class ManagerSaveException extends RuntimeException {
    private String message;

    public ManagerSaveException(final String message, Throwable cause) {
        this.message = message;
    }
}
