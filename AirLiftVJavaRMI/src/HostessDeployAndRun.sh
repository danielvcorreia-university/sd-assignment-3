echo "Transfering data to the hostess node."
sshpass -f password ssh sd107@l040101-ws06.ua.pt 'mkdir -p ~/AirLift'
sshpass -f password ssh sd107@l040101-ws06.ua.pt 'rm -rf ~/AirLift/*'
sshpass -f password scp dirHostess.zip sd107@l040101-ws06.ua.pt:~/AirLift
echo "Decompressing data sent to the hostess node."
sshpass -f password ssh sd107@l040101-ws06.ua.pt 'cd ~/AirLift ; unzip -uq dirHostess.zip'
echo "Executing program at the hostess node."
sshpass -f password ssh sd107@l040101-ws06.ua.pt 'cd ~/AirLift/dirHostess ; java -cp .:genclass.jar clientSide.main.ClientAirLiftHostess'
