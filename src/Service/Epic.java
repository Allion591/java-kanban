package Service;
import java.util.LinkedList;
import java.util.List;

public class Epic extends Task {

    public Epic(int id, String taskName, String details, Status status, List<Integer> newIdSubTask) {
        super(id, taskName, details, status);
        List<Integer> listSaveSubTasksNum = new LinkedList<>(newIdSubTask);

        setSubTasksId(listSaveSubTasksNum);
    }
}