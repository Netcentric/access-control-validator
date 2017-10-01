## GUI Tool

This is the most convenient method to run your tests. You can find the tool in AEM tools -> AC Validator -> Run Tests. (/apps/netcentric/acvalidator/tools/validator.html)


Tun run tests please select the base directory of your test files. You can also select to skip simulation of actions to do a quick run.

<img src="docs/images/GUI_step_1.png">

On the next page you can select which files should be executed. By default, all tests are run.

<img src="docs/images/GUI_step_2.png">

Now it is time to start test execution. You will see how many tests were executed and if there were any errors.
You can also show the details of the test executions.

<img src="docs/images/GUI_step_3.png">


## JMX Interface

The [JMX interface](docs/Jmx.md) provides utility functions such as installing and dumping ACLs or showing the history. 

## Health Checks

The AC Validator provides a factory configuration. This allows to define multiple health checks for different directories.

Service PID: biz.netcentric.aem.tools.acvalidator.healthcheck.ACValidatorHealthCheck

Properties:

Property name | Description
------------- | -----------
path | Please enter the base path where to search for test files. The folder will be search recursively.
hc.tags | Health check tags. This can be used to group checks.

Example:

File name: biz.netcentric.aem.tools.acvalidator.healthcheck.ACValidatorHealthCheck-myconfig.config

```
path="/apps/myapp/tests/authors"
hc.tags=["acvalidator"]
```
