/** Comparison of mutual exclusion algorithms based on read-write registers
 */

package ReadWriteRegisterMutexes;

import java.io.IOException;

/** App class is the main class of the application
 * 
 * It runs benchmarks for all the implemented read-write register locks.
 */
public class App {

    /** Entry point of the App class
     * 
     * It runs benchmarks for all the implemented read-write register locks.
     */
    public static void main(String[] args) {

        // Run the benchmarks
        try {
            org.openjdk.jmh.Main.main(args);
        } catch (IOException e) {
            System.out.println("ERROR: " + e);
        }
    }
}
