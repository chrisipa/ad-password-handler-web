#!/bin/bash

# include parent entrypoint script
source /tomcat.sh

# execute command
exec "$@"
