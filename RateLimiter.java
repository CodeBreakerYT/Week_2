import java.util.concurrent.*;
import java.util.*;

class TokenBucket {

    private int maxTokens;
    private double refillRate; // tokens per second
    private double tokens;
    private long lastRefillTime;

    public TokenBucket(int maxTokens, int refillPerHour) {
        this.maxTokens = maxTokens;
        this.refillRate = refillPerHour / 3600.0;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // refill tokens based on time passed
    private synchronized void refill() {
        long now = System.currentTimeMillis();
        double seconds = (now - lastRefillTime) / 1000.0;

        double refillTokens = seconds * refillRate;

        tokens = Math.min(maxTokens, tokens + refillTokens);

        lastRefillTime = now;
    }

    public synchronized boolean allowRequest() {
        refill();

        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }

        return false;
    }

    public synchronized int remainingTokens() {
        refill();
        return (int) tokens;
    }
}

public class RateLimiter {

    private ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();

    private int maxRequests = 1000;

    // check rate limit
    public String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId,
                new TokenBucket(maxRequests, maxRequests));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {

            return "Allowed (" +
                    bucket.remainingTokens() +
                    " requests remaining)";
        }

        return "Denied (Rate limit exceeded)";
    }

    // rate limit status
    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null)
            return "Client not found";

        int remaining = bucket.remainingTokens();

        return "{used: " + (maxRequests - remaining) +
                ", limit: " + maxRequests +
                ", remaining: " + remaining + "}";
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}