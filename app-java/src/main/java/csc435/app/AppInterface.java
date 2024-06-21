package csc435.app;  // Package declaration indicating that this class belongs to the csc435.app package.

import java.util.Scanner;  // Import statement for the Scanner class from the java.util package.

public class AppInterface {  // Declaration of a public class named AppInterface.
    private ProcessingEngine engine;  // Declaration of a private instance variable 'engine' of type ProcessingEngine.

    public AppInterface(ProcessingEngine engine) {  // Constructor that takes a ProcessingEngine instance as a parameter.
        this.engine = engine;  // Assigning the parameter 'engine' to the instance variable 'engine'.
    }

    public void readCommands() {  // Method to read user commands and interact with the ProcessingEngine.
        Scanner sc = new Scanner(System.in);  // Creating a Scanner object to read input from the console.
        while (true) {  // Infinite loop to continuously read commands until user exits.
            System.out.print("> ");  // Printing the command prompt.
            String command = sc.nextLine().trim();  // Reading the user input and trimming leading and trailing whitespace.

            if ("quit".equalsIgnoreCase(command)) {  // Checking if the user wants to quit the application.
                System.out.println("Exiting application.");  // Displaying a message indicating application exit.
                break;  // Exiting the loop.
            } else if (command.startsWith("index ")) {  // Checking if the user wants to index files.
                String path = command.substring("index ".length()).trim();  // Extracting the path from the command.
                engine.indexFiles(path);  // Invoking the indexFiles method of the ProcessingEngine with the provided path.
            } else if (command.startsWith("search ")) {  // Checking if the user wants to search for files.
                String query = command.substring("search ".length()).trim();  // Extracting the query from the command.
                long startTime = System.currentTimeMillis();  // Recording the start time of the search operation.
                String results = engine.search(query);  // Performing the search operation using the ProcessingEngine.
                long endTime = System.currentTimeMillis();  // Recording the end time of the search operation.
                double searchTime = (endTime - startTime) / 1000.0;  // Calculating the search time in seconds.
                System.out.printf("Search completed in %.1f seconds\n", searchTime);  // Displaying the search time.
                System.out.println(results);  // Displaying the search results.
            } else {  // Handling unrecognized commands.
                System.out.println("Unrecognized command! Please try again.");  // Prompting the user to try again.
            }
        }
        sc.close();  // Closing the Scanner object to release system resources.
    }
}
