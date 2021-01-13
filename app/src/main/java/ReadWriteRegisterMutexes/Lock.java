/** Simple mutex lock interface
 */

package ReadWriteRegisterMutexes;

/** Simple mutex lock interface
 */
public interface Lock {
    /** Lock or critical section entry protocol method of mutex
     * 
     * @param tid Thread ID
     */
    public void lock(int tid);

    /** Unlock or critical section exit protocol method of mutex
     * 
     * @param tid Thread ID
     */
    public void unlock(int tid);
}
