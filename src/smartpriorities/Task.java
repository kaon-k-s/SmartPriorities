package smartpriorities;

import java.time.LocalDate;

/**
 * Represents a task with various attributes such as name, priority, due date,
 * urgency, links to other tasks, categories, notes, calculated points, and a flag
 * indicating whether points have been calculated.
 */
public class Task {
    private int taskId;
    private int taskUserIdFK;
    private String taskName;
    private int taskPriority;
    private LocalDate taskDueDate; 
    private boolean taskUrgent;
    private int taskLinkTo;
    private boolean taskWorkStudy;
    private boolean taskContracts;
    private boolean taskPeople;
    private boolean taskHealth;
    private String taskNotes;
    private double points;
    private boolean pointsCalculated;
    
    /**
    * Constructor for creating a new Task object.
    * 
    * @param taskId Unique identifier for the task
    * @param taskUserIdFK Foreign key linking the task to a user
    * @param taskName Name of the task
    * @param taskPriority Priority level of the task
    * @param taskDueDate Due date of the task
    * @param taskUrgent Flag indicating if the task is urgent
    * @param taskLinkTo Link to another task (parent task)
    * @param taskWorkStudy Flag indicating if the task is related to work/study
    * @param taskContracts Flag indicating if the task is related to contracts
    * @param taskPeople Flag indicating if the task is related to people
    * @param taskHealth Flag indicating if the task is related to health
    * @param taskNotes Additional notes about the task
    */
    public Task(int taskId, int taskUserIdFK, String taskName, int taskPriority, LocalDate taskDueDate, boolean taskUrgent, int taskLinkTo, 
            boolean taskWorkStudy, boolean taskContracts, boolean taskPeople, boolean taskHealth, String taskNotes) {
        this.taskId = taskId;
        this.taskUserIdFK = taskUserIdFK;
        this.taskName = taskName;
        this.taskPriority = taskPriority;
        this.taskDueDate = taskDueDate;
        this.taskUrgent = taskUrgent;
        this.taskLinkTo = taskLinkTo;
        this.taskWorkStudy = taskWorkStudy;
        this.taskContracts = taskContracts;
        this.taskPeople = taskPeople;
        this.taskHealth = taskHealth;
        this.taskNotes = taskNotes;      
    }

    
    // Getter Setter methods for task properties
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskUserIdFK() {
        return taskUserIdFK;
    }

    public void setTaskUserIdFK(int taskUserIdFK) {
        this.taskUserIdFK = taskUserIdFK;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(int taskPriority) {
        this.taskPriority = taskPriority;
    }

    public LocalDate getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(LocalDate taskDueDate) {
        this.taskDueDate = taskDueDate;
    }
    
    public boolean getTaskUrgent() {
        return taskUrgent;
    }

    public void setTaskUrgent(boolean taskUrgent) {
        this.taskUrgent = taskUrgent;
    }
    
    public int getTaskLinkTo() {
        return taskLinkTo;
    }

    public void setTaskLinkTo(int taskLinkTo) {
        this.taskLinkTo = taskLinkTo;
    }
    
    public boolean getTaskWorkStudy() {
        return taskWorkStudy;
    }

    public void setTaskWorkStudy(boolean taskWorkStudy) {
        this.taskWorkStudy = taskWorkStudy;
    }
    
    public boolean getTaskContracts() {
        return taskContracts;
    }

    public void setTaskContracts(boolean taskContracts) {
        this.taskContracts = taskContracts;
    }
    
    public boolean getTaskPeople() {
        return taskPeople;
    }

    public void setTaskPeople(boolean taskPeople) {
        this.taskPeople = taskPeople;
    }
    
    public boolean getTaskHealth() {
        return taskHealth;
    }

    public void setTaskHealth(boolean taskHealth) {
        this.taskHealth = taskHealth;
    }
    public String getTaskNotes() {
        return taskNotes;
    }

    public void setTaskNotes(String taskNotes) {
        this.taskNotes = taskNotes;
    }
    
    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }
    
    public boolean isPointsCalculated() {
        return pointsCalculated;
    }

    public void setPointsCalculated(boolean pointsCalculated) {
        this.pointsCalculated = pointsCalculated;
    }
}



