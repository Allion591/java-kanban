package ru.cherry.itask;

import ru.cherry.itask.service.FileBackedTaskManager;
import ru.cherry.itask.service.InMemoryTaskManager;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.cherry.itask.model.Task.Status.*;

public class Main {
    private static String road = "D:\\Java practicum\\java-kanban\\src\\ru\\cherry\\itask\\service";
    private static Path dir = Paths.get(road);
    private static File saveFile = new File(dir + File.separator + "SaveData.csv");

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        FileBackedTaskManager fileManager = new FileBackedTaskManager(dir);

        Task task1 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW);
        fileManager.createTask(task1);

        Epic epic1 = new Epic("Упростить формирование ID", "Убрать установку в конст-ре");
        fileManager.createEpicTask(epic1);

        SubTask subTask1 = new SubTask("Работа с Эпик", "Добавить лист", NEW, epic1.getID());
        fileManager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Работа с Эпик", "Добавить лист", NEW, epic1.getID());
        fileManager.createSubTask(subTask2);

        Epic epic2 = new Epic("Упростить формирование ID", "Убрать установку в конст-ре");
        fileManager.createEpicTask(epic2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(saveFile);

        System.out.println(loadedManager.getAllTasksOfTask());
        System.out.println(loadedManager.getAllTasksOfEpic());
        System.out.println(loadedManager.getAllTasksOfSubTask());
    }
    }