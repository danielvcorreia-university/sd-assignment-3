echo "Transfering data to the departure airport node."
sshpass -f password ssh sd107@l040101-ws04.ua.pt 'mkdir -p ~/AirLift'
sshpass -f password ssh sd107@l040101-ws04.ua.pt 'rm -rf ~/AirLift/*'
sshpass -f password scp dirDepartureAirport.zip sd107@l040101-ws04.ua.pt:~/AirLift/
echo "Decompressing data sent to the departure airport node."
sshpass -f password ssh sd107@l040101-ws04.ua.pt 'cd ~/AirLift ; unzip -uq dirDepartureAirport.zip'
echo "Executing program at the departure airport node."
sshpass -f password ssh sd107@l040101-ws04.ua.pt 'cd ~/AirLift/dirDepartureAirport ; ./depair_com.sh sd107'
