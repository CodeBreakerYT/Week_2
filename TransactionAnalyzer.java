import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time; // timestamp

    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class TransactionAnalyzer {

    // Classic Two-Sum
    public static List<String> findTwoSum(List<Transaction> transactions, int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                result.add("(" + other.id + ", " + t.id + ")");
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // Two-Sum within 1 hour window
    public static List<String> findTwoSumWithTimeWindow(
            List<Transaction> transactions, int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                long diff = Math.abs(t.time - other.time);

                if (diff <= 3600) { // within 1 hour
                    result.add("(" + other.id + ", " + t.id + ")");
                }
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // Duplicate detection
    public static List<String> detectDuplicates(List<Transaction> transactions) {

        HashMap<String, List<String>> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            String key = t.amount + "_" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t.account);
        }

        for (String key : map.keySet()) {

            List<String> accounts = map.get(key);

            if (accounts.size() > 1) {

                result.add(key + " accounts: " + accounts);
            }
        }

        return result;
    }

    // K-Sum (recursive)
    public static List<List<Integer>> findKSum(
            int[] nums, int target, int k, int start) {

        List<List<Integer>> res = new ArrayList<>();

        if (k == 2) {

            HashSet<Integer> set = new HashSet<>();

            for (int i = start; i < nums.length; i++) {

                int complement = target - nums[i];

                if (set.contains(complement)) {

                    res.add(Arrays.asList(nums[i], complement));
                }

                set.add(nums[i]);
            }

            return res;
        }

        for (int i = start; i < nums.length; i++) {

            List<List<Integer>> subsets =
                    findKSum(nums, target - nums[i], k - 1, i + 1);

            for (List<Integer> subset : subsets) {

                List<Integer> temp = new ArrayList<>();
                temp.add(nums[i]);
                temp.addAll(subset);

                res.add(temp);
            }
        }

        return res;
    }

    public static void main(String[] args) {

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(new Transaction(1, 500, "StoreA", "acc1", 36000));
        transactions.add(new Transaction(2, 300, "StoreB", "acc2", 36900));
        transactions.add(new Transaction(3, 200, "StoreC", "acc3", 37800));

        System.out.println("Two-Sum:");
        System.out.println(findTwoSum(transactions, 500));

        System.out.println("\nTwo-Sum within 1 hour:");
        System.out.println(findTwoSumWithTimeWindow(transactions, 500));

        System.out.println("\nDuplicate Transactions:");
        System.out.println(detectDuplicates(transactions));

        int[] nums = {500, 300, 200};

        System.out.println("\nK-Sum:");
        System.out.println(findKSum(nums, 1000, 3, 0));
    }
}