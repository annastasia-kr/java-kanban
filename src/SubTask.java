public class SubTask extends Task{
    private Integer epicId;

    public SubTask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(SubTask subTask){
        super(subTask);
        this.epicId = subTask.epicId;
    }

    public int getEpicId() {
        return epicId;
    }

}
