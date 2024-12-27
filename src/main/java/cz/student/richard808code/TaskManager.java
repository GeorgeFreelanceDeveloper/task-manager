package cz.student.richard808code;

import cz.common.ConsoleColors;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class TaskManager {

    private static String[][] tasks;
    private static Scanner scanner;
    private static final String[] OPTIONS = {"add", "remove", "list", "exit"};

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        tasks = readTasks();

        while (true) {
            displayUserOptions();
            switch (scanner.nextLine().toLowerCase()) {
                case "add" -> addTask();
                case "remove" -> removeTask();
                case "list" -> listTasks();
                case "exit" -> exit();
                default -> System.err.println("Please select a correct option!");
            }
        }
    }

    public static void displayUserOptions() {
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "Please select an option from the following:" + ConsoleColors.RESET);
        for (String option : OPTIONS) {
            System.out.println(ConsoleColors.WHITE_BRIGHT + option + ConsoleColors.RESET);
        }
        System.out.println(ConsoleColors.BLUE_BRIGHT + "Type in the selected option:" + ConsoleColors.RESET);
    }

    public static String[][] readTasks() {
        final var path = Paths.get("tasks.csv");
        try {
            final var lines = Files.readAllLines(path);

            tasks = new String[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                tasks[i] = lines.get(i).split(",");
            }
        } catch (IOException e) {
            System.err.println("Failed to load tasks.csv");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        return tasks;
    }

    public static String[][] addTask() {
        System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Please enter the description of the task including importance: " + ConsoleColors.RESET);
        final String description = scanner.nextLine();

        String deadline;
        while (true) {
            System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Please enter the deadline(YYYY-MM-DD): " + ConsoleColors.RESET);
            deadline = scanner.nextLine();
            if (deadline.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                final String[] splitDate = deadline.split("-");
                final var month = Integer.parseInt(splitDate[1]);
                final var day = Integer.parseInt(splitDate[2]);
                if ((month >= 1 && month <= 12) && (day >= 1 && day <= 31)) {
                    break;
                } else {
                    System.err.println("Please enter a valid month(1-12) or a valid day(1-31)");
                }
            } else {
                System.err.println("Invalid format! Please use YYYY-MM-DD format!");
            }
        }

        Boolean done = null;
        while (true) {
            System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT + "Please enter true if the task is done or enter false if it is yet to be done: " + ConsoleColors.RESET);
            if (scanner.hasNextBoolean()) {
                done = scanner.nextBoolean();
                scanner.nextLine();
                break;
            } else {
                System.err.println("Invalid input. Please enter true or false.");
                scanner.next();
            }
        }

        tasks = Arrays.copyOf(tasks, tasks.length + 1);
        final var task = new String[3];
        task[0] = description;
        task[1] = deadline;
        task[2] = String.valueOf(done);

        tasks[tasks.length - 1] = task;

        System.out.println(ConsoleColors.GREEN + "Task added successfully!" + ConsoleColors.RESET);

        return tasks;
    }

    public static void listTasks() {
        if (tasks.length == 0) {
            System.out.println(ConsoleColors.RED + "No tasks found!" + ConsoleColors.RESET);
        }
        for (int i = 0; i < tasks.length; i++) {
            System.out.println(ConsoleColors.BLUE_BOLD + "Task #" + (i + 1) + ": " + ConsoleColors.RESET
                    + tasks[i][0] + ConsoleColors.YELLOW + " Deadline: " + ConsoleColors.RESET
                    + tasks[i][1] + ConsoleColors.PURPLE + " Completion: " + ConsoleColors.RESET
                    + tasks[i][2] + "\n");
        }
    }

    public static String[][] removeTask() {
        listTasks();
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Insert the number of the task to remove: " + ConsoleColors.RESET);

        while (true) {
            if (scanner.hasNextInt()) {
                final int taskIndex = scanner.nextInt();
                scanner.nextLine();
                if (taskIndex > 0 && taskIndex <= tasks.length) {
                    tasks = ArrayUtils.remove(tasks, taskIndex - 1);
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT + "Task removed successfully!" + ConsoleColors.RESET);
                    break;
                } else {
                    System.err.println("Invalid index, try again.");
                }
            } else {
                System.err.println("Please enter a number!");
                scanner.next();
            }
        }

        return tasks;
    }

    public static void saveTasksToFile() {
        final var path = Paths.get("tasks.csv");

        final var sb = new StringBuilder();

        for (var task : tasks) {
            sb.append(String.join(", ", task));
            sb.append("\n");
        }

        try {
            Files.writeString(path, sb.toString());
        } catch (IOException e) {
            System.err.println("Failed to save tasks.csv");
            e.printStackTrace(System.err);
        }
    }

    public static void exit() {
        saveTasksToFile();
        System.out.println(ConsoleColors.RED + "The program finished!" + ConsoleColors.RESET);
        scanner.close();
        System.exit(0);
    }
}
