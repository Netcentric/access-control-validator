Access Control Validator for Adobe Experience Manager
=====================================================

The Access Control Validator provides an easy to use tool for testers and developers to validate AEM ACLs.

Features:
* Easy-to-read Yaml configuration file format
* Page and user management tests
* Run mode support
* Health checks
* Service API
* JMX interface

Supported test cases:
* Page
  * Read, create, modify, delete and publish pages
  * Roll-out pages
* User management
  * Create, modify and delete user
  * Assign user to group
  * Create, modify and delete groups

All tests can be performed as positive (action is allowed) and negative (action is denied) case.

# Requirements

The AC Tool requires Java 7 and AEM 6.2 or later.

# Installation

The package is available via [Maven](???). Install it e.g. via CRX package manager.

```
    <groupId>biz.netcentric.cq.tools.accesscontrolvalidator</groupId>
    <artifactId>accesscontrolvalidator-package</artifactId>
```

See also the examples package:

```
    <groupId>biz.netcentric.cq.tools.accesscontrolvalidator</groupId>
    <artifactId>accesscontrolvalidator-examples-package</artifactId>
```

# Define Tests

## Yaml Files

For better human readability and easy editing the AC Validator test files use the YAML format, and reside somewhere in the repository. 
Prefereably under a dedicated folder. 

## Run Mode Support

A Folder containg testfiles can have runmodes in its name separated by dots.  
Each folder can include one or more Yaml files ("*.yaml“). Folder names can contain runmodes in the same way as OSGi configurations (installation of OSGi bundles through JCR packages in Sling) using a . (e.g. myproject.author will only become active on author).
Additionally, multiple runmodes combinations can be given separated by comma to avoid duplication of configuration (e.g. myproject.author.test,author.dev will be active on authors of dev and test environment only).  

Examples:

- project.author: runs on "author" run mode only
- project.author.dev: runs only when run modes "author" and "dev" are present
- project.author.test,author.dev: requires run mode "author" and either "test" or "dev" to be present
[Test file how-to](docs/yaml.md)



## Simulation of Testcases

Apart from simply checking the set permission in the repository (contained in the ACLs of a node), the tool can also simulate tests.
Therefore the tool temporarily creates a testuser, makes it member of the group of interest and tries to execute the desired action using the testusers’ session and the respective API needed for the action to be done ( Sling API, AEM API, Jackrabbit API,…)

This not only makes testing more thorough but also allows to define additional parameters in the respective test definition like e.g. a template when testing if a page can be creates under a certain path of a property name which will be used when testing if modification of a page is possible.

The tool does not leave any test content in the repository such as the testuser, or modified properties, by either not persisting any changes or if this can not be avoided, deletes them when the test is done.

## Overall structure a of an AC configuration file

Every testfile comprises one or more variables section(s) (optional) and a „tests“ section. Inside, there can be testcases for one or more groups inside a group block den d by the respective groupID. Inside each group block, there can be page related testcases inside a „pages“ block and user/group related testcases inside a „useradmin“ block.

So here’s the overall structure:
```
- variables (block 1):
  - name 1: value
  - name n: value
...
- variables (block n):

- tests:

  - group1:
    - pages:
       (testcases)
    - useradmin:
       (testcases)

    - group n:
     ...
```

###Common testcase Properties:###

<b>path</b>: denoting a path in the repository

<b>actions</b>: action that should be tested (one of: read, modify, create, delete, readACL, writeACL, publish for page testcases, or
assignUserToGroup, createUser, createGroup, deleteUser, deleteGroup, modifyUser, modifyGroup for user/group related testcases)

<b>permission</b>: either 'allow' if the specified action is expected to be possible, otherwise 'deny'. In that case the respective
action will only be tested by checking the set permission(s) for that path in the repository.

###Page Testcases:###

<b>simulate</b>: if set to 'true' the test will be simulated by attempting the action using a testuser. Obmitting this property is equals to 'false'

Example:
```
-   path: /content
    actions: read
    permission: allow
    simulate: 'true'
```

##Actions##

<b>read:</b>

Example:
```
-   path: /content
    actions: read
    permission: allow
```
<b>modify:</b>

Example:
```
-   path: /content
    actions: modify
    permission: allow
```
additional parameters:

*propertyNamesModify*: comma separated list of property names for which modifiction will be tested by the tool. Mandatory
when simulate is set to 'true'.

<b>create:</b>

additional parameters:

*template*: template of a testpage the tools will try to create under the given path in simulation mode. Mandatory
when simulate is set to 'true'.

Example:
```
-   path: /content
    actions: create
    permission: allow
    template: /apps/myapp/templates/page
```   
<b>delete:</b>

Example:
```
-   path: /content
    actions: delete
    permission: deny
```
<b>read acl:</b> 

The user can read the access control list of the page or child pages.

Example:
```
-   path: /content
    actions: readACL
    permission: allow
```
<b>write acl:</b> 
The user can modify the access control list of the page or any child pages.

Example:
```
-   path: /content
    actions: writeACL
    permission: allow
```
<b>publish:</b> 

The user can replicate content to another environment (for example, the Publish environment). The privilege is also applied to any child pages.

Example:
```
-   path: /content/site/page
    actions: publish
    permission: allow
    simulate: 'true'
    isDeactivate: 'false'
```
additional parameters:

*isDeactivate*: publication action activate or deactivate will be tried by the tool in simulation mode. Can be one of true or false. Mandatory
when simulate is set to 'true'.

# Run Tests

## GUI Tool

This is the most convenient method to run your tests. You can find the tool in AEM tools -> AC Validator -> Run Tests.

TODO image

Tun run tests please select the base directory of your test files. You can also select to skip simulation of actions to do a quick run.

TODO image

On the next page you can select which files should be executed. By default, all tests are run.

TODO image

Now it is time to start test execution. You will see how many tests were executed and if there were any errors.
You can also show the details of the test executions.

TODO image


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

## Service API

TODO

# License

The AC Tool is licensed under the [Eclipse Public License - v 1.0](LICENSE.txt).