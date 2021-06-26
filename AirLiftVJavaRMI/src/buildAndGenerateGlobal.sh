echo "Compiling source code."
javac -cp genclass.jar */*.java */*/*.java
echo "Distributing intermediate code to the different execution environments."
echo "  RMI registry"
rm -rf dirRMIRegistry/interfaces
mkdir -p dirRMIRegistry/interfaces
cp interfaces/*.class dirRMIRegistry/interfaces
echo "  Register Remote Objects"
rm -rf dirRegistry/serverSide dirRegistry/interfaces
mkdir -p dirRegistry/serverSide dirRegistry/serverSide/main dirRegistry/serverSide/objects dirRegistry/interfaces
cp serverSide/main/ServerRegisterRemoteObject.class dirRegistry/serverSide/main
cp serverSide/objects/RegisterRemoteObject.class dirRegistry/serverSide/objects
cp interfaces/Register.class dirRegistry/interfaces
cp genclass.jar dirRegistry
echo "  General Repository of Information"
rm -rf dirGeneralRepos/serverSide dirGeneralRepos/clientSide dirGeneralRepos/interfaces
mkdir -p dirGeneralRepos/serverSide dirGeneralRepos/serverSide/main dirGeneralRepos/serverSide/objects dirGeneralRepos/interfaces \
         dirGeneralRepos/clientSide dirGeneralRepos/clientSide/entities
cp serverSide/main/SimulPar.class serverSide/main/ServerAirLiftGeneralRepos.class dirGeneralRepos/serverSide/main
cp serverSide/objects/GeneralRepos.class dirGeneralRepos/serverSide/objects
cp interfaces/Register.class interfaces/GeneralReposInterface.class dirGeneralRepos/interfaces
cp clientSide/entities/HostessStates.class clientSide/entities/PassengerStates.class clientSide/entities/PilotStates.class dirGeneralRepos/clientSide/entities
cp genclass.jar dirGeneralRepos
echo "  Departure Airport"
rm -rf dirDepartureAirport/serverSide dirDepartureAirport/clientSide dirDepartureAirport/interfaces dirDepartureAirport/commInfra
mkdir -p dirDepartureAirport/serverSide dirDepartureAirport/serverSide/main dirDepartureAirport/serverSide/objects dirDepartureAirport/interfaces \
         dirDepartureAirport/clientSide dirDepartureAirport/clientSide/entities dirDepartureAirport/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerAirLiftDepartureAirport.class dirDepartureAirport/serverSide/main
cp serverSide/objects/DepartureAirport.class dirDepartureAirport/serverSide/objects
cp interfaces/*.class dirDepartureAirport/interfaces
cp clientSide/entities/HostessStates.class clientSide/entities/PassengerStates.class clientSide/entities/PilotStates.class dirDepartureAirport/clientSide/entities
cp commInfra/*.class dirDepartureAirport/commInfra
cp genclass.jar dirDepartureAirport
echo "  Plane"
rm -rf dirPlane/serverSide dirPlane/clientSide dirPlane/interfaces dirPlane/commInfra
mkdir -p dirPlane/serverSide dirPlane/serverSide/main dirPlane/serverSide/objects dirPlane/interfaces \
         dirPlane/clientSide dirPlane/clientSide/entities dirPlane/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerAirLiftPlane.class dirPlane/serverSide/main
cp serverSide/objects/Plane.class dirPlane/serverSide/objects
cp interfaces/*.class dirPlane/interfaces
cp clientSide/entities/HostessStates.class clientSide/entities/PassengerStates.class clientSide/entities/PilotStates.class dirPlane/clientSide/entities
cp commInfra/*.class dirPlane/commInfra
cp genclass.jar dirPlane
echo "  Destination Airport"
rm -rf dirDestinationAirport/serverSide dirDestinationAirport/clientSide dirDestinationAirport/interfaces dirDestinationAirport/commInfra
mkdir -p dirDestinationAirport/serverSide dirDestinationAirport/serverSide/main dirDestinationAirport/serverSide/objects dirDestinationAirport/interfaces \
         dirDestinationAirport/clientSide dirDestinationAirport/clientSide/entities dirDestinationAirport/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerAirLiftDestinationAirport.class dirDestinationAirport/serverSide/main
cp serverSide/objects/DestinationAirport.class dirDestinationAirport/serverSide/objects
cp interfaces/*.class dirDestinationAirport/interfaces
cp clientSide/entities/HostessStates.class clientSide/entities/PassengerStates.class clientSide/entities/PilotStates.class dirDestinationAirport/clientSide/entities
cp commInfra/*.class dirDestinationAirport/commInfra
cp genclass.jar dirDestinationAirport
echo "  Pilot"
rm -rf dirPilot/serverSide dirPilot/clientSide dirPilot/interfaces
mkdir -p dirPilot/serverSide dirPilot/serverSide/main dirPilot/clientSide dirPilot/clientSide/main dirPilot/clientSide/entities \
         dirPilot/interfaces
cp serverSide/main/SimulPar.class dirPilot/serverSide/main
cp clientSide/main/ClientAirLiftPilot.class clientSide/main/SimulPar.class dirPilot/clientSide/main
cp clientSide/entities/Pilot.class clientSide/entities/PilotStates.class dirPilot/clientSide/entities
cp interfaces/*.class dirPilot/interfaces
cp genclass.jar dirPilot
echo "  Hostess"
rm -rf dirHostess/serverSide dirHostess/clientSide dirHostess/interfaces
mkdir -p dirHostess/serverSide dirHostess/serverSide/main dirHostess/clientSide dirHostess/clientSide/main dirHostess/clientSide/entities \
         dirHostess/interfaces
cp serverSide/main/SimulPar.class dirHostess/serverSide/main
cp clientSide/main/ClientAirLiftHostess.class dirHostess/clientSide/main
cp clientSide/entities/Hostess.class clientSide/entities/HostessStates.class dirHostess/clientSide/entities
cp interfaces/*.class dirHostess/interfaces
cp genclass.jar dirHostess
echo "  Passengers"
rm -rf dirPassengers/serverSide dirPassengers/clientSide dirPassengers/interfaces
mkdir -p dirPassengers/serverSide dirPassengers/serverSide/main dirPassengers/clientSide dirPassengers/clientSide/main dirPassengers/clientSide/entities \
         dirPassengers/interfaces
cp serverSide/main/SimulPar.class dirPassengers/serverSide/main
cp clientSide/main/ClientAirLiftPassenger.class dirPassengers/clientSide/main
cp clientSide/entities/Passenger.class clientSide/entities/PassengerStates.class dirPassengers/clientSide/entities
cp interfaces/*.class dirPassengers/interfaces
cp genclass.jar dirPassengers
echo "Compressing execution environments."
echo "  RMI registry"
rm -f  dirRMIRegistry.zip
zip -rq dirRMIRegistry.zip dirRMIRegistry
echo "  Register Remote Objects"
rm -f  dirRegistry.zip
zip -rq dirRegistry.zip dirRegistry
echo "  General Repository of Information"
rm -f  dirGeneralRepos.zip
zip -rq dirGeneralRepos.zip dirGeneralRepos
echo "  Departure Airport"
rm -f  dirDepartureAirport.zip
zip -rq dirDepartureAirport.zip dirDepartureAirport
echo "  Plane"
rm -f  dirPlane.zip
zip -rq dirPlane.zip dirPlane
echo "  Destination Airport"
rm -f  dirDestinationAirport.zip
zip -rq dirDestinationAirport.zip dirDestinationAirport
echo "  Pilot"
rm -f  dirPilot.zip
zip -rq dirPilot.zip dirPilot
echo "  Hostess"
rm -f  dirHostess.zip
zip -rq dirHostess.zip dirHostess
echo "  Passenger"
rm -f  dirPassengers.zip
zip -rq dirPassengers.zip dirPassengers
