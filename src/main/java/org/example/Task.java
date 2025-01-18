package org.example;

import java.time.LocalDate;

public class Task {
    private final String name;
    private final Main.Priority priority;
    private final LocalDate dueDate;
    private final int id;
    private static int idCounter = 0;

    public Task(String description, Main.Priority priority, LocalDate dueDate) {
        this.name = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.id = ++idCounter;
    }

    public String getName() {
        return name;
    }

    public Main.Priority getPriority(){
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task ID: " + id + " Task: " + name + " Priority: " + priority + " Due: " + dueDate;
    }
}