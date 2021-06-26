echo "Transfering data to the destination airport node."
sshpass -f password ssh sd107@l040101-ws03.ua.pt 'mkdir -p ~/AirLift'
sshpass -f password ssh sd107@l040101-ws03.ua.pt 'rm -rf ~/AirLift/*'
sshpass -f password scp dirDestinationAirport.zip sd107@l040101-ws03.ua.pt:~/AirLift/
echo "Decompressing data sent to the destination airport node."
sshpass -f password ssh sd107@l040101-ws03.ua.pt 'cd ~/AirLift ; unzip -uq dirDestinationAirport.zip'
echo "Executing program at the destination airport node."
sshpass -f password ssh sd107@l040101-ws03.ua.pt 'cd ~/AirLift/dirDestinationAirport ; ./desair_com.sh sd107'
