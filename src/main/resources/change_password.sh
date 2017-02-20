#!/bin/bash

# read parameters
username="$1"
domainControllerIp="$2"
oldPassword="$3"
newPassword="$4"

# execute smbpasswd to change domain password
(echo $oldPassword; echo $newPassword; echo $newPassword) | smbpasswd -s -U "$username" -r "$domainControllerIp"
