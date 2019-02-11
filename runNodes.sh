#! /bin/bash

echo "Killing all running nodes..."
fuser -k -n tcp 10003
fuser -k -n tcp 10013
fuser -k -n tcp 10023
fuser -k -n tcp 10043

echo "Running all Corda nodes..."

echo "Starting Lincoln..."
cd ./build/nodes/Lincoln
nohup java -jar corda.jar &

echo "Starting Roosevelt..."
cd ../Roosevelt/
nohup java -jar corda.jar &

echo "Starting Madison..."
cd ../Madison/
nohup java -jar corda.jar &

echo "Starting Washington..."
cd ../Washington/
nohup java -jar corda.jar &

echo "All corda nodes are up"
