package ru.cherry.itask.service;

import org.junit.jupiter.api.Test;
import ru.cherry.itask.exception.NotFoundException;
import ru.cherry.itask.exception.TimeConflictException;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;
import static org.junit.jupiter.api.Assertions.*;
import static ru.cherry.itask.model.Task.Status.*;

import java.time.Duration;
import java.time.LocalDateTime;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    //----------------Получение списка всех задач.---------------------
    @Test
    void showReceiptOfAllTask() throws NotFoundException {
        assertEquals(1, taskManager.getAllTasksOfTask().size(), "Список задач пуст");
    }

    @Test
    void showReceiptOfAllEpic() throws NotFoundException {
        assertEquals(1, taskManager.getAllTasksOfEpic().size(), "Список эпик задач пуст");
    }

    @Test
    void showReceiptOfAllSubtask() throws NotFoundException {
        assertEquals(1, taskManager.getAllTasksOfSubTask().size(), "Список подзадач пуст");
    }

    //-----------Удаление всех задач.------------------------
    @Test
    void showRemoveAllTasksOfTask() throws NotFoundException {
        taskManager.removeAllTasksOfTask();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskManager.getAllTasksOfTask().size();
        });
    }

    @Test
    void showRemoveAllTasksOfEpic() throws NotFoundException {
        taskManager.removeAllTasksOfEpic();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskManager.getAllTasksOfEpic().size();
        });
    }

    @Test
    void showRemoveAllTasksOfSubTask() throws NotFoundException {
        taskManager.removeAllTasksOfSubTask();
        assertEquals(0, taskManager.getAllTasksOfSubTask().size());
    }

    //-------------Получение по идентификатору.---------------
    @Test
    void showGetTaskById() throws NotFoundException {
        Task task2 = taskManager.getTaskById(0);
        assertEquals(0, task2.getID(), "Получена не та задача");
    }

    @Test
    void showGetTaskByIdOfEpic() throws NotFoundException {
        Epic epic2 = taskManager.getTaskByIdOfEpic(1);
        assertEquals(1, epic2.getID(), "Получена не та эпик задача");
    }

    @Test
    void showGetTaskByIdOfSubTask() throws NotFoundException {
        SubTask subTask2 = taskManager.getTaskByIdOfSubTask(2);
        assertEquals(2, subTask2.getID(), "Получена не та подзадача");
    }

    //---------------------Создание задач--------------------------------
    @Test
    void showCreatedTask() throws TimeConflictException, NotFoundException {
        Task task3 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW,
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(60));
        taskManager.createTask(task3);
        Task task3test = taskManager.getTaskById(task3.getID());
        assertEquals(task3, task3test, "Задача не создана");
    }

    @Test
    void showCreatedEpicTask() throws NotFoundException {
        Epic epic3 = new Epic("Убрать работу с консолью", "Удалить из Маин всё");
        taskManager.createEpicTask(epic3);
        Epic epic3test = taskManager.getTaskByIdOfEpic(epic3.getID());
        assertEquals(epic3, epic3test, "Задача эпик не создана");
    }

    @Test
    void showCreatedSubTask() throws NotFoundException {
        SubTask subTask3 = new SubTask("Test", "subTask", Task.Status.NEW, 1,
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.createSubTask(subTask3);
        SubTask subTask3test = taskManager.getTaskByIdOfSubTask(subTask3.getID());
        assertEquals(subTask3, subTask3test, "Подадача не создана");
    }

    //----------------Обновление.--------------------------
    @Test
    void showUpdatedTask() throws NotFoundException {
        Task task4 = taskManager.getTaskById(0);
        task4.setStatus(IN_PROGRESS);
        taskManager.updateTask(task4);
        assertEquals(IN_PROGRESS, taskManager.getTaskById(0).getStatus(), "Задача не обновлена");
    }

    @Test
    void showUpdatedEpicTask() throws NotFoundException {
        Epic epic4 = taskManager.getTaskByIdOfEpic(1);
        epic4.setDetails("Изменили детали");
        taskManager.updateEpicTask(epic4);
        assertEquals("Изменили детали", taskManager.getTaskByIdOfEpic(1).getDetails(),
                "Задача эпик не обновлена");
    }

    @Test
    void showUpdatedSubTask() throws NotFoundException {
        SubTask subTask4 = taskManager.getTaskByIdOfSubTask(2);
        subTask4.setDetails("Изменили детали");
        taskManager.updateSubTask(subTask4);
        assertEquals("Изменили детали", taskManager.getTaskByIdOfSubTask(2).getDetails(),
                "Подадача не обновлена");
    }

    //-------------------Удаление задачи по идентификатору---------------
    @Test
    void showRemovedTaskById() throws NotFoundException {
        taskManager.removeTaskById(0);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskManager.getAllTasksOfTask().size();
        });
    }

    @Test
    void showRemovedEpicTaskById() throws NotFoundException {
        taskManager.removeEpicTaskById(1);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskManager.getAllTasksOfEpic().size();
        });
    }

    @Test
    void showRemovedSubTaskById() throws NotFoundException {
        taskManager.removeSubTaskById(2);
        assertEquals(0, taskManager.getAllTasksOfSubTask().size());

    }

    //-------------------------Управление статусом задачи----------------------------------------------
    @Test
    void showSettingNewStatusOfTask() throws NotFoundException {
        Task task5 = taskManager.getTaskById(0);
        task5.setStatus(IN_PROGRESS);
        taskManager.updateTask(task5);
        assertEquals(IN_PROGRESS, taskManager.getTaskById(0).getStatus(), "Статус задачи не обновлен");
    }

    @Test
    void showSettingNewStatusOfSubTask() throws NotFoundException {
        SubTask subTask5 = taskManager.getTaskByIdOfSubTask(2);
        subTask5.setStatus(DONE);
        taskManager.updateSubTask(subTask5);
        assertEquals(DONE, taskManager.getTaskByIdOfSubTask(2).getStatus(),
                "Статус подзадачи не обновлен");
    }

    //--------------------------Вызов истории просмотров---------------------------------------
    @Test
    void showGettingHistory() throws NotFoundException {
        taskManager.getAllTasksOfTask();
        taskManager.getAllTasksOfEpic();
        taskManager.getAllTasksOfSubTask();

        assertEquals(3, taskManager.getHistory().size(), "Список истории пуст");
    }
}