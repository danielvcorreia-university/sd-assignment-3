package serverSide.objects;

import clientSide.entities.PassengerStates;
import commInfra.*;
import serverSide.main.*;
import genclass.GenericIO;
import interfaces.*;
import java.rmi.*;

/**
 *    Destination Airport.
 *
 *    It is responsible to keep a continuously updated account of the number of passengers that are already
 *    in the destination airport.
 *    There are two methods. One to get the total number os passengers in destination airport
 *    and one to increment by one the number of passenger in destination airport.
 */

public class DestinationAirport implements DestinationAirportInterface{
    /**
     * For each flight, the number of passengers that left the plane.
     */

    private int PTAL;

    /**
     *   Number of entity groups requesting the shutdown.
     */

    private int nEntities;

    /**
     * Reference to the general repository.
     */

    private final GeneralReposInterface repos;

    /**
     * Destination airport instantiation.
     *
     * @param repos reference to the general repository
     */

    public DestinationAirport(GeneralReposInterface repos) {
        PTAL = 0;
        nEntities = 0;
        this.repos = repos;
    }

    /**
     * Operation leave the plane
     * <p>
     * It is called by the passengers when they leave the plane.
     *
     *     @param passengerId identification of the passenger
     *     @param inF Number of passengers that flew in this flight.
     *     @return true if this is the last passenger to leave the plane,
     *     false otherwise
     *     and Passenger state
     */

    public synchronized ReturnBoolean leaveThePlane(int passengerId, int inF) throws RemoteException{
        boolean lastPassenger = false;
        PTAL += 1;

        try{
            repos.setPassengerState(passengerId, PassengerStates.AT_DESTINATION);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Passenger remote exception on leaveThePlane - : setPassengerState" + e.getMessage ());
            System.exit (1);
        }

        if (PTAL == inF) { PTAL = 0; lastPassenger = true; }

        return new ReturnBoolean(lastPassenger, PassengerStates.AT_DESTINATION);
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
    public synchronized void endOperation () throws RemoteException
    {
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
    public synchronized void shutdown () throws RemoteException
    {
        nEntities += 1;
        if (nEntities >= SimulPar.E)
            ServerAirLiftDestinationAirport.shutdown ();
        notifyAll ();
    }
}
