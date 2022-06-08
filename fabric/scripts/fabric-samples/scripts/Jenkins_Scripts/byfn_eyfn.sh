#!/bin/bash
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#

# docker container list
CONTAINER_LIST=(peer0.org1 peer1.org1 peer0.org2 peer1.org2 peer0.org3 peer1.org3 orderer)
COUCHDB_CONTAINER_LIST=(couchdb0 couchdb1 couchdb2 couchdb3 couchdb4 couchdb5)
MARCH=$(echo "$(uname -s|tr '[:upper:]' '[:lower:]'|sed 's/mingw64_nt.*/windows/')-$(uname -m | sed 's/x86_64/amd64/g')" | awk '{print tolower($0)}')
echo "MARCH: $MARCH"
echo "======== PULL fabric BINARIES ========"
echo
cd $BASE_FOLDER/fabric-samples || exit
# Download fabric binaries from nexus
curl https://nexus.hyperledger.org/content/repositories/releases/org/hyperledger/fabric/hyperledger-fabric/$MARCH-$VERSION/hyperledger-fabric-$MARCH-$VERSION.tar.gz | tar xz
if [ $? -ne 0 ]; then
   echo -e "\033[31m FAILED to download binaries" "\033[0m"
   exit 1
fi

cd $BASE_FOLDER/fabric-samples/first-network || exit
export PATH=$BASE_FOLDER/fabric-samples/bin:$PATH

logs() {

# Create Docker_Container_Logs directory
mkdir -p $WORKSPACE/Docker_Container_Logs

# Save logs in Logs directory
for CONTAINER in ${CONTAINER_LIST[*]}; do
    docker logs $CONTAINER.example.com >& $WORKSPACE/Docker_Container_Logs/$CONTAINER-$1.log
    echo
done
}

if [ ! -z $2 ]; then

    for CONTAINER in ${COUCHDB_CONTAINER_LIST[*]}; do
        docker logs $CONTAINER >& $WORKSPACE/Docker_Container_Logs/$CONTAINER-$1.log
        echo
    done
fi

copy_logs() {

# Call logs function
logs $2 $3

if [ $1 != 0 ]; then
    echo -e "\033[31m $2 test case is FAILED" "\033[0m"
    exit 1
fi
}

echo "############## BYFN,EYFN DEFAULT CHANNEL TEST ###################"
echo "#################################################################"
echo y | ./byfn.sh -m down
echo y | ./byfn.sh -m up -t 60
copy_logs $? default-channel
echo y | ./eyfn.sh -m up -t 60
copy_logs $? default-channel
echo y | ./eyfn.sh -m down
echo

echo "############### BYFN,EYFN CUSTOM CHANNEL WITH COUCHDB TEST ##############"
echo "#########################################################################"
echo y | ./byfn.sh -m up -c custom-channel-couchdb -s couchdb -t 75 -d 15
copy_logs $? custom-channel-couch couchdb
echo y | ./eyfn.sh -m up -c custom-channel-couchdb -s couchdb -t 75 -d 15
copy_logs $? custom-channel-couch
echo y | ./eyfn.sh -m down
echo

echo "############### BYFN,EYFN WITH NODE Chaincode. TEST ################"
echo "####################################################################"
echo y | ./byfn.sh -m up -l node -t 60
copy_logs $? default-channel-node
echo y | ./eyfn.sh -m up -l node -t 60
copy_logs $? default-channel-node
echo y | ./eyfn.sh -m down
echo

echo "############### BYFN WITH CA TEST ################"
echo "##################################################"
echo y | ./byfn.sh -m up -a 
copy_logs $? default-channel-ca 
echo y | ./byfn.sh -m down -a
echo 

echo "############### BYFN WITH NO CHAINCODE TEST ################"
echo "############################################################"
echo y | ./byfn.sh -m up -n
copy_logs $? default-channel-ca
echo y | ./byfn.sh -m down -n
echo 