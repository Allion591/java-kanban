package ru.cherry.itask.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;
import ru.cherry.itask.service.InMemoryTaskManager;
import ru.cherry.itask.service.Managers;

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

public class HttpHistoryServerTest {
    private HttpTaskServer httpTaskServer;
    private final Gson gson = HistoryHandler.getGson();
    private InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    void setUp() throws Exception {
        Managers managers = new Managers();
        httpTaskServer = new HttpTaskServer(managers);
        inMemoryTaskManager = managers.getDefault();
        inMemoryTaskManager.removeAllTasksOfTask();
        httpTaskServer.start();
    }

    @AfterEach
    void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testGetHistoryTasks() throws IOException, InterruptedException {
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


        Epic epicTest_1 = new Epic("TestTask_1", "Test");
        Epic epicTest_2 = new Epic("TestTask_2", "Test");

        String epic_1_Json = gson.toJson(epicTest_1);
        String epic_2_Json = gson.toJson(epicTest_2);

        URI urlEpic = URI.create("http://localHost:8080/api/epics");

        HttpRequest requestEpic_1 = HttpRequest.newBuilder().uri(urlEpic).POST(HttpRequest.BodyPublishers.
                ofString(epic_1_Json)).build();
        HttpRequest requestEpic_2 = HttpRequest.newBuilder().uri(urlEpic).POST(HttpRequest.BodyPublishers.
                ofString(epic_2_Json)).build();

        HttpResponse<String> responseEpic_1 = httpClient.send(requestEpic_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseEpic_2 = httpClient.send(requestEpic_2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseEpic_1.statusCode(), "Задача не получена");
        assertEquals(201, responseEpic_2.statusCode(), "Задача не получена");


        HttpRequest requestGetEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .GET()
                .build();
        HttpResponse<String> responseEpic = httpClient.send(requestGetEpic, HttpResponse.BodyHandlers.ofString());

        Type epicListType = new TypeToken<List<Epic>>() {
        }.getType();

        List<Epic> epicFromServer = gson.fromJson(responseEpic.body(), epicListType);


        Epic epicTest_3 = new Epic("TestTask_1", "Test");
        String epic_3_Json = gson.toJson(epicTest_3);

        HttpRequest requestEpic_3 = HttpRequest.newBuilder().uri(urlEpic).POST(HttpRequest.BodyPublishers.
                ofString(epic_3_Json)).build();
        HttpResponse<String> responseEpic_3 = httpClient.send(requestEpic_3, HttpResponse.BodyHandlers.ofString());

        Epic epicFromServer_3 = gson.fromJson(responseEpic_3.body(), Epic.class);

        SubTask subTaskTest_1 = new SubTask("TestTask_1", "Test", Task.Status.NEW, epicFromServer_3
                .getID(),
                LocalDateTime.now().plusDays(2), Duration.ofMinutes(30));
        SubTask subTaskTest_2 = new SubTask("TestTask_2", "Test", Task.Status.NEW, epicFromServer_3
                .getID(),
                LocalDateTime.now().plusDays(3), Duration.ofMinutes(30));

        String subTask_1_Json = gson.toJson(subTaskTest_1);
        String subTask_2_Json = gson.toJson(subTaskTest_2);

        URI urlSubTasks = URI.create("http://localHost:8080/api/subtasks");

        HttpRequest requestSubTask_1 = HttpRequest.newBuilder().uri(urlSubTasks).POST(HttpRequest.BodyPublishers.
                ofString(subTask_1_Json)).build();
        HttpRequest requestSubTask_2 = HttpRequest.newBuilder().uri(urlSubTasks).POST(HttpRequest.BodyPublishers.
                ofString(subTask_2_Json)).build();
        HttpResponse<String> responseSubtask_1 = httpClient.send(requestSubTask_1, HttpResponse.BodyHandlers
                .ofString());

        HttpResponse<String> responseSubtask_2 = httpClient.send(requestSubTask_2, HttpResponse.BodyHandlers
                .ofString());

        assertEquals(201, responseSubtask_1.statusCode(), "Задача не получена");
        assertEquals(201, responseSubtask_2.statusCode(), "Задача не получена");


        HttpRequest requestGetSubtasks = HttpRequest.newBuilder()
                .uri(urlSubTasks)
                .GET()
                .build();
        HttpResponse<String> responseGetSubtasks = httpClient.send(requestGetSubtasks, HttpResponse.BodyHandlers
                .ofString());

        Type subTaskListType = new TypeToken<List<SubTask>>() {
        }.getType();
        List<SubTask> subTasksFromServer = gson.fromJson(responseGetSubtasks.body(), subTaskListType);


        URI urlHistory = URI.create("http://localHost:8080/api/history");

        HttpRequest requestHistory = HttpRequest.newBuilder()
                .uri(urlHistory)
                .GET()
                .build();
        HttpResponse<String> responseHistory = httpClient.send(requestHistory, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksFromServerHistory = gson.fromJson(responseHistory.body(), taskListType);

        assertEquals(tasksFromServer.size() + epicFromServer.size() + subTasksFromServer.size(),
                tasksFromServerHistory.size(), "Нет задач в истории");
    }
}