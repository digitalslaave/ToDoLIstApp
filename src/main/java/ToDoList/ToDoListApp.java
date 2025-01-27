package ToDoList;

import java.util.List;
import java.util.Scanner;

class ToDoListApp {
    private Scanner scanner;
    private DatabaseManager dbManager;
    private User currentUser;

    public ToDoListApp() {
        scanner = new Scanner(System.in);
        String url = "jdbc:postgresql://localhost:5432/todolistdb";
        String user = "postgres";
        String password = "123";
        dbManager = new DatabaseManager(url, user, password);
    }

    public void run() {
        System.out.println("=== To-Do List Terminal Application ===");
        // Регистрация или вход
        currentUser = loginOrRegister();
        if (currentUser == null) {
            System.out.println("Failed to authenticate. Exiting.");
            return;
        }

        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();
            switch (input.toLowerCase()) {
                case "1":
                    addTask();
                    break;
                case "2":
                    deleteTask();
                    break;
                case "3":
                    toggleTask();
                    break;
                case "4":
                    printItems();
                    break;
                case "5":
                    System.out.println("Exiting the application. Goodbye!");
                    return;
                default:
                    System.out.println("Unknown command. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\nPlease choose an option:");
        System.out.println("1. Add a task");
        System.out.println("2. Delete a task");
        System.out.println("3. Toggle task status");
        System.out.println("4. List all items");
        System.out.println("5. Exit");
        System.out.print("Your choice: ");
    }

    private User loginOrRegister() {
        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Register");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                return login();
            } else if (choice.equals("2")) {
                return register();
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private User login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        return dbManager.getUserByUsernameAndPassword(username, password);
    }

    private User register() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine().trim();

        if (dbManager.registerUser(username, password)) {
            return login();
        }
        return null;
    }

    private void addTask() {
        System.out.print("Enter task description: ");
        String description = scanner.nextLine().trim();
        if (!description.isEmpty()) {
            dbManager.addTask(currentUser.getId(), description);
            System.out.println("Task added successfully.");
        } else {
            System.out.println("Task description cannot be empty.");
        }
    }

    private void deleteTask() {
        System.out.print("Enter the task number to delete: ");
        String taskNumberStr = scanner.nextLine().trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberStr);
            List<Task> tasks = dbManager.getTasksByUserId(currentUser.getId());
            if (taskNumber > 0 && taskNumber <= tasks.size()) {
                Task task = tasks.get(taskNumber - 1);
                dbManager.deleteTask(task.getId());
                System.out.println("Task deleted successfully.");
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void toggleTask() {
        System.out.print("Enter the task number to toggle status: ");
        String taskNumberStr = scanner.nextLine().trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberStr);
            List<Task> tasks = dbManager.getTasksByUserId(currentUser.getId());
            if (taskNumber > 0 && taskNumber <= tasks.size()) {
                Task task = tasks.get(taskNumber - 1);
                dbManager.toggleTaskStatus(task.getId());
                System.out.println("Task status toggled successfully.");
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void printItems() {
        List<Task> tasks = dbManager.getTasksByUserId(currentUser.getId());
        if (tasks.isEmpty()) {
            System.out.println("No items to display.");
            return;
        }
        System.out.println("\nCurrent Items:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }
}