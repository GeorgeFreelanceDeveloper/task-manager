package cz.student.adamos204;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static final String SAVE_FILE = "tasks.csv";
    private static final String[] allowedCommands = {"add", "delete", "list", "info", "exit", "view", "update"};

    public static void main(String[] args) {
        displayAllowedCommands(allowedCommands);
        loadTasks();

        while (true) {
            System.out.println("Waiting for your next command:");
            String command = scanner.nextLine();
            switch (command) {
                case "add" -> addTask();
                case "delete" -> deleteTask();
                case "list" -> listTasks();
                case "info" -> displayAllowedCommands(allowedCommands);
                case "exit" -> {
                    saveTasks();
                    System.exit(0);
                }
                case "view" -> viewTask();
                default -> System.out.println("Unknown command, for command list type info");
            }
        }
    }

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    public static void displayAllowedCommands(String[] allowedCommands) {
        System.out.println("Allowed system commands:");
        for (String item : allowedCommands) {
            System.out.println("# " + item);
        }
    }

    public static void addTask() {
        final String description = addTaskInput(
                "Enter new task name: ",
                "Task name cannot be empty. Enter a valid name.",
                input -> !input.isEmpty()
        );

        final Priority priority = Priority.valueOf(
                addTaskInput(
                        "Enter task priority (high, medium, low): ",
                        "Invalid priority. Please enter a valid option.",
                        input -> Arrays.stream(Priority.values()).anyMatch(p -> p.name().equalsIgnoreCase(input))
                ).toUpperCase()
        );

        final LocalDate dueDate = LocalDate.parse(
                addTaskInput(
                        "Enter the due date (YYYY-MM-DD): ",
                        "Invalid date format. Please enter a valid format.",
                        input -> {
                            try {
                                LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
                                return true;
                            } catch (Exception e) {
                                return false;
                            }
                        }
                )
        );

        final Task task = new Task(description, priority, dueDate);
        tasks.add(task);
        System.out.println("Task has been added");
    }

    public static void deleteTask() {
        boolean deleted = false;

        while (true) {
            try {
                System.out.println("Write id of a task you want to delete (number before task description):");
                final int inputId = Integer.parseInt(scanner.nextLine());
                boolean taskFound = false;

                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getId() == inputId) {
                        taskFound = true;
                        System.out.println("Are you sure you want to delete this task (Y/n)?");
                        System.out.println(tasks.get(i));

                        while (true) {
                            final String inputDelete = scanner.nextLine();

                            if (inputDelete.equalsIgnoreCase("y") || inputDelete.isEmpty()) {
                                tasks.remove(i);
                                System.out.println("Task has been deleted");
                                deleted = true;
                                break;
                            } else if (inputDelete.equalsIgnoreCase("n")) {
                                System.out.println("Nothing has changed");
                                break;
                            } else {
                                System.out.println("Invalid input. Please type 'Y' for yes or 'n' for no.");
                            }
                        }
                        break;
                    }
                }

                if (!taskFound) {
                    System.out.println("Task with ID '" + inputId + "' not found. Use list to view all your tasks.");
                }

                if (deleted || taskFound) {
                    break;
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numerical ID.");
            }
        }
    }

    private static void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Your list is empty. Add new task with 'add' command");
        } else {
            for (Task task : tasks) {
                System.out.println(task.toString());
            }
        }
    }

    private static void viewTask() {
        boolean exists = false;
        System.out.println("Type ID of the task");
        final int input = scanner.nextInt();
        for (Task task : tasks) {
            if (task.getId() == input) {
                System.out.println("Task info:");
                System.out.println(task);
                exists = true;
                break;
            }
        }

        if (!exists) {
            System.out.println("Task with ID '" + input + "' not found. Use list to view all your tasks");
        }

        scanner.nextLine();
    }

    private static void saveTasks() {
        final Path path = Paths.get(SAVE_FILE);
        final List<String> lines = tasks.stream()
                .map(task -> task.getName() + "," + task.getPriority() + "," + task.getDueDate())
                .collect(Collectors.toList());
        try {
            Files.write(path, lines, StandardCharsets.UTF_8);
            System.out.println("List has been saved to " + SAVE_FILE);
            scanner.close();
        } catch (IOException e) {
            System.err.println("Error has occurred while saving tasks: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void loadTasks() {
        final Path path = Paths.get(SAVE_FILE);
        if (!Files.exists(path)) {
            System.out.println("No save file found. Starting with an empty list.");
            System.out.println("If you think this is a mistake, make sure there is a file in the correct directory.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    final String description = parts[0];
                    final Priority priority = Priority.valueOf(parts[1].toUpperCase());
                    final LocalDate dueDate = LocalDate.parse(parts[2], DateTimeFormatter.ISO_LOCAL_DATE);
                    tasks.add(new Task(description, priority, dueDate));
                }
            }
            System.out.println("Tasks have been loaded.");
        } catch (IOException e) {
            System.err.println("Error has occurred while loading tasks: " + e.getMessage());
            System.err.println("Starting with an empty list");
            e.printStackTrace();
        }
    }

    private static String addTaskInput(String promptMessage, String errorMessage, Predicate<String> validator) {
        while (true) {
            System.out.println(promptMessage);
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            }
            System.out.println(errorMessage);
        }
    }

}
