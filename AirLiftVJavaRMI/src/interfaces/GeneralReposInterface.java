package interfaces;

import java.rmi.*;

/**
 *   Operational interface of a remote object of type GeneralRepos.
 *
 *     It provides the functionality to access the General Repository of Information.
 */

public interface GeneralReposInterface extends Remote
{
    /**
     *  Set passenger state.
     *
     *     @param passengerId passenger id
     *     @param state passenger state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void setPassengerState (int passengerId, int state) throws RemoteException;

    /**
     *  Set hostess state.
     *
     *     @param idHostess hostess id
     *     @param state hostess state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void setHostessState (int idHostess, int state) throws RemoteException;

    /**
     *  Set pilot state.
     *
     *     @param state hostess state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void setPilotState (int state) throws RemoteException;

    /**
     *  Initial status of the General Repository
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void reportInitialStatus () throws RemoteException;

    /**
     *  Report the status of the General Repository
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void reportStatus () throws RemoteException;

    /**
     *  Report the final report of the General Repository when the pilot ended all the flights
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */

    public void reportFinalInfo () throws RemoteException;

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
