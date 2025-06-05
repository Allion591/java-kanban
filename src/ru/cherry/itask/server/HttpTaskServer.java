package ru.cherry.itask.server;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import ru.cherry.itask.service.Managers;

public class HttpTaskServer {
    private static final int PORT = 8080;
    HttpServer server;
    static Managers managers = new Managers();

    public HttpTaskServer(Managers managers) throws IOException {
        HttpTaskServer.managers = managers;
        server = HttpServer.create(new InetSocketAddress("localHost", PORT), 0);
        server.createContext("/api/tasks", new TasksHandler(managers.getDefault()));
        server.createContext("/api/epics", new EpicHandler(managers.getDefault()));
        server.createContext("/api/subtasks", new SubTasksHandler(managers.getDefault()));
        server.createContext("/api/history", new HistoryHandler(managers.getDefault()));
        server.createContext("/api/prioritized", new PrioritizedHandler(managers.getDefault()));
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpServer = new HttpTaskServer(managers);
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