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
  * Create, modify user
  * Assign user to group
  * Create, modify groups

All tests can be performed as positive (action is allowed) and negative (action is denied) case.

## Simulation of Testcases

Apart from simply checking the set permission in the repository (contained in the ACLs of a node), the tool can also simulate tests.
Therefore the tool temporarily creates a testuser, makes it member of the group of interest and tries to execute the desired action using the testusers’ session and the respective API needed for the action to be done ( Sling API, AEM API, Jackrabbit API,…)

This not only makes testing more thorough but also allows to define additional parameters in the respective test definition like e.g. a template when testing if a page can be creates under a certain path of a property name which will be used when testing if modification of a page is possible.

The tool does not leave any test content in the repository such as the testuser, or modified properties, by either not persisting any changes or if this can not be avoided, deletes them when the test is done.

## Run Mode Support

A folder containing testfiles can have runmodes in its name separated by dots.  
Each folder can include one or more Yaml files ("*.yaml“). Folder names can contain runmodes in the same way as OSGi configurations (installation of OSGi bundles through JCR packages in Sling) using a . (e.g. myproject.author will only become active on author).
Additionally, multiple runmodes combinations can be given separated by comma to avoid duplication of configuration (e.g. myproject.author.test,author.dev will be active on authors of dev and test environment only).  

Examples:

- project.author: runs on "author" run mode only
- project.author.dev: runs only when run modes "author" and "dev" are present
- project.author.test,author.dev: requires run mode "author" and either "test" or "dev" to be present
[Test file how-to](docs/testfiles.md)

# Requirements

The AC Tool requires Java 7 and AEM 6.2 or later.

# Installation

For now you have to checkout the project and build it on a local instance. Once the first release is done, it will be available in the Maven Central repository.

See also the examples package:

```
    <groupId>biz.netcentric.cq.tools.accesscontrolvalidator</groupId>
    <artifactId>accesscontrolvalidator-examples-package</artifactId>
```

# Define Tests

## Yaml Files

For better human readability and easy editing the AC Validator [test definition files](docs/testfiles.md) are written in YAML format, and reside somewhere in the repository. 
Preferably under a dedicated folder. 



# Run Tests

The tools offers [several ways](docs/runtests.md) to execute testcases


## Service API

TODO

# License

The AC Tool is licensed under the [Eclipse Public License - v 1.0](LICENSE.txt).