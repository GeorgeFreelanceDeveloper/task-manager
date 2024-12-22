package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    static List<String> options = new ArrayList<>(List.of("add", "remove", "list", "exit"));
    static List<List<String>> tasks = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        readTasks("tasks.csv");
        selectOption();
    }

    public static void readTasks(String fileName) {
        Path path = Paths.get(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] words = line.split(",");
                tasks.add(new ArrayList<>(List.of(words)));
            }
        } catch (IOException e) {
            System.out.println("File not found.");
            e.printStackTrace(System.err);
        }
    }

    public static void selectOption() {
        System.out.println("Please select an option from the list below: " + ConsoleColors.BLUE);
        for (String option: options) {
            System.out.println(option);
        }
        System.out.println(ConsoleColors.RESET);
        while (scanner.hasNextLine()) {
            String option = scanner.nextLine();
            if (option.equals("exit")) {
                exit();
                break;
            }
            switch (option) {
                case "add" -> addTask();
                case "remove" -> removeTask();
                case "list" -> displayTasks();
                default -> System.out.println("Please select a correct option.");
            }
            System.out.println();
            System.out.println("Select next option:");
        }

    }

    public static void displayTasks() {
        for (List<String> task: tasks) {
            System.out.println(task);
        }
    }

    public static void addTask() {
        System.out.println("Enter the name of the task");
        String taskName = scanner.nextLine();
        System.out.println("Enter the due date of the task");
        String taskDate = scanner.nextLine();
        System.out.println("Has the task been completed? yes/no");
        String taskStatus = scanner.nextLine();
        while (!taskStatus.equalsIgnoreCase("yes") && !taskStatus.equalsIgnoreCase("no")) {
            System.out.println("Please enter yes or no.");
            taskStatus = scanner.nextLine();
        }
        boolean taskCompleted = taskStatus.equalsIgnoreCase("yes");
        List<String> task = Arrays.asList(taskName, taskDate, Boolean.toString(taskCompleted));
        tasks.add(task);
        System.out.println("Task added: " + task);
    }

    public static void removeTask() {
        int taskNumber;
        System.out.println("Enter the number of the task.");
        while (true) {
            try {
                String userInput = scanner.nextLine();
                taskNumber = Integer.parseInt(userInput);
                if (taskNumber > 0 && taskNumber <= tasks.size()) {
                    break;
                }
                System.out.println("Please enter the correct value.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter the numeric value.");
            }
        }
        int taskIndex = taskNumber - 1;
        tasks.remove(taskIndex);
        System.out.println("Task number " + taskNumber + " removed.");
    }

    public static void exit() {
        scanner.close();
        Path newPath = Paths.get("tasks.csv");
        List<String> updatedTasks = new ArrayList<>();
        try {
            for (List<String> task: tasks) {
                var sb = new StringBuilder();
                for (String word : task) {
                    sb.append(word).append(" ");
                }
                updatedTasks.add(sb.toString().trim());
            }
            Files.write(newPath, updatedTasks);
        } catch (IOException e) {
            System.out.println("Unable to save the file.");
            e.printStackTrace(System.err);
        }
    }

}
