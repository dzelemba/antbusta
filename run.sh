#!/bin/bash

# Compile
javac -cp ./src ./src/MainGame.java -d ./bin

# Run
java -cp ./bin MainGame