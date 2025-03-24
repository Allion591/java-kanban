package Model;
import Service.Epic;
import Service.SubTask;
import Service.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TaskManager {
    private static final List<Integer> newIdOfSubTask = new ArrayList<>();
    private static int idOfTask = 0;
    public static HashMap<Integer, Task> saveTasks = new HashMap<>();
    public static HashMap<Integer, Epic> saveEpicTasks = new HashMap<>();
    public static HashMap<Integer, SubTask> saveSubTasks = new HashMap<>();
    private static int epicID;
    static List<Task.Status> status = new ArrayList<>();
    static List<SubTask> getSubTasks = new ArrayList<>();;
    static List<Integer> listOfnumberOfTask = new LinkedList<>();

    //---------Счетчик всех задач---------------
    private static void taskCount() {
        idOfTask++;
    }
    //-------------------------------------------


    //----------------Получение списка всех задач.---------------○------
    public static List<Task> getAllTasksOfTask() {
        List<Task> listForTasks = new ArrayList<>();
        for (Task task: saveTasks.values()){
            listForTasks.add(task);
        }
        return listForTasks;
    }

    public static List<Epic> getAllTasksOfEpic() {
        List<Epic> listForEpicTasks = new ArrayList<>();
        for (int epicId : saveEpicTasks.keySet()) {
            checkEpicStatus(epicId);
        } for (Epic epic: saveEpicTasks.values()) {
            listForEpicTasks.add(epic);
        }
        return listForEpicTasks;
    }

    public static List<SubTask> getAllTasksOfSubTask() {
        List<SubTask> listForSubTasks = new ArrayList<>();
        for (SubTask subTask: saveSubTasks.values()) {
            listForSubTasks.add(subTask);
        }
        return listForSubTasks;
    }
    //------------------------------------------------------------------


    //-----------Удаление всех задач.------------------------
    public static void removeAllTasksOfTask() {
        saveTasks.clear();
    }

    public static void removeAllTasksOfEpic() {
        saveEpicTasks.clear();
    }

    public static void removeAllTasksOfSubTask() {
        saveSubTasks.clear();
        for (int epicId : saveEpicTasks.keySet()) {
            checkEpicStatus(epicId);
        }
    }
    //--------------------------------------------------------


    //-------------Получение по идентификатору.---------------
    public static Task getIdTask(int idTask) {
        return saveTasks.get(idTask);
    }

    public static Epic getIdTaskOfEpic(int idEpicTask) {
        for (int epicId : saveEpicTasks.keySet()) {
            checkEpicStatus(epicId);
        }
        return saveEpicTasks.get(idEpicTask);
    }

    public static SubTask getIdTaskOfSubTask(int idSubTask) {
        return saveSubTasks.get(idSubTask);
    }
    //---------------------------------------------------------


    //-----------Создание задач--------------------------------
    public static void createTask(String taskName, String details, Task.Status status) {
        Task task = new Task(idOfTask, taskName, details, status);
        saveTasks.put(idOfTask, task);
        taskCount();
    }

    public static void createEpicTask(String taskName, String epicDetails) {
        Epic epic = new Epic(epicID, taskName, epicDetails, null, newIdOfSubTask);
        saveEpicTasks.put(epicID, epic);
        checkEpicStatus(epicID);
        taskCount();
        newIdOfSubTask.clear();
    }

    public static int saveEpicId() {
        epicID = idOfTask;
        return epicID;
    }

    public static void createSubTask(String subTasksName, String subTaskDetails, Task.Status newStatus) {
        taskCount();
        SubTask subTask = new SubTask(epicID, idOfTask, subTasksName, subTaskDetails, newStatus);
        newIdOfSubTask.add(idOfTask);
        saveSubTasks.put(idOfTask, subTask);

    }
    //----------------------------------------------------------------------------------------------------------


    //----------------Обновление.--------------------------
    public static void updateTask(Task task) {
        saveTasks.put(task.getId(), task);
    }

    public static void updateEpicTask(Epic epic) {
        saveEpicTasks.put(epic.getId(), epic);
        checkEpicStatus(epic.getId());
    }

    public static void updateSubTask(SubTask subTask) {
        saveSubTasks.put(subTask.getId(), subTask);
        for (int epicId : saveEpicTasks.keySet()) {
            checkEpicStatus(epicId);
        }
    }
    //-----------------------------------------------------

    //-----Удаление задачи по идентификатору---------------
    public static void removeOnIdTask(int id) {
        saveTasks.remove(id);
    }

    public static void removeOnIdEpicTask(int id) {
        getAllSubTasks(id);
        if (listOfnumberOfTask.isEmpty()) {
            saveEpicTasks.remove(id);
        } else {
            for (int i: listOfnumberOfTask) {
                saveSubTasks.remove(i);
            }
            saveEpicTasks.remove(id);
        }
        listOfnumberOfTask.clear();
    }

    public static void removeOnIdSubTask(int id) {
        SubTask subTask = getIdTaskOfSubTask(id);
        int numEpic = subTask.getEpicId();
        List<Integer> newValueSubTasks;

        Epic epic = getIdTaskOfEpic(numEpic);
        newValueSubTasks = epic.getSubTasksId();
        newValueSubTasks.remove(id);
        updateEpicTask(epic);
        saveSubTasks.remove(id);
    }
    //----------------------------------------------------

    //------------Получение списка всех подзадач определённого эпика---------------------
    public static List<Integer> getAllSubTasks(int epicID) {
        Epic epic = saveEpicTasks.get(epicID);
        List<Integer> forCopyIdTasks;
        forCopyIdTasks = epic.getSubTasksId();
        for (int i: forCopyIdTasks){
            listOfnumberOfTask.add(i);
        }
        return listOfnumberOfTask;
    }
    //------------------------------------------------------------------------------------

    //------------Управление статусом задачи----------------------------------------------
    public static void setNewStatusOfTask(int taskId, Task.Status status) {
        Task task = getIdTask(taskId);
        task.setStatus(status);
        updateTask(task);
    }

    public static void setNewStatusOfSubTask(int subTaskId, Task.Status status) {
        SubTask subTask = getIdTaskOfSubTask(subTaskId);
        subTask.setStatus(status);
        updateSubTask(subTask);
        checkEpicStatus(subTask.getEpicId());
    }
    //------------------------------------------------------------------------------------

    //----------------Проверка статуса эпик задачи----------------------------------------
    public static void checkEpicStatus(int epicId) {
        getAllSubTasks(epicId);
        Epic epic = saveEpicTasks.get(epicId);

        if (listOfnumberOfTask.isEmpty()) {
            epic.setStatus(Task.Status.NEW);
        } else {
            for (int i: listOfnumberOfTask){
                getSubTasks.add(saveSubTasks.get(i));
            } for (SubTask subTasks : getSubTasks) {
                if (subTasks == null){
                    return;
                } else {
                    status.add(subTasks.getStatus());
                }
            } if (!status.contains(Task.Status.IN_PROGRESS) && !status.contains(Task.Status.DONE)) {
                epic.setStatus(Task.Status.NEW);
            } else if (!status.contains(Task.Status.IN_PROGRESS) && !status.contains(Task.Status.NEW)) {
                epic.setStatus(Task.Status.DONE);
            } else {
                epic.setStatus(Task.Status.IN_PROGRESS);
            }
        }
        listOfnumberOfTask.clear();
        getSubTasks.clear();
        status.clear();
    }
}
//--------------------------------------------------------------------------------------------