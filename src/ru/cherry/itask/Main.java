package ru.cherry.itask;

import ru.cherry.itask.service.InMemoryTaskManager;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

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

        SubTask subTask3 = new SubTask("Статусы упростить", "Лишнее убрать", NEW, epic1.getID());
        taskManager.createSubTask(subTask3);

        Epic epic2 = new Epic("Менеджер инкапсулировать", "Поля менеджера и методы  закрыть");
        taskManager.createEpicTask(epic2);



        System.out.println("Запрос задач 1:");
        for (Task task : taskManager.getAllTasksOfTask()) {
            System.out.println(task);
        }

        System.out.println("Запрос истории 1:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Запрос задач 2:");
        for (Task task : taskManager.getAllTasksOfEpic()) {
            System.out.println(task);
        }
        System.out.println("Запрос истории 2:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Запрос задач 3:");
        for (Task task : taskManager.getAllTasksOfSubTask()) {
            System.out.println(task);
        }
        System.out.println("Запрос истории 3:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("Запрос задач 2:");
        for (Task task : taskManager.getAllTasksOfEpic()) {
            System.out.println(task);
        }
        System.out.println("Запрос истории 2:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Запрос задач 1:");
        for (Task task : taskManager.getAllTasksOfTask()) {
            System.out.println(task);
        }
        System.out.println("Запрос истории 1:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Запрос задач 3:");
        for (Task task : taskManager.getAllTasksOfSubTask()) {
            System.out.println(task);
        }
        System.out.println("Запрос истории 3:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.removeTaskById(task1.getID());
        System.out.println("Запрос истории после удаления задачи:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.removeEpicTaskById(epic1.getID());

        System.out.println("Запрос истории после удаления задачи Эпик 1 с подзадачами:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

}