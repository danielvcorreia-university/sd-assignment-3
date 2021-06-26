package serverSide.objects;

import clientSide.entities.*;
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
     * True for the passengers that have reached their turn to check in.
     */

    private final boolean [] passengers;

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
     *   Number of entity groups requesting the shutdown.
     */

    private int nEntities;

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
        passengers = new boolean[SimulPar.N];
        readyForNextPassenger = false;
        for (int i = 0; i < SimulPar.N; i++)
            passengers[i] = false;
        try {
            boardingQueue = new MemFIFO<>(new Integer[SimulPar.N]);
        } catch (MemException e) {
            GenericIO.writelnString("Instantiation of boarding FIFO failed: " + e.getMessage());
            boardingQueue = null;
            System.exit(1);
        }
        nEntities = 0;
        this.repos = repos;
    }

    /**
     * Operation queue empty
     * <p>
     * It is called to check if the passenger queue is currently empty
     */

    public boolean queueEmpty() throws RemoteException { return inQ == 0;
    }

    /**
     * Operation prepare for pass boarding
     * <p>
     * It is called by the hostess while waiting for passengers to arrive at the airport.
     */
    @Override
    public synchronized int prepareForPassBoarding () throws RemoteException {

        try{
            repos.setHostessState(0, HostessStates.WAIT_FOR_PASSENGER);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess remote exception on prepareForPassBoarding - : setHostessState" + e.getMessage ());
            System.exit (1);
        }

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
        return HostessStates.WAIT_FOR_PASSENGER;
    }

    /**
     * Operation wait in queue.
     * <p>
     * It is called by a passenger while waiting for his turn to show his documents to the hostess.
     *
     *     @param passengerId identification of the passenger
     *     @return true, if the passenger queue at the airport is not empty,
     *     false, otherwise
     *     and Hostess state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public synchronized int waitInQueue(int passengerId) throws RemoteException {

        try{
            repos.setPassengerState(passengerId, PassengerStates.IN_QUEUE);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Passenger remote exception on waitInQueue - : setPassengerState" + e.getMessage ());
            System.exit (1);
        }

        inQ++;                                        // the passenger arrives at the airport,

        try {
            boardingQueue.write(passengerId);                    // the customer sits down to wait for his turn
        } catch (MemException e) {
            GenericIO.writelnString("Insertion of customer id in waiting FIFO failed: " + e.getMessage());
            System.exit(1);
        }

        notifyAll();

        while (!passengers[passengerId]) {
            try {
                wait();
            } catch (InterruptedException e) {
                GenericIO.writelnString("Interruption: " + e.getMessage());
                System.exit(1);
            }
        }

        passengers[passengerId] = false;

        return PassengerStates.IN_QUEUE;
    }


    /**
     * Operation check documents.
     * <p>
     * It is called by the hostess while waiting for the first costumer in queue to show his documents.
     *
     *     @return true, if the passenger queue at the airport is not empty,
     *     false, otherwise
     *     and Hostess state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public synchronized ReturnBoolean checkDocuments() throws RemoteException {
        int passengerId;                                        //passenger id

        try{
            repos.setHostessState(0, HostessStates.CHECK_PASSENGER);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess remote exception on checkDocuments - : setHostessState" + e.getMessage ());
            System.exit (1);
        }

        inQ--;

        try {
            passengerId = boardingQueue.read();                            // the hostess calls the customer
            if ((passengerId < 0) || (passengerId >= SimulPar.N))
                throw new MemException("illegal passenger id!");
        } catch (MemException e) {
            GenericIO.writelnString("Retrieval of passenger id from boarding FIFO failed: " + e.getMessage());
            passengerId = -1;
            System.exit(1);
        }

        passengers[passengerId] = true;

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
        return new ReturnBoolean((!queueEmpty()), HostessStates.CHECK_PASSENGER);
    }

    /**
     * Operation show documents.
     * <p>
     * It is called by a passenger if the hostess has called him to check his documents.
     */

    public synchronized void  showDocuments() throws RemoteException {
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
     *
     *     @param HostessCount number of passengers on the plane
     *     @param CheckedPassengers number of passengers checked by hostess
     *     @return true, if the passenger queue at the airport is not empty,
     *     false, otherwise
     *     and Hostess state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public synchronized ReturnBoolean waitForNextPassenger(int HostessCount, int CheckedPassengers) throws RemoteException {

        try{
            repos.setHostessState(0, HostessStates.WAIT_FOR_PASSENGER);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess remote exception on waitForNextPassenger - : setHostessState" + e.getMessage ());
            System.exit (1);
        }

        canBoardThePlane = true;

        notifyAll();
        boolean b = (HostessCount+1) < 5;
        System.out.print("");
        b = (!readyForNextPassenger);
        System.out.print("");
        b = !((inP + CheckedPassengers) >= SimulPar.N);
        System.out.print("");
        System.out.print("");
        System.out.print("");
        System.out.print("");
        System.out.print("");
        System.out.print("");
        for(int i = 0; i < 20; i++) { System.out.print(""); }

        while ((inQ == 0 && (HostessCount+1) < 5 || (!readyForNextPassenger)) && !((inP + CheckedPassengers) >= SimulPar.N))    // the hostess waits for a passenger to enter the plane
        {
            try {
                wait();
            } catch (InterruptedException e) {
                GenericIO.writelnString("Interruption: " + e.getMessage());
                System.exit(1);
            }
        }

        readyForNextPassenger = false;
        return new ReturnBoolean((!queueEmpty()), HostessStates.WAIT_FOR_PASSENGER);
    }

    /**
     * Operation boarding the plane
     * <p>
     * It is called by the passengers when they are allowed to enter the plane.
     *
     *     @param passengerId identification of the passenger
     *     @return passenger state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public synchronized int boardThePlane(int passengerId) throws RemoteException {

        readyForNextPassenger = true;
        inP +=1;

        try{
            repos.setPassengerState(passengerId, PassengerStates.IN_FLIGHT);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Passenger remote exception on boardThePlane - : setPassengerState" + e.getMessage ());
            System.exit (1);
        }

        notifyAll();
        return PassengerStates.IN_FLIGHT;
    }

    /**
     *  Operation end of work.
     *
     *   End operation.
     *
     *      @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                              service fails
     */

    @Override
    public synchronized void endOperation () throws RemoteException {
        while (nEntities == 0)
            try
            { wait ();
            }
            catch (InterruptedException e) {}
    }

    /**
     *   Operation server shutdown.
     *
     *   Shutdown operation.
     *
     *      @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                              service fails
     */

    @Override
    public synchronized void shutdown () throws RemoteException {
        nEntities += 1;
        if (nEntities >= SimulPar.E)
            ServerAirLiftDepartureAirport.shutdown ();
        notifyAll ();
    }
}