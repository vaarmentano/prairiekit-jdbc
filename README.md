# No-Code Plugin

The No-Code plugin is a developer toolkit to add no-code (or low-code) features to your app. 

Specifically, we define and implement the interfaces you need to manage runtime-defined objects.  These are the objects your code manages but whose structure (schema) is controlled by the user.

## JDBC
This module offers a service which, once given a data connection, will manage the persistence of both the user-defined object _definitions_ and the user-defined objects themselves.  

## Demo

[Getting Started](src/docs/java/org.varmentano.nocode_plugin.jdbc/GettingStarted.java)

## Build

`./gradlew build`
