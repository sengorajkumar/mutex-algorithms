
package ReadWriteRegisterMutexes;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/** No lock benchmarks
 */
@BenchmarkMode(Mode.SingleShotTime) // Measure single run time in benchmarks
@OutputTimeUnit(TimeUnit.NANOSECONDS) // Use nanoseconds for output
@Fork(1) // Run 1 fork with no warmup forks
@Warmup(iterations=5) // Run that number of warmup iterations
@Measurement(iterations=20) // Run that number of measurement iterations
public class NoLockBenchmarks {

    /** No contention benchmark state
     */
    @State(Scope.Benchmark)
    public static class NoContentionState {

        /** Benchmark used
         */
        IncrementBenchmark benchmark;

        /** Shared variable final state
         */
        int cFinal;

        /** Setup trial variables for benchmark
         */
        @Setup(Level.Trial)
        public void doSetupTrial() {
            System.out.print("Setup trial: ");
            this.benchmark = new IncrementBenchmark();
        }

        /** Setup iteration variables for benchmark
         */
        @Setup(Level.Iteration)
        public void doSetupIteration() {
            System.out.print("Setup iteration: ");
            this.cFinal = -1;   // Set to negative to ensure it fails if not run
        }

        /** Teardown iteration variables for benchmark
         */
        @TearDown(Level.Iteration)
        public void doTearDownIteration() {
            System.out.print("Teardown iteration: ");
            if (this.cFinal != this.benchmark.getIncrementNum()) {
                System.out.print("FAIL: Concurrency error: got " + this.cFinal
                    + " expected " + this.benchmark.getIncrementNum() + ": ");
            } else {
                System.out.print("PASS: ");
            }
        }
    }

    /** No contention benchmark
     * 
     * The benchmark measures the average time that gNoContentionThreadNum
     * number of worker threads take to increment a shared variable gIncrements
     * number of times. Each time that the thread wants to increment the shared
     * variable, it musts request the lock, and it releases the lock immediately
     * after.
     * 
     * This benchmark is designed to measure how much overhead this lock
     * implementation adds to the operation without any contention.
     */
    @Benchmark
    public void noContention(NoContentionState state) {
        System.out.print("Running benchmark: ");
        state.cFinal = state.benchmark.runIncrementBenchmark
        (
            state.benchmark.getNoContentionThreadNum(),
            state.benchmark.getIncrementNum(),
            null
        );
    }
}