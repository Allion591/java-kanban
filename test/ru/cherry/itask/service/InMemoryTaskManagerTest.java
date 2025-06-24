package ru.cherry.itask.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.cherry.itask.exception.NotFoundException;
import ru.cherry.itask.exception.TimeConflictException;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static ru.cherry.itask.model.Task.Status.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() throws TimeConflictException, NotFoundException {
        taskManager = new InMemoryTaskManager();
        taskManager.removeAllTasksOfTask();
        taskManager.removeAllTasksOfEpic();
        taskManager.removeAllTasksOfSubTask();

        Task task1 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW,
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(30));
        taskManager.createTask(task1);

        Epic epic = new Epic("Test", "Epic");
        taskManager.createEpicTask(epic);

        SubTask subTask = new SubTask("Test", "subTask", Task.Status.NEW, 1,
                LocalDateTime.now().plusDays(2), Duration.ofMinutes(60));
        taskManager.createSubTask(subTask);
    }

    @Test
    void generatedIdsAndSendIdsNotConflict() throws TimeConflictException, NotFoundException {
        Task setIdTask = taskManager.getTaskById(0);
        setIdTask.setID(4);
        taskManager.createTask(setIdTask);

        Task generateIdTask = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW,
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(30));

        assertNotEquals(setIdTask, generateIdTask, "ID не должны быть одинаковыми");
    }

    @Test
    void immutabilityWhenAddingTaskToManager() throws TimeConflictException, NotFoundException {
        Task task_3 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW,
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(30));
        taskManager.createTask(task_3);

        Task task_4 = taskManager.getTaskById(task_3.getID());

        assertEquals(task_3, task_4, "Задачи должны совпадать");
    }

    @Test
    void removeAllTasksInMap() throws NotFoundException {
        assertEquals(1, taskManager.getAllTasksOfTask().size());
        assertEquals(1, taskManager.getAllTasksOfEpic().size());
        assertEquals(1, taskManager.getAllTasksOfSubTask().size());

        taskManager.removeAllTasksOfTask();
        taskManager.removeAllTasksOfEpic();
        taskManager.removeAllTasksOfSubTask();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskManager.getAllTasksOfTask().size();
        });
        NotFoundException exception1 = assertThrows(NotFoundException.class, () -> {
            taskManager.getAllTasksOfEpic().size();
        });
        NotFoundException exception2 = assertThrows(NotFoundException.class, () -> {
            taskManager.getAllTasksOfSubTask().size();
        });
    }

    @Test
    void showUpdateAllTasks() throws NotFoundException {
        Task task = taskManager.getTaskById(0);
        Epic epic = taskManager.getTaskByIdOfEpic(1);
        SubTask subTask = taskManager.getTaskByIdOfSubTask(2);


        task.setStatus(Task.Status.IN_PROGRESS);
        taskManager.updateTask(task);
        subTask.setStatus(Task.Status.DONE);
        taskManager.updateSubTask(subTask);
        epic.setDetails("Change Details");
        String details = "Change Details";
        taskManager.updateEpicTask(epic);

        assertEquals(Task.Status.IN_PROGRESS, taskManager.getTaskById(task.getID()).getStatus());
        assertEquals(Task.Status.DONE, taskManager.getTaskByIdOfSubTask(subTask.getID()).getStatus());
        assertEquals(details, taskManager.getTaskByIdOfEpic(epic.getID()).getDetails());
    }

    @Test
    void shouldRemoveTaskFromHistoryWhenDeleted() throws NotFoundException {
        Task task = taskManager.getTaskById(0);
        int taskID = task.getID();
        taskManager.getTaskById(taskID);
        taskManager.removeTaskById(taskID);

        assertTrue(taskManager.getHistory().isEmpty(), "Задача не удалена из истории");
    }

    @Test
    void epicShouldNotContainRemovedSubtaskId() throws NotFoundException {
        Epic epic = taskManager.getTaskByIdOfEpic(1);
        SubTask subTask = taskManager.getTaskByIdOfSubTask(2);

        int subtaskId = subTask.getID();
        taskManager.removeSubTaskById(subtaskId);

        assertFalse(epic.getSubtasksIDs().contains(subtaskId), "ID подзадачи остался в эпике");
    }

    @Test
    void changingTaskIdShouldNotAffectManager() throws NotFoundException {
        Task task = taskManager.getTaskById(0);
        int originId = task.getID();

        task.setID(666);
        Task retivedTask = taskManager.getTaskById(originId);

        assertNotNull(retivedTask, "Задача должна быть доступна по оригинальному ID");
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskManager.getTaskById(666);
        });
    }

    @Test
    void showCalculationOfEpicStatusBoundaryConditions() throws NotFoundException {
        SubTask subTask_2 = new SubTask("Test", "subTask", Task.Status.NEW, 1,
                LocalDateTime.now().plusDays(5), Duration.ofMinutes(120));
        taskManager.createSubTask(subTask_2);
        Epic epic_2 = taskManager.getTaskByIdOfEpic(1);

        assertEquals(NEW, epic_2.getStatus(), "Статус эпика не NEW");

        SubTask subTask_3 = taskManager.getTaskByIdOfSubTask(2);

        subTask_2.setStatus(DONE);
        subTask_3.setStatus(DONE);
        taskManager.updateSubTask(subTask_2);
        taskManager.updateSubTask(subTask_3);
        taskManager.checkEpicStatus(epic_2.getID());
        assertEquals(DONE, epic_2.getStatus(), "Статус эпика не DONE");

        subTask_2.setStatus(NEW);
        subTask_3.setStatus(DONE);
        taskManager.updateSubTask(subTask_2);
        taskManager.updateSubTask(subTask_3);
        taskManager.checkEpicStatus(epic_2.getID());
        assertEquals(NEW, epic_2.getStatus(), "Статус эпика не NEW");

        subTask_2.setStatus(IN_PROGRESS);
        subTask_3.setStatus(IN_PROGRESS);
        taskManager.updateSubTask(subTask_2);
        taskManager.updateSubTask(subTask_3);
        taskManager.checkEpicStatus(epic_2.getID());
        assertEquals(IN_PROGRESS, epic_2.getStatus(), "Статус эпика не IN_PROGRESS");
    }

    @Test
    void showThePresenceOfAnAssociatedEpic() throws NotFoundException {
        SubTask subTask = taskManager.getTaskByIdOfSubTask(2);

        assertEquals(taskManager.getTaskByIdOfEpic(1).getID(), subTask.getEpicId(),
                "Неправильный ID эпик задачи");
    }

    @Test
    void showCheckingTheIntersectionOfIntervals() throws TimeConflictException, NotFoundException {
        Task task = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createTask(task);

        assertThrows(TimeConflictException.class, () -> {
            taskManager.taskIntersection(task);
        }, "Задачи не пересекаются по времени");
    }
}