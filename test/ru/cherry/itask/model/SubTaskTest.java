package ru.cherry.itask.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void tasksAreEqualIfTheirIDIsEqual() {
        SubTask subTask_1 = new SubTask("Test", "details", Task.Status.NEW, 1,
                LocalDateTime.now(), Duration.ofMinutes(60));
        subTask_1.setID(0);

        assertEquals(subTask_1, subTask_1, "Задачи не совпадают");
        assertEquals(subTask_1.hashCode(), subTask_1.hashCode(), "HashCode не совпал");
    }

    @Test
    void subtaskShouldNotBeItsOwnEpic() {
        Epic epic_3 = new Epic("Test", "details");
        epic_3.setID(3);

        SubTask subTask_2 = new SubTask("Test", "details", Task.Status.NEW, epic_3.getID(),
                LocalDateTime.now(), Duration.ofMinutes(60));
        subTask_2.setID(3);

        assertFalse(epic_3.getSubtasksIDs().contains(subTask_2.getID()), "Подзадача не может быть Эпиком");
    }
}