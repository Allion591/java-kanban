package ru.cherry.itask.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.cherry.itask.service.InMemoryTaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final InMemoryTaskManager inMemoryTaskManager;

    HistoryHandler(InMemoryTaskManager inMemoryTaskManager) {
        this.inMemoryTaskManager = inMemoryTaskManager;
    }

    private static final Gson gson = getGson();

    @Override
    protected void sendText(HttpExchange exchange, String text) throws IOException {
        super.sendText(exchange, text);
    }

    @Override
    public void sendNotFound(HttpExchange exchange, String text) throws IOException {
        super.sendNotFound(exchange, text);
    }

    @Override
    public void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        super.sendHasInteractions(exchange, text);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equals("GET")) {
                if (Pattern.matches("^/api/history$", path)) {
                    String response = gson.toJson(inMemoryTaskManager.getHistory());
                    sendText(exchange, response);
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            sendInternalServerError(exchange, "Internal Server Error");
        } finally {
            exchange.close();
        }
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}