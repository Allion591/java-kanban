package ru.cherry.itask.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void AddAndFindAllTaskTypes() {
        Task task = new Task("Test", "Task", Task.Status.NEW);
        manager.createTask(task);
        Epic epic = new Epic("Test", "Epic");
        manager.createEpicTask(epic);
        SubTask subTask = new SubTask("Test", "subTask", Task.Status.NEW, epic.getID());
        manager.createSubTask(subTask);

        assertNotNull(manager.getTaskById(task.getID()));
        assertNotNull(manager.getTaskByIdOfEpic(epic.getID()));
        assertNotNull(manager.getTaskByIdOfSubTask(subTask.getID()));
    }

    @Test
    void generatedIdsAndSendIdsNotConflict() {
        Task setIdTask = new Task("Test2", "Task2", Task.Status.NEW);
        setIdTask.setID(4);
        manager.createTask(setIdTask);

        Task generateIdTask = new Task("Test3", "Task3", Task.Status.IN_PROGRESS);
        manager.createTask(generateIdTask);

        assertNotEquals(setIdTask, generateIdTask, "ID не должны быть одинаковыми");
    }

    @Test
    void immutabilityWhenAddingTaskToManager() {
        Task task_3 = new Task("Test4", "task_3", Task.Status.DONE);
        manager.createTask(task_3);

        Task task_4 = manager.getTaskById(task_3.getID());

        assertEquals(task_3, task_4, "Задачи должны совпадать");
    }

    @Test
    void removeAllTasksInMap() {
        Task task = new Task("Test", "Task", Task.Status.NEW);
        manager.createTask(task);
        Epic epic = new Epic("Test", "Epic");
        manager.createEpicTask(epic);
        SubTask subTask = new SubTask("Test", "subTask", Task.Status.NEW, epic.getID());
        manager.createSubTask(subTask);

        assertEquals(1, manager.getAllTasksOfTask().size());
        assertEquals(1, manager.getAllTasksOfEpic().size());
        assertEquals(1, manager.getAllTasksOfSubTask().size());

        manager.removeAllTasksOfTask();
        manager.removeAllTasksOfEpic();
        manager.removeAllTasksOfSubTask();

        assertEquals(0, manager.getAllTasksOfTask().size());
        assertEquals(0, manager.getAllTasksOfEpic().size());
        assertEquals(0, manager.getAllTasksOfSubTask().size());
    }

    @Test
    void showUpdateAllTasks() {
        Task task = new Task("Test", "Task", Task.Status.NEW);
        manager.createTask(task);
        Epic epic = new Epic("Test", "Epic");
        manager.createEpicTask(epic);
        SubTask subTask = new SubTask("Test", "subTask", Task.Status.NEW, epic.getID());
        manager.createSubTask(subTask);


        task.setStatus(Task.Status.IN_PROGRESS);
        manager.updateTask(task);
        subTask.setStatus(Task.Status.DONE);
        manager.updateSubTask(subTask);
        epic.setDetails("Change Details");
        String details = "Change Details";
        manager.updateEpicTask(epic);

        assertEquals(Task.Status.IN_PROGRESS, manager.getTaskById(task.getID()).getStatus());
        assertEquals(Task.Status.DONE, manager.getTaskByIdOfSubTask(subTask.getID()).getStatus());
        assertEquals(details, manager.getTaskByIdOfEpic(epic.getID()).getDetails());
    }
}