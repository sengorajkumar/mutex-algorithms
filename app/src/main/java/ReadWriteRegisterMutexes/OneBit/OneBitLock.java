/*
 * One-Bit algorithm uses exactly n shared bits to achieve deadlock-free mutual exclusion.
 * Developed by J.E.Burns (1981) and by L. Lamport (1986).
 * use minimum possible shared space.
 *
 * */
package ReadWriteRegisterMutexes.OneBit;
import ReadWriteRegisterMutexes.Lock;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.concurrent.atomic.AtomicBoolean;

public class OneBitLock implements Lock {

    /* Number of processes / threads */
    private int N;

    /* N shared bits */
    private AtomicBoolean b[];

    private static final Logger log = LogManager.getRootLogger();

    public OneBitLock(int n) {
        this.N = n;
        b = new AtomicBoolean[n];
        for(int i=0; i<this.N; i++){
            b[i] = new AtomicBoolean(false);
        }
    }

    @Override
    public void lock(int pid) {
        log.debug("Enter lock" );
        while(b[pid].get() == false) {
            b[pid].set(true); // Process i indicates that its interested in critical section
            int j = 0;
            while ((b[pid].get() == true) && j < pid) { // Check the bits of all the processes that are less than its process id
                if (b[j].get() == true) {
                    b[pid].set(false);  // Set to false so that the outer do - while starts again
                    while (b[j].get() == true) { // If some other process j's bit is true then wait
                        ; //no-op
                        log.warn("while 1. Waiting for bit of process " + j + " to be false" );
                    }
                }
                j++;
            }
        } // Process i exists the loop if other bits are false but its one bit is true

        for(int j=pid+1; j<this.N; j++){ // Check the bits of all processes that are higher than current process id
            while(b[j].get() == true){  // Wait till the other process's bit is false
                ;//wait no-op
                log.warn("while 2. Waiting for bit of process " + j + " to be false" );
            }
        }
        log.debug("Exit lock" );
    }

    @Override
    public void unlock(int pid) {
        b[pid].set(false);
    }
}
