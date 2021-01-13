/** AdaptiveLock is a mutex lock implementation of the Simple Adaptive Algorithm
 * by M. Merritt and G. Taubenfeld.
 * 
 * This mutex implementation is based on the algorithm description from the
 * Synchronization Algorithms and Concurrent Programming textbook by Gadi
 * Taubenfeld in pages 105 to 110.
 * 
 * If the maximum number of splitters (infArrSize) is reached, a ReentrantLock
 * is acquired to ensure safety without requiring infinite memory.
 */
package ReadWriteRegisterMutexes.Adaptive;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** AdaptiveLock class implements a mutex lock using the Simple Adaptive
 * Algorithm
 * 
 * This mutex uses the Simple Adaptive Algorithm by M. Merritt and G. Taubenfeld.
 * 
 * If the maximum number of splitters (infArrSize) is reached, a ReentrantLock
 * is acquired to ensure safety without requiring infinite memory.
 */
public class AdaptiveLock implements ReadWriteRegisterMutexes.Lock {
    /** Infinite array size
     * 
     * This variable sets the real size of the arrays behind the lock
     * implementation, because infinite arrays are not a thing in the real world.
     */
    private int infArrSize;

    /** Number of threads
     */
    private int n;

    /** Next level
     * 
     * Shared variable (it might be accessed by multiple threads concurrently).
     */
    private AtomicInteger next;

    /** Array of x values for all levels
     * 
     * Shared variable (all entries might be accessed by multiple threads
     * concurrently).
     */
    private AtomicInteger[] x;

    /** Array of y values for all levels
     * 
     * Shared variable (all entries might be accessed by multiple threads
     * concurrently).
     */
    private AtomicBoolean[] y;

    /** Array of z values for all levels
     * 
     * Shared variable (all entries might be accessed by multiple threads
     * concurrently).
     */
    private AtomicBoolean[] z;

    /** Array of b values for all levels
     * 
     * Shared variable (all entries might be accessed by multiple threads
     * concurrently).
     */
    private AtomicBoolean[] b;

    /** Current levels of the threads
     * 
     * Local variable (each entry is accessed only by one thread). The ith entry
     * contains the level for the ith thread.
     */
    private int[] level;

    /** Constructor
     * 
     * @param numThreads    Number of threads using the lock
     * @param maxSplitters  Maximum number of splitters after to acquire another lock
     */
    public AdaptiveLock(int numThreads, int maxSplitters) {
        // Initialize instance variables
        this.infArrSize = maxSplitters;
        this.n = numThreads;
        this.next = new AtomicInteger(0);
        this.x = new AtomicInteger[this.infArrSize];
        this.y = new AtomicBoolean[this.infArrSize];
        this.z = new AtomicBoolean[this.infArrSize];
        this.b = new AtomicBoolean[this.infArrSize];
        this.level = new int[this.n];

        for (int i=0; i<this.infArrSize; i++) {
            this.x[i] = new AtomicInteger(0);
            this.y[i] = new AtomicBoolean(false);
            this.z[i] = new AtomicBoolean(false);
            this.b[i] = new AtomicBoolean(false);
        }

        for (int i=0; i<this.n; i++) {
            this.level[i] = 0;
        }
    }

    /** Lock or critical section entry protocol method of mutex
     * 
     * @param tid Thread ID
     */
    public void lock(int tid) {
        // Enter the chain (list) of splitters
        // start: level := next
        // This part was moved, see START comment
        boolean start = true;
        boolean win = false;  // Ensure we run the while loop at least once

        // repeat
        while (!win) {
            // START
            // Since Java does not support the `goto` statement used in the
            // algorithm pseudo-code, we use the boolean variable start to run
            // the `level := next` statement once the first time the while loop
            // runs and once after each supposed `goto start` statement. The
            // `goto start` statements are replaced with setting the start
            // variable to true and a `continue` statement, which has the same
            // behavior as the `goto start`.
            if (start) {
                start = false;
                this.level[tid] = this.next.get();

                if (this.level[tid] >= (this.infArrSize - 1)) {
                    System.out.println("T" +tid + " reached last level or higher: "
                        + this.level[tid]);
                }
            }

            // x[level] := i
            this.x[this.level[tid]].set(tid);

            // if y[level] then
            if (this.y[this.level[tid]].get()) {
                // b[level] := 1
                this.b[this.level[tid]].set(true);

                // await level < next
                while ( !( this.level[tid] < this.next.get() ) ) {
                    System.out.print(""); // Do nothing
                }

                // goto start
                start = true;
                continue;
            } // fi

            // y[level] := 1
            this.y[this.level[tid]].set(true);

            // if x[level] != i then
            if (this.x[this.level[tid]].get() != tid) {
                // await (b[level] = 1) or (z[level] = 1)
                while ( !( this.b[this.level[tid]].get() ||
                           this.z[this.level[tid]].get() ) ) {
                    System.out.print(""); // Do nothing
                }

                // if z[level] = 1 then
                if (this.z[this.level[tid]].get()) {
                    // Move right
                    // await level < next
                    while ( !( this.level[tid] < this.next.get() ) ) {
                        System.out.print(""); // Do nothing
                    }

                    // goto start
                    start = true;
                    continue;
                } else { // else
                    // Move down
                    // level := level + 1
                    this.level[tid] = this.level[tid] + 1;

                    if (this.level[tid] >= (this.infArrSize - 1)) {
                        System.out.println("T" +tid + " reached last level or higher: "
                            + this.level[tid]);
                    }
                } // fi
            } else { // else
                // z[level] := 1
                this.z[this.level[tid]].set(true);

                // if b[level] = 0 then
                if (!this.b[this.level[tid]].get()) {
                    // Win
                    // win := 1
                    win = true;
                } else { // else
                    // Move down
                    // level := level + 1
                    this.level[tid] = this.level[tid] + 1;

                    if (this.level[tid] >= (this.infArrSize - 1)) {
                        System.out.println("T" +tid + " reached last level or higher: "
                            + this.level[tid]);
                    }
                } // fi
            } // fi
        } // until win = 1
    }

    /** Unlock or critical section exit protocol method of mutex
     * 
     * @param tid Thread ID
     */
    public void unlock(int tid) {
        // Exit
        if ((this.level[tid] + 1) >= (this.infArrSize - 1)) {
            System.out.println("T" +tid + " set next to last level or higher: "
                + (this.level[tid] + 1));
        }

        // next := level + 1
        this.next.set(this.level[tid] + 1);
    }
}
