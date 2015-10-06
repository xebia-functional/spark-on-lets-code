#!/bin/bash

export OPSCENTER_IP=$(getent hosts "$OPS_IP" | awk '{print $1 ; exit}')
export SEED_IP=$(getent hosts "$SEED" | awk '{print $1 ; exit}')
export CASS_SLAVE_IP=$(getent hosts "$CASS_SLAVE" | awk '{print $1 ; exit}')

echo "OPSCENTER_IP = $OPSCENTER_IP"
echo "SEED = $SEED_IP"
echo "CASS_SLAVE = $CASS_SLAVE_IP"

WAIT_COMMAND_COND=

is_ready() {
  eval [ $(curl --write-out %{http_code} --silent --output /dev/null http://$OPSCENTER_IP:8888/cluster-configs)  = 200 ]
}

# wait until is ready
i=0
while ! is_ready; do
    i=`expr $i + 1`
    if [ $i -ge $WAIT_LOOPS ]; then
        echo "$(date) - still not ready, giving up"
        exit 1
    fi
    echo "$(date) - waiting to be ready"
    sleep $WAIT_SLEEP
done

#start the script
echo "Registering cluster with OpsCenter"
curl \
 http://${OPSCENTER_IP}:8888/cluster-configs \
 -X POST \
 -d \
 "{
     \"cassandra\": {
       \"seed_hosts\": \"$SEED_IP, $CASS_SLAVE_IP\"
     },
     \"cassandra_metrics\": {},
     \"jmx\": {
       \"port\": \"7199\"
     }
 }" > /dev/null