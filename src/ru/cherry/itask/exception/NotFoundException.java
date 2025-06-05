package ru.cherry.itask.exception;

public class NotFoundException extends Exception {
    public NotFoundException(final String message) {
        super(message);
    }
}