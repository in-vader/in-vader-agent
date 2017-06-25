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
 log:
    fileName: 'in-vader-agent.log' # the name of the log file, will default to 'in-vader-agent.log' if not present
    filePath: '/logs' # the location where log files will be stored, will default to '.' if not present
    fileCount: 5 # the number of log files to keep
    sizeLimit: 100 # the size limit for log files
    daily: true # roll logs daily
    level: INFO # logging level
 bindings:
    dir: '/path/to/dir/with/additional/bindings' # leave blank (or skip this section entirely) to disable
```

#### Additional binding format

Each binding should be placed in a separate `.yml` file using the following format:
```
name: HttpServlet
className: javax.servlet.http.HttpServlet # name of class to intercept
methods: # list of methods to intercept
  -
    name: service # name of method
    parameters: # list of method parameter types
      - javax.servlet.ServletRequest
      - javax.servlet.ServletResponse
```

You can also make the bindings match on an interface implementation, by replacing `className` with `interfaceName`.

In order to match subclasses of the provided class, add `includeSubclasses: true` after the `className` attribute.

Matching parameterless methods should be done by providing an empty list for the `parameters` attribute.
Skipping the `parameters` attribute will match on all methods with the specified name.

### Mock agent controller

In order to run the mock server you need to have [node](https://nodejs.org/) and [json-server](https://github.com/typicode/json-server) installed first.

To start the mock server execute the following line in the `src/test/resources/json-server` directory:

```
json-server --routes routes.json db.json
```
