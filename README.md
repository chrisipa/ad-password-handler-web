AD Password Handler Web
=======

[![Build Status](https://papke.it/jenkins/buildStatus/icon?job=ad-password-handler)](https://papke.it/jenkins/job/ad-password-handler-web/)
[![Code Analysis](https://img.shields.io/badge/code%20analysis-available-blue.svg)](https://papke.it/sonar/overview?id=196)

Overview
--------
If you have external partners with active directory accounts (e.g. for VPN connections) and password expiration enabled, this tool is for you. It checks the expiration of active directory password and sends a mail to the users with a link to change it:

![Screenshot](https://raw.githubusercontent.com/chrisipa/ad-password-handler-web/master/public/screenshot_password_change.png)

Features
---------
* Mobile optimized web frontend to change active directory password
* Scheduler for checking password expiration of active directory users
* Sending HTML mails with a customizable velocity template

Prerequisites
-------------
* [Docker](https://docs.docker.com/engine/installation/) must be installed
* [Docker-Compose](https://docs.docker.com/compose/install/) must be installed

Usage
-----
1. Create docker compose file `docker-compose.yml` with your configuration data:
  ```yml
  ad-password-handler-web:
    image: chrisipa/ad-password-handler-web:latest
    volumes:
      - /etc/localtime:/etc/localtime:ro
    ports:
      - 8080:8080
      - 8443:8443
    environment:
      - TZ=Europe/Berlin
      - ad.server.host=my-mail-server-hostname-or-ip
      - ad.server.port=389
      - ad.server.base.dn=DC=my,DC=domain,DC=grp
      - ad.server.user.dn=CN=Name,OU=Group,OU=Users,OU=Organisation,DC=my,DC=domain,DC=grp
      - ad.server.user.secret=Password
      - application.url=http://my-app-url.com
      - mail.from=test@test.com
      - mail.host=my-mail-server-hostname-or-ip
      - mail.port=25
      - mail.send=true
      - password.expiration.cron.expression=0 0 0 * * ?
      - password.expiration.days.till.expires=14
      - password.expiration.user.filter=(objectClass=person)
  ```

2. Run docker containers with docker compose:
  ```
  docker-compose up -d
  ```