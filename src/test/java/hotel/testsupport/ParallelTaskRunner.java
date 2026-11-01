package hotel.testsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

/** Runs the same task on several threads at once and waits until all of them are finished (and rethrows their errors). */
public final class ParallelTaskRunner {

    private ParallelTaskRunner() {
    }

    public static void runTaskOnEachOfThreads(int threadCount, IntConsumer taskReceivingItsThreadNumber) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> tasks = new ArrayList<>();
        for (int threadNumber = 0; threadNumber < threadCount; threadNumber++) {
            int numberOfThisTask = threadNumber;
            tasks.add(executorService.submit(() -> taskReceivingItsThreadNumber.accept(numberOfThisTask)));
        }
        for (Future<?> task : tasks) {
            task.get(30, TimeUnit.SECONDS);
        }
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
    }
}
