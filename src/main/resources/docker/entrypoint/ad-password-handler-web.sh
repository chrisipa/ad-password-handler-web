#!/bin/bash

# include parent entrypoint script
source /tomcat.sh

# set config file variables
APPLICATION_CONFIG_FILE="$TOMCAT_WEBAPPS_FOLDER/ROOT/WEB-INF/classes/application.properties"

# change ad server host in application properties
if [ -f "$APPLICATION_CONFIG_FILE" ]
then
    if [ -n "$AD_SERVER_HOST" ]
    then
        sed -i 's|'ad.server.host=.*'|'ad.server.host=$AD_SERVER_HOST'|' "$APPLICATION_CONFIG_FILE"
    fi

    if [ -n "$AD_SERVER_PORT" ]
    then
        sed -i 's|'ad.server.port=.*'|'ad.server.port=$AD_SERVER_PORT'|' "$APPLICATION_CONFIG_FILE"
    fi

    if [ -n "$AD_SERVER_BASE_DN" ]
    then
        sed -i 's|'ad.server.base.dn=.*'|'ad.server.base.dn=$AD_SERVER_BASE_DN'|' "$APPLICATION_CONFIG_FILE"
    fi

    if [ -n "$AD_SERVER_USER_DN" ]
    then
        sed -i 's|'ad.server.user.dn=.*'|'ad.server.user.dn=$AD_SERVER_USER_DN'|' "$APPLICATION_CONFIG_FILE"
    fi

    if [ -n "$AD_SERVER_USER_SECRET" ]
    then
        sed -i 's|'ad.server.user.secret=.*'|'ad.server.user.secret=$AD_SERVER_USER_SECRET'|' "$APPLICATION_CONFIG_FILE"
    fi
fi

# execute command
exec "$@"
