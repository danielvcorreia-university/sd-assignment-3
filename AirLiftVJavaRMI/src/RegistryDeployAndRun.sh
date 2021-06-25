echo "Transfering data to the registry node."
sshpass -f password ssh sd107@l040101-ws06.ua.pt 'mkdir -p ~/AirLift'
sshpass -f password scp dirRegistry.zip sd107@l040101-ws06.ua.pt:~/AirLift
echo "Decompressing data sent to the registry node."
sshpass -f password ssh sd107@l040101-ws06.ua.pt 'cd ~/AirLift ; unzip -uq dirRegistry.zip'
echo "Executing program at the registry node."
sshpass -f password ssh sd107@l040101-ws06.ua.pt 'cd ~/AirLift/dirRegistry ; ./registry_com_d.sh sd107'
