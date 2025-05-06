package ru.cherry.itask.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void tasksAreEqualIfTheirIDIsEqual() {
        Epic epic_1 = new Epic("Test", "details");
        epic_1.setID(0);

        assertEquals(epic_1, epic_1, "Задачи не совпадают");
        assertEquals(epic_1.hashCode(), epic_1.hashCode(), "HashCode не совпал");
    }

    @Test
    void notCanAddEpicToEpic() {
        Epic epic_2 = new Epic("Test_2", "details");
        int epicId = epic_2.getID();

        epic_2.addSubtaskId(epicId);
        assertFalse(epic_2.getSubtasksIDs().contains(epicId), "Эпик не может иметь себя в качестве подзадачи");

    }
}