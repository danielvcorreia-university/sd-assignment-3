package interfaces;

import java.rmi.*;

/**
 *   Operational interface of a remote object of type Plane.
 *
 *     It provides the functionality to access the Plane.
 */

public interface PlaneInterface extends Remote
{
    /**
     *  Operation to get number passengers in flight.
     *
     *  It is called by the passenger when he is leaving the plane at destination airport so he can check if he is the last to leave.
     *
     *     @return num pass in flight
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public ReturnInt getInF () throws RemoteException;

    /**
     *  Operation to report the final report.
     *
     *  It is called by the pilot after he parks the plane at the transfer gate and there are no more passengers to transport.
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void reportFinalReport () throws RemoteException;

    /**
     *  Operation park at transfer gate.
     *
     *  It is called by the pilot when he parks the plane at the transfer gate.
     *
     *     @return state of the pilot
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public int parkAtTransferGate () throws RemoteException;

    /**
     *  Operation inform plane ready for boarding.
     *
     *  It is called by the pilot to inform the hostess that the plane is ready for boarding.
     *
     *     @return state of the pilot
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public int informPlaneReadyForBoarding () throws RemoteException;

    /**
     *  Operation to get number passengers in flight.
     *
     *  It is called by the passenger when he is leaving the plane at destination airport so he can check if he is the last to leave.
     *
     *     @param first true if it is the first function call, false otherwise.
     *     @param checkedPassengers Count number of passengers checked by hostess.
     *     @return state of the hostess and checkedPassengers
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public ReturnInt waitForNextFlight (boolean first, int checkedPassengers) throws RemoteException;

    /**
     *  Operation wait for all passengers to board the plane.
     *
     *  It is called by the pilot after he announced the hostess
     *  that the plane is ready for boarding .
     *
     *     @return state of the pilot
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public int waitForAllInBoarding () throws RemoteException;

    /**
     *  Operation inform the pilot that the plane is ready to departure.
     *
     *  It is called by the hostess when she ended the check in of the passengers.
     *
     *     @return state of the hostess
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public int informPlaneReadyToTakeOff () throws RemoteException;

    /**
     *  Operation wait for end of flight.
     *
     *  It is called by the passengers when they are inside the plane and begin their waiting journey.
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void waitForEndOfFlight () throws RemoteException;

    /**
     *  Operation announce that the plane has arrived at the destination airport.
     *
     *  It is called by the pilot when the plane has arrived at the destination airport.
     *
     *     @param transportedPassengers Count number of passengers transported by pilot.
     *     @return state of the pilot and transportedPassengers
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public ReturnInt announceArrival (int transportedPassengers) throws RemoteException;

    /**
     *  Operation to notify the pilot.
     *
     *  It is called by the last passenger when he is leaving the plane to awake the pilot who is waiting.
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void notifyPilot () throws RemoteException;

    /**
     *  Operation end of work.
     *
     *   New operation.
     *
     *      @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                              service fails
     */

    public void endOperation () throws RemoteException;

    /**
     *   Operation server shutdown.
     *
     *   New operation.
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void shutdown () throws RemoteException;
}
