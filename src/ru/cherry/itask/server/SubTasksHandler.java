package ru.cherry.itask.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.cherry.itask.exception.NotFoundException;
import ru.cherry.itask.exception.TimeConflictException;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.service.InMemoryTaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class SubTasksHandler extends BaseHttpHandler implements HttpHandler {
    private final InMemoryTaskManager inMemoryTaskManager;

    SubTasksHandler(InMemoryTaskManager inMemoryTaskManager) {
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
                    if (Pattern.matches("^/api/subtasks$", path)) {
                        String response = gson.toJson(inMemoryTaskManager.getAllTasksOfSubTask());
                        sendText(exchange, response);
                        return;
                    }
                    if (Pattern.matches("^/api/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(inMemoryTaskManager.getTaskByIdOfSubTask(id));
                            sendText(exchange, response);
                        }
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/api/subtasks$", path)) {
                        String jsonString = readText(exchange);
                        SubTask subTask = gson.fromJson(jsonString, SubTask.class);
                        if (inMemoryTaskManager.taskIntersection(subTask)) {
                            throw new TimeConflictException("");
                        } else {
                            String response = gson.toJson(inMemoryTaskManager.createSubTask(subTask));
                            inMemoryTaskManager.setStartTimeAndDurationForEpic(subTask.getEpicId());
                            send201Response(exchange, response);
                            return;
                        }
                    } else if (Pattern.matches("^/api/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String jsonString = readText(exchange);
                            SubTask subTask = gson.fromJson(jsonString, SubTask.class);
                            inMemoryTaskManager.updateSubTask(subTask);
                            inMemoryTaskManager.setStartTimeAndDurationForEpic(subTask.getEpicId());
                            exchange.sendResponseHeaders(201, 0);
                        }
                    }
                }
                break;
                case "DELETE": {
                    if (Pattern.matches("^/api/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            inMemoryTaskManager.removeSubTaskById(id);
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

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }

    private String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}