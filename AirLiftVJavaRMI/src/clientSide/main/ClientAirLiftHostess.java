package clientSide.main;

import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.*;
import clientSide.entities.Hostess;
import interfaces.*;
import genclass.GenericIO;

/**
 *    Client side of the AirLift (hostess).
 *
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on Java RMI.
 */

public class ClientAirLiftHostess
{
    /**
     *  Main method.
     *
     *    @param args runtime arguments
     *        args[0] - name of the platform where is located the RMI registering service
     *        args[1] - port number where the registering service is listening to service requests
     */

    public static void main (String [] args)
    {
        String rmiRegHostName = "l040101-ws05.ua.pt";                  // name of the platform where is located the RMI registering service
        int rmiRegPortNumb = 22164;                                    // port number where the registering service is listening to service requests


        /* problem initialization */

        Hostess hostess;                                               // reference to the hostess thread
        String nameEntryDepartureAirport = "DepartureAirport";         // public name of the departure airport object
        DepartureAirportInterface depAirport = null;                   // remote reference to the departure airport object
        String nameEntryPlane = "Plane";                               // public name of the plane object
        PlaneInterface plane = null;                                   // remote reference to the plane object
        String nameEntryDestinationAirport = "DestinationAirport";     // public name of the destination airport object
        DestinationAirportInterface desAirport = null;                 // remote reference to the destination airport object
        Registry registry = null;                                      // remote reference for registration in the RMI registry service

        try
        { registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("RMI registry creation exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }

        try
        { depAirport = (DepartureAirportInterface) registry.lookup (nameEntryDepartureAirport);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("DepartureAirport lookup exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        catch (NotBoundException e)
        { GenericIO.writelnString ("DepartureAirport not bound exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }

        try
        { plane = (PlaneInterface) registry.lookup (nameEntryPlane);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Plane lookup exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        catch (NotBoundException e)
        { GenericIO.writelnString ("Plane not bound exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }

        try
        { desAirport = (DestinationAirportInterface) registry.lookup (nameEntryDestinationAirport);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("DestinationAirport lookup exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        catch (NotBoundException e)
        { GenericIO.writelnString ("DestinationAirport not bound exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }

        hostess = new Hostess("Hostess_" + (1), 0, depAirport, plane, desAirport);

        /* start of the simulation */

        hostess.start();

        /* waiting for the end of the simulation */

        GenericIO.writelnString ();
        try
        { hostess.join ();
        }
        catch (InterruptedException e) {}
        GenericIO.writelnString("The hostess " + (1) + " has terminated.");
        GenericIO.writelnString ();
        while (hostess.isAlive ())
        { try
            { depAirport.endOperation ();
            }
            catch (RemoteException e)
            { GenericIO.writelnString ("Hostess generator remote exception on DepartureAirport endOperation: " + e.getMessage ());
                System.exit (1);
            }
            Thread.yield ();
        }
        try
        { depAirport.shutdown ();
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess generator remote exception on DepartureAirport shutdown: " + e.getMessage ());
            System.exit (1);
        }
        try
        { plane.shutdown ();
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess generator remote exception on Plane shutdown: " + e.getMessage ());
            System.exit (1);
        }
        try
        { desAirport.shutdown ();
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Hostess generator remote exception on DestinationAirport shutdown: " + e.getMessage ());
            System.exit (1);
        }
    }
}
