package ru.cherry.itask.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.cherry.itask.exception.NotFoundException;
import ru.cherry.itask.exception.TimeConflictException;
import ru.cherry.itask.model.Task;
import ru.cherry.itask.service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager inMemoryTaskManager;

    TasksHandler(TaskManager inMemoryTaskManager) {
        this.inMemoryTaskManager = inMemoryTaskManager;
    }

    private static final Gson gson = getGson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    if (Pattern.matches("^/api/tasks$", path)) {
                        String response = gson.toJson(inMemoryTaskManager.getAllTasksOfTask());
                        sendText(exchange, response);
                        return;
                    }
                    if (Pattern.matches("^/api/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/tasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(inMemoryTaskManager.getTaskById(id));
                            sendText(exchange, response);
                        }
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/api/tasks$", path)) {
                        String jsonString = readText(exchange);
                        Task task = gson.fromJson(jsonString, Task.class);
                        if (inMemoryTaskManager.taskIntersection(task)) {
                            throw new TimeConflictException("");
                        } else {
                            String response = gson.toJson(inMemoryTaskManager.createTask(task));
                            send201Response(exchange, response);
                            return;
                        }
                    } else if (Pattern.matches("^/api/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/tasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String jsonString = readText(exchange);
                            Task task = gson.fromJson(jsonString, Task.class);
                            inMemoryTaskManager.updateTask(task);
                            exchange.sendResponseHeaders(201, 0);
                        }
                    }
                }
                break;
                case "DELETE": {
                    if (Pattern.matches("^/api/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/tasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            inMemoryTaskManager.removeTaskById(id);
                            sendText(exchange, "Задача успешно удалена");
                        }
                    }
                    break;
                }
                default: {
                    throw new Exception();
                }
            }
        } catch (TimeConflictException e) {
            sendHasInteractions(exchange, "Not Acceptable");
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Not Found");
        } catch (Exception e) {
            sendInternalServerError(exchange, "Internal Server Error");
        } finally {
            exchange.close();
        }
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}