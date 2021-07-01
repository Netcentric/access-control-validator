## Overall structure a of an AC test definition file

Every testfile comprises one or more variables section(s) (optional) and a „tests“ section. Inside, there can be testcases for one or more groups/systemuser. A group/systemuser for wich testcases should be executed gets defined by the respective ID. Inside each block, there can be page related testcases inside a „pages“ block and user/group related testcases inside a „useradmin“ block.

So here’s the overall structure:
```
- variables (block 1):
  - name 1: value
  - name n: value
...
- variables (block n):

- tests:

  - group/systemuser 1:
    - pages:
       (testcases)
    - useradmin:
       (testcases)

    - group/systemuser n:
     ...
```

###Common testcase Properties:
 
<b>actions</b>: action that should be tested (one of: read, modify, create, delete, readACL, writeACL, publish for page testcases, or
assignUserToGroup, createUser, createGroup, deleteUser, deleteGroup, modifyUser, modifyGroup for user/group related testcases).

<b>permission</b>: either 'allow' if the specified action is expected to be possible, otherwise 'deny'.

###Page Testcases:

<b>path</b>: denoting a path in the repository for which a test should get executed.

<b>simulate</b>: if set to 'true' the test will be simulated by attempting the action using a testuser. Obmitting this property is equals to 'false'. In that case the respective action will only be tested by checking the set permission(s) for that path in the repository.

Example:
```
-   path: /content
    actions: read
    permission: allow
    simulate: 'true'
```

##Actions

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

*propertyNamesModify*: comma separated list of property names for which modification will be tested by the tool. Mandatory
when simulate is set to 'true'.

<b>create:</b>

additional parameters:

*template*: template of a testpage the tool will try to create under the given path in simulation mode. Mandatory
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

The user can replicate content to another environment (for example, the publish environment). The privilege is also applied to any child page.

Example:
```
-   path: /content/site/page
    actions: publish
    permission: allow
    simulate: 'true'
    isDeactivate: 'false'
```
additional parameters:

*isDeactivate*: publication action activate or deactivate will be tested by the tool in simulation mode. Can be one of *true* or *false*. Mandatory when simulate is set to 'true'.

###User/Group Testcases:

Since for these tescases there is no cq:action equivalent which could be tested against by simply checking set permissions in the repository, simulation of the action always takes place. Therefore no *simulate* parameter needs to be added.

<b>create group:</b> 

Example:
```
- action: createGroup
  path: /home/groups/example
  permission: example-group
  
```

<b>modify group:</b>

Example:
```
- action: modifyGroup
  groupId: example-group
  permission: allow
```

<b>create user:</b>

Example:
```
- action: createUser
  path: /home/users/example
  permission: allow
```

<b>modify user:</b>

Example:
```
 - action: modifyUser
   userId: exampleuser
   permission: allow
```

<b>assign user to group:</b>

Example:
```
 - action: assignUserToGroup
   userId: examplegroup
   permission: allow
```
