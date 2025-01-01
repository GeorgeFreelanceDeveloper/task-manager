package cz.student.adambrg;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.ArrayUtils;

public class Main {
    static final String fileName = "tasks.csv";  //soubor, ze ktereho ziskavame informace
    static final String[] OPTIONS = {"add", "remove", "list", "exit"};
    static String[][] tasks; // pole, do ktereho se zapisuji informace ze souboru
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) { //hlavni metoda, ktera načte data ze souboru a zavola "setOptions" a spusti tim program
        retrieveData();
        setOptions();
    }

    public static void displayOptions() { //metoda, ktera nam vypise vsechny moznosti
        System.out.println(ConsoleColors.BLUE + "Please select an option: ");
        for (String option : OPTIONS) {
            System.out.println(ConsoleColors.RESET + option);
        }
    }

    public static void setOptions() { //metoda, ktera umoznuje uzivateli vybrat vystup
        while (true) {
            displayOptions();
            String input = scanner.nextLine();
            switch (input.toLowerCase()) {
                case "add" -> addTask();
                case "remove" -> removeTask();
                case "list" -> showList();
                case "exit" -> exit();
                default -> System.out.print(ConsoleColors.BLUE + "Please enter a valid option: ");
            }
        }
    }

    public static void retrieveData() { //metoda, ktera ziskava informace ze souboru a zapisuje je do pole "tasks"
        try {
            final var lines = Files.readAllLines(Path.of("tasks.csv"));

            tasks = new String[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                tasks[i] = lines.get(i).split(", ");
            }
        } catch (IOException e) {
            System.err.println("Failed to load tasks.csv");
            e.printStackTrace(System.err);
        }
    }

    public static void addTask() {  //metoda, která nam umoznuje zapisovat informace do souboru
        tasks = Arrays.copyOf(tasks, tasks.length + 1);
        tasks[tasks.length - 1] = new String[3];
        System.out.println(ConsoleColors.BLUE + "Please enter a task: ");
        String name = scanner.nextLine();
        tasks[tasks.length - 1][0] = name;
        System.out.println(ConsoleColors.BLUE + "Enter a date in the format (YYYY-MM-DD)");
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            String date = scanner.nextLine();
            try {

                LocalDate parsedDate = LocalDate.parse(date, myFormatObj);
                break;
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date format, please try again. (YYYY-MM-DD)");
            }
            tasks[tasks.length - 1][1] = date;
        }
        System.out.println(ConsoleColors.BLUE + "Is the task important? (true/false): ");
        String isImportant = scanner.nextLine();
        tasks[tasks.length - 1][2] = isImportant;
        System.out.println(ConsoleColors.GREEN + "TASK ADDED SUCCESSFULLY" + ConsoleColors.RESET);
    }

    public static void saveTasksToFile() { // metoda, ktera uklada zmeny do souboru pri "removeTask"
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (int i = 0; i < tasks.length; i++) {
                if (i == tasks.length - 1) {
                    writer.print(String.join(", ", tasks[i]));
                } else {
                    writer.println(String.join(", ", tasks[i]));
                }
            }
        } catch (IOException e) {
            System.err.println("Saving failed.");
            e.printStackTrace(System.err);
        }
    }

    public static void removeTask() { // metoda, ktera odstranuje informace ze souboru
        while (true) {
            try {
                showList();
                System.err.println(ConsoleColors.RED + "Enter the task number to remove: " + ConsoleColors.RESET);

                String input = scanner.nextLine();
                int index = Integer.parseInt(input) - 1;

                if (index < 0) {
                    System.err.println(ConsoleColors.RED + "Index cannot be negative." + ConsoleColors.RESET);
                    continue;
                }
                tasks = ArrayUtils.remove(tasks, index);
                System.out.println(ConsoleColors.GREEN + "TASK SUCCESSFULLY DELETED!" + ConsoleColors.RESET);

                break;

            } catch (NumberFormatException e) {
                System.err.println(ConsoleColors.RED + "Input must be a NUMBER");
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Index is out of bounds");
                e.getStackTrace();
            }
        }
    }

    public static void showList() { //metoda, ktera vypise list vsech tasku
        System.out.println(ConsoleColors.BLUE + "List of all tasks:" + ConsoleColors.RESET);
        for (int i = 0; i < tasks.length; i++) {
            System.out.println((i + 1) + " - " + Arrays.toString(tasks[i]));
        }
        if (tasks.length == 0) {
            System.err.println(ConsoleColors.RED + "No tasks found");
        }
    }

    public static void exit() {  //metoda k ukonceni programu
        System.out.println(ConsoleColors.RED + "Exiting application..");
        saveTasksToFile();
        scanner.close();
        System.exit(0);
    }
}

