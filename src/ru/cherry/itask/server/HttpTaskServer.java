package ru.cherry.itask.server;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import ru.cherry.itask.service.Managers;
import ru.cherry.itask.service.TaskManager;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        server = HttpServer.create(new InetSocketAddress("localHost", PORT), 0);
        server.createContext("/api/tasks", new TasksHandler(manager));
        server.createContext("/api/epics", new EpicHandler(manager));
        server.createContext("/api/subtasks", new SubTasksHandler(manager));
        server.createContext("/api/history", new HistoryHandler(manager));
        server.createContext("/api/prioritized", new PrioritizedHandler(manager));
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpServer = new HttpTaskServer();
        httpServer.start();
    }

    public void start() {
        System.out.println("Server started " + PORT);
        System.out.println("http://localHost:" + PORT + "/api");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер на порту: " + PORT + ", остановлен");
    }
}