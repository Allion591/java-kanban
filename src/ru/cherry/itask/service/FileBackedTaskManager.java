package ru.cherry.itask.service;

import ru.cherry.itask.exception.ManagerSaveException;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path dir;

    enum taskTypes {
        Task,
        Epic,
        SubTask
    }

    public FileBackedTaskManager(Path file) {
        this.dir = file;
    }

    private String toString(Task task) {
        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            return task.getID() +
                    "," + task.getClass().getSimpleName() +
                    "," + task.getTaskName() +
                    "," + task.getStatus() +
                    "," + task.getDetails() +
                    "," + subTask.getEpicId();
        }
        return task.getID() +
                "," + task.getClass().getSimpleName() +
                "," + task.getTaskName() +
                "," + task.getStatus() +
                "," + task.getDetails();
    }

    private Task fromString(String value) {
        String[] tasksElements = value.split(",");
        int oldId = Integer.parseInt(tasksElements[0]);
        taskTypes type = taskTypes.valueOf(tasksElements[1]);
        String name = tasksElements[2];
        Task.Status status = Task.Status.valueOf(tasksElements[3]);
        String details = tasksElements[4];

        switch (type) {
            case Task:
                return new Task(name, details, status);
            case Epic:
                return new Epic(name, details);
            case SubTask:
                int epicId = Integer.parseInt(tasksElements[5]);
                return new SubTask(name, details, status, epicId);
            default:
                return null;
        }
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(dir.toFile(), StandardCharsets.UTF_8);
             BufferedWriter br = new BufferedWriter(fileWriter)) {
            StringBuilder tasksToStrings = new StringBuilder();
            tasksToStrings.append("id,type,name,status,description,epic\n");

            for (Task task : getAllTasksOfTask()) {
                tasksToStrings.append(toString(task)).append("\n");
            }
            for (Epic epic : getAllTasksOfEpic()) {
                tasksToStrings.append(toString(epic)).append("\n");
            }
            for (SubTask subTask : getAllTasksOfSubTask()) {
                tasksToStrings.append(toString(subTask)).append("\n");
            }
            br.write(tasksToStrings.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8); BufferedReader br =
                new BufferedReader(reader)) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader || line.isEmpty()) {
                    isHeader = false;
                    continue;
                }

                Task task = manager.fromString(line);
                if (task != null) {
                    taskTypes type = taskTypes.valueOf(task.getClass().getSimpleName());

                    switch (type) {
                        case Task:
                            manager.createTask(task);
                            break;
                        case Epic:
                            manager.createEpicTask((Epic) task);
                            break;
                        case SubTask:
                            manager.createSubTask((SubTask) task);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        }
        return manager;
    }

    //-----------Создание задач--------------------------------
    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public Epic createEpicTask(Epic epic) {
        super.createEpicTask(epic);
        save();
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
        return subTask;
    }
    //----------------------------------------------------------------------------------------------------------

    //-----------Удаление всех задач.------------------------
    @Override
    public void removeAllTasksOfTask() {
        super.removeAllTasksOfTask();
        save();
    }

    @Override
    public void removeAllTasksOfEpic() {
        super.removeAllTasksOfEpic();
        save();
    }

    @Override
    public void removeAllTasksOfSubTask() {
        super.removeAllTasksOfSubTask();
        save();
    }
    //--------------------------------------------------------

    //----------------Обновление.--------------------------
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpicTask(Epic epic) {
        super.updateEpicTask(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }
    //-----------------------------------------------------

    //-----Удаление задачи по идентификатору---------------
    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicTaskById(int id) {
        super.removeEpicTaskById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }
    //----------------------------------------------------

    //------------Управление статусом задачи----------------------------------------------
    @Override
    public void setNewStatusOfTask(Task task) {
        super.setNewStatusOfTask(task);
        save();
    }

    @Override
    public void setNewStatusOfSubTask(SubTask subTask) {
        super.setNewStatusOfSubTask(subTask);
        save();
    }
    //------------------------------------------------------------------------------------
}