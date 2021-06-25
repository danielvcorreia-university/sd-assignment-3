package interfaces;

import java.rmi.*;

/**
 *   Operational interface of a remote object of type DepartureAirport.
 *
 *     It provides the functionality to access the Departure Airport.
 */

public interface DepartureAirportInterface extends Remote
{
    /**
     *  Operation prepare for pass boarding.
     *
     *  It is called by the hostess while waiting for passengers to arrive at the airport.
     *
     *     @return state of the hostess
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public int prepareForPassBoarding () throws RemoteException;

    /**
     *  Operation wait in queue.
     *
     *  It is called by a passenger while waiting for his turn to show his documents to the hostess.
     *
     *     @param passengerId identification of the passenger
     *     @return state of the passenger and ReadyToShowDocuments
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public int waitInQueue (int passengerId) throws RemoteException;

    /**
     *  Operation check documents.
     *
     *  It is called by the hostess while waiting for the first costumer in queue to show his documents.
     *
     *     @return state of the hostess and passengerInQueue
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public ReturnBoolean checkDocuments () throws RemoteException;

    /**
     *  Operation show documents.
     *
     *  It is called by a passenger if the hostess has called him to check his documents.
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void showDocuments () throws RemoteException;

    /**
     *  Operation wait for next passenger.
     *
     *  It is called by the hostess while waiting for the next passenger in queue.
     *
     *     @param hostessCount Count number of passengers on the plane.
     *     @param checkedPassengers Count number of passengers checked by hostess.
     *     @return state of the hostess and passengerInQueue
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public ReturnBoolean waitForNextPassenger (int hostessCount, int checkedPassengers) throws RemoteException;

    /**
     *  Operation boarding the plane.
     *
     *  It is called by the passengers when they are allowed to enter the plane.
     *
     *     @param passengerId identification of the passenger
     *     @return state of the passenger
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public int boardThePlane (int passengerId) throws RemoteException;

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
