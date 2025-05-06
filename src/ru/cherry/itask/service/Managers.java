package ru.cherry.itask.service;

public class Managers {
    private final HistoryManager defaultHistory = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public HistoryManager getDefaultHistory() {
        return defaultHistory;
    }
}