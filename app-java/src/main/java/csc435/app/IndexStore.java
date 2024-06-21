package csc435.app;  // Package declaration indicating that this class belongs to the csc435.app package.

import java.util.Collections;  // Import statement for the Collections class from the java.util package.
import java.util.Map;  // Import statement for the Map interface from the java.util package.
import java.util.Set;  // Import statement for the Set interface from the java.util package.
import java.util.concurrent.ConcurrentHashMap;  // Import statement for the ConcurrentHashMap class from the java.util.concurrent package.

public class IndexStore {  // Declaration of a public class named IndexStore.
    private Map<String, ConcurrentHashMap<String, Integer>> index = new ConcurrentHashMap<>();  // Declaration of a private ConcurrentHashMap 'index' to store the index data.

    public void updateIndex(String term, String document) {  // Method to update the index with a term-document pair.
        index.computeIfAbsent(term, k -> new ConcurrentHashMap<>()).merge(document, 1, Integer::sum);  // Using ConcurrentHashMap's merge method to update the index with the term-document pair.
    }

    public Set<String> lookupIndex(String term) {  // Method to lookup the index for a given term.
        return index.containsKey(term) ? index.get(term).keySet() : Collections.emptySet();  // Returning the set of documents associated with the term, or an empty set if the term is not found.
    }
}
