
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

import ReadWriteRegisterMutexes.Adaptive.AdaptiveLock;

/** AdaptiveLock benchmarks
 */
@BenchmarkMode(Mode.SingleShotTime) // Measure single run time in benchmarks
@OutputTimeUnit(TimeUnit.NANOSECONDS) // Use nanoseconds for output
@Fork(1) // Run 1 fork with no warmup forks
@Warmup(iterations=5) // Run that number of warmup iterations
@Measurement(iterations=20) // Run that number of measurement iterations
public class AdaptiveLockBenchmarks {

    /** Heavy contention benchmark state
     */
    @State(Scope.Benchmark)
    public static class HeavyContentionState {

        /** Global setting for maximum number of splitters in adaptive locks
         */
        int adaptiveMaxSplitters;

        /** Benchmark used
         */
        IncrementBenchmark benchmark;

        /** Lock being benchmarked
         */
        AdaptiveLock lock;

        /** Shared variable final state
         */
        int cFinal;

        /** Setup trial variables for benchmark
         */
        @Setup(Level.Trial)
        public void doSetupTrial() {
            System.out.print("Setup trial: ");
            this.adaptiveMaxSplitters = 80000000;
            this.benchmark = new IncrementBenchmark();
        }

        /** Setup iteration variables for benchmark
         */
        @Setup(Level.Iteration)
        public void doSetupIteration() {
            System.out.print("Setup iteration: ");
            this.lock = new AdaptiveLock
            (
                this.benchmark.getHeavyContentionThreadNum(),
                this.adaptiveMaxSplitters
            );
            this.cFinal = -1;   // Set to negative to ensure it fails if not run
        }

        /** Teardown iteration variables for benchmark
         */
        @TearDown(Level.Iteration)
        public void doTearDownIteration() {
            System.out.print("Teardown iteration: ");
            if (this.cFinal != 0) {
                System.out.print("FAIL: Concurrency error: got " + this.cFinal
                    + " expected 0: ");
            } else {
                System.out.print("PASS: ");
            }
            this.lock = null;
        }
    }

    /** No contention benchmark state
     */
    @State(Scope.Benchmark)
    public static class NoContentionState {

        /** Global setting for maximum number of splitters in adaptive locks
         */
        int adaptiveMaxSplitters;

        /** Benchmark used
         */
        IncrementBenchmark benchmark;

        /** Lock being benchmarked
         */
        AdaptiveLock lock;

        /** Shared variable final state
         */
        int cFinal;

        /** Setup trial variables for benchmark
         */
        @Setup(Level.Trial)
        public void doSetupTrial() {
            System.out.print("Setup trial: ");
            this.adaptiveMaxSplitters = 10000000;
            this.benchmark = new IncrementBenchmark();
        }

        /** Setup iteration variables for benchmark
         */
        @Setup(Level.Iteration)
        public void doSetupIteration() {
            System.out.print("Setup iteration: ");
            this.lock = new AdaptiveLock
            (
                this.benchmark.getNoContentionThreadNum(),
                this.adaptiveMaxSplitters
            );
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
            this.lock = null;
        }
    }

    /** Heavy contention benchmark
     * 
     * The benchmark measures the average time that gHeavyContentionThreadNum
     * number of worker threads take to increment/decrement a shared variable
     * gIncrements number of times. Half of the threads will increment the
     * shared variable by 1 each time in a loop, and the other half will
     * decrement it by 1 each time in a loop. Each time that any of the threads
     * wants to increment/decrement the shared variable, they must request the
     * lock, and they release the lock immediately after.
     * 
     * This benchmark is designed to measure how well this lock implementation
     * performs under heavy contention.
     */
    @Benchmark
    public void heavyContention(HeavyContentionState state) {
        System.out.print("Running benchmark: ");
        state.cFinal = state.benchmark.runIncrementBenchmark
        (
            state.benchmark.getHeavyContentionThreadNum(),
            state.benchmark.getIncrementNum(),
            state.lock
        );
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
            state.lock
        );
    }
}