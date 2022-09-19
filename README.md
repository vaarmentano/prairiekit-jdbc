# Prairiekit JDBC

_prairiekit-jdbc_ is a java library for adding headless CMS (Content Management System) or No-code features to your existing application. Specifically, this module offers a service which, once connected to your database, will manage the persistence of both user-defined-object definitions and the user-defined-objects themselves.

User-defined-objects (or "UDOs" for short) are the objects your code manages but whose structure (schema) is controlled by the user.

## Demo

Refer to the [GettingStarted](src/docs/java/org/prairiekit/persist/jdbc/GettingStarted.java) guide for a quick walkthrough on using this library to define a new UDO definition, save it to the database, then perform persistent CRUD operations with this dynamically defined object.

## Build

`./gradlew build`
