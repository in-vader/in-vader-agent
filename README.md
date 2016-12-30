in-vader-agent
=====

[![Build Status](https://travis-ci.org/in-vader/in-vader-agent.svg?branch=master)](https://travis-ci.org/in-vader/in-vader-agent)

**run**

java -DagentConfigFile=agent-config.yml -javaagent:in-vader.jar -jar spring-example-0.0.1-SNAPSHOT.jar

**config example**

`server: 'http://localhost:8080'
 group: 'testGroup'
 name: 'testAppName'
`