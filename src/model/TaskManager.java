package model;
import service.Epic;
import service.SubTask;
import service.Task;

import java.util.*;

public class TaskManager {
    private int idOfTask = 0;
    private final HashMap<Integer, Task> saveTasks = new HashMap<>();
    private final HashMap<Integer, Epic> saveEpicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> saveSubTasks = new HashMap<>();


    //---------Счетчик всех задач---------------
    private int taskCount() {
        return idOfTask++;
    }
    //-------------------------------------------


    //----------------Получение списка всех задач.---------------○------
    List<Task> getAllTasksOfTask() {
        List<Task> listForTasks = new ArrayList<>();
        for (Task task : saveTasks.values()) {
            listForTasks.add(task);
        }
        return listForTasks;
    }

    List<Epic> getAllTasksOfEpic() {
        for (int epicKey : saveEpicTasks.keySet()) {
            checkEpicStatus(epicKey);
        }
        List<Epic> listForEpicTasks = new ArrayList<>();
        for (Epic epic : saveEpicTasks.values()) {
            listForEpicTasks.add(epic);
        }
        return listForEpicTasks;
    }

    List<SubTask> getAllTasksOfSubTask() {
        List<SubTask> listForSubTasks = new ArrayList<>();
        for (SubTask subTask : saveSubTasks.values()) {
            listForSubTasks.add(subTask);
        }
        return listForSubTasks;
    }
    //------------------------------------------------------------------


    //-----------Удаление всех задач.------------------------
    void removeAllTasksOfTask() {
        saveTasks.clear();
    }

    void removeAllTasksOfEpic() {
        saveEpicTasks.clear();
    }

    void removeAllTasksOfSubTask() {
        saveSubTasks.clear();
        for (int epicKey : saveEpicTasks.keySet()) {
            checkEpicStatus(epicKey);
        }
    }
    //--------------------------------------------------------


    //-------------Получение по идентификатору.---------------
    Task getIdTask(int idTask) {
        return saveTasks.get(idTask);
    }

    Epic getIdTaskOfEpic(int idEpicTask) {
        return saveEpicTasks.get(idEpicTask);
    }

    SubTask getIdTaskOfSubTask(int idSubTask) {
        return saveSubTasks.get(idSubTask);
    }
    //---------------------------------------------------------


    //-----------Создание задач--------------------------------
    void createTask(Task task) {
        int newIdForTask = taskCount();
        task.setID(newIdForTask);
        saveTasks.put(newIdForTask, task);
    }

    Epic createEpicTask(Epic epic) {
        int newIdForEpic = taskCount();
        epic.setID(newIdForEpic);
        saveEpicTasks.put(newIdForEpic, epic);
        return epic;
    }

    SubTask createSubTask(SubTask subTask) {
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
    void updateTask(Task task) {
        saveTasks.put(task.getID(), task);
    }

    void updateEpicTask(Epic epic) {
        saveEpicTasks.put(epic.getID(), epic);
    }

    void updateSubTask(SubTask subTask) {
        saveSubTasks.put(subTask.getID(), subTask);
    }

    //-----------------------------------------------------

    //-----Удаление задачи по идентификатору---------------
    void removeOnIdTask(int id) {
        saveTasks.remove(id);
    }

    void removeOnIdEpicTask(int id) {
        Epic epic = getIdTaskOfEpic(id);
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

    void removeOnIdSubTask(int id) {
        List<Integer> newValueSubTasks;
        SubTask subTask = getIdTaskOfSubTask(id);
        int numEpic = subTask.getEpicId();

        Epic epic = getIdTaskOfEpic(numEpic);
        newValueSubTasks = epic.getSubtasksIDs();
        newValueSubTasks.remove(id);
        updateEpicTask(epic);
        checkEpicStatus(epic.getID());
        saveSubTasks.remove(id);
    }
    //----------------------------------------------------

    //------------Получение списка всех подзадач определённого эпика---------------------
    private List<Integer> getAllSubTasks(int epicID) {
        Epic epic = saveEpicTasks.get(epicID);

        return new ArrayList<>(epic.getSubtasksIDs());
    }
    //------------------------------------------------------------------------------------

    //------------Управление статусом задачи----------------------------------------------
    public void setNewStatusOfTask(Task task) {
        updateTask(task);
    }

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
}
//------------------------------------------------------------------------------------------