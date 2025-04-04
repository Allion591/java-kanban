package ru.cherry.itask;

import ru.cherry.itask.service.InMemoryHistoryManager;
import ru.cherry.itask.service.InMemoryTaskManager;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;
import ru.cherry.itask.service.Managers;

import static ru.cherry.itask.model.Task.Status.*;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Пакеты с маленьких букв", "Подправить имена пакетов", NEW);
        taskManager.createTask(task2);


        Epic epic1 = new Epic("Упростить формирование ID", "Убрать установку в конст-ре");
        taskManager.createEpicTask(epic1);

        SubTask subTask1 = new SubTask("Работа с Эпик", "Добавить лист", NEW, epic1.getID());
        taskManager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Работа с подзадачей", "Добавить поле Эпик", NEW, epic1.getID());
        taskManager.createSubTask(subTask2);

        Epic epic2 = new Epic("Менеджер инкапсулировать", "Поля менеджера и методы  закрыть");
        taskManager.createEpicTask(epic2);

        SubTask subTask3 = new SubTask("Статусы упростить", "Лишнее убрать", NEW, epic2.getID());
        taskManager.createSubTask(subTask3);

        System.out.println(taskManager.getAllTasksOfTask());
        System.out.println(taskManager.getAllTasksOfEpic());
        System.out.println(taskManager.getAllTasksOfSubTask());
        System.out.println();

        task1.setStatus(DONE);
        taskManager.setNewStatusOfTask(task1);

        task2.setStatus(IN_PROGRESS);
        taskManager.setNewStatusOfTask(task2);

        subTask1.setStatus(IN_PROGRESS);
        taskManager.setNewStatusOfSubTask(subTask1);

        subTask2.setStatus(DONE);
        taskManager.setNewStatusOfSubTask(subTask2);

        subTask3.setStatus(DONE);
        taskManager.setNewStatusOfSubTask(subTask3);

        System.out.println(taskManager.getAllTasksOfSubTask());
        System.out.println(taskManager.getAllTasksOfTask());
        System.out.println(taskManager.getAllTasksOfEpic());
        System.out.println();

        taskManager.removeOnIdTask(task1.getID());
        taskManager.removeOnIdEpicTask(epic1.getID());

        System.out.println(taskManager.getAllTasksOfSubTask());
        System.out.println(taskManager.getAllTasksOfTask());
        System.out.println(taskManager.getAllTasksOfEpic());

        System.out.println(taskManager.getTaskByIdOfEpic(5));
        System.out.println(taskManager.getTaskById(1));

        printAllTasks(taskManager);
    }

    private static void printAllTasks(InMemoryTaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getAllTasksOfTask()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : taskManager.getAllTasksOfEpic()) {
            System.out.println(epic);

        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getAllTasksOfSubTask()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}