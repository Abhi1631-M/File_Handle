package com.example.filehandlers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileHandlerApp {
    private static final List<User> initialUsers = Arrays.asList(
            new User("user1", "Alice", 30, "alice@example.com"),
            new User("user2", "Bob", 25, "bob@example.com"),
            new User("user3", "Charlie", 35, "charlie@example.com")
    );
    public static  void main(String[] args)
    {
        String baseDir="data";
        new File(baseDir).mkdirs();
        runFileHandler(new XmlHandler(), "xml", new File(baseDir, "users.xml"));
        runFileHandler(new PropertiesHandle(),"properties", new File(baseDir,"users.properties"));

    }
    private static void runFileHandler(IFileHandler<User>handler, String type, File file)
    {
        System.out.println("--- Running " + type.toUpperCase() + " Handler ---");
        try {

            System.out.println("Creating " + type + " data...");
            handler.create(file, initialUsers);
            System.out.println("Data created successfully.");


            System.out.println("Reading all users from " + type + " data source...");
            List<User> users = handler.read(file);
            users.forEach(System.out::println);


            System.out.println("Reading user with ID 'user2'");
            handler.read(file, "user2").ifPresent(user ->
                    System.out.println("Found: " + user)
            );


            System.out.println("Updating user with ID 'user2'");
            User updatedBob = new User("user2", "Bob Smith", 26, "bob.smith@updated.com");
            boolean updated = handler.update(file, updatedBob, "user2");
            System.out.println("Update successful? " + updated);


            System.out.println("Reading all users after update");
            handler.read(file).forEach(System.out::println);


            System.out.println("Deleting user with ID 'user3'");
            boolean deleted = handler.delete(file, "user3");
            System.out.println("Delete successful? " + deleted);


            System.out.println("Reading all users after delete...");
            handler.read(file).forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("An error occurred with " + type + " data handling: " + e.getMessage());
            e.printStackTrace();
        }


    }
}
