/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartpriorities;

/**
 * Wrapper class for {@link TaskItem} that includes a display name alongside the task item itself.
 * This class is useful when integrating task items into UI components that require a more descriptive or formatted representation.
 */
public class TaskItemWrapper {
    private TaskItem taskItem;  // The underlying TaskItem object
    private String displayName; // A human-readable name or description for display purposes

    /**
     * Constructor for creating a new TaskItemWrapper object.
     * 
     * @param taskItem The TaskItem object to wrap
     * @param displayName A string to be displayed instead of the task item's name
     */
    public TaskItemWrapper(TaskItem taskItem, String displayName) {
        this.taskItem = taskItem;
        this.displayName = displayName;
    }

    /**
     * Getter method for retrieving the wrapped TaskItem object.
     * 
     * @return The TaskItem object contained within this wrapper
     */
    public TaskItem getTaskItem() {
        return taskItem;
    }

    /**
     * Getter method for retrieving the display name associated with this wrapper.
     * 
     * @return The display name intended for UI presentation
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Overrides the default toString() method to return the display name instead of the default string representation.
     * This allows for more meaningful output when printing or logging instances of this class.
     * 
     * @return The display name of the task item
     */
    @Override
    public String toString() {
        return displayName;
    }
}
