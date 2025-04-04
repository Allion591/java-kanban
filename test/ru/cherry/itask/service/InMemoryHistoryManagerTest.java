package ru.cherry.itask.service;

import org.junit.jupiter.api.Test;
import ru.cherry.itask.model.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    @Test
    void TaskVersionsInHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task = new Task("Test", "epic", Task.Status.NEW);
        task.setID(1);

        historyManager.add(task);

        task.setStatus(Task.Status.DONE);
        historyManager.add(task);

        List<Task> listHistory = historyManager.getHistory();

        assertEquals(2, listHistory.size());
        assertEquals(Task.Status.NEW, listHistory.get(0).getStatus());
        assertEquals(Task.Status.DONE, listHistory.get(1).getStatus());
    }
}