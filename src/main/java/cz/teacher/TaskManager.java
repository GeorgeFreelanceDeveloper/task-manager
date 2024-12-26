package cz.teacher;

import cz.common.ConsoleColors;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class TaskManager {

    private static final Path TASK_FILE_PATH = Paths.get("tasks.csv");
    private static final String[] OPTIONS = {"add", "remove", "list", "exit"};
    public static final String DELIMITER = ", ";
    public static final String SEPARATOR = "-----------------";
    public static final int TASK_ITEMS_COUNT = 3;

    private static String[][] tasks;
    private static Scanner scanner;

    public static void main(final String[] args) {
        init();

        while (true) {
            printOptions(OPTIONS);
            final var input = scanner.nextLine();
            switch (input) {
                case "add" -> add();
                case "remove" -> remove();
                case "list" -> list();
                case "exit" -> exit();
                default -> System.out.println("Please select a correct option.");
            }
        }
    }

    private static void init() {
        loadTasks();

        scanner = new Scanner(System.in);

        // Logo
        System.out.println(ConsoleColors.GREEN_BOLD + SEPARATOR);
        System.out.println(ConsoleColors.GREEN_BOLD + "Task manager");
        System.out.println(ConsoleColors.GREEN_BOLD + SEPARATOR);
    }

    public static void printOptions(final String[] tab) {
        System.out.println(ConsoleColors.BLUE);
        System.out.println("Please select an option: " + ConsoleColors.RESET);
        for (String option : tab) {
            System.out.println(option);
        }
        System.out.print(ConsoleColors.RESET);
    }

    private static void list() {
        System.out.println(ConsoleColors.PURPLE + "Id:[Note, Date, IsImportant ]");
        System.out.println(ConsoleColors.RESET + SEPARATOR);
        for (int i = 0; i < tasks.length; i++) {
            System.out.println(i + ":" + Arrays.toString(tasks[i]));
        }
    }

    private static void add() {
        System.out.println("Task name:");
        final var name = scanner.nextLine();

        System.out.println("Task date [yyyy-mm-dd]:");
        final var date = loadAndValidateDate();

        System.out.println("Task is important [true/false]: :");
        final var isImportant = loadAndValidateIsImportant();

        final var task = new String[TASK_ITEMS_COUNT];
        task[0] = name;
        task[1] = date;
        task[2] = isImportant;
        tasks = Arrays.copyOf(tasks, tasks.length + 1);
        tasks[tasks.length - 1] = task;
    }

    private static String loadAndValidateDate() {
        while (true) {
            final var input = scanner.nextLine();
            try {
                LocalDate.parse(input);
                return input;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please enter a valid date in YYYY-MM-DD format.");
            }
        }
    }

    private static String loadAndValidateIsImportant() {
        while (true) {
            final var isImportant = scanner.nextLine().toLowerCase();
            if (isImportant.equals("true") || isImportant.equals("false")) {
                return isImportant;
            } else {
                System.out.println("Invalid input. Please enter 'true' or 'false'.");
            }
        }
    }

    private static void remove() {
        list();
        while (true) {
            System.out.println("Select id task for delete:");
            final var id = scanner.nextInt();

            if (id > 0 && id < tasks.length) {
                tasks = ArrayUtils.remove(tasks, id);
                break;
            } else {
                System.out.println("Please select a valid id.");
            }
        }
    }

    private static void exit() {
        saveTask();
        scanner.close();
        System.exit(0);
    }

    private static void loadTasks() {
        try {
            final var lines = Files.readAllLines(TASK_FILE_PATH);
            tasks = new String[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                tasks[i] = lines.get(i).split(DELIMITER);
            }
        } catch (IOException e) {
            System.err.println("Failed to load tasks.csv");
            e.printStackTrace(System.err);
            System.exit(1);
        }

    }

    private static void saveTask() {
        final var sb = new StringBuilder();

        for (var task : tasks) {
            sb.append(String.join(DELIMITER, task));
            sb.append(System.lineSeparator());
        }

        try {
            Files.writeString(TASK_FILE_PATH, sb.toString());
        } catch (IOException e) {
            System.err.println("Failed to save tasks.csv");
            e.printStackTrace(System.err);
            System.exit(1);
        }

    }
}
