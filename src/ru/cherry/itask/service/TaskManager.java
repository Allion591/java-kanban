package ru.cherry.itask.service;

import ru.cherry.itask.exception.NotFoundException;
import ru.cherry.itask.exception.TimeConflictException;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.util.List;

public interface TaskManager {
    //----------------Получение списка всех задач.---------------------
    List<Task> getAllTasksOfTask() throws NotFoundException;

    List<Epic> getAllTasksOfEpic() throws NotFoundException;

    List<SubTask> getAllTasksOfSubTask() throws NotFoundException;

    //-----------Удаление всех задач.------------------------
    void removeAllTasksOfTask() throws NotFoundException;

    void removeAllTasksOfEpic() throws NotFoundException;

    void removeAllTasksOfSubTask() throws NotFoundException;

    //-------------Получение по идентификатору.---------------
    Task getTaskById(int idTask) throws NotFoundException;

    Epic getTaskByIdOfEpic(int idEpicTask) throws NotFoundException;

    SubTask getTaskByIdOfSubTask(int idSubTask) throws NotFoundException;

    //-----------Создание задач--------------------------------
    Task createTask(Task task) throws TimeConflictException, NotFoundException;

    Epic createEpicTask(Epic epic) throws NotFoundException;

    SubTask createSubTask(SubTask subTask) throws NotFoundException;

    //----------------Обновление.--------------------------
    void updateTask(Task task) throws NotFoundException;

    void updateEpicTask(Epic epic) throws NotFoundException;

    void updateSubTask(SubTask subTask) throws NotFoundException;

    //-----Удаление задачи по идентификатору---------------
    void removeTaskById(int id) throws NotFoundException;

    void removeEpicTaskById(int id) throws NotFoundException;

    void removeSubTaskById(int id) throws NotFoundException;

    //------------Управление статусом задачи----------------------------------------------
    void setNewStatusOfTask(Task task) throws NotFoundException;

    void setNewStatusOfSubTask(SubTask subTask) throws NotFoundException;

    //------------Вызов истории просмотров---------------------------------------
    List<Task> getHistory();
}