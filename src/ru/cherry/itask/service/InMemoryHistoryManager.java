package ru.cherry.itask.service;

import ru.cherry.itask.model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private final int maxSizeHistoryList = 9;

    @Override
    public void add(Task task) {
        Task copy = task.copy();
        if (history.size() >= maxSizeHistoryList) {
            history.removeFirst();
        } else {
            history.add(copy);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}