package com.passman;

import java.util.Hashtable;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void viewPassword(Hashtable<String, String> passwordTable) {
        if (passwordTable.isEmpty()) {
            System.out.println("No passwords stored yet.");
            return;
        }

        System.out.printf("%-20s %-20s%n", "Service Name", "Password");
        for (String key : passwordTable.keySet()) {
            System.out.printf("%-20s %-20s%n", key, passwordTable.get(key));
        }
    }

    public static void addPassword(Hashtable<String, String> passwordTable, String serviceName, String servicePass) {
        String existing = passwordTable.putIfAbsent(serviceName, servicePass);
        if (existing == null) {
            System.out.println("Password for " + serviceName + " added successfully.");
        } else {
            System.out.println("Service " + serviceName + " already exists! Use update option instead.");
        }
    }

    public static void updatePassword(Hashtable<String, String> passwordTable, String serviceName, String newPass) {
        if (passwordTable.containsKey(serviceName)) {
            passwordTable.put(serviceName, newPass);
            System.out.println("Password for " + serviceName + " updated successfully.");
        } else {
            System.out.println("Service name " + serviceName + " not found!");
        }
    }

    public static void deletePassword(Hashtable<String, String> passwordTable, String serviceName) {
        if (passwordTable.containsKey(serviceName)) {
            passwordTable.remove(serviceName);
            System.out.println("Password for " + serviceName + " deleted successfully.");
        } else {
            System.out.println("Service name " + serviceName + " not found!");
        }
    }

    public static void savePasswordsToJSON(Hashtable<String, String> table, String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(table, writer);
        } catch (IOException e) {
            System.out.println("Error saving passwords: " + e.getMessage());
        }
    }

    public static void loadPasswordsFromJSON(Hashtable<String, String> table, String filename) {
        Gson gson = new Gson();
        Type type = new TypeToken<Hashtable<String, String>>(){}.getType();
        try (FileReader reader = new FileReader(filename)) {
            Hashtable<String, String> loaded = gson.fromJson(reader, type);
            if (loaded != null) table.putAll(loaded);
        } catch (IOException e) {
            System.out.println("No saved passwords found. Starting fresh.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Hashtable<String, String> storedPassword = new Hashtable<>();

        loadPasswordsFromJSON(storedPassword, "passwords.json");

        boolean isRunning = true;

        while (isRunning) {
            System.out.println("""
                    \nChoose an option:
                    1. View your passwords
                    2. Enter a new password
                    3. Update your password
                    4. Delete your password
                    5. Exit the app
                    """);

            int choice = -1;

            while (choice < 1 || choice > 5) {
                System.out.print("What would you like to do? (1-5): ");
                try {
                    choice = scanner.nextInt();
                    if (choice < 1 || choice > 5) {
                        System.out.println("Invalid input. Please enter a number between 1 and 5.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 5.");
                    scanner.nextLine();
                }
            }

            String serviceName, servicePass;

            switch (choice) {
                case 1:
                    viewPassword(storedPassword);
                    break;

                case 2:
                    System.out.print("Enter the Service name (e.g. Google): ");
                    serviceName = scanner.next();
                    System.out.print("Enter the Service password: ");
                    servicePass = scanner.next();
                    addPassword(storedPassword, serviceName, servicePass);
                    savePasswordsToJSON(storedPassword, "passwords.json");
                    break;

                case 3:
                    System.out.print("Enter the Service name to update: ");
                    serviceName = scanner.next();
                    if (storedPassword.containsKey(serviceName)) {
                        System.out.print("Enter the new password: ");
                        servicePass = scanner.next();
                        updatePassword(storedPassword, serviceName, servicePass);
                        savePasswordsToJSON(storedPassword, "passwords.json");
                    } else {
                        System.out.println("Service name " + serviceName + " not found!");
                    }
                    break;

                case 4:
                    System.out.print("Enter the Service name to delete: ");
                    serviceName = scanner.next();
                    deletePassword(storedPassword, serviceName);
                    savePasswordsToJSON(storedPassword, "passwords.json");
                    break;

                case 5:
                    isRunning = false;
                    System.out.println("Exiting the app.");
                    break;
            }
        }

        scanner.close();
    }
}
