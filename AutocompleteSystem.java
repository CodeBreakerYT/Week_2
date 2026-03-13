import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;
}

public class AutocompleteSystem {

    private TrieNode root = new TrieNode();

    // query -> frequency
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    // Insert query into Trie
    private void insert(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;
    }

    // Add search query
    public void addQuery(String query) {

        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);

        insert(query);
    }

    // DFS to collect queries
    private void collectQueries(TrieNode node,
                                String prefix,
                                List<String> results) {

        if (node.isEnd)
            results.add(prefix);

        for (char c : node.children.keySet()) {
            collectQueries(node.children.get(c),
                    prefix + c, results);
        }
    }

    // Get top suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        List<String> results = new ArrayList<>();
        collectQueries(node, prefix, results);

        // Sort by frequency
        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) ->
                        frequencyMap.get(b) - frequencyMap.get(a));

        pq.addAll(results);

        List<String> top = new ArrayList<>();

        int count = 0;
        while (!pq.isEmpty() && count < 10) {
            top.add(pq.poll() + " (" +
                    frequencyMap.get(top.isEmpty() ? pq.peek() : top.get(top.size()-1).split(" ")[0]) + ")");
            count++;
        }

        return top;
    }

    // Update frequency after new search
    public void updateFrequency(String query) {
        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");

        System.out.println(system.search("jav"));

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println(system.search("java"));
    }
}