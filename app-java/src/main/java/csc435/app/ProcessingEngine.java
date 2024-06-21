package csc435.app;  // Package declaration indicating that this class belongs to the csc435.app package.

import java.io.IOException;  // Import statement for the IOException class from the java.io package.
import java.nio.charset.StandardCharsets;  // Import statement for the StandardCharsets class from the java.nio.charset package.
import java.nio.file.Files;  // Import statement for the Files class from the java.nio.file package.
import java.nio.file.Path;  // Import statement for the Path interface from the java.nio.file package.
import java.nio.file.Paths;  // Import statement for the Paths class from the java.nio.file package.
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;  // Import statement for the ConcurrentHashMap class from the java.util.concurrent package.
import java.util.concurrent.ExecutorService;  // Import statement for the ExecutorService interface from the java.util.concurrent package.
import java.util.concurrent.Executors;  // Import statement for the Executors class from the java.util.concurrent package.
import java.util.concurrent.TimeUnit;  // Import statement for the TimeUnit enum from the java.util.concurrent package.
import java.util.stream.Collectors;  // Import statement for the Collectors class from the java.util.stream package.
import java.util.stream.Stream;  // Import statement for the Stream interface from the java.util.stream package.

public class ProcessingEngine {  // Declaration of a public class named ProcessingEngine.
    private IndexStore store;  // Declaration of a private instance variable 'store' of type IndexStore.
    private int numThreads;  // Declaration of a private instance variable 'numThreads' of type int.

    public ProcessingEngine(IndexStore store, int numThreads) {  // Constructor to initialize the ProcessingEngine with an IndexStore instance and the number of threads.
        this.store = store;  // Assigning the parameter 'store' to the instance variable 'store'.
        this.numThreads = numThreads;  // Assigning the parameter 'numThreads' to the instance variable 'numThreads'.
    }



    public void indexFiles(String directoryPath) {  // Method to index files in a specified directory.
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);  // Creating a fixed-size thread pool with the specified number of threads.
        System.out.printf("Completed indexing using %d worker threads\n", numThreads);  // Displaying the number of worker threads used for indexing.
        Path path = Paths.get(directoryPath);  // Converting the directory path string to a Path object.
        long startTime = System.currentTimeMillis();  // Recording the start time of the indexing process.

        try (Stream<Path> paths = Files.walk(path)) {  // Using try-with-resources to ensure the stream is closed after use.
            List<Path> files = paths.filter(Files::isRegularFile).collect(Collectors.toList());  // Collecting regular files within the directory into a list.
            long totalSize = files.stream().mapToLong(file -> file.toFile().length()).sum();  // Calculating the total size of files in bytes.

            for (Path file : files) {  // Iterating over each file in the list.
                executor.submit(() -> processFile(file));  // Submitting a task to the executor to process each file concurrently.
            }
            executor.shutdown();  // Initiating an orderly shutdown of the executor.
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Waiting for all tasks to complete execution.

            long endTime = System.currentTimeMillis();  // Recording the end time of the indexing process.
            double timeTakenSeconds = (endTime - startTime) / 1000.0;  // Calculating the time taken for indexing in seconds.
            double throughput = totalSize / 1024.0 / 1024.0 / timeTakenSeconds;  // Calculating the indexing throughput in MB/s.
            System.out.printf("Completed indexing %d bytes of data\n", totalSize);  // Displaying the total size of data indexed.
            System.out.printf("Completed indexing in %.3f seconds\n", timeTakenSeconds);  // Displaying the time taken for indexing.
            System.out.printf("Indexing throughput: %.3f MB/s\n", throughput);  // Displaying the indexing throughput.

        } catch (IOException | InterruptedException e) {  // Handling IOException and InterruptedException.
            System.err.println("Error walking the directory path: " + directoryPath);  // Displaying an error message.
            e.printStackTrace();  // Printing the stack trace of the exception.
        }
    }

    private void processFile(Path filePath) {  // Method to process a single file and update the index.
        try (Stream<String> lines = Files.lines(filePath, StandardCharsets.UTF_8)) {  // Using try-with-resources to read lines from the file.
            lines.flatMap(line -> Arrays.stream(line.split("\\P{Alnum}+")))  // Splitting each line into words and flattening the resulting stream.
                    .filter(word -> !word.isEmpty())  // Filtering out empty words.
                    .forEach(word -> store.updateIndex(word.toLowerCase(), filePath.toString()));  // Updating the index with each word and its corresponding file path.
        } catch (IOException e) {  // Handling IOException.
            System.err.println("Failed to read file " + filePath);  // Displaying an error message.
        }
    }

    public String search(String query) { // Method to perform a search based on a query.
        long startTime = System.currentTimeMillis(); // Record the start time for the search.
        String[] terms = query.toLowerCase().split("\\P{Alnum}+"); // Split the query into terms using the regular expression.
        Map<String, Integer> documentScores = new HashMap<>(); // Map to hold the scores of documents.

        for (String term : terms) { // Iterate over each term.
            Set<String> termDocs = store.lookupIndex(term); // Get documents containing the term.
            termDocs.forEach(doc -> documentScores.merge(doc, 1, Integer::sum)); // Merge scores into the map.
        }

      //  long endTime = System.currentTimeMillis(); // Record the end time of the search.
//        double searchDuration = (endTime - startTime) / 1000.0; // Calculate the duration of the search in seconds.
//        System.out.printf("Search completed in %.2f seconds\n", searchDuration); // Print the duration of the search.

        return formatSearchResults(documentScores); // Return formatted search results.
    }

    private String formatSearchResults(Map<String, Integer> documentScores) { // Method to format search results.
        return documentScores.entrySet().stream() // Stream the entries of the document score map.
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) // Sort the entries by their values in descending order.
                .limit(10)  // Limit to the top 10 results.
                .map(entry -> "* " + entry.getKey() + " " + entry.getValue()) // Format each entry.
                .collect(Collectors.joining("\n", "Search results (top 10):\n", "")); // Collect results into a single string.
    }
}
