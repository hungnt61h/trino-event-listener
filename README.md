# TRINO AUDIT PLUGIN (trino-360)

## 1. Introduction
This project implements trino's interfaces ```io.trino.spi.Plugin```, ```io.trino.spi.eventlistener.EventListener``` in order to capture query events such as:
 - Query creation event.
 - Query completion event (success of failure).
 - Split completion event (success of failure).

## 2. Project structure (How to write plugin)
To create plugin, you must create a class which ```implements io.trino.spi.Plugin```.
This interface contains many methods to be overridden, and we choose ```Iterable<EventListenerFactory> getEventListenerFactories()``` to override.
The function is entry of plugin, allows loading ```EventListenerFactory``` objects to register plugins.
Each plugin has its own factory to define its name (by overriding method ```getName()```) and create ```EventListener``` instance listening the events above (by overriding ```EventListener create(Map<String, String> config)```).
The ```Map<String, String> config``` param is loaded from plugin configuration file. This file would look like:
```properties
# event-listener.name is required
# The value is name of plugin defined in class implementing EventListenerFactory interface
event-listener.name=custom-event-listener

# Other properties with your own definition
# These properties will be loaded to Map<String, String> config
custom-property1=custom-value1
custom-property2=custom-value2
```
Trino supports multiple instances of the same or different event listeners.
Install and configure multiple instances by setting event-listener.config-files in [Config properties](https://trino.io/docs/current/installation/deployment.html#config-properties) to a comma-separated list of the event listener configuration files:
```properties
event-listener.config-files=etc/event-listener.properties,etc/event-listener-second.properties
```
Next, you create a custom listener class implementing ```EventListener```.
This class could override each/all of three methods:
```java
public interface EventListener {
    default void queryCreated(QueryCreatedEvent queryCreatedEvent) { }

    default void queryCompleted(QueryCompletedEvent queryCompletedEvent) { }

    default void splitCompleted(SplitCompletedEvent splitCompletedEvent) { }
}
```
After that, you must return new instance of the class in ```EventListener create(Map<String, String> config)``` method in factory class.
Because Trino loads plugins by reading all jar files in ```plugin``` folder. To help Trino know how to load plugins, the plugin class must be provided by:
 - Create folder ```META-INF/services``` in ```resources``` folder.
 - Put a file with file's name is name of the implemented plugin interface (```io.trino.spi.Plugin``` in this case).
 - The created file has only one line which is name of class implementing plugin interface (```vn.edu.ptit.AuditEventListenerPlugin``` in this case).
Finally, you build project with maven then copy output files in ```target``` and ```target/lib``` to same directory in ```<trino>/plugin/```.
   
## 3. How to build
Must use JDK 11 and Maven 3.6.3 or above for building this project. 

## 4. Plugin Configurations
### 4.1. Configurations for plugin
The plugin configuration file has following properties:

| Property | Description | Default value | Optional |
| :--- | :--- | :--- | :---: |
| ```event-listener.name``` | Name of the plugin. | ```audit-event-listener``` | false |
| ```datetime-format``` | Format of datetime field in event. | ```yyyy-MM-dd HH:mm:ss``` | true |
| ```inline-query``` | If value is ```true```, print query in one line in log file. | ```false``` | true |
| ```log4j-path``` | Log4j2 configuration file path. | | false |
| ```creation.enable``` | Enable to capture query creation event. This value should be ```true``` in coordinators. | ```false``` | false |
| ```creation.log-format``` | Format of log line as [MessageFormat](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/MessageFormat.html) pattern, except that plugin only support position of parameters not their interpretation | <code>{0}&#124;{1}&#124;{2}&#124;{3}</code> | true |
| ```creation.log-fields``` | Fields to get from event, order of them is corresponding log-format. Each of fields is separated by semi-colon `;` | ```meta.queryId;meta.query;ctx.user;ctx.remoteClientAddress``` | true |
| ```completion.enable``` | Enable to capture query completion event. This value should be ```true``` in coordinators. | ```false``` | false |
| ```completion.log-format``` | Format of log line as [MessageFormat](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/MessageFormat.html) pattern, except that plugin only support position of parameters not their interpretation | <code>{0}&#124;{1}&#124;{2}&#124;{3}</code> | true |
| ```completion.log-fields``` | Fields to get from event, order of them is corresponding log-format. Each of fields is separated by semi-colon `;` | ```meta.queryId;meta.query;ctx.user;ctx.remoteClientAddress``` | true |
| ```split.enable``` | Enable to capture split completion event. This value should be ```true``` in workers. | ```false``` | false |
| ```split.log-format``` | Format of log line as [MessageFormat](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/MessageFormat.html) pattern, except that plugin only support position of parameters not their interpretation | <code>{0}&#124;{1}&#124;{2}&#124;{3}</code> | true |
| ```split.log-fields``` | Fields to get from event, order of them is corresponding log-format. Each of fields is separated by semi-colon `;` | ```spl.queryId;spl.payload;spl.startTime;spl.failureMessage``` | true |
Note: ```log-format``` will be prepended with prefix depending on kind of event
 - Query creation event: ```[CREATION] - ```
 - Query completion event: ```[COMPLETION] - ```
 - Split completion event: ```[SPLIT] - ```

Sample configuration file for ```audit-event-listener.properties``` is below:
```properties
event-listener.name=audit-event-listener

datetime-format=yyyy-MM-dd HH:mm:ss
inline-query=false
log4j-path=/etc/trino/audit-event-log.properties

creation.enable=true
creation.log-format={0}|{1}|{2}|{3}|{4}|{5}
creation.log-fields=meta.queryId;meta.query;meta.tables;ctx.user;ctx.remoteClientAddress;crt.createdTime

completion.enable=true
completion.log-format={0}|{1}|{2}|{3}|{4}|{5}|{6}
completion.log-fields=meta.queryId;meta.query;meta.tables;ctx.user;ctx.remoteClientAddress;cpl.createTime;cpl.endTime
```
### 4.2. Configurations for Log4j2
How to configure log4j2 is [here](https://logging.apache.org/log4j/2.x/manual/configuration.html).
Sample configuration file is below:
```properties
status = error
name = PropertiesConfig

#Make sure to change log file path as per your need
property.filename = /var/log/trino/audit/audit.log

filters = threshold

filter.threshold.type = ThresholdFilter
filter.threshold.level = debug

appenders = rolling

appender.rolling.type = RollingFile
appender.rolling.name = RollingFile
appender.rolling.fileName = ${filename}
appender.rolling.filePattern = /var/log/trino/audit/audit-%d{yyyy-MM-dd}-%i.log.gz
appender.rolling.layout.type = PatternLayout
appender.rolling.layout.pattern = %d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
appender.rolling.policies.type = Policies
appender.rolling.policies.time.type = TimeBasedTriggeringPolicy
appender.rolling.policies.time.interval = 1
appender.rolling.policies.time.modulate = true
appender.rolling.policies.size.type = SizeBasedTriggeringPolicy
appender.rolling.policies.size.size=1024MB
appender.rolling.strategy.type = DefaultRolloverStrategy
appender.rolling.strategy.max = 20

loggers = rolling

#Make sure to change the package structure as per your application

logger.rolling.name = vn.edu.ptit
logger.rolling.level = debug
logger.rolling.additivity = false
logger.rolling.appenderRef.rolling.ref = RollingFile

rootLogger.level = info
rootLogger.appenderRef.stdout.ref = stdout
```
### 4.3. Event fields
1. Query metadata fields (used in query creation and query completion events)

| Field | Description |
| :--- | :--- |
| ```meta.queryId``` | Query ID of submitted query by user |
| ```meta.transactionId``` | Transaction ID of query |
| ```meta.query``` | Submitted query |
| ```meta.updateType``` | There are x update types: PREPARE, DEALLOCATE,... |
| ```meta.preparedQuery``` | Prepared query |
| ```meta.queryState``` | Current state of query |
| ```meta.tables``` | Tables are accessed in query. Each of them is displayed in form ```<catalog>.<schema>.<table>``` and separated by colon. |
| ```meta.routines``` | |
| ```meta.uri``` | Query metadata link |
| ```meta.plan``` | Plan of query generated by Trino engine |
| ```meta.payload``` | |

2. Query context fields (used in query creation and query completion events)

| Field | Description |
| :--- | :--- |
| ```ctx.user``` | Username of user submitting query |
| ```ctx.principal``` | Principle of user |
| ```ctx.groups``` | Groups of user, separated by colon |
| ```ctx.traceToken``` | |
| ```ctx.remoteClientAddress``` | IP address of user |
| ```ctx.userAgent``` | Kind of agent that user use to query |
| ```ctx.clientInfo``` | Client's information |
| ```ctx.clientCapabilities``` |  |
| ```ctx.clientTags``` | |
| ```ctx.source``` | Query's source from client |
| ```ctx.catalog``` | Catalog in query |
| ```ctx.schema``` | Schema in query |
| ```ctx.resourceGroupId``` | Resource group |
| ```ctx.sessionProperties``` | Session properties in <key, value> form |
| ```ctx.resourceEstimates``` | Estimated resource for query: execution time, CPU time, peak memory in byte |
| ```ctx.serverAddress``` | FQDN or IP address of server |
| ```ctx.serverVersion``` | Version of Trino server |
| ```ctx.environment``` | Enviroment of server |
| ```ctx.queryType``` | Type of query: DATA_DEFINITION, DELETE, DESCRIBE, EXPLAIN, ANALYZE, INSERT, SELECT |

3. Query creation event fields

| Field | Description |
| :--- | :--- |
| ```crt.createdTime``` | Query created time |

4. Query completion event fields

| Field | Description |
| :--- | :--- |
| ```cpl.errorCode``` | Error code of query when failure. Some types of error: USER_ERROR, INTERNAL_ERROR, INSUFFICIENT_RESOURCES, EXTERNAL |
| ```cpl.failureType``` | Class of exception causing failure |
| ```cpl.failureMessage``` | Detail failure message |
| ```cpl.failureTask``` | Which task failed |
| ```cpl.failureHost``` | Which host failed |
| ```cpl.failuresJson``` | Full log of failure in JSON form: type, message, exception stack trace, error location, error code |
| ```cpl.queryWarnings``` | List of warnings when query is being executed |
| ```cpl.createTime``` | When query is submitted |
| ```cpl.executionStartTime``` | When query execution starts |
| ```cpl.endTime``` | When query execution completes |
| ```cpl.statistics``` | Query statistics during execution: CPU time, wall time, queued time, scheduled time, waiting time, analysis time, planning time, execution time, peak user memory in byte, peak total non-revocable memory in byte, peak task user memory, peak task total memory, amount of physical input bytes, amount of physical input rows, amount of internal network bytes, amount of internal network rows, total bytes, total rows, output bytes, output rows, written bytes, written rows, cumulative memory, stage GC statistics, completed splits, completion status, cpu time distribution, operator summaries, plan node stats and costs. *(These fields need diving deeper to understand)* |
| ```cpl.ioMetadata``` | Containing input and output metadata. Input: input catalog name, input schema, input table, selected input columns, amount of physical input bytes/rows. Some queries may produce output to tables and their output metadata preoperties: output catalog name, output schema, output table, connector output metadata, JSON length limit exceeded. *(These fields need diving deeper to understand)* |

5. Split completion event fields

| Field | Description |
| :--- | :--- |
| ```spl.failureType``` | *(This field needs diving deeper to understand)* |
| ```spl.failureMessage``` | *(This field needs diving deeper to understand)* |
| ```spl.payload``` | *(This field needs diving deeper to understand)* |
| ```spl.queryId``` | *(This field needs diving deeper to understand)* |
| ```spl.stageId``` | *(This field needs diving deeper to understand)* |
| ```spl.taskId``` | *(This field needs diving deeper to understand)* |
| ```spl.catalogName``` | *(This field needs diving deeper to understand)* |
| ```spl.createTime``` | *(This field needs diving deeper to understand)* |
| ```spl.startTime``` | *(This field needs diving deeper to understand)* |
| ```spl.endTime``` | *(This field needs diving deeper to understand)* |
| ```spl.statistics``` | *(This field needs diving deeper to understand)* |

## 5. Future work
 - Push log to kafka
 - Drill down some fields
 - Fig bugs (when needed)

## 6. Author
Nguyễn Thuần Hưng - hungnt61h  
*If you found that my project is useful, don't hesitate to 'star' me! Thank you so much!*

## 7. Reference