xterm  -T "General Repository" -hold -e "./GeneralReposDeployAndRun.sh" &
sleep 2
xterm  -T "Departure Airport" -hold -e "./DepartureAirportDeployAndRun.sh" &
sleep 2
xterm  -T "Plane" -hold -e "./PlaneDeployAndRun.sh" &
sleep 2
xterm  -T "Destination Airport Airport" -hold -e "./DestinationAirportDeployAndRun.sh" &
sleep 1
xterm  -T "Pilot" -hold -e "./PilotDeployAndRun.sh" &
xterm  -T "Hostess" -hold -e "./HostessDeployAndRun.sh" &
xterm  -T "Passengers" -hold -e "./PassengersDeployAndRun.sh" &
