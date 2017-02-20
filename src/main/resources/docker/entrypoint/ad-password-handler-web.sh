#!/bin/bash

# include parent entrypoint script
source /tomcat.sh

# set config file variables
APPLICATION_CONFIG_FILE="$TOMCAT_WEBAPPS_FOLDER/ad-password-handler-web/WEB-INF/classes/application.properties"

# change ad server host in application properties
if [ -n $AD_SERVER_HOST ]
then
    sed -i 's|'ad.server.host=.*'|'ad.server.host=$AD_SERVER_HOST'|' $APPLICATION_CONFIG_FILE
fi

# execute command
exec "$@"
