package sharedRegions;

import commInfra.*;
import serverSide.main.*;
import genclass.GenericIO;
import interfaces.*;
import java.rmi.*;

/**
 *    Departure Airport.
 *
 *    It is responsible to keep a continuously updated account of the entities inside the departure airport
 *    and is implemented as an implicit monitor.
 *    All public methods are executed in mutual exclusion.
 *    There are four internal synchronization points: a single blocking point for the hostess, where she waits until
 *    the plane is ready for boarding so that she may proceed to the next flight;
 *    another single blocking point for the hostess, where she waits for the passengers to arrive at the airport;
 *    another single blocking point for the hostess, where she waits for the passenger at the front of the queue to
 *    show her his documents;
 *    and an array of blocking points, one per each passenger, where he both waits his turn to show the hostess
 *    his documents and waits until she has checked his documents and calls the next passenger.
 */

public class DepartureAirport implements DepartureAirportInterface {
    /**
     * Number of passengers in queue waiting for to show their documents to the hostess.
     */

    private int inQ;

    /**
     * Number of passengers waiting in the plane.
     */

    private int inP;

    /**
     * Reference to passenger threads.
     */

    private final Thread [] passengers;

    /**
     * Reference to hostess thread.
     */

    private Thread hostess;

    /**
     * Waiting queue at the transfer gate.
     */

    private MemFIFO<Integer> boardingQueue;

    /**
     * True if the hostess has finished checking current passenger and can move on to the next.
     */

    private boolean readyForNextPassenger;

    /**
     * True if the hostess can check next passenger documents.
     */

    private boolean readyToCheckDocuments;

    /**
     * True if the passenger can board the plane after showing documents to hostess.
     */

    private boolean canBoardThePlane;

    /**
     * Reference to the general repository.
     */

    private final GeneralReposInterface repos;

    /**
     * Departure airport instantiation.
     *
     * @param repos reference to the general repository
     */

    public DepartureAirport(GeneralReposInterface repos) {
        hostess = null;
        passengers = new Thread[SimulPar.N];
        readyForNextPassenger = false;
        for (int i = 0; i < SimulPar.N; i++)
            passengers[i] = null;
        try {
            boardingQueue = new MemFIFO<>(new Integer[SimulPar.N]);
        } catch (MemException e) {
            GenericIO.writelnString("Instantiation of boarding FIFO failed: " + e.getMessage());
            boardingQueue = null;
            System.exit(1);
        }
        this.repos = repos;
    }

    /**
     * Operation queue empty
     * <p>
     * It is called to check if the passenger queue is currently empty
     */

    public boolean queueEmpty() {
        return inQ == 0;
    }

    /**
     * Operation prepare for pass boarding
     * <p>
     * It is called by the hostess while waiting for passengers to arrive at the airport.
     */
    @Override
    public synchronized int prepareForPassBoarding () throws RemoteException {

        hostess = (Hostess) Thread.currentThread();

        ((Hostess) Thread.currentThread()).setHostessState(HostessStates.WAIT_FOR_PASSENGER);
        repos.setHostessState(((Hostess) Thread.currentThread()).getHostessId(), ((Hostess) Thread.currentThread()).getHostessState());
        ((Hostess) Thread.currentThread()).setHostessCount(0);
        inP = 0;
        while (inQ == 0)                             // the hostess waits for a passenger to arrive
        {
            try {
                wait();
            } catch (InterruptedException e) {
                GenericIO.writelnString("Interruption: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Operation wait in queue.
     * <p>
     * It is called by a passenger while waiting for his turn to show his documents to the hostess.
     */

    public synchronized void waitInQueue() {
        int passengerId;                                      // passenger id

        passengerId = ((Passenger) Thread.currentThread()).getPassengerId();
        passengers[passengerId] = (Passenger) Thread.currentThread();
        passengers[passengerId].setPassengerState(PassengerStates.IN_QUEUE);
        repos.setPassengerState(passengerId, passengers[passengerId].getPassengerState());
        inQ++;                                        // the passenger arrives at the airport,

        try {
            boardingQueue.write(passengerId);                    // the customer sits down to wait for his turn
        } catch (MemException e) {
            GenericIO.writelnString("Insertion of customer id in waiting FIFO failed: " + e.getMessage());
            System.exit(1);
        }

        notifyAll();

        while (!(((Passenger) Thread.currentThread()).getReadyToShowDocuments())) {
            try {
                wait();
            } catch (InterruptedException e) {
                GenericIO.writelnString("Interruption: " + e.getMessage());
                System.exit(1);
            }
        }
        ((Passenger) Thread.currentThread()).setReadyToShowDocuments(false);
    }


    /**
     * Operation check documents.
     * <p>
     * It is called by the hostess while waiting for the first costumer in queue to show his documents.
     */

    public synchronized void checkDocuments() {
        int passengerId;                                        //passenger id

        ((Hostess) Thread.currentThread()).setHostessState(HostessStates.CHECK_PASSENGER);
        repos.setHostessState(((Hostess) Thread.currentThread()).getHostessId(), ((Hostess) Thread.currentThread()).getHostessState());

        inQ--;
        ((Hostess) Thread.currentThread()).setPassengerInQueue(!queueEmpty());

        try {
            passengerId = boardingQueue.read();                            // the hostess calls the customer
            if ((passengerId < 0) || (passengerId >= SimulPar.N))
                throw new MemException("illegal passenger id!");
        } catch (MemException e) {
            GenericIO.writelnString("Retrieval of passenger id from boarding FIFO failed: " + e.getMessage());
            passengerId = -1;
            System.exit(1);
        }

        passengers[passengerId].setReadyToShowDocuments(true);

        notifyAll();

        while (!readyToCheckDocuments)             // the hostess waits for the passenger to give his documents
        {
            try {
                wait();
            } catch (InterruptedException e) {
                GenericIO.writelnString("Interruption: " + e.getMessage());
                System.exit(1);
            }
        }

        readyToCheckDocuments = false;
    }

    /**
     * Operation show documents.
     * <p>
     * It is called by a passenger if the hostess has called him to check his documents.
     */

    public synchronized void  showDocuments() {
        readyToCheckDocuments = true;

        notifyAll();
        while (!canBoardThePlane)   // the passenger waits until he is clear to proceed
        {
            try {
                wait();
            } catch (InterruptedException e) {
                GenericIO.writelnString("Interruption: " + e.getMessage());
                System.exit(1);
            }
        }
        canBoardThePlane = false;
    }

    /**
     * Operation wait for next passenger.
     * <p>
     * It is called by the hostess while waiting for the next passenger in queue.
     */

    public synchronized void waitForNextPassenger() {
        ((Hostess) Thread.currentThread()).setHostessState(HostessStates.WAIT_FOR_PASSENGER);
        repos.setHostessState(((Hostess) Thread.currentThread()).getHostessId(), ((Hostess) Thread.currentThread()).getHostessState());
        ((Hostess) Thread.currentThread()).setHostessCount(((Hostess) Thread.currentThread()).getHostessCount()+1);
        canBoardThePlane = true;

        notifyAll();
        while ((inQ == 0 && ((Hostess) Thread.currentThread()).getHostessCount() < 5 || (!readyForNextPassenger)) && !((inP + ((Hostess) Thread.currentThread()).getCheckedPassengers()) >= SimulPar.N))    // the hostess waits for a passenger to enter the plane
        {
            //Plane.getInF()
            try {
                wait();
            } catch (InterruptedException e) {
                GenericIO.writelnString("Interruption: " + e.getMessage());
                System.exit(1);
            }
        }

        readyForNextPassenger = false;
        ((Hostess) Thread.currentThread()).setPassengerInQueue(!queueEmpty());
    }

    /**
     * Operation boarding the plane
     * <p>
     * It is called by the passengers when they are allowed to enter the plane.
     */

    public synchronized void boardThePlane() {

        readyForNextPassenger = true;
        inP +=1;
        ((Passenger) Thread.currentThread()).setPassengerState(PassengerStates.IN_FLIGHT);
        repos.setPassengerState(((Passenger) Thread.currentThread()).getPassengerId(), ((Passenger) Thread.currentThread()).getPassengerState());
        notifyAll();
    }
}