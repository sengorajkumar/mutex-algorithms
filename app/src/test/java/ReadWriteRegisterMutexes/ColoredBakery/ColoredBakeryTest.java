package ReadWriteRegisterMutexes.ColoredBakery;
import ReadWriteRegisterMutexes.ColoredBakery.ColoredBakeryLock;
import ReadWriteRegisterMutexes.Lock;
import ReadWriteRegisterMutexes.Worker;
import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;

public class ColoredBakeryTest {
    /** Test the ColoredBakeryLock by incrementing the c shared variable 10,000
     * times while concurrently decrementing it another 10,000 times. There
     * is no guarantee of the atomicity of the increments or decrements except
     * if the lock works.
     */
    private static int gIncrements = 10000;
    private static int NUM_THREADS = 4;

    @Test
    public void Test1(){
        ColoredBakeryLock lockBench = new ColoredBakeryLock(NUM_THREADS);
        runIncrementBenchmark(NUM_THREADS, gIncrements,
                lockBench);

    }
    private void runIncrementBenchmark(int numWorkers, int increments, Object lockObj) {
        Runnable[] workers = new Runnable[numWorkers];
        Thread[] threads = new Thread[numWorkers];

        if (lockObj == null) {
            // No lock
            // Initialize workers
            for (int i=0; i< numWorkers; i++) {
                // Even workers add, odd workers subtract
                workers[i] = new Worker(i, (((i%2) == 0) ? true : false),
                        increments);
                System.out.println("i = " + i + " i%2 = " + i%2);
            }
        } else if (lockObj instanceof Lock) {
            // Lock interface
            Lock lock = (Lock) lockObj;

            // Initialize workers
            for (int i=0; i< numWorkers; i++) {
                // Even workers add, odd workers subtract
                workers[i] = new Worker(i, (((i%2) == 0) ? true : false),
                        increments, lock);
                System.out.println("i = " + i + " i%2 = " + i%2);
            }
        } else if (lockObj instanceof ReentrantLock) {
            // ReentrantLock
            ReentrantLock lock = (ReentrantLock) lockObj;

            // Initialize workers
            for (int i=0; i< numWorkers; i++) {
                // Even workers add, odd workers subtract
                workers[i] = new Worker(i, (((i%2) == 0) ? true : false),
                        increments, lock);
            }
        } else {
            throw new IllegalArgumentException("ERROR: Unknown type of lock");
        }

        // Initialize the shared counter c
        ((Worker)workers[0]).setC(0);

        // Spawn threads
        for (int i=0; i<numWorkers; i++) {
            threads[i] = new Thread(workers[i], "T" + i);
        }

        // Start threads
        for (int i=0; i<numWorkers; i++) {
            threads[i].start();
        }
        //System.out.println(((Worker) workers[0]).getC());

        // Wait for threads to terminate
        for (int i=0; i<numWorkers; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("ERROR: T" + i + ": " + e);
            }
        }

        assertEquals("Synchronization error: ", 0, ((Worker)workers[0]).getC());
        System.out.println("Finished: c = " + ((Worker)workers[0]).getC()
                + " expected 0");
    }
}
