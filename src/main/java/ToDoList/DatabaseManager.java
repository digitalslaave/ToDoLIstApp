package ToDoList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DatabaseManager {
    private Connection connection;

    public DatabaseManager(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully.");
            createTables();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    private void createTables() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, username VARCHAR(50) UNIQUE NOT NULL, password VARCHAR(50) NOT NULL);";
        String createTasksTable = "CREATE TABLE IF NOT EXISTS tasks (id SERIAL PRIMARY KEY, user_id INT NOT NULL, description VARCHAR(255) NOT NULL, is_completed BOOLEAN NOT NULL, FOREIGN KEY (user_id) REFERENCES users(id));";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createTasksTable);
            System.out.println("Tables are ready.");
        } catch (SQLException e) {
            System.out.println("Failed to create tables.");
            e.printStackTrace();
        }
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        String query = "SELECT id, username, password FROM users WHERE username = ? AND password = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                return new User(id, username, password);
            }
        } catch (SQLException e) {
            System.out.println("Error during user retrieval.");
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String username, String password) {
        String insertUser = "INSERT INTO users (username, password) VALUES (?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Username already exists.");
            return false;
        }
    }

    public List<Task> getTasksByUserId(int userId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT id, description, is_completed FROM tasks WHERE user_id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                boolean isCompleted = rs.getBoolean("is_completed");
                tasks.add(new Task(id, description, isCompleted));
            }
        } catch (SQLException e) {
            System.out.println("Error during task retrieval.");
            e.printStackTrace();
        }
        return tasks;
    }

    public void addTask(int userId, String description) {
        String insertTask = "INSERT INTO tasks (user_id, description, is_completed) VALUES (?, ?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(insertTask)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, description);
            pstmt.setBoolean(3, false);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add task.");
            e.printStackTrace();
        }
    }

    public void deleteTask(int taskId) {
        String deleteTask = "DELETE FROM tasks WHERE id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteTask)) {
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete task.");
            e.printStackTrace();
        }
    }

    public void toggleTaskStatus(int taskId) {
        String toggleTask = "UPDATE tasks SET is_completed = NOT is_completed WHERE id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(toggleTask)) {
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to toggle task status.");
            e.printStackTrace();
        }
    }
}
