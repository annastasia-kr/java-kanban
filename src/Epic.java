import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subTasksId;

    public Epic(String name, String description) {
        super(name, description, null);
        subTasksId = new ArrayList<>();
    }

    public Epic(Epic epic){
        super(epic);
        this.subTasksId = epic.subTasksId;
    }

    public ArrayList<Integer> getSubTasksId() {
        ArrayList<Integer> subTasksId = new ArrayList<>(this.subTasksId);
        return subTasksId;
    }

    public boolean addToSubTasksId(Integer subTaskId){
        return subTasksId.add(subTaskId);
    }

    public boolean removeFromSubTasksId(Integer subTaskId){
        return subTasksId.remove(subTaskId);
    }
}
