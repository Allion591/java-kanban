package ru.cherry.itask.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasksAreEqualIfTheirIDIsEqual() {
        Task task_1 = new Task("test", "test-test", Task.Status.NEW);
        task_1.setID(1);

        assertEquals(task_1, task_1, "Задачи не совпадают");
        assertEquals(task_1.hashCode(), task_1.hashCode(), "HashCode не совпал");
    }
}