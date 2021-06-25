echo "Transfering data to the plane node."
sshpass -f password ssh sd107@l040101-ws02.ua.pt 'mkdir -p ~/AirLift'
sshpass -f password ssh sd107@l040101-ws02.ua.pt 'rm -rf ~/AirLift/*'
sshpass -f password scp dirPlane.zip sd107@l040101-ws02.ua.pt:~/AirLift/
echo "Decompressing data sent to the plane node."
sshpass -f password ssh sd107@l040101-ws02.ua.pt 'cd ~/AirLift ; unzip -uq dirPlane.zip'
echo "Executing program at the plane node."
sshpass -f password ssh sd107@l040101-ws02.ua.pt 'cd ~/AirLift/dirPlane ; ./plane_com.sh sd107'
