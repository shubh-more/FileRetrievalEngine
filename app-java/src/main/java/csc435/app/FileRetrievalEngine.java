package csc435.app;  // Package declaration indicating that this class belongs to the csc435.app package.

public class FileRetrievalEngine {  // Declaration of a public class named FileRetrievalEngine.
    public static void main(String[] args) {  // Declaration of the main method, the entry point of the program.
        IndexStore store = new IndexStore();  // Creation of an instance of the IndexStore class and assignment to the variable 'store'.
        ProcessingEngine engine = new ProcessingEngine(store, 4);  // Creation of an instance of the ProcessingEngine class with 4 worker threads and assignment to the variable 'engine'.
        AppInterface appInterface = new AppInterface(engine);  // Creation of an instance of the AppInterface class with the ProcessingEngine instance as a parameter and assignment to the variable 'appInterface'.

        appInterface.readCommands();  // Invocation of the readCommands method on the appInterface object to start reading user commands.
    }
}
