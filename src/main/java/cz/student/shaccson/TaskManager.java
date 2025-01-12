package cz.student.shaccson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import cz.common.ConsoleColors;

public class TaskManager {
    static String[][] tasks;
    static String taskFile = "tasks.csv";
    private static Scanner scanner = new Scanner(System.in);
    private static final String[] optionsArray = {"add","remove","list","exit"};

    // Hlavni menu aplikace
    public static void main(String[] args) {
        try {
            loadTasks(taskFile);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
        while (true) {
            displayOptions();
            switch (scanner.nextLine().toLowerCase()) {
                case "add"      -> addTask(scanner);
                case "remove"   -> removeTask(scanner);
                case "list"     -> listTasks();
                case "exit"     -> exit();
                default -> System.out.println(ConsoleColors.RED + "Invalid Input!" + ConsoleColors.RESET);
            }
        }
    }

    public static void displayOptions() {
        System.out.println(ConsoleColors.BLUE + "Please select an option:" + ConsoleColors.RESET);
        for (String option : optionsArray) {
            System.out.println(option);
        }
    }

    //Logika nacitani dat ze souboru do arraye
    public static void loadTasks(String filePath) throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            lines = reader.lines().toList();
        }

        tasks = new String[lines.size()][3];

        for (int i = 0; i < lines.size(); i++) {
            tasks[i] = Arrays.stream(lines.get(i).split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
        }
    }

    //Pridavani novych tasku
    public static void addTask(Scanner scanner) {
        System.out.println("Enter task name:");
        String name = scanner.nextLine();
        String date;
        while (true) {
            System.out.println("Enter task date (YYYY-MM-DD):");
            date = scanner.nextLine();
            try {
                LocalDate.parse(date);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please enter a valid date in YYYY-MM-DD format.");
            }
        }
        String isCompleted;
        while (true) {
            System.out.println("Enter task completion state (true/false):");
            isCompleted = scanner.nextLine().toLowerCase();
            if (isCompleted.equals("true") || isCompleted.equals("false")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'true' or 'false'.");
            }
        }

        String[] newTask = {name, date, isCompleted};
        tasks = Arrays.copyOf(tasks, tasks.length + 1);
        tasks[tasks.length - 1] = newTask;
        System.out.println(ConsoleColors.GREEN + "Task added successfully" + ConsoleColors.RESET);
    }

    public static void listTasks() {
        if (tasks.length == 0) {
            System.out.println("No tasks currently available");
        } else {
            System.out.println("Task list:");
            for (String[] task : tasks) {
                System.out.println(Arrays.toString(task));
            }
        }
    }

    // Odebirani tasku, posunuto do cisel od 1 pro vetsi user friendliness
    public static void removeTask(Scanner scanner) {
        if (tasks.length == 0) {
            System.out.println("No tasks available to remove");
            return;
        }
        System.out.println("Type in task's number to remove it:");
        for (int i = 0; i < tasks.length; i++) {
            System.out.println((i + 1) + " - " + tasks[i][0]);
        }

        int taskIndex = -1;
        try {
            taskIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (taskIndex < 0 || taskIndex >= tasks.length) {
                System.out.println(ConsoleColors.RED + "Invalid task number" + ConsoleColors.RESET);
                return;
            }
            for (int i = taskIndex; i < tasks.length - 1; i++) {
                tasks[i] = tasks[i + 1];
            }
            tasks = Arrays.copyOf(tasks, tasks.length - 1);
            System.out.println(ConsoleColors.GREEN + "Task removed successfully" + ConsoleColors.RESET);
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED + "Invalid input, please enter a valid task number" + ConsoleColors.RESET);
        }
    }

    private static void saveTask() {
        String result = Arrays.stream(tasks)
                .map(task -> String.join(",", task))
                .collect(Collectors.joining(System.lineSeparator()));

        try {
            Files.writeString(Path.of(taskFile), result,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save " + taskFile);
            e.printStackTrace(System.err);
        }
    }

    private static void exit() {
        System.out.println(ConsoleColors.RED + "Exiting program..." + ConsoleColors.RESET);
        scanner.close();
        saveTask();
        System.exit(0);
    }
}