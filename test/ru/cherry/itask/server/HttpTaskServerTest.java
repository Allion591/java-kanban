package ru.cherry.itask.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.cherry.itask.model.Task;
import ru.cherry.itask.service.Managers;
import ru.cherry.itask.service.TaskManager;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private final Gson gson = TasksHandler.getGson();
    private TaskManager inMemoryTaskManager;

    @BeforeEach
    void setUp() throws Exception {
        inMemoryTaskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
        inMemoryTaskManager.removeAllTasksOfTask();
        httpTaskServer.start();
    }

    @AfterEach
    void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddTasks() throws Exception {
        Task taskTest_1 = new Task("TestTask_1", "Test", Task.Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task taskTest_2 = new Task("TestTask_2", "Test", Task.Status.NEW, LocalDateTime.now()
                .plusDays(1), Duration.ofMinutes(30));

        String task_1_Json = gson.toJson(taskTest_1);
        String task_2_Json = gson.toJson(taskTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/tasks");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Задача #1 не создана");
        assertEquals(201, response2.statusCode(), "Задача #2 не создана");

        List<Task> tasksFromManager = inMemoryTaskManager.getAllTasksOfTask();

        assertNotNull(tasksFromManager, "Задачи отсутствуют в менеджере задач");

        assertEquals(2, tasksFromManager.size(), "Добавлены не все задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task taskTest_1 = new Task("TestTask_1", "Test", Task.Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task taskTest_2 = new Task("TestTask_2", "Test", Task.Status.NEW, LocalDateTime.now()
                .plusDays(1), Duration.ofMinutes(30));

        String task_1_Json = gson.toJson(taskTest_1);
        String task_2_Json = gson.toJson(taskTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/tasks");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        Task createdTask1 = gson.fromJson(response.body(), Task.class);
        Task createdTask2 = gson.fromJson(response2.body(), Task.class);

        URI urlTasks_1_WithId = URI.create("http://localHost:8080/api/tasks/" + createdTask1.getID());
        URI urlTasks_2_WithId = URI.create("http://localHost:8080/api/tasks/" + createdTask2.getID());

        HttpRequest requestTaskFromServer_1 = HttpRequest.newBuilder().uri(urlTasks_1_WithId).POST(HttpRequest
                .BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestFromServerFromServer_2 = HttpRequest.newBuilder().uri(urlTasks_2_WithId).POST(HttpRequest
                .BodyPublishers.
                ofString(task_2_Json)).build();

        HttpResponse<String> responseTask_1_Update = httpClient.send(requestTaskFromServer_1, HttpResponse.
                BodyHandlers.ofString());

        HttpResponse<String> responseTask_2_Update = httpClient.send(requestFromServerFromServer_2,
                HttpResponse.BodyHandlers
                        .ofString());

        assertEquals(201, responseTask_1_Update.statusCode(), "Задача не обновлена");
        assertEquals(201, responseTask_2_Update.statusCode(), "Задача не обновлена");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task taskTest_1 = new Task("TestTask_1", "Test", Task.Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task taskTest_2 = new Task("TestTask_2", "Test", Task.Status.NEW, LocalDateTime.now()
                .plusDays(1), Duration.ofMinutes(30));

        String task_1_Json = gson.toJson(taskTest_1);
        String task_2_Json = gson.toJson(taskTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/tasks");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Задача не получена");
        assertEquals(201, response2.statusCode(), "Задача не получена");


        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(urlTasks)
                .GET()
                .build();
        HttpResponse<String> responseGet = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksFromServer = gson.fromJson(responseGet.body(), taskListType);

        assertEquals(2, tasksFromServer.size(), "Неверное количество задач");
        assertEquals(taskTest_1.getTaskName(), tasksFromServer.get(0).getTaskName(), "Неверная задача");
        assertEquals(taskTest_2.getTaskName(), tasksFromServer.get(1).getTaskName(), "Неверная задача");
    }

    @Test
    public void testGetTaskFromId() throws IOException, InterruptedException {
        Task taskTest_1 = new Task("TestTask_1", "Test", Task.Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task taskTest_2 = new Task("TestTask_2", "Test", Task.Status.NEW, LocalDateTime.now()
                .plusDays(1), Duration.ofMinutes(30));

        String task_1_Json = gson.toJson(taskTest_1);
        String task_2_Json = gson.toJson(taskTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/tasks");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        Task createdTask1 = gson.fromJson(response.body(), Task.class);
        Task createdTask2 = gson.fromJson(response2.body(), Task.class);

        URI urlTasks_1_WithId = URI.create("http://localHost:8080/api/tasks/" + createdTask1.getID());
        URI urlTasks_2_WithId = URI.create("http://localHost:8080/api/tasks/" + createdTask2.getID());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(urlTasks_1_WithId)
                .GET()
                .build();
        HttpResponse<String> responseGetTask_1 = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGetTask_1.statusCode(), "Задача не возвращена");

        Task taskFromServer_1 = gson.fromJson(responseGetTask_1.body(), Task.class);

        HttpRequest requestGetTask_2 = HttpRequest.newBuilder()
                .uri(urlTasks_2_WithId)
                .GET()
                .build();
        HttpResponse<String> responseGetTask_2 = httpClient.send(requestGetTask_2, HttpResponse.BodyHandlers
                .ofString());

        assertEquals(200, responseGetTask_2.statusCode(), "Задача не возвращена");

        Task taskFromServer_2 = gson.fromJson(responseGetTask_2.body(), Task.class);

        assertEquals(createdTask1.getID(), taskFromServer_1.getID(), "Задачи не одинаковы");
        assertEquals(createdTask2.getID(), taskFromServer_2.getID(), "Задачи не одинаковы");
    }

    @Test
    public void testDeleteTaskFromId() throws IOException, InterruptedException {
        Task taskTest_1 = new Task("TestTask_1", "Test", Task.Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task taskTest_2 = new Task("TestTask_2", "Test", Task.Status.NEW, LocalDateTime.now()
                .plusDays(1), Duration.ofMinutes(30));

        String task_1_Json = gson.toJson(taskTest_1);
        String task_2_Json = gson.toJson(taskTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/tasks");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        Task createdTask1 = gson.fromJson(response.body(), Task.class);
        Task createdTask2 = gson.fromJson(response2.body(), Task.class);

        URI urlTasks_1_WithId = URI.create("http://localHost:8080/api/tasks/" + createdTask1.getID());
        URI urlTasks_2_WithId = URI.create("http://localHost:8080/api/tasks/" + createdTask2.getID());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(urlTasks_1_WithId)
                .DELETE()
                .build();
        HttpResponse<String> responseDeleteTask_1 = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGetTask_2 = HttpRequest.newBuilder()
                .uri(urlTasks_2_WithId)
                .DELETE()
                .build();
        HttpResponse<String> responseDeleteTask_2 = httpClient.send(requestGetTask_2, HttpResponse.BodyHandlers
                .ofString());

        assertEquals(200, responseDeleteTask_1.statusCode(), "Задача #1 не удалена");
        assertEquals(200, responseDeleteTask_2.statusCode(), "Задача #2 не удалена");
    }

    @Test
    public void testNotAcceptable() throws IOException, InterruptedException {
        Task taskTest_1 = new Task("TestTask_1", "Test", Task.Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task taskTest_2 = new Task("TestTask_2", "Test", Task.Status.NEW, LocalDateTime.now()
                , Duration.ofMinutes(30));

        String task_1_Json = gson.toJson(taskTest_1);
        String task_2_Json = gson.toJson(taskTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/tasks");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode(), "Заадачи не пересеклись по времени");
    }

    @Test
    public void testNotFound() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks_1_WithId = URI.create("http://localHost:8080/api/tasks/" + 0);

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(urlTasks_1_WithId)
                .GET()
                .build();
        HttpResponse<String> responseGetTask_1 = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGetTask_1.statusCode());
    }

    @Test
    public void testInternalServerError() throws IOException, InterruptedException {
        Task taskTest_1 = new Task("TestTask_1", "Test", Task.Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task taskTest_2 = new Task("TestTask_2", "Test", Task.Status.NEW, LocalDateTime.now()
                .plusDays(1), Duration.ofMinutes(30));

        String task_1_Json = gson.toJson(taskTest_1);
        String task_2_Json = gson.toJson(taskTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/tasks");

        HttpRequest requestTask_1 = HttpRequest.newBuilder()
                .uri(urlTasks)
                .PUT(HttpRequest
                        .BodyPublishers
                        .ofString(task_1_Json))
                .build();
        HttpRequest requestTask_2 = HttpRequest
                .newBuilder()
                .uri(urlTasks)
                .PUT(HttpRequest
                        .BodyPublishers.
                        ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode(), "Запрос не вызывал исключение");
        assertEquals(500, response2.statusCode(), "Запрос не вызывал исключение");
    }
}