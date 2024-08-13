/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package smartpriorities;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Date;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class TaskManager extends javax.swing.JFrame {

    private Database db; // Instance of Database class for database operations
    private List<Task> tasks = new ArrayList<>(); // List to store tasks
    int currentUserId = 1; // Placeholder for the currently logged-in user's ID
    
    private boolean isUserSignedIn() {
        return true; // Placeholder return value
    }
    
    /**
     * Calculates points for each task based on priority, urgency, due date proximity,
     * links to other tasks, and categories. Then sorts the tasks based on these points.
     * 
     * @param tasksToSort List of tasks to calculate points for and sort
     * @return Sorted list of tasks based on calculated points
     */
    public List<Task> calculateAndSortTasks(List<Task> tasksToSort) {
        LocalDate today = LocalDate.now();  // Get current date

        // Calculate points for each task based on various criteria
        for (Task task : tasksToSort) {
            if (!task.isPointsCalculated()) { // Only calculate points if not already done
                double points = task.getTaskPriority(); // Start with priority points

                long daysUntilDue = ChronoUnit.DAYS.between(today, task.getTaskDueDate());
                if (daysUntilDue <= 3) points += 150;
                else if (daysUntilDue <= 7) points += 100;
                else if (daysUntilDue <= 14) points += 50;
                else if (daysUntilDue <= 21) points += 30;
                else if (daysUntilDue <= 30) points += 20;

                if (task.getTaskUrgent()) points += 100;

                if (task.getTaskLinkTo() != -1) {
                    // Find the parent task in the same list
                    Task parentTask = tasksToSort.stream()
                                                 .filter(t -> t.getTaskId() == task.getTaskLinkTo())
                                                 .findFirst()
                                                 .orElse(null);

                    if (parentTask != null) {
                        // Ensure parent task points are calculated first
                        calculateAndSortTasks(Collections.singletonList(parentTask));

                        if (points > parentTask.getPoints()) {
                            parentTask.setPoints(points + 1); // Update parent task points
                            parentTask.setPointsCalculated(true); // Mark as calculated
                        }
                    }
                }

                if (task.getTaskWorkStudy()) points += 20;
                if (task.getTaskContracts()) points += 20;
                if (task.getTaskPeople()) points += 20;
                if (task.getTaskHealth()) points += 20;

                task.setPoints(points); // Set calculated points
                task.setPointsCalculated(true); // Mark as calculated
            }
        }

        // Sort tasks based on calculated points
        Collections.sort(tasksToSort, Comparator.comparingDouble(Task::getPoints).reversed());

        return tasksToSort;
    }

    /**
     * Loads tasks for the current user from the database, calculates and sorts them,
     * then updates the UI with the sorted tasks.
     */
    private void loadSortTasks(int currentUserId) {
        // Clear the current tasks in the table
        ((DefaultTableModel) tblTaskList.getModel()).setRowCount(0);

        // Fetch tasks for the current user
        List<Task> tasksToSort = null;
        try {
            tasksToSort = db.showTasks(currentUserId);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception
            return;
        }
        
        List<Task> sortedTasks = calculateAndSortTasks(tasksToSort);
        
        cbNewTaskLinkTo.removeAllItems();
        
        // Adding "None" option as the first item
        cbNewTaskLinkTo.addItem(new TaskItem("None", -1)); 
        
        // Update the table with the sorted tasks
        if (sortedTasks != null) {
            DefaultTableModel model = (DefaultTableModel) tblTaskList.getModel();
            for (Task task : sortedTasks) {
                Object[] rowData = {task.getTaskId(), task.getTaskName(), task.getTaskDueDate(), task.getTaskUrgent()};
                model.addRow(rowData);
                
                // Add the TaskItem to the JComboBox
                cbNewTaskLinkTo.addItem(new TaskItem(task.getTaskName(), task.getTaskLinkTo()));
            }
        }
        
    }
    
    /**
     * Saves a new task entered by the user to the database.
     */
    private void saveTaskData() {
        try {
            // Gather input data, create a Task object, and save it to the database
            String taskName = tfNewTaskName.getText();

            Date dateUtil = (Date) ftNewTaskDueDate.getValue();
            LocalDate taskDueDate = dateUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            int taskPriority = sliderNewTaskPriority.getValue();
            boolean taskUrgent = checkNewTaskUrgent.isSelected();
            
            int taskLinkTo;

            TaskItem selectedItem = (TaskItem) cbNewTaskLinkTo.getSelectedItem();
            if (selectedItem != null) {
                int selectedTaskLinkId = selectedItem.getTaskLinkToTaskId();
                    taskLinkTo = selectedTaskLinkId;
            } else {
                taskLinkTo = -1;
            }

            boolean taskWorkStudy = checkNewTaskWorkStudy.isSelected();
            boolean taskContracts = checkNewTaskConracts.isSelected();
            boolean taskPeople = checkNewTaskPeople.isSelected();
            boolean taskHealth = checkNewTaskHealth.isSelected();
            String taskNotes = tpNewTaskNotes.getText();


            // Instantiating the Task object
            Task newTask = new Task(1, currentUserId, taskName, taskPriority, taskDueDate, taskUrgent, taskLinkTo, taskWorkStudy, taskContracts, taskPeople, taskHealth, taskNotes);


            // Passing the Task object to the saveTask method of the Database class
            db.saveTask(newTask);
        } catch (SQLException e) {
        e.printStackTrace();
        }
    }
    
    /**
     * Constructor for TaskManager. Initializes components, sets up UI, and loads tasks.
     */
    public TaskManager() {
        initComponents();
        cbNewTaskLinkTo.setRenderer(new TaskNameOnlyRenderer()); // Set custom renderer for JComboBox
        db = new Database(); // Initialize database connection
        // Initialize sign-in status
        SmartPriorities.isUserSignedIn = true; // Placeholder sign-in status
        
        // Load user`s tasks if the user is signed in
        if(SmartPriorities.isUserSignedIn)
        {
            loadSortTasks(currentUserId);
        }

        // Hide "Sign In" and "Sign Up" buttons initially
        btnSignInTaskMan.setVisible(false);
        btnSignUpTaskMan.setVisible(false);
        lblTaskManUsername.setVisible(false);
        lblUserIdLabel.setVisible(false);
        lblUserId.setVisible(false);

        // Set up ActionListener for btnSave
        btnSaveNewTask.addActionListener(e1 -> {
            if (!SmartPriorities.isUserSignedIn) {
                // Open the SignIn form if the user is not signed in
                SignIn signInForm = new SignIn();
                signInForm.setVisible(true);
                signInForm.pack();
                signInForm.setLocationRelativeTo(null);
            } else {
                saveTaskData(); // Save task data if user is signed in
                loadSortTasks(currentUserId);
            }
        });

        // ActionListener for signInButton
        btnSignInTaskMan.addActionListener(e2 -> {
            // Open the SignIn form
            SignIn signInForm = new SignIn();
            signInForm.setVisible(true);
            signInForm.pack();
            signInForm.setLocationRelativeTo(null);
        });

        // ActionListener for signUpButton
        btnSignUpTaskMan.addActionListener(e3 -> {
            // Open the SignUp form
            SignUp signUpForm = new SignUp();
            signUpForm.setVisible(true);
            signUpForm.pack();
            signUpForm.setLocationRelativeTo(null);
        });

        // Periodically check sign-in status and update UI
        Timer timer = new Timer(1000, e4 -> {
            // Update UI components visibility based on sign-in status
            if (SmartPriorities.isUserSignedIn) {
                btnSignInTaskMan.setVisible(false);
                btnSignUpTaskMan.setVisible(false);
                lblTaskManUsername.setVisible(true);
                lblUserIdLabel.setVisible(true);
                lblUserId.setVisible(true);
            } else {
                btnSignInTaskMan.setVisible(true);
                btnSignUpTaskMan.setVisible(true);
                lblTaskManUsername.setVisible(false);
            }
        });
        timer.start();
        
//        lblMotivation.setText(db.getRandomMotivationQuote());
        lblUserId.setText(Integer.toString(currentUserId));  // Display current user ID
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panTaskManager = new javax.swing.JPanel();
        panAddTask = new javax.swing.JPanel();
        lblNewTaskName = new javax.swing.JLabel();
        tfNewTaskName = new javax.swing.JTextField();
        lblPanAddTask = new javax.swing.JLabel();
        lblNewTaskDueDate = new javax.swing.JLabel();
        lblNewTaskPriority = new javax.swing.JLabel();
        lblNewTaskLinkTo = new javax.swing.JLabel();
        checkNewTaskWorkStudy = new javax.swing.JCheckBox();
        checkNewTaskConracts = new javax.swing.JCheckBox();
        checkNewTaskPeople = new javax.swing.JCheckBox();
        checkNewTaskHealth = new javax.swing.JCheckBox();
        btnSaveNewTask = new javax.swing.JButton();
        cbNewTaskLinkTo = new javax.swing.JComboBox<>();
        lblNewTaskNotes = new javax.swing.JLabel();
        spNewTaskNotes = new javax.swing.JScrollPane();
        tpNewTaskNotes = new javax.swing.JTextPane();
        checkNewTaskUrgent = new javax.swing.JCheckBox();
        sliderNewTaskPriority = new javax.swing.JSlider();
        ftNewTaskDueDate = new javax.swing.JFormattedTextField();
        lblDateFormat = new javax.swing.JLabel();
        panToDo = new javax.swing.JPanel();
        lblPanToDo = new javax.swing.JLabel();
        scrollPanTaskList = new javax.swing.JScrollPane();
        tblTaskList = new javax.swing.JTable();
        lblLogoPic = new javax.swing.JLabel();
        lblLogoSmart = new javax.swing.JLabel();
        lblTaskManUsername = new javax.swing.JLabel();
        lblLogoPriorities = new javax.swing.JLabel();
        btnSignInTaskMan = new javax.swing.JButton();
        btnSignUpTaskMan = new javax.swing.JButton();
        lblUserIdLabel = new javax.swing.JLabel();
        lblUserId = new javax.swing.JLabel();
        lblMotivation = new javax.swing.JLabel();
        lblDecorCircleLeft = new javax.swing.JLabel();
        lblDecorCircleRight = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Smart Priorities: Task Manager");
        setBackground(new java.awt.Color(204, 204, 0));
        setPreferredSize(new java.awt.Dimension(800, 800));

        panTaskManager.setBackground(new java.awt.Color(67, 44, 58));
        panTaskManager.setPreferredSize(new java.awt.Dimension(800, 800));

        panAddTask.setBackground(new java.awt.Color(126, 97, 116));

        lblNewTaskName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNewTaskName.setForeground(new java.awt.Color(255, 255, 255));
        lblNewTaskName.setLabelFor(tfNewTaskName);
        lblNewTaskName.setText("Name *");

        tfNewTaskName.setName(""); // NOI18N
        tfNewTaskName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfNewTaskNameActionPerformed(evt);
            }
        });

        lblPanAddTask.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblPanAddTask.setForeground(new java.awt.Color(227, 201, 193));
        lblPanAddTask.setText("Add a new task");

        lblNewTaskDueDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNewTaskDueDate.setForeground(new java.awt.Color(255, 255, 255));
        lblNewTaskDueDate.setText("Due Date");

        lblNewTaskPriority.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNewTaskPriority.setForeground(new java.awt.Color(255, 255, 255));
        lblNewTaskPriority.setLabelFor(sliderNewTaskPriority);
        lblNewTaskPriority.setText("Priority");

        lblNewTaskLinkTo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNewTaskLinkTo.setForeground(new java.awt.Color(255, 255, 255));
        lblNewTaskLinkTo.setLabelFor(cbNewTaskLinkTo);
        lblNewTaskLinkTo.setText("Linked to another task");

        checkNewTaskWorkStudy.setBackground(new java.awt.Color(126, 97, 116));
        checkNewTaskWorkStudy.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        checkNewTaskWorkStudy.setForeground(new java.awt.Color(255, 255, 255));
        checkNewTaskWorkStudy.setText("Affects work/study");
        checkNewTaskWorkStudy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkNewTaskWorkStudyActionPerformed(evt);
            }
        });

        checkNewTaskConracts.setBackground(new java.awt.Color(126, 97, 116));
        checkNewTaskConracts.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        checkNewTaskConracts.setForeground(new java.awt.Color(255, 255, 255));
        checkNewTaskConracts.setText("Affects contracts");

        checkNewTaskPeople.setBackground(new java.awt.Color(126, 97, 116));
        checkNewTaskPeople.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        checkNewTaskPeople.setForeground(new java.awt.Color(255, 255, 255));
        checkNewTaskPeople.setText("Affects other people");

        checkNewTaskHealth.setBackground(new java.awt.Color(126, 97, 116));
        checkNewTaskHealth.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        checkNewTaskHealth.setForeground(new java.awt.Color(255, 255, 255));
        checkNewTaskHealth.setText("Affects health/wellness");
        checkNewTaskHealth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkNewTaskHealthActionPerformed(evt);
            }
        });

        btnSaveNewTask.setBackground(new java.awt.Color(102, 157, 163));
        btnSaveNewTask.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSaveNewTask.setForeground(new java.awt.Color(227, 201, 193));
        btnSaveNewTask.setText("Save");
        btnSaveNewTask.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(227, 201, 193), 0));
        btnSaveNewTask.setName(""); // NOI18N
        btnSaveNewTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveNewTaskActionPerformed(evt);
            }
        });

        cbNewTaskLinkTo.setForeground(new java.awt.Color(67, 44, 58));
        cbNewTaskLinkTo.setModel(new javax.swing.DefaultComboBoxModel<>());

        lblNewTaskNotes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNewTaskNotes.setForeground(new java.awt.Color(255, 255, 255));
        lblNewTaskNotes.setLabelFor(spNewTaskNotes);
        lblNewTaskNotes.setText("Notes");

        spNewTaskNotes.setViewportView(tpNewTaskNotes);

        checkNewTaskUrgent.setBackground(new java.awt.Color(126, 97, 116));
        checkNewTaskUrgent.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        checkNewTaskUrgent.setForeground(new java.awt.Color(255, 255, 255));
        checkNewTaskUrgent.setText("Urgent ");
        checkNewTaskUrgent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        checkNewTaskUrgent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkNewTaskUrgentActionPerformed(evt);
            }
        });

        sliderNewTaskPriority.setBackground(new java.awt.Color(126, 97, 116));
        sliderNewTaskPriority.setForeground(new java.awt.Color(227, 201, 193));
        sliderNewTaskPriority.setMajorTickSpacing(1);
        sliderNewTaskPriority.setMaximum(10);
        sliderNewTaskPriority.setPaintLabels(true);
        sliderNewTaskPriority.setSnapToTicks(true);

        ftNewTaskDueDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd/MM/yyyy"))));

        lblDateFormat.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblDateFormat.setForeground(new java.awt.Color(227, 201, 193));
        lblDateFormat.setText("dd/MM/yyyy");

        javax.swing.GroupLayout panAddTaskLayout = new javax.swing.GroupLayout(panAddTask);
        panAddTask.setLayout(panAddTaskLayout);
        panAddTaskLayout.setHorizontalGroup(
            panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panAddTaskLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkNewTaskUrgent)
                    .addGroup(panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnSaveNewTask, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panAddTaskLayout.createSequentialGroup()
                                .addComponent(lblNewTaskNotes)
                                .addGap(18, 18, 18)
                                .addComponent(spNewTaskNotes))
                            .addComponent(lblPanAddTask, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panAddTaskLayout.createSequentialGroup()
                                .addComponent(lblNewTaskLinkTo, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbNewTaskLinkTo, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(checkNewTaskWorkStudy)
                            .addComponent(checkNewTaskConracts)
                            .addComponent(checkNewTaskPeople)
                            .addComponent(checkNewTaskHealth)))
                    .addGroup(panAddTaskLayout.createSequentialGroup()
                        .addGroup(panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNewTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNewTaskDueDate)
                            .addComponent(lblNewTaskPriority))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sliderNewTaskPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(tfNewTaskName)
                            .addComponent(ftNewTaskDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDateFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        panAddTaskLayout.setVerticalGroup(
            panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panAddTaskLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPanAddTask, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNewTaskName)
                    .addComponent(tfNewTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNewTaskDueDate)
                    .addComponent(ftNewTaskDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDateFormat))
                .addGroup(panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panAddTaskLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(sliderNewTaskPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panAddTaskLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblNewTaskPriority)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkNewTaskUrgent)
                .addGap(18, 18, 18)
                .addGroup(panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNewTaskLinkTo)
                    .addComponent(cbNewTaskLinkTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(checkNewTaskWorkStudy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkNewTaskConracts)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkNewTaskPeople)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkNewTaskHealth)
                .addGap(18, 18, 18)
                .addGroup(panAddTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNewTaskNotes)
                    .addComponent(spNewTaskNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnSaveNewTask)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        tfNewTaskName.getAccessibleContext().setAccessibleName("");
        lblPanAddTask.getAccessibleContext().setAccessibleName("");
        lblNewTaskDueDate.getAccessibleContext().setAccessibleName("");
        lblNewTaskPriority.getAccessibleContext().setAccessibleName("");
        checkNewTaskWorkStudy.getAccessibleContext().setAccessibleName("");
        btnSaveNewTask.getAccessibleContext().setAccessibleName("");
        lblNewTaskNotes.getAccessibleContext().setAccessibleName("");
        checkNewTaskUrgent.getAccessibleContext().setAccessibleName(" ");

        panToDo.setBackground(new java.awt.Color(102, 157, 163));

        lblPanToDo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblPanToDo.setForeground(new java.awt.Color(227, 201, 193));
        lblPanToDo.setText("To-Do");

        tblTaskList.setBackground(new java.awt.Color(227, 201, 193));
        tblTaskList.setForeground(new java.awt.Color(67, 44, 58));
        tblTaskList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "TaskID", "Task", "Due Date", "Urgent"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTaskList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        tblTaskList.setGridColor(new java.awt.Color(227, 201, 193));
        tblTaskList.setIntercellSpacing(new java.awt.Dimension(10, 0));
        scrollPanTaskList.setViewportView(tblTaskList);
        if (tblTaskList.getColumnModel().getColumnCount() > 0) {
            tblTaskList.getColumnModel().getColumn(0).setMinWidth(50);
            tblTaskList.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblTaskList.getColumnModel().getColumn(0).setMaxWidth(50);
            tblTaskList.getColumnModel().getColumn(1).setMinWidth(170);
            tblTaskList.getColumnModel().getColumn(1).setPreferredWidth(170);
            tblTaskList.getColumnModel().getColumn(1).setMaxWidth(170);
            tblTaskList.getColumnModel().getColumn(2).setMinWidth(90);
            tblTaskList.getColumnModel().getColumn(2).setPreferredWidth(90);
            tblTaskList.getColumnModel().getColumn(2).setMaxWidth(90);
            tblTaskList.getColumnModel().getColumn(3).setMinWidth(50);
            tblTaskList.getColumnModel().getColumn(3).setPreferredWidth(50);
            tblTaskList.getColumnModel().getColumn(3).setMaxWidth(50);
        }

        javax.swing.GroupLayout panToDoLayout = new javax.swing.GroupLayout(panToDo);
        panToDo.setLayout(panToDoLayout);
        panToDoLayout.setHorizontalGroup(
            panToDoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPanTaskList, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
            .addGroup(panToDoLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblPanToDo, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panToDoLayout.setVerticalGroup(
            panToDoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panToDoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPanToDo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPanTaskList, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(281, Short.MAX_VALUE))
        );

        lblLogoPic.setIcon(new javax.swing.ImageIcon("C:\\Users\\tn\\Documents\\NetBeansProjects\\SmartPriorities\\src\\icon\\SPlogoS.png")); // NOI18N

        lblLogoSmart.setFont(new java.awt.Font("Impact", 0, 24)); // NOI18N
        lblLogoSmart.setForeground(new java.awt.Color(227, 201, 193));
        lblLogoSmart.setText("Smart");

        lblTaskManUsername.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTaskManUsername.setForeground(new java.awt.Color(227, 201, 193));
        lblTaskManUsername.setText("Username");

        lblLogoPriorities.setFont(new java.awt.Font("Impact", 0, 24)); // NOI18N
        lblLogoPriorities.setForeground(new java.awt.Color(102, 157, 163));
        lblLogoPriorities.setText("Priorities");

        btnSignInTaskMan.setBackground(new java.awt.Color(67, 44, 58));
        btnSignInTaskMan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSignInTaskMan.setForeground(new java.awt.Color(102, 157, 163));
        btnSignInTaskMan.setText("Sign In");
        btnSignInTaskMan.setBorder(null);

        btnSignUpTaskMan.setBackground(new java.awt.Color(67, 44, 58));
        btnSignUpTaskMan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSignUpTaskMan.setForeground(new java.awt.Color(227, 201, 193));
        btnSignUpTaskMan.setText("Sign Up");
        btnSignUpTaskMan.setBorder(null);

        lblUserIdLabel.setForeground(new java.awt.Color(227, 201, 193));
        lblUserIdLabel.setText("UserID:");

        lblUserId.setForeground(new java.awt.Color(227, 201, 193));
        lblUserId.setText("1");

        lblMotivation.setFont(new java.awt.Font("Impact", 0, 24)); // NOI18N
        lblMotivation.setForeground(new java.awt.Color(227, 201, 193));
        lblMotivation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMotivation.setText("You only fail if you stop trying.");

        lblDecorCircleLeft.setFont(new java.awt.Font("Impact", 0, 48)); // NOI18N
        lblDecorCircleLeft.setForeground(new java.awt.Color(227, 201, 193));
        lblDecorCircleLeft.setText("°");

        lblDecorCircleRight.setFont(new java.awt.Font("Impact", 0, 48)); // NOI18N
        lblDecorCircleRight.setForeground(new java.awt.Color(227, 201, 193));
        lblDecorCircleRight.setText("°");

        javax.swing.GroupLayout panTaskManagerLayout = new javax.swing.GroupLayout(panTaskManager);
        panTaskManager.setLayout(panTaskManagerLayout);
        panTaskManagerLayout.setHorizontalGroup(
            panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTaskManagerLayout.createSequentialGroup()
                .addComponent(panAddTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panToDo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panTaskManagerLayout.createSequentialGroup()
                        .addComponent(lblUserIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblUserId, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSignUpTaskMan)
                            .addComponent(btnSignInTaskMan))
                        .addGap(83, 83, 83))))
            .addGroup(panTaskManagerLayout.createSequentialGroup()
                .addGroup(panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panTaskManagerLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(lblLogoPic, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblLogoSmart, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblLogoPriorities, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(151, 151, 151)
                        .addComponent(lblTaskManUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panTaskManagerLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(lblDecorCircleLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMotivation, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDecorCircleRight, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panTaskManagerLayout.setVerticalGroup(
            panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTaskManagerLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblLogoSmart)
                            .addComponent(lblLogoPriorities)
                            .addComponent(lblTaskManUsername))
                        .addComponent(lblLogoPic))
                    .addGroup(panTaskManagerLayout.createSequentialGroup()
                        .addGroup(panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblUserIdLabel)
                            .addComponent(lblUserId)
                            .addComponent(btnSignInTaskMan))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSignUpTaskMan)))
                .addGap(18, 18, 18)
                .addGroup(panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panAddTask, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panToDo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28)
                .addGroup(panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panTaskManagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblDecorCircleRight, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDecorCircleLeft, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblMotivation, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        panToDo.getAccessibleContext().setAccessibleName("btnSignIn");
        lblTaskManUsername.getAccessibleContext().setAccessibleName("userName");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panTaskManager, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panTaskManager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("TaskManager");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfNewTaskNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNewTaskNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfNewTaskNameActionPerformed

    private void checkNewTaskWorkStudyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkNewTaskWorkStudyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkNewTaskWorkStudyActionPerformed

    private void checkNewTaskHealthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkNewTaskHealthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkNewTaskHealthActionPerformed

    private void checkNewTaskUrgentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkNewTaskUrgentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkNewTaskUrgentActionPerformed

    private void btnSaveNewTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveNewTaskActionPerformed
        // TODO add your handling here:
    }//GEN-LAST:event_btnSaveNewTaskActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(TaskManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(TaskManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(TaskManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(TaskManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new TaskManager().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSaveNewTask;
    private javax.swing.JButton btnSignInTaskMan;
    private javax.swing.JButton btnSignUpTaskMan;
    private javax.swing.JComboBox<TaskItem> cbNewTaskLinkTo;
    private javax.swing.JCheckBox checkNewTaskConracts;
    private javax.swing.JCheckBox checkNewTaskHealth;
    private javax.swing.JCheckBox checkNewTaskPeople;
    private javax.swing.JCheckBox checkNewTaskUrgent;
    private javax.swing.JCheckBox checkNewTaskWorkStudy;
    private javax.swing.JFormattedTextField ftNewTaskDueDate;
    private javax.swing.JLabel lblDateFormat;
    private javax.swing.JLabel lblDecorCircleLeft;
    private javax.swing.JLabel lblDecorCircleRight;
    private javax.swing.JLabel lblLogoPic;
    private javax.swing.JLabel lblLogoPriorities;
    private javax.swing.JLabel lblLogoSmart;
    private javax.swing.JLabel lblMotivation;
    private javax.swing.JLabel lblNewTaskDueDate;
    private javax.swing.JLabel lblNewTaskLinkTo;
    private javax.swing.JLabel lblNewTaskName;
    private javax.swing.JLabel lblNewTaskNotes;
    private javax.swing.JLabel lblNewTaskPriority;
    private javax.swing.JLabel lblPanAddTask;
    private javax.swing.JLabel lblPanToDo;
    private javax.swing.JLabel lblTaskManUsername;
    private javax.swing.JLabel lblUserId;
    private javax.swing.JLabel lblUserIdLabel;
    private javax.swing.JPanel panAddTask;
    private javax.swing.JPanel panTaskManager;
    private javax.swing.JPanel panToDo;
    private javax.swing.JScrollPane scrollPanTaskList;
    private javax.swing.JSlider sliderNewTaskPriority;
    private javax.swing.JScrollPane spNewTaskNotes;
    private javax.swing.JTable tblTaskList;
    private javax.swing.JTextField tfNewTaskName;
    private javax.swing.JTextPane tpNewTaskNotes;
    // End of variables declaration//GEN-END:variables
}
