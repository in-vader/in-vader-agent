in-vader-agent
=====

[![Build Status](https://travis-ci.org/in-vader/in-vader-agent.svg?branch=master)](https://travis-ci.org/in-vader/in-vader-agent)

The in-vader agent is a java agent based application that allows to introduce random failures and delays
into the inputs that your application exposes. Currently, the following type of inputs are supported by default:
- HTTP (`HttpServlet` to be specific)
- JMS listener
- RabbitMQ listener

The random failure and delay functionality is introduced via instrumentation of methods in specific classes (e.g. `service` method in `HttpServlet`).

Apart from the built in support, additional inputs can be intercepted using configurable bindings (configuration details in [Sample agent config](#sample-agent-config) and [Additional binding format](#additional-binding-format) sections).

## Interceptors

### Failure

Randomly throws an exception when processing input, therefore breaking processing of the request (e.g. causes a 500 for HTTP communication).

Configuration parameters:
- probability - the probability (0-1) with which exceptions should be thrown

Sample configuration:
```json
"failure": {
  "probability": 0.5
}
```

Using the above config will cause the interceptor to fail approximately 50% requests.

### Delay

Sleeps the processing thread for a random number of milliseconds from the given range.
 
Configuration parameters:
- min - the lower bound of the delay range (inclusive)
- max - the upper bound of the delay range (exclusive)

Sample configuration:
```json
"delay": {
  "min": 100,
  "max": 200
}
```

Using the above config will cause the interceptor to delay the requests for a random period of time ranging from 100 ms to 199 ms.

### Peak

Enabled simulation of a peak in delay of a service.

Configuration parameters:
- startTime - time of day with time zone offset when the peak starts
- endTime - time of day with time zone offset when the peak ends
- delayMidpoints - array of integers that represent the delays to use during peak time (will be spread evenly)

Sample configuration:
```json
"peak": {
  "startTime": "15:00:00+01:00",
  "endTime":   "16:00:00+01:00",
  "delayMidpoints": [100,500,1000,700,700,300,300,300,100,100,100,100]
}
```

Using the above config will cause the interceptor to delay the requests forming a peak similar to the one below:
```
        response [ms]
         ^
  1000ms |   #
         |  ###
         |  #####
   500ms |  #####
         |  #####
         |  ########
   100ms | #############
         +-|-----|-----|---------> time [HH:MM]
         15:00 15:30 16:00
```

## Running the agent

```
java -Dinvader.config.file=agent-config.yml -javaagent:in-vader.jar -jar spring-example-0.0.1-SNAPSHOT.jar
```

### Sample agent config

```
 config:
    source: 'http://localhost:8080' # the location of the interceptor configuration source
    intervalSeconds: 10 # interceptor configuration refresh interval, will default to 10 if not present
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
#### Interceptor config source types

The source parameter can have two formats:
- `http(s)://` - to use a remote agent controller for managing interceptor configuration
- `file://` - to use a local file for managing interceptor configuration

##### Local file source

When using the local file source, one has to provide a JSON file in the following format:
```json
{
  "delay": {
    "min": 100,
    "max": 200
  },
  "failure": {
    "probability": 0.5
  },
  "peak": {
    "startTime": "10:15:30+01:00",
    "endTime": "11:15:30+01:00",
    "delayMidpoints": [100, 400, 400, 200, 50]
  }
}
```

##### Remote server source

When using a remote server configuration source, one has to provide a GET endpoint at `/api/groups/{group}/apps/{app}/agent-config`
(where `group` and `app` parameters are taken from agent config) that returns a JSON document in the same format as the local file source described above.

A sample implementation can be found in the [in-vader-controller](https://github.com/in-vader/in-vader-controller) repository.

###### Mock remote server source
      
In order to run the mock server you need to have [node](https://nodejs.org/) and [json-server](https://github.com/typicode/json-server) installed first.
      
To start the mock server execute the following line in the `src/test/resources/json-server` directory:
      
```
json-server --routes routes.json db.json
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