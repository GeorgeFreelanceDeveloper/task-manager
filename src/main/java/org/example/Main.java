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

    // Hlavni menu aplikace
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                loadData(taskFile);
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
                return;
            }
            options();
            String input = scanner.nextLine();
            switch (input) {
                case "add":
                    addTask(scanner);
                    System.out.println("toto je add");
                    break;
                case "remove":
                    removeTask(scanner);
                    break;
                case "list":
                    listTasks();
                    break;
                case "exit":
                    System.out.println(ConsoleColors.RED + "Exiting program..." + ConsoleColors.RESET);
                    return;
                default:
                    System.out.println(ConsoleColors.RED + "Invalid Input!" + ConsoleColors.RESET);
                    break;
            }
        }
    }

    public static void options() {
        String[] optionsArray = {"add","remove","list","exit"};
        System.out.println(ConsoleColors.BLUE + "Please select an option:" + ConsoleColors.RESET);
        for (String option : optionsArray) {
            System.out.println(option);
        }
    }

    //Logika nacitani dat ze souboru do arraye, volam pokazde, kdyz se resetuje while loop v main
    public static void loadData(String filePath) throws IOException {
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

        removeNewlines(taskFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(taskFile, true))) {
            writer.newLine();
            writer.write(name + ", " + date + ", " + isCompleted);
            System.out.println(ConsoleColors.GREEN + "Task added successfully" + ConsoleColors.RESET);
        } catch (IOException e) {
            System.out.println(ConsoleColors.RED + "Error writing to file: " + e.getMessage() + ConsoleColors.RESET);
        }
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

        removeNewlines(taskFile);
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
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(taskFile))) {
                for (int i = 0; i < tasks.length; i++) {
                    writer.write(tasks[i][0] + ", " + tasks[i][1] + ", " + tasks[i][2]);
                    if (i < tasks.length - 1) {
                        writer.newLine();
                    }
                }
                System.out.println(ConsoleColors.GREEN + "Task removed successfully" + ConsoleColors.RESET);
            } catch (IOException e) {
                System.out.println(ConsoleColors.RED + "Error writing to file: " + e.getMessage() + ConsoleColors.RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED + "Invalid input, please enter a valid task number" + ConsoleColors.RESET);
        }
    }

    //V kodu nemuze dojit, ze by zustal radek prazdny v csv souboru, pridano, kdyby doslo v souboru k rucni uprave.
    public static void removeNewlines(String filePath) {
        try {
            Path path = Path.of(filePath);
            List<String> lines = Files.readAllLines(path);
            boolean hasEmptyLines = lines.stream().anyMatch(line -> line.trim().isEmpty());
            if (hasEmptyLines) {
                List<String> filteredLines = lines.stream().filter(line -> !line.trim().isEmpty()).collect(Collectors.toList());
                String noEmptyLines = String.join(System.lineSeparator(), filteredLines);
                Files.writeString(path, noEmptyLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }
}