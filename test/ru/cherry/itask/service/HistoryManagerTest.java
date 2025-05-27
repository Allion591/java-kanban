package ru.cherry.itask.service;

import org.junit.jupiter.api.Test;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.cherry.itask.model.Task.Status.NEW;


abstract class HistoryManagerTest <T extends HistoryManager> {
    protected T historyManager;

    @Test
    void showAddTask() {
        Task task1 = historyManager.getHistory().get(0);

        historyManager.add(task1);
        assertEquals(task1, historyManager.getHistory().get(2), "Задачи не совпадают");
    }

    @Test
    void showRemoveTask() {
        Epic epic = (Epic) historyManager.getHistory().get(1);
        historyManager.remove(epic.getID());

        assertEquals(2, historyManager.getHistory().size(), "Задача не удалена");
    }

    @Test
    void showGetHistory() {
        assertEquals(3, historyManager.getHistory().size(), "Не совпадает кол-во задач в истории");
    }

    @Test
    void showEmptyHistory() {
        historyManager.remove(0);
        historyManager.remove(1);
        historyManager.remove(2);

        assertEquals(0, historyManager.getHistory().size(), "История не пуста");
    }

    @Test
    void showNoDuplication() {
        Task task1 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW,
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(30));
        task1.setID(0);

        historyManager.add(task1);

        assertEquals(3, historyManager.getHistory().size(), "Добавилась дублированная задача");
    }

    @Test
    void deletionFromHistoryBeginning() {
        historyManager.remove(0);
        assertEquals(2, historyManager.getHistory().size(), "Первая задача не удалена");
    }

    @Test
    void deletionFromHistoryMiddle() {
        historyManager.remove(1);
        assertEquals(2, historyManager.getHistory().size(), "Вторая задача не удалена");
    }

    @Test
    void deletionFromHistoryEnd() {
        historyManager.remove(2);
        assertEquals(2, historyManager.getHistory().size(), "Последняя задача не удалена");
    }
}