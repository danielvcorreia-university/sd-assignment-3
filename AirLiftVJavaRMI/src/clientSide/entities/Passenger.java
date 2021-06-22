package clientSide.entities;

import commInfra.*;
import interfaces.*;
import java.rmi.*;
import serverSide.main.SimulPar;
import genclass.GenericIO;

/**
 *   Passenger thread.
 *
 *   It simulates the passenger life cycle.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on remote calls under Java RMI.
 */

public class Passenger extends Thread {
    /**
     * Passenger identification.
     */

    private int passengerId;

    /**
     * Passenger state.
     */

    private int passengerState;

    /**
     * True if the passenger has been called by the hostess to show his documents.
     */

    private boolean readyToShowDocuments;

    /**
     * Remote reference to the departure airport.
     */

    private final DepartureAirportInterface depAirport;

    /**
     * Remote reference to the plane.
     */

    private final PlaneInterface plane;

    /**
     * Remote reference to the destination airport.
     */

    private final DestinationAirportInterface destAirport;

    /**
     * Instantiation of a passenger thread.
     *
     * @param name        thread name
     * @param passengerId passenger id
     * @param depAirport  remote reference to the departure airport
     * @param plane       remote reference to the plane
     * @param destAirport remote reference to the destination airport
     */

    public Passenger(String name, int passengerId, DepartureAirportInterface depAirport, PlaneInterface plane, DestinationAirportInterface destAirport) {
        super(name);
        this.readyToShowDocuments = false;
        this.passengerId = passengerId;
        passengerState = PassengerStates.GOING_TO_AIRPORT;
        this.depAirport = depAirport;
        this.plane = plane;
        this.readyToShowDocuments = false;
        this.destAirport = destAirport;
    }

    /**
     * Set if passenger is ready to show documents to hostess.
     *
     * @param bool ready to show documents
     */

    public void setReadyToShowDocuments(boolean bool) {
        readyToShowDocuments = bool;
    }

    /**
     * Get ready to show documents.
     *
     * @return True if ready to show documents
     */

    public boolean getReadyToShowDocuments() {
        return readyToShowDocuments;
    }

    /**
     * Life cycle of the passenger.
     */

    @Override
    public void run() {
        boolean lastPassenger = false;
        int inF;

        travelToAirport();                // Takes random time
        waitInQueue();
        showDocuments();
        boardThePlane();
        waitForEndOfFlight();
        inF = getInF();
        lastPassenger = leaveThePlane(inF);             //see you later aligator
        if (lastPassenger) { notifyPilot(); }
    }

    /**
     *  Last Passenger that leaves the plane notifies the pilot that the plane can return to Departure Airport.
     *
     *  Remote operation.
     */

    private void notifyPilot()
    {
        try
        { passengerState = plane.notifyPilot();
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Passenger " + passengerId + " remote exception on notifyPilot: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *  Passenger leaves the plane at Destination Airport.
     *
     *  Remote operation.
     */

    private boolean leaveThePlane(int inF)
    {
        ReturnBoolean ret = null;

        try
        { ret = destAirport.leaveThePlane (passengerId, inF);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Passenger " + passengerId + " remote exception on leaveThePlane: " + e.getMessage ());
            System.exit (1);
        }
        passengerState = ret.getIntStateVal ();
        return ret.getBooleanVal ();
    }

    /**
     *  Passenger gets information about how much passengers are still in flight.
     *
     *  Remote operation.
     */

    private int getInF()
    {
        ReturnInt ret = null;                                // return value

        try
        { ret = plane.getInF ();
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Passenger " + passengerId + " remote exception on getInF: " + e.getMessage ());
            System.exit (1);
        }
        passengerState = ret.getIntStateVal ();
        return ret.getIntVal ();
    }

    /**
     *  Passenger waits for the end of the flight.
     *
     *  Remote operation.
     */

    private void waitForEndOfFlight()
    {
        try
        { passengerState = plane.waitForEndOfFlight();
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Passenger " + passengerId + " remote exception on waitForEndOfFlight: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *  Passenger boards the plane.
     *
     *  Remote operation.
     */

    private void boardThePlane()
    {
        try
        { passengerState = depAirport.boardThePlane (passengerId);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Passenger " + passengerId + " remote exception on boardThePlane: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *  Passenger shows documents to hostess.
     *
     *  Remote operation.
     */

    private void showDocuments()
    {
        try
        { passengerState = depAirport.showDocuments();
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Passenger " + passengerId + " remote exception on showDocuments: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *  Passenger waits in queue until he is called by the hostess.
     *
     *  Remote operation.
     */

    private void waitInQueue()
    {
        try
        { passengerState = depAirport.waitInQueue (passengerId, getReadyToShowDocuments());
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Passenger " + passengerId + " remote exception on waitInQueue: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     * Travel to airport.
     * <p>
     * Internal operation. Sleeps for an amount of time.
     */

    private void travelToAirport() {
        try {
            sleep((long) (1 + 400 * Math.random()));
        } catch (InterruptedException e) {
            GenericIO.writelnString("Interruption: " + e.getMessage());
            System.exit(1);
        }
    }
}


