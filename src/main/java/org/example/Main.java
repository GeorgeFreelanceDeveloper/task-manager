package org.example;

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

public class Main {
    static String[][] tasks;
    static String taskFile = "tasks.csv";
    static Scanner scanner = new Scanner(System.in);
    private static final String[] optionsArray = {"add","remove","list","exit"};

    // Hlavni menu aplikace
    public static void main(String[] args) {
        removeNewlines(taskFile);
        try {
            loadTasks(taskFile);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace(System.err);
            return;
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

    //Logika nacitani dat ze souboru do arraye, volam pokazde, kdyz se resetuje while loop v main
    public static void loadTasks(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        int rowCount = 0;
        while (reader.readLine() != null) {
            rowCount++;
        }
        reader.close();
        tasks = new String[rowCount][3];
        reader = new BufferedReader(new FileReader(filePath));
        String line;
        int currentRow = 0;
        while ((line = reader.readLine()) != null) {
            tasks[currentRow] = Arrays.stream(line.split(",")).map(String::trim).toArray(String[]::new);
            currentRow++;
        }
        reader.close();
    }

    //Pridavani novych tasku, rovnou zapisuje do csv souboru
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

    // Odebirani tasku, take rovnou zapisuje, posunuto do cisel od 1 pro vetsi user friendliness
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
        final var sb = new StringBuilder();

        for (var task : tasks) {
            sb.append(String.join(", ", task));
            sb.append("\n");
        }

        try {
            Files.writeString(Path.of(taskFile), sb.toString());
        } catch (IOException e) {
            System.err.println("Failed to save " + taskFile);
            e.printStackTrace(System.err);
        }

    }

    private static void exit() {
        System.out.println(ConsoleColors.RED + "Exiting program..." + ConsoleColors.RESET);
        scanner.close();
        saveTask();
        removeNewlines(taskFile);
        System.exit(0);
    }

    //Odebere prazdne radky, pokud je tam uzivatel pri rucnim editu prida a odebere posledni volny radek.
    public static void removeNewlines(String filePath) {
        try {
            Path path = Path.of(filePath);
            List<String> lines = Files.readAllLines(path);
            List<String> filteredLines = lines.stream().filter(line -> !line.trim().isEmpty()).collect(Collectors.toList());
            if (!filteredLines.isEmpty() && filteredLines.get(filteredLines.size() - 1).isEmpty()) {
                filteredLines.remove(filteredLines.size() - 1);
            }
            String noEmptyLines = String.join(System.lineSeparator(), filteredLines);
            Files.writeString(path, noEmptyLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}