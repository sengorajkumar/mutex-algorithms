package ReadWriteRegisterMutexes.OneBit;

import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.Runnable;
import java.util.concurrent.locks.ReentrantLock;
import ReadWriteRegisterMutexes.Worker;
import ReadWriteRegisterMutexes.Tournament.TournamentLock;

public class OneBitLockTest {
    /** Test the OneBitLock by incrementing the c shared variable 1,000,000
     * times while concurrently decrementing it another 1,000,000 times. There
     * is no guarantee of the atomicity of the increments or decrements except
     * if the lock works.
     */
    @Test
    public void testOneBitLockIncrement() {
        int numWorkers = 8;
        int increments = 1000000;
        OneBitLock lock = new OneBitLock(numWorkers);
        Runnable[] workers = new Runnable[numWorkers];
        Thread[] threads = new Thread[numWorkers];

        // Initialize workers
        for (int i=0; i< numWorkers; i++) {
            // Even workers add, odd workers subtract
            workers[i] = new Worker(i, (((i%2) == 0) ? true : false),
                    increments, lock);
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
                assertTrue("Exception caught for T" + i + ": " + e, false);
            }
        }

        //System.out.println("Success: c = " + ((Worker)workers[0]).getC()
        //        + " expected 0");
        // Check we got the right result
        assertEquals("Synchronization error: ", 0, ((Worker)workers[0]).getC());
    }
}

