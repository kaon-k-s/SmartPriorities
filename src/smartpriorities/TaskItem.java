/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartpriorities;

/**
 * Represents an item in a task list, primarily used for displaying tasks in UI components like combo boxes.
 * Each TaskItem holds a task name and an identifier for linking tasks.
 */
public class TaskItem {
    private final String taskName;  // Name of the task
    private final int taskLinkToTaskId; // Identifier used for linking tasks, typically representing a parent task ID

    /**
     * Constructor for creating a new TaskItem object.
     * 
     * @param taskName Name of the task
     * @param taskLinkToTaskId Identifier for linking tasks, usually a parent task ID
     */
    public TaskItem(String taskName, int taskLinkToTaskId) {
        this.taskName = taskName;
        this.taskLinkToTaskId = taskLinkToTaskId;
    }

    /**
     * Getter method for retrieving the name of the task.
     * 
     * @return Name of the task
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Getter method for retrieving the identifier used for linking tasks.
     * 
     * @return Identifier for linking tasks, typically a parent task ID
     */
    public int getTaskLinkToTaskId() {
        return taskLinkToTaskId;
    }
}
