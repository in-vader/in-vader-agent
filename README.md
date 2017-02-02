in-vader-agent
=====

[![Build Status](https://travis-ci.org/in-vader/in-vader-agent.svg?branch=master)](https://travis-ci.org/in-vader/in-vader-agent)

## Running the agent

```
java -Dinvader.config.file=agent-config.yml -javaagent:in-vader.jar -jar spring-example-0.0.1-SNAPSHOT.jar
```

### Sample agent config

```
 server: 'http://localhost:8080' # the url of the agent controller
 group: 'testGroup' # the name of the application group
 name: 'testAppName' # the name of the application
```

### Mock agent controller

In order to run the mock server you need to have [node](https://nodejs.org/) and [json-server](https://github.com/typicode/json-server) installed first.

To start the mock server execute the following line in the `src/test/resources/json-server` directory:

```
json-server --routes.json db.json
```
