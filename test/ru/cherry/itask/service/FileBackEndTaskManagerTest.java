package ru.cherry.itask.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.cherry.itask.model.Task.Status.NEW;

public class FileBackEndTaskManagerTest {
    FileBackedTaskManager fileManager;
    File timelesFile;


    @BeforeEach
    void beforeEach() {
        try {
            timelesFile = File.createTempFile("temp1", "csv");
            fileManager = new FileBackedTaskManager(timelesFile.toPath());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла");
        }
    }

    @Test
    void savingAndUploadingAnEmptyFile() {

        Task task1 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW);
        fileManager.createTask(task1);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(timelesFile);
        Task task2 = loadedManager.getTaskById(0);

        assertEquals(task1, task2, "Задачи не одинаковы");

    }

    @Test
    void savingMultipleTasks() {
        Task task0 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW);
        fileManager.createTask(task0);

        Epic epic1 = new Epic("Упростить формирование ID", "Убрать установку в конст-ре");
        fileManager.createEpicTask(epic1);

        SubTask subTask2 = new SubTask("Работа с Эпик", "Добавить лист", NEW, epic1.getID());
        fileManager.createSubTask(subTask2);

        SubTask subTask3 = new SubTask("Работа с Эпик 2", "Добавить лист 1", NEW, epic1.getID());
        fileManager.createSubTask(subTask3);

        Epic epic4 = new Epic("Убрать установку в конст-ре", "Упростить формирование ID");
        fileManager.createEpicTask(epic4);

        try { //в файле будет еще заголовок поэтому число строк на единицу больше зем задач
            List<String> lines = Files.readAllLines(timelesFile.toPath());
            assertEquals(6, lines.size(), "Число строк в списке и в файле не совпадают");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void uploadingMultipleTasks() {
        Task task0 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW);
        fileManager.createTask(task0);

        Epic epic1 = new Epic("Упростить формирование ID", "Убрать установку в конст-ре");
        fileManager.createEpicTask(epic1);

        SubTask subTask2 = new SubTask("Работа с Эпик", "Добавить лист", NEW, epic1.getID());
        fileManager.createSubTask(subTask2);

        SubTask subTask3 = new SubTask("Работа с Эпик 2", "Добавить лист 1", NEW, epic1.getID());
        fileManager.createSubTask(subTask3);

        Epic epic4 = new Epic("Убрать установку в конст-ре", "Упростить формирование ID");
        fileManager.createEpicTask(epic4);

        FileBackedTaskManager loadedManagerTwo = FileBackedTaskManager.loadFromFile(timelesFile);

        int sumTasksCount;
        sumTasksCount = loadedManagerTwo.getAllTasksOfTask().size() + loadedManagerTwo.getAllTasksOfEpic().size() +
                loadedManagerTwo.getAllTasksOfSubTask().size();

        assertEquals(5, sumTasksCount, "Задачи не прогружены");
    }
}