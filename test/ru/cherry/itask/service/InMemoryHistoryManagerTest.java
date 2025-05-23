package ru.cherry.itask.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static ru.cherry.itask.model.Task.Status.NEW;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @BeforeEach
    public void beforeEach() {
        historyManager.remove(0);
        historyManager.remove(1);
        historyManager.remove(2);

        Task task1 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW,
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(30));
        task1.setID(0);
        historyManager.add(task1);

        Epic epic = new Epic("Test", "Epic");
        epic.setID(1);
        historyManager.add(epic);

        SubTask subTask = new SubTask("Test", "subTask", Task.Status.NEW, 1,
                LocalDateTime.now().plusDays(2), Duration.ofMinutes(60));
        subTask.setID(2);
        historyManager.add(subTask);
    }

    @Test
    void shouldMaintainInsertionOrder() {
        assertEquals(1, historyManager.getHistory().get(1).getID(), "Порядок задач нарушен");
        assertEquals(2, historyManager.getHistory().get(2).getID(), "Порядок задач нарушен");
    }

    @Test
    void TaskVersionsInHistory() {
        historyManager.remove(1);
        historyManager.remove(2);
        Task task = historyManager.getHistory().get(0);

        task.setStatus(Task.Status.DONE);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(), "Должна быть только последняя версия");
        assertEquals(Task.Status.DONE, historyManager.getHistory().get(0).getStatus(), "Статус не обновлен");
    }
}