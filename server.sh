#!/bin/sh
java -Djava.awt.headless=true -cp "lib/jsci-xtra.jar:lib/jsci-core.jar:lib/core.jar:jardist/nano.jar:lib/Acme.jar:lib/mysqldriver.jar" nano.server.Server $1
