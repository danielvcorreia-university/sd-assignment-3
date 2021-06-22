package serverSide.main;

import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.*;
import serverSide.objects.*;
import interfaces.*;
import genclass.GenericIO;

/**
 *    Instantiation and registering of the Plane object.
 *
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on Java RMI.
 */

public class ServerAirLiftPlane
{
    /**
     *  Flag signaling the end of operations.
     */

    private static boolean end = false;

    /**
     *  Main method.
     *
     *        args[0] - port number for listening to service requests
     *        args[1] - name of the platform where is located the RMI registering service
     *        args[2] - port number where the registering service is listening to service requests
     */

    public static void main (String[] args)
    {
        int portNumb = 22161;                                             // port number for listening to service requests
        String rmiRegHostName = "l040101-ws05.ua.pt";                     // name of the platform where is located the RMI registering service
        int rmiRegPortNumb = 22164;                                       // port number where the registering service is listening to service requests

        /* create and install the security manager */

        if (System.getSecurityManager () == null)
            System.setSecurityManager (new SecurityManager ());
        GenericIO.writelnString ("Security manager was installed!");

        /* get a remote reference to the general repository object */

        String nameEntryGeneralRepos = "GeneralRepository";            // public name of the general repository object
        GeneralReposInterface reposStub = null;                        // remote reference to the general repository object
        Registry registry = null;                                      // remote reference for registration in the RMI registry service

        try
        { registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("RMI registry creation exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        GenericIO.writelnString ("RMI registry was created!");

        try
        { reposStub = (GeneralReposInterface) registry.lookup (nameEntryGeneralRepos);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("GeneralRepos lookup exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        catch (NotBoundException e)
        { GenericIO.writelnString ("GeneralRepos not bound exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }

        /* instantiate a plane object */

        Plane plane = new Plane (reposStub);
        PlaneInterface planeStub = null;

        try
        { planeStub = (PlaneInterface) UnicastRemoteObject.exportObject (plane, portNumb);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Plane stub generation exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        GenericIO.writelnString ("Stub was generated!");

        /* register it with the general registry service */

        String nameEntryBase = "RegisterHandler";                      // public name of the object that enables the registration
        // of other remote objects
        String nameEntryObject = "Plane";                              // public name of the plane object
        Register reg = null;                                           // remote reference to the object that enables the registration
        // of other remote objects

        try
        { reg = (Register) registry.lookup (nameEntryBase);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("RegisterRemoteObject lookup exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        catch (NotBoundException e)
        { GenericIO.writelnString ("RegisterRemoteObject not bound exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }

        try
        { reg.bind (nameEntryObject, planeStub);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Plane registration exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        catch (AlreadyBoundException e)
        { GenericIO.writelnString ("Plane already bound exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        GenericIO.writelnString ("Plane object was registered!");

        /* wait for the end of operations */

        GenericIO.writelnString ("Plane is in operation!");
        try
        { while (!end)
            synchronized (Class.forName ("serverSide.main.ServerAirLiftPlane"))
            { try
            { (Class.forName ("serverSide.main.ServerAirLiftPlane")).wait ();
            }
            catch (InterruptedException e)
            { GenericIO.writelnString ("Plane main thread was interrupted!");
            }
            }
        }
        catch (ClassNotFoundException e)
        { GenericIO.writelnString ("The data type ServerAirLiftPlane was not found (blocking)!");
            e.printStackTrace ();
            System.exit (1);
        }

        /* server shutdown */

        boolean shutdownDone = false;                                  // flag signalling the shutdown of the plane service

        try
        { reg.unbind (nameEntryObject);
        }
        catch (RemoteException e)
        { GenericIO.writelnString ("Plane deregistration exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        catch (NotBoundException e)
        { GenericIO.writelnString ("Plane not bound exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }
        GenericIO.writelnString ("Plane was deregistered!");

        try
        { shutdownDone = UnicastRemoteObject.unexportObject (plane, true);
        }
        catch (NoSuchObjectException e)
        { GenericIO.writelnString ("Plane unexport exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit (1);
        }

        if (shutdownDone)
            GenericIO.writelnString ("Plane was shutdown!");
    }

    /**
     *  Close of operations.
     */

    public static void shutdown ()
    {
        end = true;
        try
        { synchronized (Class.forName ("serverSide.main.ServerAirLiftPlane"))
        { (Class.forName ("serverSide.main.ServerAirLiftPlane")).notify ();
        }
        }
        catch (ClassNotFoundException e)
        { GenericIO.writelnString ("The data type ServerAirLiftPlane was not found (waking up)!");
            e.printStackTrace ();
            System.exit (1);
        }
    }
}
