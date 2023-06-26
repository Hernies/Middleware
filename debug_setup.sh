#!/bin/bash

# Check if OpenMQ server is running
if pgrep -x "imqbrokerd" >/dev/null; then
  echo "OpenMQ server is already running."
  mvn compile
else
  echo "OpenMQ server is not running. Launching the server..."
  screen -dmS imq sh -c "imqbrokerd -tty; exec"
fi
