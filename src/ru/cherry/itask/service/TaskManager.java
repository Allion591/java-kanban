package ru.cherry.itask.service;

import ru.cherry.itask.exception.TimeConflictException;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.util.List;

public interface TaskManager {
    //----------------Получение списка всех задач.---------------------
    List<Task> getAllTasksOfTask();

    List<Epic> getAllTasksOfEpic();

    List<SubTask> getAllTasksOfSubTask();

    //-----------Удаление всех задач.------------------------
    void removeAllTasksOfTask();

    void removeAllTasksOfEpic();

    void removeAllTasksOfSubTask();

    //-------------Получение по идентификатору.---------------
    Task getTaskById(int idTask);

    Epic getTaskByIdOfEpic(int idEpicTask);

    SubTask getTaskByIdOfSubTask(int idSubTask);

    //-----------Создание задач--------------------------------
    void createTask(Task task) throws TimeConflictException;

    Epic createEpicTask(Epic epic);

    SubTask createSubTask(SubTask subTask);

    //----------------Обновление.--------------------------
    void updateTask(Task task);

    void updateEpicTask(Epic epic);

    void updateSubTask(SubTask subTask);

    //-----Удаление задачи по идентификатору---------------
    void removeTaskById(int id);

    void removeEpicTaskById(int id);

    void removeSubTaskById(int id);

    //------------Управление статусом задачи----------------------------------------------
    void setNewStatusOfTask(Task task);

    void setNewStatusOfSubTask(SubTask subTask);

    //------------Вызов истории просмотров---------------------------------------
    List<Task> getHistory();
}