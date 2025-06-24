package ru.cherry.itask.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.cherry.itask.exception.NotFoundException;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
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

public class HttpEpicServerTest {
    private HttpTaskServer httpTaskServer;
    private final Gson gson = EpicHandler.getGson();
    private TaskManager inMemoryTaskManager;

    @BeforeEach
    void setUp() throws Exception {
        inMemoryTaskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
        inMemoryTaskManager.removeAllTasksOfEpic();
        httpTaskServer.start();
    }

    @AfterEach
    void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddEpicTasks() throws Exception {
        inMemoryTaskManager.removeAllTasksOfEpic();
        Epic epicTest_1 = new Epic("TestTask_1", "Test");
        Epic epicTest_2 = new Epic("TestTask_2", "Test");

        String task_1_Json = gson.toJson(epicTest_1);
        String task_2_Json = gson.toJson(epicTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/epics");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();

        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Задача #1 не создана");
        assertEquals(201, response2.statusCode(), "Задача #2 не создана");

        List<Epic> tasksFromManager = inMemoryTaskManager.getAllTasksOfEpic();

        assertNotNull(tasksFromManager, "Задачи отсутствуют в менеджере задач");

        assertEquals(2, tasksFromManager.size(), "Добавлены не все задачи");
        assertEquals("TestTask_1", tasksFromManager.getFirst().getTaskName(), "Имена задач не совпали");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epicTest_1 = new Epic("TestTask_1", "Test");
        Epic epicTest_2 = new Epic("TestTask_2", "Test");

        String task_1_Json = gson.toJson(epicTest_1);
        String task_2_Json = gson.toJson(epicTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/epics");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        Epic createdTask1 = gson.fromJson(response.body(), Epic.class);
        Epic createdTask2 = gson.fromJson(response2.body(), Epic.class);

        URI urlTasks_1_WithId = URI.create("http://localHost:8080/api/epics/" + createdTask1.getID());
        URI urlTasks_2_WithId = URI.create("http://localHost:8080/api/epics/" + createdTask2.getID());

        String task_Json_1 = gson.toJson(createdTask1);
        String task_Json_2 = gson.toJson(createdTask2);

        HttpRequest requestTaskFromServer_1 = HttpRequest
                .newBuilder()
                .uri(urlTasks_1_WithId)
                .POST(HttpRequest
                        .BodyPublishers.
                        ofString(task_Json_1))
                .build();
        HttpRequest requestFromServerFromServer_2 = HttpRequest.newBuilder()
                .uri(urlTasks_2_WithId)
                .POST(HttpRequest
                        .BodyPublishers.
                        ofString(task_Json_2))
                .build();

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
        Epic epicTest_1 = new Epic("TestTask_1", "Test");
        Epic epicTest_2 = new Epic("TestTask_2", "Test");

        String task_1_Json = gson.toJson(epicTest_1);
        String task_2_Json = gson.toJson(epicTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/epics");

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
        assertEquals(epicTest_1.getTaskName(), tasksFromServer.get(0).getTaskName(), "Неверная задача");
        assertEquals(epicTest_2.getTaskName(), tasksFromServer.get(1).getTaskName(), "Неверная задача");
    }

    @Test
    public void testGetTaskFromId() throws IOException, InterruptedException {
        Epic epicTest_1 = new Epic("TestTask_1", "Test");
        Epic epicTest_2 = new Epic("TestTask_2", "Test");

        String task_1_Json = gson.toJson(epicTest_1);
        String task_2_Json = gson.toJson(epicTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/epics");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        Epic createdTask1 = gson.fromJson(response.body(), Epic.class);
        Epic createdTask2 = gson.fromJson(response2.body(), Epic.class);

        URI urlTasks_1_WithId = URI.create("http://localHost:8080/api/epics/" + createdTask1.getID());
        URI urlTasks_2_WithId = URI.create("http://localHost:8080/api/epics/" + createdTask2.getID());

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(urlTasks_1_WithId)
                .GET()
                .build();
        HttpResponse<String> responseGetTask_1 = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseGetTask_1.statusCode(), "Задача не возвращена");

        Epic taskFromServer_1 = gson.fromJson(responseGetTask_1.body(), Epic.class);

        HttpRequest requestGetTask_2 = HttpRequest.newBuilder()
                .uri(urlTasks_2_WithId)
                .GET()
                .build();
        HttpResponse<String> responseGetTask_2 = httpClient.send(requestGetTask_2, HttpResponse.BodyHandlers
                .ofString());

        assertEquals(200, responseGetTask_2.statusCode(), "Задача не возвращена");

        Epic taskFromServer_2 = gson.fromJson(responseGetTask_2.body(), Epic.class);

        assertEquals(createdTask1.getID(), taskFromServer_1.getID(), "Задачи не одинаковы");
        assertEquals(createdTask2.getID(), taskFromServer_2.getID(), "Задачи не одинаковы");
    }

    @Test
    public void testDeleteTaskFromId() throws IOException, InterruptedException {
        Epic epicTest_1 = new Epic("TestTask_1", "Test");
        Epic epicTest_2 = new Epic("TestTask_2", "Test");

        String task_1_Json = gson.toJson(epicTest_1);
        String task_2_Json = gson.toJson(epicTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/epics");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        Epic createdTask1 = gson.fromJson(response.body(), Epic.class);
        Epic createdTask2 = gson.fromJson(response2.body(), Epic.class);

        URI urlTasks_1_WithId = URI.create("http://localHost:8080/api/epics/" + createdTask1.getID());
        URI urlTasks_2_WithId = URI.create("http://localHost:8080/api/epics/" + createdTask2.getID());

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
    public void testNotFound() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks_1_WithId = URI.create("http://localHost:8080/api/epics/" + 3);

        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(urlTasks_1_WithId)
                .GET()
                .build();
        HttpResponse<String> responseGetTask_1 = httpClient.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseGetTask_1.statusCode());
    }

    @Test
    public void testInternalServerError() throws IOException, InterruptedException {
        Epic epicTest_1 = new Epic("TestTask_1", "Test");
        Epic epicTest_2 = new Epic("TestTask_2", "Test");

        String task_1_Json = gson.toJson(epicTest_1);
        String task_2_Json = gson.toJson(epicTest_2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTasks = URI.create("http://localHost:8080/api/epics");

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

    @Test
    public void testGetSubtasksOfEpic() throws IOException, InterruptedException, NotFoundException {
        Epic epicTest_1 = new Epic("TestTask_1", "Test");
        String epic_1_Json = gson.toJson(epicTest_1);

        HttpClient httpClient = HttpClient.newHttpClient();

        URI urlEpics = URI.create("http://localHost:8080/api/epics");

        HttpRequest requestEpic_1 = HttpRequest.newBuilder().uri(urlEpics).POST(HttpRequest.BodyPublishers.
                ofString(epic_1_Json)).build();
        HttpResponse<String> responseEpic = httpClient.send(requestEpic_1, HttpResponse.BodyHandlers.ofString());

        Epic epicFromServer = gson.fromJson(responseEpic.body(), Epic.class);

        SubTask taskTest_1 = new SubTask("TestTask_1", "Test", Task.Status.NEW, epicFromServer.getID(),
                LocalDateTime.now(), Duration.ofMinutes(30));
        SubTask taskTest_2 = new SubTask("TestTask_2", "Test", Task.Status.NEW, epicFromServer.getID(),
                LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));

        String task_1_Json = gson.toJson(taskTest_1);
        String task_2_Json = gson.toJson(taskTest_2);

        URI urlTasks = URI.create("http://localHost:8080/api/subtasks");

        HttpRequest requestTask_1 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_1_Json)).build();
        HttpRequest requestTask_2 = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.
                ofString(task_2_Json)).build();
        HttpResponse<String> response = httpClient.send(requestTask_1, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response2 = httpClient.send(requestTask_2, HttpResponse.BodyHandlers.ofString());

        URI urlEpicTasks = URI.create("http://localHost:8080/api/epics/" + epicFromServer.getID() + "/subtasks");
        HttpRequest requestGetSubtasks = HttpRequest.newBuilder()
                .uri(urlEpicTasks)
                .GET()
                .build();
        HttpResponse<String> responseWithSubtasks = httpClient.send(requestGetSubtasks, HttpResponse.BodyHandlers
                .ofString());

        Type taskListType = new TypeToken<List<Integer>>() {
        }.getType();
        List<Integer> tasksFromServer = gson.fromJson(responseWithSubtasks.body(), taskListType);

        assertNotNull(tasksFromServer, "Задачи отсутствуют в менеджере задач");

        assertEquals(2, tasksFromServer.size(), "Добавлены не все задачи");
    }
}