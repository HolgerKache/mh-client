# mh-client
Project to debug problem with Message Hub client authentication

## Build
mvn -f mh-client/ clean install

## Deploy
cp mh-client/target/mh-client-0.0.1-SNAPSHOT.war wlp/usr/servers/mh-test/apps/mh-client.war

## Run
wlp/bin/server start mh-test

## Test
curl -X POST http://localhost:9080/mh-client/rest/ClientApplication/_run?username=1VWshDNeZAiZ6RWa&password=rqiUk6bdRBkfYgShvUy9P0DUxKcRzSRw

