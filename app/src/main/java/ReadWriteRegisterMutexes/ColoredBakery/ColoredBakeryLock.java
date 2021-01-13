package ReadWriteRegisterMutexes.ColoredBakery;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * ColoredBakeryLock is a lock implementation of the Black-White Bakery Algorithm
 *
 * This implementation is based on the pseudo-code from the Synchronization
 * Algorithms and Concurrent Programming textbook by Gadi Taubenfeld at page 56.
 */

public class ColoredBakeryLock implements ReadWriteRegisterMutexes.Lock{

    /* Ticket Colors */
    private final int WHITE = 0;
    private final int BLACK = 1;

    /* Shared color bit */
    private AtomicInteger sharedColor;

    /* Flag to indicate the process in doorway */
    private AtomicBoolean choosing[];

    /* Ticket number. ticketNum[i] is the ticket for process i */
    private AtomicInteger []ticketNum;

    /* Ticket color. ticketColor[i] is the color of process i's ticket */
    private AtomicInteger []ticketColor;

    /* Number of process*/
    private int N;
    //private static final Logger log = LogManager.getRootLogger();

    public ColoredBakeryLock(int numProcess){
        N = numProcess;
        this.sharedColor = new AtomicInteger(this.WHITE);
        this.choosing = new AtomicBoolean[N];
        this.ticketNum = new AtomicInteger[N];
        this.ticketColor = new AtomicInteger[N];
        for (int i = 0; i < N; i++) {
            this.choosing[i] = new AtomicBoolean(false);
            this.ticketNum[i] = new AtomicInteger(0);
            this.ticketColor[i] = new AtomicInteger(this.WHITE);
        }
    }

    @Override
    public void lock(int pid) {
        //log.debug("Enter lock, PID : " + pid);
        //Step 1
        //Begin of Doorway
        int i = pid;
        choosing[i].set(true);
        //ticketColor[i].set(sharedColor.get());
        ticketColor[i].getAndSet(sharedColor.get());

        for (int j = 0; j < N; j++) {
            if(ticketColor[j].get() == ticketColor[i].get() && ticketNum[j].get() > ticketNum[i].get()){
                ticketNum[i].set(ticketNum[j].get());
                //log.debug("Found the largest ticket : " + ticketNum[j].get());
            }
        }
        int myTicket = ticketNum[i].incrementAndGet();
        ////log.debug("My ticket ticket : " + myTicket);
        //System.out.println("My ticket ticket : " + myTicket);
        choosing[i].set(false);
        //End of Doorway

        //Step 2
        /*
        * The order between colored tickets:
        *  - If two tickets have different colors, the ticket whose color is different from sharedColor is smaller
        *  - If two tickets have the same color, the ticket with smaller number is smaller
        *  - If tickets of two processes have the same color and the same number then the process with smaller identifier (process id) is smaller
        * */

        for (int j = 0; j < N; j++) {
            while(choosing[j].get()){
                //log.warn("Waiting for process " + j + " finish choosing and move out of doorway ");
                ; //Wait for process finish choosing and move out of doorway
            }
            if(ticketColor[j].get() == ticketColor[i].get()){
                while((ticketNum[j].get() != 0) && (ticketColor[j].get() == ticketColor[i].get()) &&
                        ((ticketNum[j].get() < ticketNum[i].get()) || ((ticketNum[j].get() == ticketNum[i].get()) && j < i))){
                    ;
                    //log.warn("PID : " + pid + " waiting at while loop 1");
                    //System.out.print("PID : " + pid + " waiting at while loop 1  ");
                }
            }else {
                while ((ticketNum[j].get() != 0) && (ticketColor[i].get() == sharedColor.get()) && (ticketColor[j].get() != ticketColor[i].get())) {
                    ;
                    //log.warn("PID : " + pid + " waiting at while loop 2");
                    //System.out.print("PID : " + pid + " waiting at while loop 2  ");
                }
            }
        }
        //log.debug("Exit lock, PID : " + pid);
    }

    @Override
    public void unlock(int pid) {
        //log.debug("Enter unlock, PID : " + pid);
        if(ticketColor[pid].get() == BLACK){
            int oldColor = sharedColor.get();
            if(!sharedColor.compareAndSet(oldColor, WHITE)){
                System.out.println("compareAndSet is false for WHITE");
            }
        }else{
            int oldColor = sharedColor.get();
            if(!sharedColor.compareAndSet(oldColor, BLACK)){
                System.out.println("compareAndSet is false for BLACK");
            }
        }
        ticketNum[pid].getAndSet(0);
        //log.debug("Exit unlock, PID : " + pid);
    }
}

