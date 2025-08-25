package com.example.filehandlers;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileHandlerApp {

    public static  void main(String[] args)
    {
        String baseDir="data";
        new File(baseDir).mkdirs();
        Scanner scanner = new Scanner(System.in);
        int fileTypeChoice = -1;

        // Interactive menu to select a file handler
        while (fileTypeChoice == -1) {
            System.out.println("Select a data format to interact with:");
            System.out.println("1. CSV");
            System.out.println("2. JSON");
            System.out.println("3. XML");
            System.out.println("4. YAML");
            System.out.println("5. Excel (XLSX)");
            System.out.println("6. Properties");
            System.out.println("7. Text");
            System.out.println("8. JDBC (In-Memory SQLite)");
            System.out.print("Enter your choice (1-8): ");

            try {
                fileTypeChoice = Integer.parseInt(scanner.nextLine());
                if (fileTypeChoice < 1 || fileTypeChoice > 3) {
                    System.out.println("Invalid choice. Please enter a number between 1 and 8.");
                    fileTypeChoice = -1; // Reset to loop again
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        IFileHandler<User> handler;
        File file = null;
        switch (fileTypeChoice) {
            /*case 1:
                handler = new CsvHandler();
                file = new File(baseDir, "users.csv");
                break;
            case 2:
                handler = new JsonHandler();
                file = new File(baseDir, "users.json");
                break;*/
            case 1:
                handler = new XmlHandler();
                file = new File(baseDir, "users.xml");
                break;
           /* case 4:
                handler = new YamlHandler();
                file = new File(baseDir, "users.yaml");
                break;*/
            case 2:
                handler = new ExcelHandler();
                file = new File(baseDir, "users.xlsx");
                break;
            case 3:
                handler = new PropertiesHandle();
                file = new File(baseDir, "users.properties");
                break;
            /*case 7:
                handler = new TextHandler();
                file = new File(baseDir, "users.txt");
                break;
            case 8:
                handler = new JdbcHandler();
                // File is not used for JDBC, so it remains null.
                break;*
                /
             */
            default:
                System.out.println("Exiting application.");
                return;
        }

        // Main interaction loop
        while (true) {
            System.out.println("\n--- Actions for selected data format ---");
            System.out.println("1. Create new user(s)");
            System.out.println("2. Read all users");
            System.out.println("3. Read a single user");
            System.out.println("4. Update a user");
            System.out.println("5. Delete a user");
            System.out.println("6. Exit");
            System.out.print("Enter your action choice (1-6): ");

            int actionChoice;
            try {
                actionChoice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            try {
                switch (actionChoice) {
                    case 1:
                        createUser(handler, file, scanner);
                        break;
                    case 2:
                        readAllUsers(handler, file);
                        break;
                    case 3:
                        readOneUser(handler, file, scanner);
                        break;
                    case 4:
                        updateUser(handler, file, scanner);
                        break;
                    case 5:
                        deleteUser(handler, file, scanner);
                        break;
                    case 6:
                        System.out.println("Exiting application. Goodbye!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid action. Please enter a number from 1 to 6.");
                        break;
                }
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static void createUser(IFileHandler<User> handler, File file, Scanner scanner) throws IOException {
        System.out.println("--- Creating New User ---");
        System.out.print("Enter Employee ID: ");
        String emp_id = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Age: ");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        List<User> newUserList = new ArrayList<>();
        newUserList.add(new User(emp_id, name, age, email));

        // The handler's create method is designed to take a list, so we read
        // the existing data and add the new user to it.
        List<User> existingUsers = handler.read(file);
        if (existingUsers.stream().anyMatch(user -> user.getEmp_id().equals(emp_id))) {
            System.out.println("User with this ID already exists. Cannot create.");
        } else {
            existingUsers.addAll(newUserList);
            handler.create(file, existingUsers);
            System.out.println("User created successfully.");
        }
    }

    private static void readAllUsers(IFileHandler<User> handler, File file) throws IOException {
        System.out.println("--- All Users ---");
        List<User> users = handler.read(file);
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void readOneUser(IFileHandler<User> handler, File file, Scanner scanner) throws IOException {
        System.out.println("--- Read Single User ---");
        System.out.print("Enter the Employee ID to find: ");
        String emp_id = scanner.nextLine();
        Optional<User> user = handler.read(file, emp_id);
        user.ifPresentOrElse(
                u -> System.out.println("Found user: " + u),
                () -> System.out.println("User with ID " + emp_id + " not found.")
        );
    }

    private static void updateUser(IFileHandler<User> handler, File file, Scanner scanner) throws IOException {
        System.out.println("--- Update User ---");
        System.out.print("Enter the Employee ID of the user to update: ");
        String emp_id_to_update = scanner.nextLine();

        Optional<User> existingUser = handler.read(file, emp_id_to_update);
        if (existingUser.isPresent()) {
            System.out.print("Enter new Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new Age: ");
            int age = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter new Email: ");
            String email = scanner.nextLine();

            User updatedUser = new User(emp_id_to_update, name, age, email);
            boolean success = handler.update(file, updatedUser, emp_id_to_update);
            System.out.println("User " + (success ? "updated" : "not updated") + " successfully.");
        } else {
            System.out.println("User with ID " + emp_id_to_update + " not found. Cannot update.");
        }
    }

    private static void deleteUser(IFileHandler<User> handler, File file, Scanner scanner) throws IOException {
        System.out.println("--- Delete User ---");
        System.out.print("Enter the Employee ID of the user to delete: ");
        String emp_id_to_delete = scanner.nextLine();
        boolean success = handler.delete(file, emp_id_to_delete);
        System.out.println("User " + (success ? "deleted" : "not deleted") + " successfully.");
    }
}
