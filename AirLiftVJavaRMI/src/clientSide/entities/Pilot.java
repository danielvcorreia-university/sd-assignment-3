package clientSide.entities;

import commInfra.*;
import interfaces.*;
import java.rmi.*;
import serverSide.main.SimulPar;
import genclass.GenericIO;

/**
 *   Pilot thread.
 *
 *   It simulates the pilot life cycle.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on remote calls under Java RMI.
 */

public class Pilot extends Thread {
    /**
     * Pilot identification.
     */

    private int pilotId;

    /**
     * Count number of passengers transported by pilot.
     */

    private int transportedPassengers;

    /**
     * Pilot state.
     */

    private int pilotState;

    /**
     * True if the plane is ready to take off.
     */

    private boolean readyToTakeOff;

    /**
     * Remote reference to the plane.
     */

    private final PlaneInterface plane;

    /**
     * Remote reference to the destination airport.
     */

    private final DestinationAirportInterface destAirport;

    /**
     * Instantiation of a pilot thread.
     *
     * @param name       thread name
     * @param pilotId    pilot id
     * @param plane      remote reference to the plane
     * @param destAirport remote reference to the destination airport
     */

    public Pilot(String name, int pilotId, PlaneInterface plane, DestinationAirportInterface destAirport) {
        super(name);
        this.readyToTakeOff = false;
        this.pilotId = pilotId;
        pilotState = PilotStates.AT_TRANSFER_GATE;
        this.plane = plane;
        this.destAirport = destAirport;
    }

    /**
     * Set pilot id.
     *
     * @param id pilot id
     */

    public void setPilotId(int id) {
        pilotId = id;
    }

    /**
     * Get pilot id.
     *
     * @return pilot id
     */

    public int getPilotId() {
        return pilotId;
    }

    /**
     * Set number of passengers which pilot has transported.
     *
     * @param nTransportedPassengers number of passengers checked
     */

    public void setTransportedPassengers(int nTransportedPassengers) { transportedPassengers = nTransportedPassengers; }

    /**
     * Get number of passengers which pilot has transported.
     *
     * @return hostess count
     */

    public int getTransportedPassengers() {
        return transportedPassengers;
    }

    /**
     * Set if hostess has informed the pilot that the plane is ready to take off.
     *
     * @param bool ready to take off
     */

    public void setReadyToTakeOff(boolean bool) {
        readyToTakeOff = bool;
    }

    /**
     * Get ready to take off
     *
     * @return True if ready to take off
     */

    public boolean getReadyToTakeOff() {
        return readyToTakeOff;
    }

    /**
     * Set pilot state.
     *
     * @param state new pilot state
     */

    public void setPilotState(int state) {
        pilotState = state;
    }

    /**
     * Get pilot state.
     *
     * @return pilot state
     */

    public int getPilotState() {
        return pilotState;
    }

    /**
     * Life cycle of the pilot.
     */

    @Override
    public void run() {
        boolean endOp = false;                                       // flag signaling end of operations

        parkAtTransferGate();
        while (!endOp) {
            informPlaneReadyForBoarding();
            waitForAllInBoarding();
            flyToDestinationPoint();
            announceArrival();
            flyToDeparturePoint();
            parkAtTransferGate();
            if (getTransportedPassengers() == SimulPar.N) {
                reportFinalReport();
                endOp = true;
            }
        }
    }

    /**
     * Flying the plane to the destination airport.
     * <p>
     * Internal operation. Sleeps for an amount of time.
     */

    private void flyToDestinationPoint() {
        try {
            sleep((long) (1 + 160 * Math.random()));
        } catch (InterruptedException e) {
            GenericIO.writelnString("Interruption: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Flying the plane to the departure airport.
     * <p>
     * Internal operation. Sleeps for an amount of time.
     */

    private void flyToDeparturePoint() {
        try {
            sleep((long) (1 + 149 * Math.random()));
        } catch (InterruptedException e) {
            GenericIO.writelnString("Interruption: " + e.getMessage());
            System.exit(1);
        }
    }
}