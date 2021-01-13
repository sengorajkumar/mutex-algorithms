package ReadWriteRegisterMutexes;

import java.util.concurrent.locks.ReentrantLock;

/** Shared counter increment mutex benchmark class
 * 
 * The benchmark consists on incrementing a shared counter a set amount of times
 * by set of threads to benchmark the mutex.
 */
public class IncrementBenchmark {

    /** Global setting for number of threads on benchmarks with heavy contention
     */
    private static int heavyContentionThreadNum = 8;
    //private static int heavyContentionThreadNum = 4;
    //private static int heavyContentionThreadNum = 2;

    /** Global setting for increments in the benchmarks
     */
    private static int incrementNum = 5000000;

    /** Global setting for number of threads on benchmarks with no contention
     */
    private static int noContentionThreadNum = 1;

    /** Getter method for number of threads for a heavy contention benchmark
     * 
     * @return Number of threads for a heavy contention benchmark (heavyContentionThreadNum)
     */
    public int getHeavyContentionThreadNum() {
        return IncrementBenchmark.heavyContentionThreadNum;
    }

    /** Getter method for number of increments
     * 
     * @return Number of increments for the benchmark (incrementNum)
     */
    public int getIncrementNum() {
        return IncrementBenchmark.incrementNum;
    }

    /** Getter method for number of threads for a no contention benchmark
     * 
     * @return Number of threads for a no contention benchmark (noContentionThreadNum)
     */
    public int getNoContentionThreadNum() {
        return IncrementBenchmark.noContentionThreadNum;
    }

    /** Run the increment a shared counter a set number of times per thread
     *  operation to benchmark
     * 
     * This benchmark operation consists of incrementing/decrementing by one a
     * shared counter by one or multiple threads. Even threads (starting from 0)
     * increment the counter, and odd threads decrement the counter. Depending
     * on the lock object passed, each increment/decrement operation is
     * synchronized by locking before incrementing/decrementing, and locking
     * immediately after it. This increment/decrement operation is repeated a
     * configured number of times.
     * 
     * @param numWorkers    Number of worker threads
     * @param increments    Number of increments/decrements per thread
     * @param lockObj   Lock object of type Lock or ReentrantLock, or null
     */
    public int runIncrementBenchmark(int numWorkers, int increments, Object lockObj) {
        Runnable[] workers = new Runnable[numWorkers];
        Thread[] threads = new Thread[numWorkers];

        if (lockObj == null) {
            // No lock
            // Initialize workers
            for (int i=0; i< numWorkers; i++) {
                // Even workers add, odd workers subtract
                workers[i] = new Worker(i, (((i%2) == 0) ? true : false),
                    increments);
            }
        } else if (lockObj instanceof Lock) {
            // Lock interface
            Lock lock = (Lock) lockObj;

            // Initialize workers
            for (int i=0; i< numWorkers; i++) {
                // Even workers add, odd workers subtract
                workers[i] = new Worker(i, (((i%2) == 0) ? true : false),
                    increments, lock);
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

        // Wait for threads to terminate
        for (int i=0; i<numWorkers; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("ERROR: T" + i + ": " + e);
            }
        }

        // Check we got the right result
        //System.out.print("c = " + ((Worker)workers[0]).getC() + ": ");
        return ((Worker)workers[0]).getC();
    }
}
