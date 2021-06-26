xterm  -T "RMI registry" -hold -e "./RMIRegistryDeployAndRun.sh" &
sleep 10
xterm  -T "Registry" -hold -e "./RegistryDeployAndRun.sh" &
sleep 10
xterm  -T "General Repository" -hold -e "./GeneralReposDeployAndRun.sh" &
sleep 5
xterm  -T "Departure Airport" -hold -e "./DepartureAirportDeployAndRun.sh" &
sleep 5
xterm  -T "Plane" -hold -e "./PlaneDeployAndRun.sh" &
sleep 5
xterm  -T "Destination Airport Airport" -hold -e "./DestinationAirportDeployAndRun.sh" &
sleep 5
xterm  -T "Pilot" -hold -e "./PilotDeployAndRun.sh" &
sleep 3
xterm  -T "Hostess" -hold -e "./HostessDeployAndRun.sh" &
sleep 3
xterm  -T "Passengers" -hold -e "./PassengersDeployAndRun.sh" &
