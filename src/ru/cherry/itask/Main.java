package ru.cherry.itask;

import ru.cherry.itask.exception.TimeConflictException;
import ru.cherry.itask.service.FileBackedTaskManager;
import ru.cherry.itask.service.InMemoryTaskManager;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static ru.cherry.itask.model.Task.Status.*;

public class Main {
    private static String road = "D:\\Java practicum\\java-kanban\\src\\ru\\cherry\\itask\\service";
    private static Path dir = Paths.get(road);
    private static File saveFile = new File(dir + File.separator + "SaveData.csv");

    public static void main(String[] args) throws TimeConflictException {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        FileBackedTaskManager fileManager = new FileBackedTaskManager(dir);

        Task task1 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW,
                LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(60));
        fileManager.createTask(task1);

        Task task2 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW,
                LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(60));
        fileManager.createTask(task2);

        Epic epic1 = new Epic("Упростить формирование ID", "Убрать установку в конст-ре");
        fileManager.createEpicTask(epic1);

        Epic epic2 = new Epic("Упростить формирование ID", "Убрать установку в конст-ре");
        fileManager.createEpicTask(epic2);

        SubTask subTask1 = new SubTask("Работа с Эпик", "Добавить лист", NEW, epic2.getID(),
                LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(30));
        fileManager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Работа с Эпик", "Добавить лист", IN_PROGRESS, epic2.getID(),
                LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(120));
        fileManager.createSubTask(subTask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(saveFile);

        for (Task task : loadedManager.getAllTasksOfTask()) {
            System.out.println(task);
        }
        for (Epic epic : loadedManager.getAllTasksOfEpic()) {
            System.out.println(epic);
        }
        for (SubTask subTask : loadedManager.getAllTasksOfSubTask()) {
            System.out.println(subTask);
        }
        //loadedManager.removeEpicTaskById(task1.getID());
        System.out.println("History:");
        loadedManager.setNewStatusOfSubTask(subTask2);

        for (Task task : loadedManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Sorted tasks:");
        for (Task task : fileManager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
    }