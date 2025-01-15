package cz.student.ondrejmal;

import cz.common.ConsoleColors;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TaskManager {

    private static final String FILE_PATH = "tasks.csv";
    private static ArrayList<String[]> tasks;
    private static Scanner scanner;

    public static void main(String[] args) {
        System.out.println(ConsoleColors.BLUE + "Task Manager Coderslab" + ConsoleColors.RESET);
        System.out.println("System will now load assigned task files.");

        tasks = loadTasks(FILE_PATH);

        scanner = new Scanner(System.in);

        printOptions();

        while (true) {
            final String command = scanner.nextLine().trim().toLowerCase();
            System.out.println("Your command is: " + command.toUpperCase());

            switch (command) {
                case "add" -> addingTask(tasks, scanner);
                case "remove" -> removingTask(tasks, scanner);
                case "list" -> listingTasks(tasks);
                case "exit" -> programExit(tasks, FILE_PATH, scanner);
                default -> System.out.println("Invalid task selection. Please enter a valid task");
            }
        }
    }

    public static void printOptions() {
        System.out.println("Please type name of possible operations to proceed.");
        System.out.println("Select the task from the list below:");
        System.out.println("1) Add");
        System.out.println("2) Remove");
        System.out.println("3) List");
        System.out.println("4) Exit");
        System.out.println("Command in not case sensitive.");
    }

    public static ArrayList<String[]> loadTasks(String filepath) {
        final ArrayList<String[]> fileTasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null)
                fileTasks.add(line.split(","));
        } catch (IOException e) {
            System.err.println("Failed to load tasks.csv.");
            e.printStackTrace(System.err);
        }
        return fileTasks;
    }

    public static void addingTask(ArrayList<String[]> tasks, Scanner scanner) {

        System.out.println("Enter note:");
        String note = scanner.nextLine();
        while (note.isEmpty()) {
            System.out.println("Please enter text to create a note.");
            note = scanner.nextLine();
        }

        System.out.println("Enter date in YYYY-MM-DD format:");
        String date = scanner.nextLine();
        while (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Format is not valid. Please enter the value again.");
            date = scanner.nextLine();
        }

        System.out.println("Enter true/false value for statement of task completion:");
        String statusInput = scanner.nextLine();

        while (!statusInput.equalsIgnoreCase("true") && !statusInput.equalsIgnoreCase("false")) {
            System.out.println("Invalid input. Please enter true or false:");
            statusInput = scanner.nextLine();
        }

        tasks.add(new String[]{note, date, statusInput});
        System.out.println("Task added successfully.");
    }

    public static void removingTask(ArrayList<String[]> tasks, Scanner scanner) {

        listingTasks(tasks);

        System.out.println("Please choose row number of task to be erased:");
        while (true) {
            try {
                final int taskToBeErased = Integer.parseInt(scanner.nextLine());
                if (taskToBeErased < 1 && taskToBeErased > tasks.size()) {
                    System.out.println("Invalid input. Please insert valid row number.");
                }
                final String[] removedTask = tasks.remove(taskToBeErased - 1);
                System.out.println("Removed task: " + String.join(", ", removedTask));
                System.out.println("Task removed successfully.");
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid value. Please enter a valid row number to erase it.");
            }
        }
    }

    public static void listingTasks(ArrayList<String[]> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ": " + Arrays.toString(tasks.get(i)));
        }
    }

    public static void programExit(ArrayList<String[]> tasks, String filePath, Scanner scanner) {
        saveTasksToFile(tasks, filePath);

        scanner.close();

        System.out.println(ConsoleColors.RED + "Exiting the program." + ConsoleColors.RESET);
        System.exit(0);
    }

    public static void saveTasksToFile(ArrayList<String[]> tasks, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (String[] task : tasks) {
                writer.println(String.join(", ", task));
            }
            System.out.println("Tasks saved successfully.");

        } catch (IOException e) {
            System.err.println("Problem with saving the file. Unable to locate the folder.");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}