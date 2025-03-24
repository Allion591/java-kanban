package Model;

import Service.Epic;
import Service.SubTask;
import Service.Task;

import  java.util.Scanner;

import static Service.Task.Status.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static int removeId;
    static Task.Status status;
    static int idTask;

    public static void main(String[] args) {
        while (true) {
            PrintMenu();
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    TaskManager.getAllTasksOfTask();
                    break;
                case "2":
                    TaskManager.getAllTasksOfEpic();
                    break;
                case "3":
                    TaskManager.getAllTasksOfSubTask();
                    break;
                case "4":
                    TaskManager.removeAllTasksOfTask();
                    break;
                case "5":
                    TaskManager.removeAllTasksOfEpic();
                    break;
                case "6":
                    TaskManager.removeAllTasksOfSubTask();
                    break;
                case "7":
                    System.out.println("Введите номер задачи");
                    idTask = Integer.parseInt(scanner.nextLine());
                    System.out.println(TaskManager.getIdTask(idTask));
                    break;
                case "8":
                    System.out.println("Введите номер Эпик задачи");
                    idTask = Integer.parseInt(scanner.nextLine());
                    System.out.println(TaskManager.getIdTaskOfEpic(idTask));
                    break;
                case "9":
                    System.out.println("Введите номер Под Задачи");
                    idTask = Integer.parseInt(scanner.nextLine());
                    System.out.println(TaskManager.getIdTaskOfSubTask(idTask));
                    break;
                case "10":
                    System.out.println("Введите задачу");
                    String taskName = scanner.nextLine();

                    System.out.println("Введите детали");
                    String details = scanner.nextLine();

                    status = getStatus();

                    TaskManager.createTask(taskName, details, status);
                    break;
                case "11":
                    System.out.println("Введите задачу");
                    String epicTaskName = scanner.nextLine();
                    System.out.println("Введите детали");
                    String epicDetails = scanner.nextLine();

                    System.out.println("Добавьте подзадачу");
                    String subTasksName = scanner.nextLine();
                    TaskManager.saveEpicId();

                    while (!subTasksName.isEmpty()) {
                        System.out.println("Опишите задачу");
                        String subTaskDetails = scanner.nextLine();

                        status = getStatus();

                        TaskManager.createSubTask(subTasksName, subTaskDetails, status);

                        System.out.println("Введите еще подзадачу или пустую строку для продолжения");
                        scanner.nextLine();
                        subTasksName = scanner.nextLine();
                    }

                    TaskManager.createEpicTask(epicTaskName, epicDetails);
                    break;
                case "12":
                    if(TaskManager.saveTasks.isEmpty()) {
                        System.out.println("Нет задач");
                    } else {
                        Task task = TaskManager.saveTasks.get(0);
                        taskName = task.getTaskName();
                        System.out.println("Старое значение - " + taskName);
                        System.out.println("Введите новое имя задачи");
                        taskName = scanner.nextLine();
                        task.setTaskName(taskName);
                        TaskManager.updateTask(task);
                        System.out.println("Новое значение сохранено - " + taskName);
                    }
                    break;
                case "13":
                    if(TaskManager.saveEpicTasks.isEmpty()) {
                        System.out.println("Нет задач");
                    } else {
                        Epic epic = TaskManager.saveEpicTasks.get(1);
                        taskName = epic.getTaskName();
                        System.out.println("Старое значение - " + taskName);
                        System.out.println("Введите новое имя задачи");
                        taskName = scanner.nextLine();
                        epic.setTaskName(taskName);
                        TaskManager.updateEpicTask(epic);
                        System.out.println("Новое значение сохранено - " + taskName);
                    }
                    break;
                case "14":
                    if(TaskManager.saveSubTasks.isEmpty()) {
                        System.out.println("Нет задач");
                    } else {
                        SubTask subTask = TaskManager.saveSubTasks.get(2);
                        taskName = subTask.getTaskName();
                        System.out.println("Старое значение - " + taskName);
                        System.out.println("Введите новое имя задачи");
                        taskName = scanner.nextLine();
                        subTask.setTaskName(taskName);
                        TaskManager.updateSubTask(subTask);
                        System.out.println("Новое значение сохранено - " + taskName);
                    }
                    break;
                case "15":
                    System.out.println("Введите идентификатор задачи");
                    removeId = scanner.nextInt();
                    TaskManager.removeOnIdTask(removeId);
                    System.out.println("Задача под номером №" + removeId + " удалена");
                    break;
                case "16":
                    System.out.println("Введите идентификатор задачи");
                    removeId = scanner.nextInt();
                    TaskManager.removeOnIdEpicTask(removeId);
                    System.out.println("Задача под номером №" + removeId + " удалена");
                    break;
                case "17":
                    System.out.println("Введите идентификатор задачи");
                    removeId = scanner.nextInt();
                    TaskManager.removeOnIdSubTask(removeId);
                    System.out.println("Задача под номером №" + removeId + " удалена");
                    break;
                case "18":
                    System.out.println("Введите номер Эпик задачи");
                    idTask = scanner.nextInt();
                    System.out.println("Список подзадач " + TaskManager.getAllSubTasks(idTask));
                    break;
                case "19":
                    System.out.println("Введите номер задачи");
                    idTask = scanner.nextInt();
                    status = getStatus();
                    TaskManager.setNewStatusOfTask(idTask, status);
                    System.out.println("Статус задачи изменен на -" + status);
                    break;
                case "20":
                    System.out.println("Введите номер задачи");
                    idTask = scanner.nextInt();
                    status = getStatus();
                    TaskManager.setNewStatusOfSubTask(idTask, status);
                    System.out.println("Статус задачи изменен на -" + status);
                    break;
                case "21":
                    System.out.println("До свидания!");
                    return;
            }
        }

    }

    private static void PrintMenu() {
        System.out.println("Выберите команду");
        System.out.println("1 - Получение списка всех задач.");
        System.out.println("2 - Получение списка всех Эпик задач.");
        System.out.println("3 - Получение списка всех Под задач.");
        System.out.println();
        System.out.println("4 - Удаление всех задач.");
        System.out.println("5 - Удаление всех Эпик задач.");
        System.out.println("6 - Удаление всех Под задач.");
        System.out.println();
        System.out.println("7 - Получение задачи по идентификатору.");
        System.out.println("8 - Получение Эпик по идентификатору.");
        System.out.println("9 - Получение подзадачи по идентификатору.");
        System.out.println();
        System.out.println("10 - Создание задачи");
        System.out.println("11 - Создание Эпика и Под задачи");
        System.out.println();
        System.out.println("12 - Обновление задачи");
        System.out.println("13 - Обновление Эпик задачи");
        System.out.println("14 - Обновление Под задачи");
        System.out.println();
        System.out.println("15 - Удаление задачи по идентификатору.");
        System.out.println("16 - Удаление Эпик задачи по идентификатору.");
        System.out.println("17 - Удаление Под задачи по идентификатору.");
        System.out.println();
        System.out.println("18 - Получение списка всех подзадач определённого эпика.");
        System.out.println("19 - Управление статусом задачи");
        System.out.println("20 - Управление статусом подзадач");
        System.out.println("21 - Выход");
    }

    public static void PrintStatusMenu() {
        System.out.println("Выберите статус");
        System.out.println("1 - NEW");
        System.out.println("2 - IN_PROGRESS");
        System.out.println("3 - DONE");
    }

    public static Task.Status getStatus() {
        PrintStatusMenu();
        Task.Status status = null;

        int insertStatus = scanner.nextInt();

        while (true) {
            if (insertStatus > 3 || insertStatus < 1) {
                System.out.println("Неверный статус");
                insertStatus = scanner.nextInt();
            } else {
                if (insertStatus == 1) {
                    status = NEW;
                    return status;

                } else if (insertStatus == 2) {
                    status = IN_PROGRESS;
                    return status;

                } else if (insertStatus == 3)
                    status = DONE;
                return status;
            }
        }
    }
}