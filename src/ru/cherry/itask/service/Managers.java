package ru.cherry.itask.service;

public class Managers {
    private final InMemoryHistoryManager defaultHistory = new InMemoryHistoryManager();
    private static final InMemoryTaskManager defaultManager = new InMemoryTaskManager();

    public InMemoryTaskManager getDefault() {
        return defaultManager;
    }

    public InMemoryHistoryManager getDefaultHistory() {
        return defaultHistory;
    }
}