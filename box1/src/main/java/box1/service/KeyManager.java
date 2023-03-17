package box1.service;

import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.random.RandomGenerator;

@Singleton
public class KeyManager {
    private final ConcurrentMap<String, TimeStamped<String>> map = new ConcurrentHashMap<>();
    private final Duration liveTimeLimit = Duration.ofSeconds(5);
    private final RandomGenerator rnd = RandomGenerator.getDefault();

    public KeyManager() {
        map.put(LocalTime.now().toString(), new TimeStamped<>("hi!"));
        map.put(LocalTime.now().toString(), new TimeStamped<>("hi!"));
        map.put(LocalTime.now().toString(), new TimeStamped<>("hi!"));
        map.put(LocalTime.now().toString(), new TimeStamped<>("hi!"));
        map.put(LocalTime.now().toString(), new TimeStamped<>("hi!"));
    }

    @Scheduled(fixedDelay = "2s", initialDelay = "5s")
    public void cleanOldKeys() throws InterruptedException {
        System.out.println("Clean attempt at " + LocalTime.now() + "on " + Thread.currentThread().getName());
        var iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            Duration liveTime = Duration.ofNanos(System.nanoTime() - entry.getValue().nanoTimeStamp);
            if (liveTime.compareTo(liveTimeLimit) < 0) {
                continue;
            }

            System.out.println("Removed " + entry.getValue().getNanoTimeStamp() + " " + entry.getValue().getContent());
            iter.remove();
            if (rnd.nextBoolean()) {
                map.put(LocalTime.now().toString(), new TimeStamped<>(entry.getValue().getContent() + " ho!"));
            }
        }
    }

    private static class TimeStamped<T> {
        private long nanoTimeStamp = System.nanoTime();
        private final T content;

        private TimeStamped(T content) {
            this.content = content;
        }

        public void refresh() {
            nanoTimeStamp = System.nanoTime();
        }

        public long getNanoTimeStamp() {
            return nanoTimeStamp;
        }

        public T getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            return content.equals(o);
        }

        @Override
        public int hashCode() {
            return content.hashCode();
        }
    }
}
