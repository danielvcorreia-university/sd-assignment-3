echo "Transfering data to the pilot node."
sshpass -f password ssh sd107@l040101-ws10.ua.pt 'mkdir -p ~/AirLift'
sshpass -f password ssh sd107@l040101-ws10.ua.pt 'rm -rf ~/AirLift/*'
sshpass -f password scp dirPilot.zip sd107@l040101-ws10.ua.pt:~/AirLift
echo "Decompressing data sent to the pilot node."
sshpass -f password ssh sd107@l040101-ws10.ua.pt 'cd ~/AirLift ; unzip -uq dirPilot.zip'
echo "Executing program at the pilot node."
sshpass -f password ssh sd107@l040101-ws10.ua.pt 'cd ~/AirLift/dirPilot ; ./pilot_com.sh'
