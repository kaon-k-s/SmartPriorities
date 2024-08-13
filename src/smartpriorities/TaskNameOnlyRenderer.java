/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartpriorities;

import javax.swing.*;
import java.awt.*;

/**
 * Custom renderer for JList components that displays only the task name of {@link TaskItem} objects.
 * This is particularly useful when you want to display a simplified view of tasks in a list, focusing solely on their names.
 */
class TaskNameOnlyRenderer extends DefaultListCellRenderer {
    /**
     * Overridden method from DefaultListCellRenderer to customize the rendering of list cells.
     * This method is called every time a cell in the list needs to be rendered.
     *
     * @param list The JList that is being rendered
     * @param value The value of the cell being rendered
     * @param index The index of the cell being rendered
     * @param isSelected Indicates whether the cell is selected
     * @param cellHasFocus Indicates whether the cell has focus
     * @return The rendered component for the cell
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Call the superclass's getListCellRendererComponent method to initialize the JLabel
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        // Check if the value is an instance of TaskItem
        if (value instanceof TaskItem) {
            // Cast the value to TaskItem and retrieve the task name
            TaskItem taskItem = (TaskItem) value;
            // Set the text of the JLabel to the task name
            label.setText(taskItem.getTaskName());
        }
        // Return the customized JLabel
        return label;
    }
}


