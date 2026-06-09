package cat.psychward.benchmark;

import cat.psychward.events.core.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SingleEventBenchmark {

    public static void main(String[] args) {
        final Object registrant = new Object();
        final EventBus eventBus = new EventBus();
        eventBus.subscribe(registrant);

        final int epochs = 100;
        final long iterations = 1_000_000;
        final List<Long> times = new ArrayList<>(epochs);
        final int listeners = 100;

        for (int i = 0; i < listeners; i++)
            eventBus.subscribe(registrant, TestEvent.class, event -> {});

        for (int i = 0; i < epochs; i++) {
            final long start = System.nanoTime();
            for (long j = 0; j < iterations; j++)
                eventBus.post(new TestEvent());
            final long end = System.nanoTime();
            times.add(end - start);
            System.out.printf("Epoch %d took %fms\n", i + 1, (end - start) / 1_000_000.0);
        }

        long max = Long.MIN_VALUE, min = Long.MAX_VALUE, average = 0;

        for (long time : times) {
            min = Math.min(min, time);
            max = Math.max(max, time);
            average += time;
        }

        System.out.println("Min time: " + min / 1_000_000.0 + "ms");
        System.out.println("Max time: " + max / 1_000_000.0 + "ms");
        System.out.println("Mean average: " + average / (double) times.size() / 1_000_000.0 + "ms");
    }

}
