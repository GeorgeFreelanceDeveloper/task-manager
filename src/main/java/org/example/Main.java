package org.example;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    static String[][] tasks;

    public static void main(String[] args) {

        tasks = loadDataToTab("tasks.csv");
        options();
        var input = new Scanner(System.in);
        while (input.hasNextLine()) {
            var option = input.nextLine();
            switch (option) {
                case "exit":
                    storeData();
                    System.out.println("Data were stored successfully");
                    System.out.println(ConsoleColors.RED + "Sayonara!");
                    System.exit(0);
                    break;
                case "add":
                    addTask();
                    break;
                case "remove":
                    removeTask();
                    break;
                case "list":
                    taskList();
                    break;
                default:
                    System.out.println("Invalid option");
            }
            options();
        }
        System.out.println(Arrays.deepToString(tasks));
    }

    public static void options() {
        String[] options = {"add", "remove", "list", "exit"};
        System.out.println(ConsoleColors.BLUE);
        System.out.println("Please select the option for task manager:");
        for (var option : options) {
            System.out.println(ConsoleColors.BLUE + option);
        }
        System.out.println(ConsoleColors.RESET);
    }

    public static String[][] loadDataToTab(String fileName) {
        Path file = Paths.get(fileName);
        String[][] dataTab = null;

        if (!Files.exists(file)) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        try {
            List<String> lines = Files.readAllLines(file);
            dataTab = new String[lines.size()][];
            for (int i = 0; i < lines.size(); i++) {
                dataTab[i] = lines.get(i).split(",");
                System.arraycopy(lines.get(i).split(","), 0, dataTab[i], 0, dataTab[i].length);
            }
        } catch (IOException e) {
            System.err.println("Error reading file.");
            e.printStackTrace(System.err);
        }
        return dataTab;
    }

    public static void addTask() {

        var input = new Scanner(System.in);
        System.out.println("Please enter the task description:");
        var taskDescription = input.nextLine();
        System.out.println("Then, please enter the due date:");
        var dueDate = input.nextLine();
        System.out.println("And lastly, please enter the importance of the task (true/false):");
        var importance = input.nextLine();


        tasks = Arrays.copyOf(tasks, tasks.length +1);
        tasks[tasks.length-1] = new String[3];
        tasks[tasks.length-1][0] = taskDescription;
        tasks[tasks.length-1][1] = dueDate;
        tasks[tasks.length-1][2] = importance;
    }

    public static void taskList() {
        for (int i = 0; i < tasks.length; i++) {
            System.out.print(i + 1 + ". ");
            for (int j = 0; j < tasks[i].length; j++) {
                System.out.print(tasks[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void removeTask() {
        var input = new Scanner(System.in);
        System.out.println("Please enter the number of the task you would like to remove:");
        var taskNumber = input.nextLine();
        int index;
        if (NumberUtils.isParsable(taskNumber)) {
            index = Integer.parseInt(taskNumber);
        } else {
            System.out.println("Invalid task number format");
            return;
        }
        // taskNumber is indexed with -1 because it's displayed from 1 to x in the method taskList
        try {
            if (index-1 < tasks.length) {
                tasks = ArrayUtils.remove(tasks, index-1);
            } else {
                System.out.println("Invalid task number");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid task number");
            e.printStackTrace(System.err);
        }
    }

    public static void storeData() {
        Path file = Paths.get("tasks.csv");
        String[] line = new String[tasks.length];

        for (int i = 0; i < tasks.length; i++) {
            line[i] = String.join(",", tasks[i]);
        }
        try {
            Files.write(file, Arrays.asList(line));
        } catch (IOException e) {
            System.err.println("Error writing file.");
            e.printStackTrace(System.err);
        }
    }
}