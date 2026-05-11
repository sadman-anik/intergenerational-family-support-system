#!/bin/zsh

echo "Starting IGFSS Server..."

cd server

javac -cp ".:../mysql-connector-j-9.7.0.jar" *.java

java -cp ".:../mysql-connector-j-9.7.0.jar" IGFSSServer