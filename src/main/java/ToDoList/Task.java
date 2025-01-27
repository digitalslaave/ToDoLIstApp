package ToDoList;

// Класс Task наследует Item
class Task extends Item {
    private boolean isCompleted;

    public Task(int id, String description, boolean isCompleted) {
        super(id, description);
        this.isCompleted = isCompleted;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void toggleStatus() {
        isCompleted = !isCompleted;
    }

    @Override
    public String toString() {
        return (isCompleted ? "[x] " : "[ ] ") + getDescription();
    }
}