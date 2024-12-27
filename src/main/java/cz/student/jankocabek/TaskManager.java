package cz.student.jankocabek;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import static cz.common.ConsoleColors.*;

public class TaskManager {
    private static Scanner scanner;
    private static String[][] tasks;

    public static void main(String[] args) {
        System.out.printf("%sStarting Task Manager 1.0%s\n", GREEN_BOLD_BRIGHT, RESET);
        loadTasks();
        displayMenu();
        scanner = new Scanner(System.in);
        do {
            System.out.printf("\n%sEnter your choice:%s", BLUE_BOLD_BRIGHT, RESET);
            switch (scanner.nextLine().toLowerCase()) {
                case "add" -> addTask();
                case "remove" -> removeTask();
                case "list" -> showTasks();
                case "exit" -> exit();
                case "?" -> displayMenu();
                default -> {
                    System.out.printf("%sInvalid option!%s\n", RED_BOLD_BRIGHT, RESET);
                    System.out.printf("%sEnter correct options or \"%s?%s\" for menu%s\n", BLUE_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, BLUE_BOLD_BRIGHT, RESET);
                }
            }
        } while (true);
    }

    //loading tasks from file checking errors in case of missing file try to create new for smooth continue.
    private static void loadTasks() {
        final Path pathToFile = Paths.get("tasks.csv");
        tasks = new String[0][];//here will be tasks data
        System.out.printf("%sSystem will now read all the tasks from the task.csv file%s\n", YELLOW_BOLD_BRIGHT, RESET);
        try {
            if (Files.exists(pathToFile)) {
                tasks = listTo2DArr(Files.readAllLines(pathToFile));
                System.out.printf("%sTask were successfully loaded from tasks.csv%s\n", GREEN_BOLD_BRIGHT, RESET);
            } else {
                System.out.printf("%sFile tasks.csv not found!%s\n", RED_BOLD_BRIGHT, RESET);
                System.out.printf("%sProgram will try create new file%s\n", YELLOW_BOLD_BRIGHT, RESET);
                Files.createFile(pathToFile);
                System.out.printf("%sFile tasks.csv successfully created%s\n", GREEN_BOLD_BRIGHT, RESET);
            }
        } catch (IOException e) {
            System.err.println("Error while reading tasks.csv file");
            e.printStackTrace(System.err);
            System.err.println("Program will try create new file");
            try {
                Files.createFile(pathToFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.printf("%sFile tasks.csv successfully created%s\n", GREEN_BOLD_BRIGHT, RESET);
        }
    }

    private static void displayMenu() {
        System.out.printf("\n%s#  Welcome to the Task Manager  #\n", GREEN_BOLD_BRIGHT);
        System.out.print("#                               #\n");
        System.out.printf("#   %s1. Add a new task:  %sadd     %s#\n", BLUE_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, GREEN_BOLD_BRIGHT);
        System.out.printf("#   %s2. Remove a task:   %sremove  %s#\n", BLUE_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, GREEN_BOLD_BRIGHT);
        System.out.printf("#   %s3. Show all tasks:  %slist    %s#\n", BLUE_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, GREEN_BOLD_BRIGHT);
        System.out.printf("#   %s4. Save and Exit:   %sexit    %s#\n", BLUE_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, GREEN_BOLD_BRIGHT);
        System.out.printf("#################################%s\n", RESET);
    }

    private static void showTasks() {
        System.out.printf("\n%sTask List:%s\n", GREEN_BOLD_BRIGHT, RESET);
        for (int i = 0; i < tasks.length; i++) {//this loop controlling if task is important or not for different colors in output
            final var task = tasks[i];
            switch (task[2].trim()) {
                case "true" ->
                        System.out.printf("%s%d. %s%s%s\n", GREEN_BOLD_BRIGHT, i + 1, RED_BOLD_BRIGHT, String.join(",", task), RESET);
                case "false" ->
                        System.out.printf("%s%d. %s%s%s\n", GREEN_BOLD_BRIGHT, i + 1, BLUE_BOLD_BRIGHT, String.join(",", task), RESET);
            }
        }
    }

    private static void addTask() {
        final var sizeTaskData = 3;
        final int taskNum = tasks.length + 1;
        final String[] task = new String[sizeTaskData];

        System.out.printf("\n%sAdding new task Number.%d:%s\n", GREEN_BOLD_BRIGHT, taskNum, RESET);
        System.out.printf("%sWrite the description of the task:%s ", BLUE_BOLD_BRIGHT, RESET);
        task[0] = scanner.nextLine();

        System.out.printf("%sWrite the date when task end -in the format %sYYYY-MM-DD%s:%s ", BLUE_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, BLUE_BOLD_BRIGHT, RESET);
        task[1] = " %s".formatted(loadAndCheckDate());//because space after comma

        System.out.printf("%sWrite \"%strue%s\" or \"%sfalse%s\" if task is important or not:%s ", BLUE_BOLD_BRIGHT, YELLOW_BRIGHT, BLUE_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, BLUE_BOLD_BRIGHT, RESET);
        while (!scanner.hasNext("true") && !scanner.hasNext("false")) {
            scanner.nextLine();//for cleaning buffer before new try
            System.out.printf("\n%sInvalid option - put only %strue%s or %sfalse%s:%s ", RED_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, RED_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, RED_BOLD_BRIGHT, RESET);
        }
        task[2] = " %s".formatted(scanner.next());//next only because if somebody write something after true/false then would catch also
        scanner.nextLine();//cleaning buffer
        tasks = ArrayUtils.add(tasks, task);
        System.out.printf("%sTask  Number %d added%s\n", GREEN_BOLD_BRIGHT, taskNum, RESET);
    }

    //my method for controlling if string is in right format and the date is at least today
    private static String loadAndCheckDate() {
        while (true) try {
            final var today = LocalDate.now();
            final String input = scanner.nextLine();
            final var date = LocalDate.parse(input);
            //date must be at least today if so method end and return input
            if (!date.isBefore(today)) return input;
            System.out.printf("%sThis date already was!!%s\n", RED_BOLD_BRIGHT, RESET);
            System.out.printf("\n%sWrite date which is at least %sToday:%s ",
                    BLUE_BOLD_BRIGHT, RED_BOLD_BRIGHT, RESET);
        } catch (DateTimeParseException e) {
            System.out.printf("%sInvalid date format!! %s\n", RED_BOLD_BRIGHT, RESET);
            System.out.printf("\n%sWrite date in the format %sYYY-MM-DD%s:%s ",
                    BLUE_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, BLUE_BOLD_BRIGHT, RESET);
        }
    }

    private static void removeTask() {
        System.out.printf("\n%sWrite the number of the task you want to remove from the list%s\n", BLUE_BOLD_BRIGHT, RESET);
        System.out.printf("%sif you don't know the number of the task write \"%s?%s\" or \"%sexit%s\" to return to the main menu:%s", BLUE_BOLD_BRIGHT, YELLOW_BOLD_BRIGHT, BLUE_BOLD_BRIGHT, YELLOW_BRIGHT, BLUE_BOLD_BRIGHT, RESET);
        do {
            final var input = scanner.nextLine().toLowerCase();
            if (NumberUtils.isDigits(input)) {
                int num = Integer.parseInt(input);//
                if (num >= 1 && num <= tasks.length) {
                    tasks = ArrayUtils.remove(tasks, num - 1);//real index is from 0 but user see tasks from 1
                    System.out.printf("Task N.%d deleted successfully", num);
                    break;
                }
                System.out.printf("In the list isn't task number %d, please try again:", num);
                continue;
            }
            if (input.equals("?")) {
                showTasks();
                System.out.printf("%sWrite the number of tasks you want to remove from the list:%s ", BLUE_BOLD_BRIGHT, RESET);
                continue;
            }
            if (input.equals("exit")) break;
            System.out.println("Invalid input!");
            System.out.print("if you don't know the number of tasks write \"?\" or \"exit\" to return to the main menu: :");
        } while (true);
    }

    private static void saveTasks() {
        final Path pathFile = Paths.get("tasks.csv");
        final var sb = new StringBuilder();

        for (var task : tasks) {
            sb.append(String.join(",", task));
            sb.append(System.lineSeparator());
        }
        try {
            Files.writeString(pathFile, sb);
        } catch (IOException e) {
            System.err.println("failed to save tasks.csv!");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static void exit() {
        saveTasks();
        scanner.close();
        System.exit(0);
    }

    private static String[][] listTo2DArr(List<String> list) {
        final String[][] arr = new String[list.size()][3];

        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i).split(",");
        }
        return arr;
    }
}