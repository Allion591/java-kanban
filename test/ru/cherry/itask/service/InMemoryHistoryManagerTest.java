package ru.cherry.itask.service;

import org.junit.jupiter.api.Test;
import ru.cherry.itask.model.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void shouldNotContainDuplicates() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Test", "Details", Task.Status.NEW);
        task.setID(1);

        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("Test", "Details", Task.Status.NEW);
        task.setID(1);

        historyManager.add(task);
        historyManager.remove(1);

        assertTrue(historyManager.getHistory().isEmpty(), "Задача не удалена");
    }

    @Test
    void shouldMaintainInsertionOrder() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Task1", "Details", Task.Status.NEW);
        task1.setID(1);
        Task task2 = new Task("Task2", "Details", Task.Status.NEW);
        task2.setID(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.get(0).getID(), "Порядок задач нарушен");
        assertEquals(2, history.get(1).getID(), "Порядок задач нарушен");
    }

    @Test
    void TaskVersionsInHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task = new Task("Test", "Details", Task.Status.NEW);
        task.setID(1);

        historyManager.add(task);
        task.setStatus(Task.Status.DONE);
        historyManager.add(task);

        List<Task> listHistory = historyManager.getHistory();

        assertEquals(1, listHistory.size(), "Должна быть только последняя версия");
        assertEquals(Task.Status.DONE, listHistory.get(0).getStatus(), "Статус не обновлен");
    }
}