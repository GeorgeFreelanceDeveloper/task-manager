package cz.student.osydorchuk89;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import cz.common.ConsoleColors;

public class TaskManager {

    private static List<String> options = new ArrayList<>(List.of("add", "remove", "list", "exit"));
    private static List<List<String>> tasks = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static String TASKS_FILE = "tasks.csv";

    public static void main(String[] args) {
        loadTasks();
        System.out.println("Please select an option from the list below: " + ConsoleColors.BLUE);
        for (String option: options) {
            System.out.println(option);
        }
        System.out.println(ConsoleColors.RESET);
        while (true) {
            switch (scanner.nextLine().toLowerCase()) {
                case "add" -> addTask();
                case "remove" -> removeTask();
                case "list" -> displayTasks();
                case "exit" -> exit();
                default -> System.out.println("Please select a correct option.");
            }
            System.out.println();
            System.out.println("Select next option:");
        }
    }

    public static void loadTasks() {
        final Path path = Paths.get(TASKS_FILE);
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                final String[] items = line.split(",");
                tasks.add(new ArrayList<>(List.of(items)));
            }
        } catch (IOException e) {
            System.err.println("Failed to load tasks.csv: " + e.getMessage());
            System.err.println("Application will work with empty tasks list");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public static void displayTasks() {
        for (List<String> task: tasks) {
            System.out.println(task);
        }
    }

    public static void addTask() {
        System.out.println("Enter the name of the task");
        final String taskName = scanner.nextLine();
        System.out.println("Enter the due date of the task");
        final String taskDate = scanner.nextLine();
        System.out.println("Has the task been completed? yes/no");
        String taskStatus = scanner.nextLine();
        while (!taskStatus.equalsIgnoreCase("yes") && !taskStatus.equalsIgnoreCase("no")) {
            System.out.println("Please enter yes or no.");
            taskStatus = scanner.nextLine();
        }
        final List<String> task = Arrays.asList(taskName, taskDate, taskStatus);
        tasks.add(task);
        System.out.println("Task added: " + task);
    }

    public static void removeTask() {
        displayTasks();
        int taskNumber;
        System.out.println("Enter the number of the task.");
        while (true) {
            try {
                final String userInput = scanner.nextLine();
                taskNumber = Integer.parseInt(userInput);
                if (taskNumber > 0 && taskNumber <= tasks.size()) {
                    break;
                }
                System.out.println("Please enter the correct value.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter the numeric value.");
            }
        }
        final int taskIndex = taskNumber - 1;
        tasks.remove(taskIndex);
        System.out.println("Task number " + taskNumber + " removed.");
    }

    public static void saveTasks() {
        final Path newPath = Paths.get("tasks.csv");
        final List<String> updatedTasks = new ArrayList<>();
        try {
            for (List<String> task: tasks) {
                final var sb = new StringBuilder();
                for (String word : task) {
                    sb.append(word).append(", ");
                }
                updatedTasks.add(sb.toString().trim());
            }
            Files.write(newPath, updatedTasks);
        } catch (IOException e) {
            System.err.println("Unable to save the file.");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public static void exit() {
        scanner.close();
        saveTasks();
        System.exit(0);
    }

}
