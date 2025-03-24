package Service;

public class SubTask extends Task {

    public SubTask(int newEpicId, int id, String taskName, String details, Status status) {
        super(id, taskName, details, status);
        setEpicId(newEpicId);
    }
}