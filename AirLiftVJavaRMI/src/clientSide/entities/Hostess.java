package clientSide.entities;

import interfaces.*;
import java.rmi.*;
import serverSide.main.SimulPar;
import genclass.GenericIO;

/**
 *   Hostess thread.
 *
 *   It simulates the hostess life cycle.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on remote calls under Java RMI.
 */

public class Hostess extends Thread {

    /**
     * Hostess identification.
     */

    private int hostessId;

    /**
     * Hostess state.
     */

    private int hostessState;

    /**
     * Count number of passengers on the plane.
     */

    private int hostessCount;

    /**
     * Count number of passengers checked by hostess.
     */

    private int checkedPassengers;

    /**
     * True if there is any passenger in queue for the hostess to process.
     */

    private boolean passengerInQueue;

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
     * Instantiation of a hostess thread.
     *
     * @param name        thread name
     * @param hostessId   hostess id
     * @param depAirport  remote reference to the departure airport
     * @param plane       remote reference to the plane
     * @param destAirport remote reference to the destination airport
     */

    public Hostess(String name, int hostessId, DepartureAirportInterface depAirport, PlaneInterface plane, DestinationAirportInterface destAirport) {
        super(name);
        this.hostessCount = 0;
        this.checkedPassengers = 0;
        this.hostessId = hostessId;
        hostessState = HostessStates.WAIT_FOR_FLIGHT;
        this.depAirport = depAirport;
        this.plane = plane;
        this.destAirport = destAirport;
    }

    /**
     * Set hostess count.
     *
     * @param count hostess count
     */

    public void setHostessCount(int count) {
        hostessCount = count;
    }

    /**
     * Get hostess count.
     *
     * @return hostess count
     */

    public int getHostessCount() {
        return hostessCount;
    }

    /**
     * Set number of passengers which hostess checked documents.
     *
     * @param nCheckedPassengers number of passengers checked
     */

    public void setCheckedPassengers(int nCheckedPassengers) { checkedPassengers = nCheckedPassengers; }

    /**
     * Get number of passengers which hostess checked documents.
     *
     * @return checked passengers
     */

    public int getCheckedPassengers() {
        return checkedPassengers;
    }

    /**
     * Set if there is any passenger in queue for the hostess to process
     *
     * @param bool passenger in queue
     */

    public void setPassengerInQueue(boolean bool) {
        passengerInQueue = bool;
    }

    /**
     * Check if there is any passenger in queue for the hostess to process
     *
     * @return True if passenger in queue
     */

    public boolean getPassengerInQueue() {
        return passengerInQueue;
    }

    /**
     * Life cycle of the hostess.
     */

    @Override
    public void run() {
        boolean endOp = false;                                       // flag signaling end of operations
        waitForNextFlight(true);
        while (!endOp) {
            prepareForPassBoarding();

            while (getHostessCount() < SimulPar.MIN) {
                checkDocuments();
                waitForNextPassenger();
                if (getHostessCount() + getCheckedPassengers() == SimulPar.N) {
                    endOp = true; break;
                }
            }
            while (getPassengerInQueue() && getHostessCount() < SimulPar.MAX) {
                checkDocuments();
                waitForNextPassenger();
                if (getHostessCount() + getCheckedPassengers() == SimulPar.N) {
                    endOp = true; break;
                }
            }
            informPlaneReadyToTakeOff();

            waitForNextFlight(false);
        }
    }

    /**
     *  Hostess informs pilot that the plane is ready to take off.
     *
     *  Remote operation.
     */

    private void informPlaneReadyToTakeOff()
    {
        try
        { hostessState = plane.informPlaneReadyToTakeOff();
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess " + hostessId + " remote exception on informPlaneReadyToTakeOff: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *  Hostess waits for next passenger.
     *
     *  Remote operation.
     */

    private void waitForNextPassenger()
    {
        ReturnBoolean ret = null;

        try
        { ret = depAirport.waitForNextPassenger (getHostessCount(), getCheckedPassengers());
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess " + hostessId + " remote exception on waitForNextPassenger: " + e.getMessage ());
            System.exit (1);
        }
        hostessState = ret.getIntStateVal ();
        setHostessCount(getHostessCount()+1);
        setPassengerInQueue(ret.getBooleanVal ());
    }

    /**
     *  Hostess checks passenger documents.
     *
     *  Remote operation.
     */

    private void checkDocuments()
    {
        ReturnBoolean ret = null;

        try
        { ret = depAirport.checkDocuments();
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess " + hostessId + " remote exception on checkDocuments: " + e.getMessage ());
            System.exit (1);
        }
        hostessState = ret.getIntStateVal ();
        setPassengerInQueue(ret.getBooleanVal ());
    }

    /**
     *  Hostess prepares for pass boarding.
     *
     *  Remote operation.
     */

    private void prepareForPassBoarding()
    {
        try
        { hostessState = depAirport.prepareForPassBoarding();
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess " + hostessId + " remote exception on prepareForPassBoarding: " + e.getMessage ());
            System.exit (1);
        }
        setHostessCount(0);
    }

    /**
     *  Hostess waits for the next flight.
     *
     *  Remote operation.
     *
     *     @param first true if it is the first function call, false otherwise.
     */

    private void waitForNextFlight (boolean first)
    {
        ReturnInt ret = null;

        try
        { ret = plane.waitForNextFlight (first, getCheckedPassengers());
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess " + hostessId + " remote exception on receivePayment: " + e.getMessage ());
            System.exit (1);
        }
        hostessState = ret.getIntStateVal ();
        setCheckedPassengers(ret.getIntVal());
    }
}