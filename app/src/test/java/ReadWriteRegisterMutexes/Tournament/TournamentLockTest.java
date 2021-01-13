/** TournamentLock tests
 */
package ReadWriteRegisterMutexes.Tournament;

import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.Runnable;
import java.util.concurrent.locks.ReentrantLock;

import ReadWriteRegisterMutexes.Worker;
import ReadWriteRegisterMutexes.Tournament.TournamentLock;

public class TournamentLockTest {
    /** Test the TournamentLock by incrementing the c shared variable 1,000,000
     * times while concurrently decrementing it another 1,000,000 times. There
     * is no guarantee of the atomicity of the increments or decrements except
     * if the lock works.
     */
    @Test
    public void testTournamentLockIncrement() {
        int numWorkers = 8;
        int increments = 1000000;
        TournamentLock lock = new TournamentLock(numWorkers);
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

        // Check we got the right result
        assertEquals("Synchronization error: ", 0, ((Worker)workers[0]).getC());
        System.out.println("Success: c = " + ((Worker)workers[0]).getC()
            + " expected 0");
    }
}
