package ru.cherry.itask.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.cherry.itask.exception.TimeConflictException;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.cherry.itask.model.Task.Status.NEW;

public class FileBackEndTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File timelesFile;

    @BeforeEach
    void beforeEach() {
        try {
            timelesFile = File.createTempFile("temp1", "csv");
            taskManager = new FileBackedTaskManager(timelesFile.toPath());
            taskManager.removeAllTasksOfTask();
            taskManager.removeAllTasksOfEpic();
            taskManager.removeAllTasksOfSubTask();

            Task task1 = new Task("Убрать работу с консолью", "Удалить из Маин всё", NEW,
                    LocalDateTime.of(LocalDate.of(2025, 05, 23),
                            LocalTime.of(14, 30)), Duration.ofMinutes(30));
            taskManager.createTask(task1);

            Epic epic = new Epic("Test", "Epic");
            taskManager.createEpicTask(epic);

            SubTask subTask = new SubTask("Test", "subTask", Task.Status.NEW, 1,
                    LocalDateTime.now().plusDays(2), Duration.ofMinutes(60));
            taskManager.createSubTask(subTask);
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла");
        } catch (TimeConflictException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void savingAndUploadingAnEmptyFile() {
        Task task1 = taskManager.getTaskById(0);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(timelesFile);
        Task task2 = loadedManager.getTaskById(0);

        assertEquals(task1, task2, "Задачи не одинаковы");

    }

    @Test
    void savingMultipleTasks() {
        SubTask subTask3 = new SubTask("Работа с Эпик 2", "Добавить лист 1", NEW, 1,
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.createSubTask(subTask3);

        Epic epic4 = new Epic("Убрать установку в конст-ре", "Упростить формирование ID");
        taskManager.createEpicTask(epic4);

        try { //в файле будет еще заголовок поэтому число строк на единицу больше зем задач
            List<String> lines = Files.readAllLines(timelesFile.toPath());
            assertEquals(7, lines.size(), "Число строк в списке и в файле не совпадают");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void uploadingMultipleTasks() {
        SubTask subTask3 = new SubTask("Работа с Эпик 2", "Добавить лист 1", NEW, 1,
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.createSubTask(subTask3);

        Epic epic4 = new Epic("Убрать установку в конст-ре", "Упростить формирование ID");
        taskManager.createEpicTask(epic4);

        FileBackedTaskManager loadedManagerTwo = FileBackedTaskManager.loadFromFile(timelesFile);

        int sumTasksCount;
        sumTasksCount = loadedManagerTwo.getAllTasksOfTask().size() + loadedManagerTwo.getAllTasksOfEpic().size() +
                loadedManagerTwo.getAllTasksOfSubTask().size();

        assertEquals(5, sumTasksCount, "Задачи не прогружены");
    }

    @Test
    void loadFromNonExistentFileThrowsException() {
        Path nonExistentFile = Paths.get("");

        assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile.toFile()),
                "Загрузка из несуществующего файла должна вызывать исключение");
    }

    @Test
    void saveFromNonExistentFileThrowsException() {
        Path nonExistentFile = Paths.get("");

        FileBackedTaskManager manager = new FileBackedTaskManager(nonExistentFile);
        Task task = taskManager.getTaskById(0);

        assertThrows(RuntimeException.class, () -> manager.createTask(task),
                "Сохранение в несуществующий файл должно вызывать исключение");
    }
}