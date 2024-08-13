package smartpriorities;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.time.ZoneId;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

/**
 * Manages database connections and operations for the task management system.
 * Handles tasks such as saving new tasks, fetching tasks for a specific user, and closing the database connection.
 */
public class Database {

    private Connection con; // Holds the database connection

    /**
     * Constructor for establishing a database connection.
     * Attempts to connect to a MySQL database using JDBC.
     */
    public Database() {
        String conStr = "jdbc:mysql://localhost:3306/taskmanager";
        String username = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(conStr, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the database connection if it's not already open.
     * Throws an exception if the connection is invalid or closed.
     * @throws SQLException if there's an issue opening the connection
     */
    private void open() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Connection not valid or not opened.");
        } else {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DATABASE()");
            while (rs.next()) {
                String dbName = rs.getString(1);
                System.out.println("Connected to database: " + dbName);
            }
            rs.close();
            stmt.close();
        }
    }

    /**
     * Closes the database connection if it's open.
     * Logs any exceptions rather than printing to the console.
     */
    private void close() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging instead of printing to console
        }
    }

    /**
     * Saves a new task to the database.
     * Converts LocalDate to java.sql.Date and handles potential errors gracefully.
     * @param newTask The task to be saved
     * @throws SQLException if there's an issue executing the SQL statement
     */
    public void saveTask(Task newTask) throws SQLException {
        open();
        LocalDate localDateDue = newTask.getTaskDueDate();

        if (localDateDue == null) {
            JOptionPane.showMessageDialog(null, "Due Date is missing");
        } else {
            // Convert LocalDate to java.sql.Date
            java.sql.Date sqlDate = java.sql.Date.valueOf(localDateDue);

            String query = "INSERT INTO tasklist (taskUserIdFK, taskName, taskPriority, taskDueDate, taskUrgent, taskLinkToTaskId, taskWorkStudy, taskContracts, taskPeople, taskHealth, taskNotes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setInt(1, newTask.getTaskUserIdFK());
                pstmt.setString(2, newTask.getTaskName());
                pstmt.setInt(3, newTask.getTaskPriority());
                pstmt.setDate(4, sqlDate);
                pstmt.setBoolean(5, newTask.getTaskUrgent());
                pstmt.setInt(6, newTask.getTaskLinkTo());
                pstmt.setBoolean(7, newTask.getTaskWorkStudy());
                pstmt.setBoolean(8, newTask.getTaskContracts());
                pstmt.setBoolean(9, newTask.getTaskPeople());
                pstmt.setBoolean(10, newTask.getTaskHealth());
                pstmt.setString(11, newTask.getTaskNotes());

                pstmt.executeUpdate();

            } catch (SQLException e) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                close();
                JOptionPane.showMessageDialog(null, "The task is saved");
            }
        }
    }
    
    
//    public String getRandomMotivationQuote() {
//    String motivationQuote = null;
//    try (PreparedStatement pstmt = con.prepareStatement("SELECT quoteText FROM MotivationalQuotes ORDER BY RAND() LIMIT 1")) {
//        try (ResultSet rs = pstmt.executeQuery()) {
//            if (rs.next()) {
//                motivationQuote = rs.getString("quoteText");
//            }
//        }
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//    return motivationQuote;
//    }
    
    /**
     * Fetches all tasks for a specific user from the database.
     * Returns a list of Task objects.
     * @param userId The ID of the user whose tasks are to be fetched
     * @return A list of Task objects corresponding to the user
     * @throws SQLException if there's an issue executing the SQL statement
     */
    public List<Task> showTasks(int userId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        try {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM tasklist WHERE taskUserIdFK = ?");
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int taskId = rs.getInt("taskId");
                    String taskName = rs.getString("taskName");
                    int taskPriority = rs.getInt("taskPriority");
                    LocalDate taskDueDate = LocalDate.parse(rs.getString("taskDueDate"));
                    boolean taskUrgent = rs.getBoolean("taskUrgent");
                    int taskLinkTo = rs.getInt("taskLinkToTaskId");
                    boolean taskWorkStudy = rs.getBoolean("taskWorkStudy");
                    boolean taskContracts = rs.getBoolean("taskContracts");
                    boolean taskPeople = rs.getBoolean("taskPeople");
                    boolean taskHealth = rs.getBoolean("taskHealth");
                    String taskNotes = rs.getString("taskNotes");
                    
                    Task task = new Task(taskId, userId, taskName, taskPriority, taskDueDate, taskUrgent, taskLinkTo, taskWorkStudy, taskContracts, taskPeople, taskHealth, taskNotes);
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}

