package ru.cherry.itask.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.cherry.itask.exception.NotFoundException;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager inMemoryTaskManager;

    EpicHandler(TaskManager inMemoryTaskManager) {
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
                    if (Pattern.matches("^/api/epics$", path)) {
                        String response = gson.toJson(inMemoryTaskManager.getAllTasksOfEpic());
                        sendText(exchange, response);
                        return;
                    }
                    if (Pattern.matches("^/api/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(inMemoryTaskManager.getTaskByIdOfEpic(id));
                            sendText(exchange, response);
                        }
                    }
                    if (Pattern.matches("^/api/epics/\\d+/subtasks$", path)) {
                        String[] parts = path.split("/");
                        int id = parsePathId(parts[3]);
                        if (id != -1) {
                            String response = gson.toJson(inMemoryTaskManager.getAllSubTasksFromEpic(id));
                            sendText(exchange, response);
                        }
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/api/epics$", path)) {
                        String jsonString = readText(exchange);
                        Epic epic = gson.fromJson(jsonString, Epic.class);
                        String response = gson.toJson(inMemoryTaskManager.createEpicTask(epic));
                        send201Response(exchange, response);
                        return;
                    } else if (Pattern.matches("^/api/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String jsonString = readText(exchange);
                            Epic epic = gson.fromJson(jsonString, Epic.class);
                            inMemoryTaskManager.updateEpicTask(epic);
                            exchange.sendResponseHeaders(201, 0);
                        }
                    }
                }
                break;
                case "DELETE": {
                    if (Pattern.matches("^/api/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            inMemoryTaskManager.removeEpicTaskById(id);
                            sendText(exchange, "Задача эпик успешно удалена");
                        }
                    }
                    break;
                }
                default: {
                    throw new Exception();
                }
            }
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