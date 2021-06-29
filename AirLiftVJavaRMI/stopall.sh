pass="sd-GO7-AD!"

for i in {2..7} ; do
    sshpass -p $pass ssh sd107@l040101-ws0$i.ua.pt 'killall rmiregistry;killall java'
done

sshpass -p $pass ssh sd107@l040101-ws09.ua.pt 'killall rmiregistry ; killall java'
sshpass -p $pass ssh sd107@l040101-ws10.ua.pt 'killall rmiregistry ; killall java'
