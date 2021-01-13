/** TournamentLock is a mutex lock implementation of the Peterson's Tournament
 * Algorithm
 * 
 * This implementation is based on the algorithm description from the
 * Synchronization Algorithms and Concurrent Programming textbook by Gadi
 * Taubenfeld at pages 37 to 40.
 */
package ReadWriteRegisterMutexes.Tournament;

import java.lang.Math;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** TournamentLock class implements a mutex lock using the Peterson's Tournament
 * Algorithm
 * 
 * The mutex uses the Pterson's Tournament Algorithm by Peterson.
 */
public class TournamentLock implements ReadWriteRegisterMutexes.Lock {
    /** Number of threads or leaves of the tournament tree
     * 
     * This must be a power of 2. If n is not a power of 2, "dummy" threads that
     * do nothing must be added to make it a power of 2.
     */
    private int n;

    /** Height of the tournament tree
     * 
     * It is obtained from n.
     */
    private int hTree;

    /** Shared variable that indicates if a process wants to access the CS in
     *  each node contest
     * 
     * Both registers for process 0 and 1 in each tournament are stored in the
     * same array. This way each node in the tree will have 2 entries:
     * wantCS[level][2*node] and wantCS[level][2*node+1], where the size of
     * wantCS is the height of the tree (for the level dimension) by the number
     * of leaves in the tree (for the node dimension).
     * 
     * Array entries need to be AtomicBooleans, so that reads and writes of an
     * array entry are atomic:
     * //stackoverflow.com/questions/2236184/how-to-declare-array-elements-volatile-in-java
     */
    private volatile AtomicBoolean[][] wantCS;

    /** Shared variable that indicates the turn in each node contest
     * 
     * Each node in the tree will have an entry: turn[level][node], where the
     * size of turn is the height of the tree (for level dimension) by half of
     * the number of leaves of the tree (for the node dimension).
     * 
     * Array entries need to be AtomicIntegers, so that reads and writes of an
     * array entry are atomic:
     * //stackoverflow.com/questions/2236184/how-to-declare-array-elements-volatile-in-java
     */
    private volatile AtomicInteger[][] turn;

    /** Constructor
     * 
     * @param numThreads    Number of threads using the lock
     */
    public TournamentLock(int numThreads) {
        //System.out.println("TournamentLock: numThreads = " + numThreads);

        // Check we have a valid number of threads
        if (numThreads <= 0) {
            throw new IllegalArgumentException(
                "Invalid number of threads: numThreads must be >0");
        }

        // Initialize n to the smallest power of 2 larger or equal to numThreads
        double p = Math.ceil(Math.log((double) numThreads) / Math.log(2.0));
        this.n = (int) Math.pow(2.0, p);
        //System.out.println("TournamentLock: n = " + this.n);

        // Initialize hTree
        this.hTree = (int) Math.floor(Math.log((double) this.n) / Math.log(2.0)); // Binary tree height
        //System.out.println("TournamentLock: hTree = " + hTree);

        // Initialize wantCS array
        this.wantCS = new AtomicBoolean[this.hTree][this.n];
        //System.out.println("TournamentLock: wantCS = ");
        for (AtomicBoolean[] row : this.wantCS) {
            //System.out.print("[ ");
            for (int i=0; i<row.length; i++) {
                row[i] = new AtomicBoolean(false);

                /*
                if (i == row.length-1) {
                    System.out.println(row[i].get() + " ]");
                } else {
                    System.out.print(row[i].get() + ", ");
                }
                */
            }
        }

        // Initialize turn array
        this.turn = new AtomicInteger[this.hTree][this.n / 2];
        //System.out.println("TournamentLock: turn =");
        for (AtomicInteger[] row : this.turn) {
            //System.out.print("[ ");
            for (int i=0; i<row.length; i++) {
                row[i] = new AtomicInteger(0);

                /*
                if (i == row.length-1) {
                    System.out.println(row[i].get() + " ]");
                } else {
                    System.out.print(row[i].get() + ", ");
                }
                */
            }
        }
    }

    /** Lock or critical section entry protocol method of mutex
     * 
     * @param tid Thread ID
     */
    public void lock(int tid) {
        // System.out.println("Thread-" + tid + ": locking...");
        int level, id, idj;
        int node = tid; // Starting node (leave) is the thread ID

        // Iterate over all the levels of the tree to contest other threads
        for (level = 0; level < this.hTree; level++) {
            id = node % 2; // Find if process 0 or 1 for Peterson's contest
            idj = 1 - id; // Id of other thread in the contest
            node = Math.floorDiv(node, 2); // Find next node

            // Say we want to enter the CS
            this.wantCS[level][2 * node + id].set(true);
            // Set the turn to the other thread in the contest
            this.turn[level][node].set(idj);

            // Busy wait until we win the contest
            while (this.wantCS[level][2 * node + idj].get()
                && (this.turn[level][node].get() == idj)) {
                // Do nothing

                // DO NOT DELETE!!! For some reason the lock does not work
                // properly without this print statement.
                //System.out.print("");
            }
        }
        
        // DO NOT DELETE!!! For some reason the lock does not work properly
        // without this print statement.
        //System.out.print("");
    }

    /** Unlock or critical section exit protocol method of mutex
     * 
     * @param tid Thread ID
     */
    public void unlock(int tid) {
        //System.out.println("Thread-" + tid + ": unlocking...");
        int level, node, id;

        // Iterate the tree backwards to reset the values set by the thread
        for (level=this.hTree-1; level>=0; level--) {
            // Calculate the id from level and thread. For this, find previous
            // node in tree then id = nodePrev mod 2.
            id = Math.floorDiv(tid, (int)Math.pow(2.0, (double)(level))) % 2;

            // Calculate current node from level and thread
            node = Math.floorDiv(tid, (int)Math.pow(2.0, (double)(level+1)));

            // Reset wantCS entry
            this.wantCS[level][2*node+id].set(false);
        }
    }
}
