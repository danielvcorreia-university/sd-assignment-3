echo "Transfering data to the general repos node."
sshpass -f password ssh sd107@l040101-ws09.ua.pt 'mkdir -p ~/AirLift'
sshpass -f password ssh sd107@l040101-ws09.ua.pt 'rm -rf ~/AirLift/*'
sshpass -f password scp dirGeneralRepos.zip sd107@l040101-ws09.ua.pt:~/AirLift/
echo "Decompressing data sent to the general repos node."
sshpass -f password ssh sd107@l040101-ws09.ua.pt 'cd ~/AirLift ; unzip -uq dirGeneralRepos.zip'
echo "Executing program at the general repos node."
sshpass -f password ssh sd107@l040101-ws09.ua.pt 'cd ~/AirLift/dirGeneralRepos ; ./repos_com.sh sd107'
