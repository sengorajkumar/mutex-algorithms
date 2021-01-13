/**
 * Worker thread class to increment/decrement a shared counter using mutexes
 */
package ReadWriteRegisterMutexes;

import java.util.concurrent.locks.ReentrantLock;

/** Worker thread class to increment/decrement a shared counter
 */
public class Worker implements Runnable {
    // Class variables
    private volatile static int c; // Counter to increment/decrement

    // Instance variables
    private boolean add;         // True to increment c, False to decrement
    private int increments;      // Number of increments/decrements per worker
    private Lock lock;           // Lock using the Lock interface
    private ReentrantLock lockR; // ReentrantLock lock
    private LockType lockType;   // Lock type
    private int tid;             // Thread ID

    /** Types of locks supported
     */
    public enum LockType {
        NO_LOCK,            // No lock
        LOCK_INTERFACE,     // Lock interface compliant lock
        LOCK_REENTRANT      // ReentrantLock
    }

    /** Constructor for NO_LOCK lock type
     * 
     * @param tid   Thread ID
     * @param add   True to increment c, False to decrement
     * @param increments    Number of increments/decrements per worker
     */
    public Worker(int tid, boolean add, int increments) {
        this.tid = tid;
        this.add = add;
        this.increments = increments;
        this.lockType = LockType.NO_LOCK;
    }

    /** Constructor for LOCK_INTERFACE lock type
     * 
     * @param tid   Thread ID
     * @param add   True to increment c, False to decrement
     * @param increments    Number of increments/decrements per worker
     * @param lock  Lock using the Lock interface
     */
    public Worker(int tid, boolean add, int increments, Lock lock) {
        this.tid = tid;
        this.add = add;
        this.increments = increments;
        this.lock = lock;
        this.lockType = LockType.LOCK_INTERFACE;
    }

    /** Constructor for LOCK_REENTRANT lock type
     * 
     * @param tid   Thread ID
     * @param add   True to increment c, False to decrement
     * @param increments    Number of increments/decrements per worker
     * @param lock  ReentrantLock lock
     */
    public Worker(int tid, boolean add, int increments, ReentrantLock lock) {
        this.tid = tid;
        this.add = add;
        this.increments = increments;
        this.lockR = lock;
        this.lockType = LockType.LOCK_REENTRANT;
    }

    /** Get add instance variable value
     * 
     * @return Returns add instance variable value
     */
    public boolean getAdd() {
        return this.add;
    }

    /** Get c class variable value
     * 
     * @return Returns c class variable value
     */
    public int getC() {
        return Worker.c;
    }

    /** Get increments instance variable value
     * 
     * @return Returns increments instance variable value
     */
    public int getIncrements() {
        return this.increments;
    }

    /** Get lock instance variable value
     * 
     * @return Returns lock instance variable value
     */
    public Lock getLock() {
        return this.lock;
    }

    /** Get lockR instance variable value
     * 
     * @return Returns lockR instance variable value
     */
    public ReentrantLock getLockR() {
        return this.lockR;
    }

    /** Get lockType instance variable value
     * 
     * @return Returns lockType instance variable value
     */
    public LockType getLockType() {
        return this.lockType;
    }

    /** Get tid instance variable value
     * 
     * @return Returns tid instance variable value
     */
    public int getTid() {
        return this.tid;
    }

    /** Increment/decrement shared counter using the specified lock
     */
    public void run() {
        // Increment instance variable c the configured number of times
        for (int i=0; i<this.increments; i++) {
            // Choose what lock to use
            switch(this.lockType) {
                case NO_LOCK: // No lock
                    if (this.add) {
                        Worker.c++;
                    } else {
                        Worker.c--;
                    }
                    break;
                case LOCK_INTERFACE: // Lock interface
                    if (this.add) {
                        this.lock.lock(this.tid);
                        Worker.c++;
                        this.lock.unlock(this.tid);
                    } else {
                        this.lock.lock(this.tid);
                        Worker.c--;
                        this.lock.unlock(this.tid);
                    }
                    break;
                case LOCK_REENTRANT: // Reentrant lock
                    if (this.add) {
                        this.lockR.lock();
                        Worker.c++;
                        this.lockR.unlock();
                    } else {
                        this.lockR.lock();
                        Worker.c--;
                        this.lockR.unlock();
                    }
                    break;
                default: // Bad configuration
                    System.out.println("ERROR: T" + this.tid + " entered "
                        + "illegal state: this.lockType = " + this.lockType);
            }
        }
    }

    /** Set add instance variable value
     * 
     * @param add   True to increment c, False to decrement
     */
    public void setAdd(boolean add) {
        this.add = add;
    }

    /** Set c class variable value
     * 
     * @param c   Counter to increment/decrement
     */
    public void setC(int c) {
        Worker.c = c;
    }

    /** Set increments instance variable value
     * 
     * @param increments    Number of increments/decrements per worker
     */
    public void setIncrements(int increments) {
        this.increments = increments;
    }

    /** Set lock instance variable value
     * 
     * @param lock   Lock using the Lock interface
     */
    public void setLock(Lock lock) {
        this.lock = lock;
    }

    /** Set lockR instance variable value
     * 
     * @param lockR   ReentrantLock lock
     */
    public void setLockR(ReentrantLock lockR) {
        this.lockR = lockR;
    }

    /** Set lockType instance variable value
     * 
     * @param lockType   Lock type
     */
    public void setLockType(LockType lockType) {
        this.lockType = lockType;
    }

    /** Set tid instance variable value
     * 
     * @param tid   Thread ID
     */
    public void setTid(int tid) {
        this.tid = tid;
    }
}

