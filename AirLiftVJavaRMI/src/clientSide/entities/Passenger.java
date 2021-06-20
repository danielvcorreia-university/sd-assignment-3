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
     * Set passenger id.
     *
     * @param id passenger id
     */

    public void setPassengerId(int id) {
        passengerId = id;
    }

    /**
     * Get passenger id.
     *
     * @return passenger id
     */

    public int getPassengerId() {
        return passengerId;
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
     * Set passenger state.
     *
     * @param state new passenger state
     */

    public void setPassengerState(int state) {
        passengerState = state;
    }

    /**
     * Get passenger state.
     *
     * @return passenger state
     */

    public int getPassengerState() {
        return passengerState;
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


