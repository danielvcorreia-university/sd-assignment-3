pass="sd-GO7-AD!"

for i in {2..3} ; do
    sshpass -p $pass ssh sd107@l040101-ws0$i.ua.pt 'killall rmiregistry;killall java'
done

for i in {5..9} ; do
	sshpass -p $pass ssh sd107@l040101-ws0$i.ua.pt 'killall rmiregistry ; killall java'
done

sshpass -p $pass ssh sd107@l040101-ws10.ua.pt 'killall rmiregistry ; killall java'
