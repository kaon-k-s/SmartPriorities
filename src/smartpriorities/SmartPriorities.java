/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package smartpriorities;

public class SmartPriorities {
    
    // Variable to track if the user is signed in
    public static boolean isUserSignedIn = false;

    public static void main(String[] args) {
        TaskManager TaskManagerFrame = new TaskManager();
        TaskManagerFrame.setVisible(true);
        TaskManagerFrame.pack();
        TaskManagerFrame.setLocationRelativeTo(null);
    }    
}
