echo "Transfering data to the RMIregistry node."
sshpass -f password ssh sd107@l040101-ws05.ua.pt 'mkdir -p ~/AirLift'
sshpass -f password ssh sd107@l040101-ws05.ua.pt 'rm -rf ~/AirLift/*'
sshpass -f password ssh sd107@l040101-ws05.ua.pt 'mkdir -p ~/Public/classes/interfaces'
sshpass -f password ssh sd107@l040101-ws05.ua.pt 'rm -rf ~/Public/classes/interfaces/*'
sshpass -f password scp dirRMIRegistry.zip sd107@l040101-ws05.ua.pt:~/AirLift/
echo "Decompressing data sent to the RMIregistry node."
sshpass -f password ssh sd107@l040101-ws05.ua.pt 'cd ~/AirLift ; unzip -uq dirRMIRegistry.zip'
sshpass -f password ssh sd107@l040101-ws05.ua.pt 'cd ~/AirLift/dirRMIRegistry ; cp interfaces/*.class ~/Public/classes/interfaces ; cp set_rmiregistry.sh ~'
echo "Executing program at the RMIregistry node."
sshpass -f password ssh sd107@l040101-ws05.ua.pt './set_rmiregistry.sh sd107 22164'
