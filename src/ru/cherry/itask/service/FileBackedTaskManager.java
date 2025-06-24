package ru.cherry.itask.service;

import ru.cherry.itask.exception.ManagerSaveException;
import ru.cherry.itask.exception.NotFoundException;
import ru.cherry.itask.exception.TimeConflictException;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path dir;

    public FileBackedTaskManager(Path file) {
        this.dir = file;
    }

    private String toString(Task task) {
        return task.toCsv();
    }

    private Task fromString(String value) {
        String[] tasksElements = value.split(",", -1);
        if (tasksElements[0].equals("History")) {
            return null;
        }
        return Task.fromCsv(value);
    }

    private void save() throws NotFoundException {
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
            br.write("History" + historyToCsv());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла", e);
        } catch (NotFoundException e) {
            return;
        }
    }

    private String historyToCsv() {
        StringBuilder tasksIds = new StringBuilder();

        for (Task task : getHistory()) {
            tasksIds.append("," + task.getID());
        }
        return tasksIds.toString();
    }

    private void restoreHistory(String line) throws NotFoundException {
        String[] idsElements = line.split(",", -1);
        boolean isFirst = true;
        int id;
        for (String elementId : idsElements) {
            if (isFirst) {
                isFirst = false;
                continue;
            }
            id = Integer.parseInt(elementId);
            Task task = getTaskById(id);
            if (task != null) {
                historyManager.add(task);
            } else {
                task = getTaskByIdOfEpic(id);
                if (task != null) {
                    historyManager.add(task);
                } else {
                    task = getTaskByIdOfSubTask(id);
                    if (task != null) {
                        historyManager.add(task);
                    } else {
                        return;
                    }
                }
            }
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
                    Task.TaskTypes type = task.getTaskTypes();
                    switch (type) {
                        case TASK:
                            manager.createTask(task);
                            break;
                        case EPIC:
                            manager.createEpicTask((Epic) task);
                            break;
                        case SUBTASK:
                            manager.createSubTask((SubTask) task);
                            break;
                    }
                } else {
                    manager.restoreHistory(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        } catch (TimeConflictException | NotFoundException e) {
            System.out.println(e.getMessage());
        }
        return manager;
    }

    //-----------Создание задач--------------------------------
    @Override
    public Task createTask(Task task) throws TimeConflictException, NotFoundException {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpicTask(Epic epic) throws NotFoundException {
        super.createEpicTask(epic);
        save();
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) throws NotFoundException {
        super.createSubTask(subTask);
        save();
        return subTask;
    }
    //----------------------------------------------------------------------------------------------------------

    //-----------Удаление всех задач.------------------------
    @Override
    public void removeAllTasksOfTask() throws NotFoundException {
        super.removeAllTasksOfTask();
        save();
    }

    @Override
    public void removeAllTasksOfEpic() throws NotFoundException {
        super.removeAllTasksOfEpic();
        save();
    }

    @Override
    public void removeAllTasksOfSubTask() throws NotFoundException {
        super.removeAllTasksOfSubTask();
        save();
    }
    //--------------------------------------------------------

    //----------------Обновление.--------------------------
    @Override
    public void updateTask(Task task) throws NotFoundException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpicTask(Epic epic) throws NotFoundException {
        super.updateEpicTask(epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) throws NotFoundException {
        super.updateSubTask(subTask);
        save();
    }
    //-----------------------------------------------------

    //-----Удаление задачи по идентификатору---------------
    @Override
    public void removeTaskById(int id) throws NotFoundException {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicTaskById(int id) throws NotFoundException {
        super.removeEpicTaskById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) throws NotFoundException {
        super.removeSubTaskById(id);
        save();
    }
    //----------------------------------------------------

    //------------Управление статусом задачи----------------------------------------------
    @Override
    public void setNewStatusOfTask(Task task) throws NotFoundException {
        super.setNewStatusOfTask(task);
        save();
    }

    @Override
    public void setNewStatusOfSubTask(SubTask subTask) throws NotFoundException {
        super.setNewStatusOfSubTask(subTask);
        save();
    }
    //------------------------------------------------------------------------------------

    //-------------Получение по идентификатору.---------------
    @Override
    public Task getTaskById(int idTask) throws NotFoundException {
        return super.getTaskById(idTask);
    }

    @Override
    public Epic getTaskByIdOfEpic(int idEpicTask) throws NotFoundException {
        return super.getTaskByIdOfEpic(idEpicTask);
    }

    @Override
    public SubTask getTaskByIdOfSubTask(int idSubTask) throws NotFoundException {
        return super.getTaskByIdOfSubTask(idSubTask);
    }
    //------------------------------------------------------------------------------------

    //----------------Получение списка всех задач.---------------○------
    @Override
    public List<Task> getAllTasksOfTask() throws NotFoundException {
        return super.getAllTasksOfTask();
    }

    @Override
    public List<Epic> getAllTasksOfEpic() throws NotFoundException {
        return super.getAllTasksOfEpic();
    }

    @Override
    public List<SubTask> getAllTasksOfSubTask() throws NotFoundException {
        return super.getAllTasksOfSubTask();
    }
    //------------------------------------------------------------------
}