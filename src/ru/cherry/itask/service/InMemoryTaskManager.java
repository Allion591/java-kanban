package ru.cherry.itask.service;

import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int idOfTask = 0;
    private final HashMap<Integer, Task> saveTasks = new HashMap<>();
    private final HashMap<Integer, Epic> saveEpicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> saveSubTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    //---------Счетчик всех задач---------------
    private int taskCount() {
        return idOfTask++;
    }
    //-------------------------------------------


    //----------------Получение списка всех задач.---------------○------
    @Override
    public List<Task> getAllTasksOfTask() {
        return new ArrayList<>(saveTasks.values());
    }

    @Override
    public List<Epic> getAllTasksOfEpic() {
        for (int epicKey : saveEpicTasks.keySet()) {
            checkEpicStatus(epicKey);
        }
        return new ArrayList<>(saveEpicTasks.values());
    }

    @Override
    public List<SubTask> getAllTasksOfSubTask() {
        return new ArrayList<>(saveSubTasks.values());
    }
    //------------------------------------------------------------------


    //-----------Удаление всех задач.------------------------
    @Override
    public void removeAllTasksOfTask() {
        saveTasks.clear();
    }

    @Override
    public void removeAllTasksOfEpic() {
        saveEpicTasks.clear();
    }

    @Override
    public void removeAllTasksOfSubTask() {
        saveSubTasks.clear();
        for (int epicKey : saveEpicTasks.keySet()) {
            checkEpicStatus(epicKey);
        }
    }
    //--------------------------------------------------------


    //-------------Получение по идентификатору.---------------
    @Override
    public Task getTaskById(int idTask) {
        if (saveTasks.containsKey(idTask)) {
            historyManager.add(saveTasks.get(idTask));
        }
        return saveTasks.get(idTask);
    }

    @Override
    public Epic getTaskByIdOfEpic(int idEpicTask) {
        if (saveEpicTasks.containsKey(idEpicTask)) {
            historyManager.add(saveEpicTasks.get(idEpicTask));
        }
        return saveEpicTasks.get(idEpicTask);
    }

    @Override
    public SubTask getTaskByIdOfSubTask(int idSubTask) {
        if (saveSubTasks.containsKey(idSubTask)) {
            historyManager.add(saveSubTasks.get(idSubTask));
        }
        return saveSubTasks.get(idSubTask);
    }
    //---------------------------------------------------------


    //-----------Создание задач--------------------------------
    @Override
    public void createTask(Task task) {
        int newIdForTask = taskCount();
        task.setID(newIdForTask);
        saveTasks.put(newIdForTask, task);
    }

    @Override
    public Epic createEpicTask(Epic epic) {
        int newIdForEpic = taskCount();
        epic.setID(newIdForEpic);
        saveEpicTasks.put(newIdForEpic, epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        int newIdForSubtask = taskCount();

        subTask.setID(newIdForSubtask);
        saveSubTasks.put(newIdForSubtask, subTask);
        Epic epic = saveEpicTasks.get(epicId);
        epic.addSubtaskId(newIdForSubtask);
        updateEpicTask(epic);
        checkEpicStatus(epicId);
        return subTask;
    }
    //----------------------------------------------------------------------------------------------------------


    //----------------Обновление.--------------------------
    @Override
    public void updateTask(Task task) {
        saveTasks.put(task.getID(), task);
    }

    @Override
    public void updateEpicTask(Epic epic) {
        saveEpicTasks.put(epic.getID(), epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        saveSubTasks.put(subTask.getID(), subTask);
    }

    //-----------------------------------------------------

    //-----Удаление задачи по идентификатору---------------
    @Override
    public void removeOnIdTask(int id) {
        saveTasks.remove(id);
    }

    @Override
    public void removeOnIdEpicTask(int id) {
        Epic epic = getTaskByIdOfEpic(id);
        List<Integer> removeList = epic.getSubtasksIDs();
        if (removeList.isEmpty()) {
            saveEpicTasks.remove(id);
        } else {
            for (int i : removeList) {
                saveSubTasks.remove(i);
            }
            saveEpicTasks.remove(id);
        }
    }

    @Override
    public void removeOnIdSubTask(int id) {
        List<Integer> newValueSubTasks;
        SubTask subTask = getTaskByIdOfSubTask(id);
        int numEpic = subTask.getEpicId();

        Epic epic = getTaskByIdOfEpic(numEpic);
        newValueSubTasks = epic.getSubtasksIDs();
        newValueSubTasks.remove(id);
        updateEpicTask(epic);
        checkEpicStatus(epic.getID());
        saveSubTasks.remove(id);
    }
    //----------------------------------------------------

    //------------Получение списка всех подзадач определённого эпика---------------------
    private List<Integer> getAllSubTasksFromEpic(int epicID) {
        Epic epic = saveEpicTasks.get(epicID);

        return new ArrayList<>(epic.getSubtasksIDs());
    }
    //------------------------------------------------------------------------------------

    //------------Управление статусом задачи----------------------------------------------
    @Override
    public void setNewStatusOfTask(Task task) {
        updateTask(task);
    }

    @Override
    public void setNewStatusOfSubTask(SubTask subTask) {
        updateSubTask(subTask);
        for (int epicKey : saveEpicTasks.keySet()) {
            checkEpicStatus(epicKey);
        }
    }
    //------------------------------------------------------------------------------------

    //----------------Проверка статуса эпик задачи----------------------------------------
    private void checkEpicStatus(int epicId) {
        Epic epic = saveEpicTasks.get(epicId);
        List<Integer> subTasksFromEpic = new ArrayList<>(epic.getSubtasksIDs());

        if (subTasksFromEpic.isEmpty()) {
            epic.setStatus(Task.Status.NEW);
        }

        boolean allStatusDone = true;
        boolean inProgress = false;

        for (int subtaskId : subTasksFromEpic) {
            SubTask subTask = saveSubTasks.get(subtaskId);
            Task.Status status = subTask.getStatus();
            if (status != Task.Status.DONE) {
                allStatusDone = false;
            }
            if (status == Task.Status.IN_PROGRESS) {
                inProgress = true;
            }
        }

        if (allStatusDone) {
            epic.setStatus(Task.Status.DONE);
        } else if (inProgress) {
            epic.setStatus(Task.Status.IN_PROGRESS);
        } else {
            epic.setStatus(Task.Status.NEW);
        }
    }
    //------------------------------------------------------------------------------------------

    //------------------------------История просмотренных задач---------------------------------
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}