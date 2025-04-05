package ru.cherry.itask.service;

public class Managers {
    private static final HistoryManager defaultHistory = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return defaultHistory;
    }
}