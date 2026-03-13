import java.util.*;

class VideoData {
    String id;
    String content;

    VideoData(String id, String content) {
        this.id = id;
        this.content = content;
    }
}

public class CacheSystem {

    private LinkedHashMap<String, VideoData> L1;
    private LinkedHashMap<String, VideoData> L2;
    private HashMap<String, VideoData> database = new HashMap<>();

    private int L1_SIZE = 10000;
    private int L2_SIZE = 100000;

    // stats
    private int L1Hits = 0;
    private int L2Hits = 0;
    private int L3Hits = 0;
    private int totalRequests = 0;

    private double L1Time = 0.5;
    private double L2Time = 5;
    private double L3Time = 150;

    public CacheSystem() {

        L1 = new LinkedHashMap<String, VideoData>(L1_SIZE, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> e) {
                return size() > L1_SIZE;
            }
        };

        L2 = new LinkedHashMap<String, VideoData>(L2_SIZE, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> e) {
                return size() > L2_SIZE;
            }
        };
    }

    // add video to database
    public void addVideo(String id, String content) {
        database.put(id, new VideoData(id, content));
    }

    public VideoData getVideo(String id) {

        totalRequests++;

        // L1
        if (L1.containsKey(id)) {
            L1Hits++;
            System.out.println("L1 Cache HIT (0.5ms)");
            return L1.get(id);
        }

        System.out.println("L1 Cache MISS");

        // L2
        if (L2.containsKey(id)) {
            L2Hits++;
            VideoData video = L2.get(id);
            L1.put(id, video);

            System.out.println("L2 Cache HIT (5ms)");
            System.out.println("→ Promoted to L1");

            return video;
        }

        System.out.println("L2 Cache MISS");

        // L3 DB
        VideoData video = database.get(id);

        if (video != null) {
            L3Hits++;
            L2.put(id, video);

            System.out.println("L3 Database HIT (150ms)");
            System.out.println("→ Added to L2");
        }

        return video;
    }

    // statistics
    public void getStatistics() {

        double L1Rate = (L1Hits * 100.0) / totalRequests;
        double L2Rate = (L2Hits * 100.0) / totalRequests;
        double L3Rate = (L3Hits * 100.0) / totalRequests;

        double avgTime =
                (L1Hits * L1Time +
                        L2Hits * L2Time +
                        L3Hits * L3Time) / totalRequests;

        System.out.println("L1: Hit Rate " +
                String.format("%.0f", L1Rate) +
                "%, Avg Time: 0.5ms");

        System.out.println("L2: Hit Rate " +
                String.format("%.0f", L2Rate) +
                "%, Avg Time: 5ms");

        System.out.println("L3: Hit Rate " +
                String.format("%.0f", L3Rate) +
                "%, Avg Time: 150ms");

        System.out.println("Overall: Hit Rate " +
                String.format("%.0f", (L1Rate + L2Rate)) +
                "%, Avg Time: " +
                String.format("%.1f", avgTime) + "ms");
    }

    public static void main(String[] args) {

        CacheSystem cache = new CacheSystem();

        cache.addVideo("video_123", "Movie A");
        cache.addVideo("video_999", "Movie B");

        cache.getVideo("video_123");
        cache.getVideo("video_123");

        cache.getVideo("video_999");

        cache.getStatistics();
    }
}