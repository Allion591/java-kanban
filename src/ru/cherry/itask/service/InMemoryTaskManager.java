package ru.cherry.itask.service;

import ru.cherry.itask.exception.NotFoundException;
import ru.cherry.itask.exception.TimeConflictException;
import ru.cherry.itask.model.Epic;
import ru.cherry.itask.model.SubTask;
import ru.cherry.itask.model.Task;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Managers managers = new Managers();
    private int idOfTask = 0;
    private final HashMap<Integer, Task> saveTasks = new HashMap<>();
    private final HashMap<Integer, Epic> saveEpicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> saveSubTasks = new HashMap<>();
    protected final HistoryManager historyManager = managers.getDefaultHistory();


    //---------Счетчик всех задач---------------
    private int taskCount() {
        return idOfTask++;
    }
    //-------------------------------------------


    //----------------Получение списка всех задач.---------------○------
    @Override
    public List<Task> getAllTasksOfTask() throws NotFoundException {
        if (!saveTasks.isEmpty()) {
            return saveTasks.values().stream()
                    .peek(historyManager::add)
                    .toList();
        } else {
            throw new NotFoundException("");
        }
    }

    @Override
    public List<Epic> getAllTasksOfEpic() throws NotFoundException {
        if (!saveEpicTasks.isEmpty()) {
            return saveEpicTasks.values().stream()
                    .peek(historyManager::add)
                    .peek(epic -> checkEpicStatus(epic.getID()))
                    .toList();
        } else {
            throw new NotFoundException("");
        }
    }

    @Override
    public List<SubTask> getAllTasksOfSubTask() throws NotFoundException {
        if (!saveEpicTasks.isEmpty()) {
            return saveSubTasks.values().stream()
                    .peek(historyManager::add)
                    .toList();
        } else {
            throw new NotFoundException("Нет задач");
        }
    }
    //------------------------------------------------------------------


    //-----------Удаление всех задач.------------------------
    @Override
    public void removeAllTasksOfTask() throws NotFoundException {
        saveTasks.values().stream()
                .map(Task::getID)
                .forEach(historyManager::remove);
        saveTasks.clear();
    }

    @Override
    public void removeAllTasksOfEpic() throws NotFoundException {
        saveEpicTasks.values().stream()
                .map(Epic::getID)
                .forEach(historyManager::remove);
        saveSubTasks.values().stream()
                .map(SubTask::getID)
                .forEach(historyManager::remove);
        saveEpicTasks.clear();
        saveSubTasks.clear();
    }

    @Override
    public void removeAllTasksOfSubTask() throws NotFoundException {
        saveSubTasks.values().stream()
                .map(SubTask::getID)
                .forEach(historyManager::remove);
        saveSubTasks.clear();
        saveEpicTasks.values()
                .forEach(epic -> epic.getSubtasksIDs().clear());
    }
    //--------------------------------------------------------


    //-------------Получение по идентификатору.---------------
    @Override
    public Task getTaskById(int idTask) throws NotFoundException {
        if (saveTasks.containsKey(idTask)) {
            historyManager.add(saveTasks.get(idTask));
            return saveTasks.get(idTask);
        } else {
            throw new NotFoundException("");
        }
    }

    @Override
    public Epic getTaskByIdOfEpic(int idEpicTask) throws NotFoundException {
        if (saveEpicTasks.containsKey(idEpicTask)) {
            historyManager.add(saveEpicTasks.get(idEpicTask));
            return saveEpicTasks.get(idEpicTask);
        } else {
            throw new NotFoundException("");
        }
    }

    @Override
    public SubTask getTaskByIdOfSubTask(int idSubTask) throws NotFoundException {
        if (saveSubTasks.containsKey(idSubTask)) {
            historyManager.add(saveSubTasks.get(idSubTask));
            return saveSubTasks.get(idSubTask);
        } else {
            throw new NotFoundException("");
        }

    }
    //---------------------------------------------------------


    //-----------Создание задач--------------------------------
    @Override
    public Task createTask(Task task) throws TimeConflictException, NotFoundException {
        int newIdForTask = taskCount();
        task.setID(newIdForTask);
        saveTasks.put(newIdForTask, task);
        return task;
    }

    @Override
    public Epic createEpicTask(Epic epic) throws NotFoundException {
        int newIdForEpic = taskCount();
        epic.setID(newIdForEpic);
        saveEpicTasks.put(newIdForEpic, epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) throws NotFoundException {
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
    public void updateTask(Task task) throws NotFoundException {
        saveTasks.put(task.getID(), task);
    }

    @Override
    public void updateEpicTask(Epic epic) throws NotFoundException {
        if (saveEpicTasks.containsKey(epic.getID())) {
            saveEpicTasks.put(epic.getID(), epic);
        } else {
            throw new NotFoundException("");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) throws NotFoundException {
        saveSubTasks.put(subTask.getID(), subTask);
    }

    //-----------------------------------------------------

    //-----Удаление задачи по идентификатору---------------
    @Override
    public void removeTaskById(int id) throws NotFoundException {
        if (saveTasks.containsKey(id)) {
            saveTasks.remove(id);
            historyManager.remove(id);
        } else {
            throw new NotFoundException("Задачи с ID : " + id + " нет.");
        }
    }

    @Override
    public void removeEpicTaskById(int id) throws NotFoundException {
        Epic epic = getTaskByIdOfEpic(id);
        if (epic != null) {
            List<Integer> removeList = epic.getSubtasksIDs();
            removeList.stream()
                    .peek(saveSubTasks::remove)
                    .forEach(historyManager::remove);
            saveEpicTasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubTaskById(int id) throws NotFoundException {
        if (saveSubTasks.containsKey(id)) {
            SubTask subTask = saveSubTasks.remove(id);
            if (subTask != null) {
                historyManager.remove(id);
                Epic epic = saveEpicTasks.get(subTask.getEpicId());
                if (epic != null) {
                    epic.removeSubtaskId(id);
                    checkEpicStatus(epic.getID());
                }
            }
        } else {
            throw new NotFoundException("Задачи с ID : " + id + " нет.");
        }
    }
    //----------------------------------------------------

    //------------Получение списка всех подзадач определённого эпика---------------------
    public List<Integer> getAllSubTasksFromEpic(int epicID) throws NotFoundException {
        Epic epic = saveEpicTasks.get(epicID);
        if (epic.getSubtasksIDs() != null) {
            return new ArrayList<>(epic.getSubtasksIDs());
        } else {
            throw new NotFoundException("");
        }
    }
    //------------------------------------------------------------------------------------

    //------------Управление статусом задачи----------------------------------------------
    @Override
    public void setNewStatusOfTask(Task task) throws NotFoundException {
        updateTask(task);
    }

    @Override
    public void setNewStatusOfSubTask(SubTask subTask) throws NotFoundException {
        updateSubTask(subTask);
        saveEpicTasks.values().stream()
                .map(Epic::getID)
                .forEach(this::checkEpicStatus);
    }
    //------------------------------------------------------------------------------------

    //----------------Проверка статуса эпик задачи----------------------------------------
    public void checkEpicStatus(int epicId) {
        Epic epic = saveEpicTasks.get(epicId);
        List<Integer> subTasksFromEpic = new ArrayList<>(epic.getSubtasksIDs());

        if (subTasksFromEpic.isEmpty()) {
            epic.setStatus(Task.Status.NEW);
        }

        boolean allStatusDone = true;
        boolean inProgress = false;

        for (int subtaskId : subTasksFromEpic) {
            SubTask subTask = saveSubTasks.get(subtaskId);
            if (subTask != null) {
                Task.Status status = subTask.getStatus();
                if (status != Task.Status.DONE) {
                    allStatusDone = false;
                }
                if (status == Task.Status.IN_PROGRESS) {
                    inProgress = true;
                }
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

    //----------------Установка времени старта и длительности эпик задачи----------------------------------------
    public void setStartTimeAndDurationForEpic(int epicId) {
        Epic epic = saveEpicTasks.get(epicId);
        List<Integer> subTasksFromEpic = new ArrayList<>(epic.getSubtasksIDs());
        if (subTasksFromEpic.isEmpty()) {
            return;
        } else {
            List<SubTask> subTasks = new ArrayList<>();

            for (int ids : subTasksFromEpic) {
                subTasks.add(saveSubTasks.get(ids));
            }
            epic.setStartTime(subTasks.stream()
                    .min(Comparator.comparing(SubTask::getStartTime))
                    .map(SubTask::getStartTime)
                    .orElse(null)
            );
            epic.setDurationTask(subTasks.stream()
                    .map(SubTask::getDurationTask)
                    .reduce(Duration.ZERO, Duration::plus)
            );
            epic.setEndTime(subTasks.stream()
                    .max(Comparator.comparing(SubTask::getStartTime))
                    .map(SubTask::getEndTime)
                    .orElse(null)
            );
        }
    }
    //---------------------------------------------------------------------------------------------

    //------------------------------История просмотренных задач------------------------------------
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
    //--------------------------------------------------------------------------------------------

    //------------------------------Список задач и подзадач в заданном порядке--------------------
    public TreeSet<Task> getPrioritizedTasks() {
        Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime)
                .thenComparing(Task::getID);

        TreeSet<Task> tasksSortedByDate = new TreeSet<>(taskComparator);

        saveTasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(tasksSortedByDate::add);
        saveSubTasks.values().stream()
                .filter(subTask -> subTask.getStartTime() != null)
                .forEach(tasksSortedByDate::add);
        return tasksSortedByDate;
    }
    //---------------------------------------------------------------------------------------------

    //----------------------------Пересекаются ли две задачи---------------------------------------
    public boolean taskIntersection(Task newTask) throws TimeConflictException {
        boolean isIntersection = getPrioritizedTasks().stream()
                .anyMatch(task -> task.isOverlap(newTask));
        if (isIntersection) {
            throw new TimeConflictException("");
        }
        return false;
    }
    //---------------------------------------------------------------------------------------------
}