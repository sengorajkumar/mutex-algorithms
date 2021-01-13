ReadWriteRegisterMutexes
========================

Implementation and comparison of the following mutual exclusion algorithms based
on read-write registers: Colored Bakery, Burn's OneBit Algorithm, Tournament
Algorithm based on Peterson, and Adaptive Algorithms.

**Authors:**

* Jose Carlos Martinez Garcia-Vaso <carlosgvaso@gmail.com>  
* Rajkumar Sengottuvel <raj.sengo@utexas.edu>

Running
-------

The project is managed using Gradle, and it is set up based on
[this tutorial](https://docs.gradle.org/current/samples/sample_building_java_applications.html).

To build and run the project run the following command from the root directory
of the project:

```console
./gradlew run
```

To get all other available options run:

```console
./gradlew tasks
```

Benchmarks
----------

Available benchmarks:

* No contention, no lock
* No contention, ReentrantLock
* No contention, TournamentLock
* No contention, ColoredBakeryLock
* No contention, OneBitLock
* No contention, AdaptiveLock
* Heavy contention (2,4,8), ReentrantLock
* Heavy contention (2,4,8), TournamentLock
* Heavy contention (2,4,8), ColoredBakeryLock
* Heavy contention (2,4,8), OneBitLock
* Heavy contention (2,4,8), AdaptiveLock

All benchmarks are based in the operation of incrementing/decrementing a shared
counter variable by one a set number of times. The benchmark measures how much
time in nanoseconds it takes for a set of threads to increment/decrement a
shared counter variable a fixed number of times per thread. Since this variable
can be changed by more than one thread concurrently, the variable is always
locked before incrementing/decrementing it, and it is unlocked immediately
after.

The no contention benchmarks use a single thread to increment the shared
variable a fix number of times. This allow us to measure how much overhead each
type of lock adds by just using it without any contention. The base case is when
the counter is incremented without using a lock. This is compared to the cases
when different locks are used to lock the shared variable before incrementing
it.

The heavy contention benchmarks use 2, 4 and 8 threads incrementing and decrementing the
shared counter variable concurrently a set number of times per thread. Half of
the threads increment the counter, and the other half decrement the counter.
This allows us to compare how threads perform in a heavy contention environment.
In this benchmark we compare all the different locks implemented with the
ReentrantLock implementation in the java.util.concurrent library.
