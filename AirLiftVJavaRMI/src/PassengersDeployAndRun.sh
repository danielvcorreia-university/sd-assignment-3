echo "Transfering data to the passengers node."
sshpass -f password ssh sd107@l040101-ws03.ua.pt 'mkdir -p ~/AirLift'
sshpass -f password ssh sd107@l040101-ws03.ua.pt 'rm -rf ~/AirLift/*'
sshpass -f password scp dirPassengers.zip sd107@l040101-ws03.ua.pt:~/AirLift
echo "Decompressing data sent to the passengers node."
sshpass -f password ssh sd107@l040101-ws03.ua.pt 'cd ~/AirLift ; unzip -uq dirPassengers.zip'
echo "Executing program at the passengers node."
sshpass -f password ssh sd107@l040101-ws03.ua.pt 'cd ~/AirLift/dirPassengers ; ./passengers_com.sh'
