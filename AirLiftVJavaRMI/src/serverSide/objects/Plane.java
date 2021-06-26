package serverSide.objects;

import clientSide.entities.HostessStates;
import clientSide.entities.PilotStates;
import commInfra.*;
import serverSide.main.*;
import genclass.GenericIO;
import interfaces.*;

import javax.print.attribute.standard.RequestingUserName;
import java.rmi.*;

/**
 *    Plane.
 *
 *    It is responsible to keep a continuously updated account of the entities inside the plane
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

public class Plane implements PlaneInterface{

    /**
     * Reference to number of passengers in the plane.
     */

    private static int inF;

    /**
     * True if the pilot communicated to the hostess that the plane is ready for boarding.
     */

    private boolean nextFlight;

    /**
     * True if the plane is ready to take off per hostess signal.
     */

    private boolean readyToTakeOff;

    /**
     * True if the plane has arrived and the passengers should deboard.
     */

    private boolean startDeboarding;

    /**
     *   Number of entity groups requesting the shutdown.
     */

    private int nEntities;

    /**
     * Reference to the general repository.
     */

    private final GeneralReposInterface repos;

    /**
     * Plane instantiation.
     *
     * @param repos reference to the general repository
     */

    public Plane(GeneralReposInterface repos) {
        inF = 0;
        nEntities = 0;
        nextFlight = false;
        readyToTakeOff = false;
        startDeboarding = false;
        this.repos = repos;
    }

    /**
     * Operation to get number passengers in flight
     * <p>
     * It is called by the passenger when he is leaving the plane at destination airport so he can check if he is the last to leave.
     *
     * @return Number of passengers currently in flight
     */

    public synchronized int getInF() throws RemoteException { return inF;
    }

    /**
     * Operation to report the final report
     * <p>
     * It is called by the pilot after he parks the plane at the transfer gate and there are no more passengers to transport
     */

    public synchronized void reportFinalReport() throws RemoteException { repos.reportFinalInfo();
    }

    /**
     * Operation park at transfer gate.
     * <p>
     * It is called by the pilot when he parks the plane at the transfer gate.
     *     @return Pilot state
     */

    public synchronized int parkAtTransferGate() throws RemoteException {

        try{
            repos.setPilotState(PilotStates.AT_TRANSFER_GATE);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Pilot remote exception on parkAtTransferGate - : setHostessState" + e.getMessage ());
            System.exit (1);
        }

        return PilotStates.AT_TRANSFER_GATE;
    }

    /**
     * Operation inform plane ready for boarding
     * <p>
     * It is called by the pilot to inform the hostess that the plane is ready for boarding.
     *     @return Pilot state
     */

    public synchronized int informPlaneReadyForBoarding() throws RemoteException {
        nextFlight = true;

        try{
            repos.setPilotState(PilotStates.READY_FOR_BOARDING);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Pilot remote exception on informPlaneReadyForBoarding - : setHostessState" + e.getMessage ());
            System.exit (1);
        }

        notifyAll();

        return PilotStates.READY_FOR_BOARDING;
    }

    /**
     * Operation wait for next flight
     * <p>
     * It is called by the hostess while waiting for plane to be ready for boarding.
     *
     *     @param first true for the the first flight, false otherwise
     *     @param CheckedPassengers number of passengers that the hostess has previously checked
     *     @return total number of passengers that have taken the flight
     *     and Hostess state
     */

    public synchronized ReturnInt waitForNextFlight(boolean first, int CheckedPassengers) throws RemoteException {
        int temp;
        if(!first) {
            try{
                repos.setHostessState(0, HostessStates.WAIT_FOR_FLIGHT);
            }
            catch (RemoteException e)
            { GenericIO.writelnString ("Hostess remote exception on waitForNextFlight - : setHostessState" + e.getMessage ());
                System.exit (1);
            }
        }
        for(int i = 0; i < 40; i++) { System.out.print(""); }
        temp = inF;

        if (!(CheckedPassengers + inF == SimulPar.N)) {
            while(!nextFlight)
            {
                try {
                    wait();
                } catch (InterruptedException e) {
                    GenericIO.writelnString("Interruption: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
        nextFlight = false;

        return new ReturnInt(CheckedPassengers + temp, HostessStates.WAIT_FOR_FLIGHT);
    }

    /**
     * Operation wait for all passengers to board the plane.
     * <p>
     * It is called by the pilot after he announced the hostess
     * that the plane is ready for boarding .
     *     @return Pilot state
     */

    public synchronized int waitForAllInBoarding() throws RemoteException {

        try{
            repos.setPilotState(PilotStates.WAITING_FOR_BOARDING);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Pilot remote exception on waitForAllInBoarding - : setHostessState" + e.getMessage ());
            System.exit (1);
        }

        while (!readyToTakeOff) {
            try {
                wait();
            } catch (InterruptedException e) {
                GenericIO.writelnString("While waiting for passenger boarding: " + e.getMessage());
                System.exit(1);
            }
        }
        readyToTakeOff = false;

        try{
            repos.setPilotState(PilotStates.FLYING_FORWARD);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Pilot remote exception on announceArrival - : setHostessState" + e.getMessage ());
            System.exit (1);
        }

        return PilotStates.FLYING_FORWARD;
    }

    /**
     * Operation inform the pilot that the plane is ready to departure.
     * <p>
     * It is called by the hostess when she ended the check in of the passengers.
     *     @return Hostess state
     */

    public synchronized int informPlaneReadyToTakeOff() throws RemoteException {

        readyToTakeOff = true;

        try{
            repos.setHostessState(0, HostessStates.READY_TO_FLY);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess remote exception on informPlaneReadyToTakeOff - : setHostessState" + e.getMessage ());
            System.exit (1);
        }
        notifyAll();

        return HostessStates.READY_TO_FLY;
    }

    /**
     * Operation wait for end of flight
     * <p>
     * It is called by the passengers when they are inside the plane and begin their waiting journey.
     */

        public synchronized void waitForEndOfFlight() throws RemoteException {

        inF += 1;

        while (!startDeboarding) {
            try {
                wait();
            } catch (InterruptedException e) {
                GenericIO.writelnString("Interruption: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Operation announce that the plane has arrived at the destination airport.
     * <p>
     * It is called by the pilot when the plane has arrived at the destination airport.
     *
     *     @param TransportedPassengers number of passengers that have previously taken the flight
     *     @return total number of passengers that have taken the flight
     *     and Pilot state
     */

    public synchronized ReturnInt announceArrival(int TransportedPassengers) throws RemoteException {
        int temp;
        try{
            repos.setPilotState(PilotStates.DEBOARDING);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Pilot remote exception on announceArrival - : setHostessState" + e.getMessage ());
            System.exit (1);
        }

        temp = inF;
        startDeboarding = true;

        notifyAll();

        while (inF != 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                GenericIO.writelnString("Interruption: " + e.getMessage());
                System.exit(1);
            }
        }

        startDeboarding = false;

        try{
            repos.setPilotState(PilotStates.FLYING_BACK);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Pilot remote exception on announceArrival - : setHostessState" + e.getMessage ());
            System.exit (1);
        }

        return new ReturnInt(TransportedPassengers+temp, PilotStates.FLYING_BACK);
    }

    /**
     * Operation to notify the pilot
     * <p>
     * It is called by the last passenger when he is leaving the plane to awake the pilot who is waiting.
     */

    public synchronized void notifyPilot() throws RemoteException {
        inF = 0;

        notifyAll();
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
            ServerAirLiftPlane.shutdown ();
        notifyAll ();
    }
}
