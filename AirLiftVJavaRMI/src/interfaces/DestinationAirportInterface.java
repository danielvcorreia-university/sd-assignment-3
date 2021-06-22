package interfaces;

import java.rmi.*;

/**
 *   Operational interface of a remote object of type DestinationAirport.
 *
 *     It provides the functionality to access the Destination Airport.
 */

public interface DestinationAirportInterface extends Remote
{
    /**
     *  Operation leave the plane.
     *
     *  It is called by the passengers when they leave the plane.
     *
     *     @param passengerId true if it is the first function call, false otherwise.
     *     @param inF Count number of passengers checked by hostess.
     *     @return true if it is the last passenger and the state of the passenger
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public ReturnBoolean leaveThePlane (int passengerId, int inF) throws RemoteException;

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
