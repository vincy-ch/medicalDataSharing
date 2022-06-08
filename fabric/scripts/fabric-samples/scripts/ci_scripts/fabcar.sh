#!/bin/bash
#
# SPDX-License-Identifier: Apache-2.0
#

logs() {
    LOG_DIRECTORY=$WORKSPACE/fabcar/$1
    mkdir -p ${LOG_DIRECTORY}
    CONTAINER_LIST=$(docker ps -a --format '{{.Names}}')
    for CONTAINER in ${CONTAINER_LIST}; do
        docker logs ${CONTAINER} > ${LOG_DIRECTORY}/${CONTAINER}.log 2>&1
    done
}

copy_logs() {

# Call logs function
logs $2 $3

if [ $1 != 0 ]; then
    echo -e "\033[31m $2 test case is FAILED" "\033[0m"
    exit 1
fi
}

cd $WORKSPACE/$BASE_DIR/fabcar || exit
export PATH=gopath/src/github.com/hyperledger/fabric-samples/bin:$PATH

LANGUAGES="go java javascript typescript"
for LANGUAGE in ${LANGUAGES}; do
    echo -e "\033[1m ${LANGUAGE} Test\033[0m"
    echo -e "\033[32m starting fabcar test (${LANGUAGE})" "\033[0m"
    # Start Fabric, and deploy the smart contract
    ./startFabric.sh ${LANGUAGE}
    copy_logs $? fabcar-${LANGUAGE}
    # If an application exists for this language, test it
    if [ -d ${LANGUAGE} ]; then
        pushd ${LANGUAGE}
        if [ ${LANGUAGE} = "javascript" -o ${LANGUAGE} = "typescript" ]; then
            if [ ${LANGUAGE} = "javascript" ]; then
                COMMAND=node
                PREFIX=
                SUFFIX=.js
                npm install
            elif [ ${LANGUAGE} = "typescript" ]; then
                COMMAND=node
                PREFIX=dist/
                SUFFIX=.js
                npm install
                npm run build
            fi
            ${COMMAND} ${PREFIX}enrollAdmin${SUFFIX}
            copy_logs $? fabcar-${LANGUAGE}-enrollAdmin
            ${COMMAND} ${PREFIX}registerUser${SUFFIX}
            copy_logs $? fabcar-${LANGUAGE}-registerUser
            ${COMMAND} ${PREFIX}query${SUFFIX}
            copy_logs $? fabcar-${LANGUAGE}-query
            ${COMMAND} ${PREFIX}invoke${SUFFIX}
            copy_logs $? fabcar-${LANGUAGE}-invoke
        elif [ ${LANGUAGE} = "java" ]; then
            mvn test
            copy_logs $? fabcar-${LANGUAGE}
        else
            echo -e "\033[31m do not know how to handle ${LANGUAGE}" "\033[0m"
            exit 1
        fi
        popd
    fi
    docker ps -aq | xargs docker rm -f
    docker rmi -f $(docker images -aq dev-*)
    docker volume prune -f
    docker network prune -f
    echo -e "\033[32m finished fabcar test (${LANGUAGE})" "\033[0m"
done
