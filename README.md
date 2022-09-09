# No-Code Plugin

The No-Code plugin is a developer toolkit to add no-code (or low-code) features to your app. 

Specifically, we define and implement the interfaces you need to manage runtime-defined objects.  These are the objects your code manages but whose structure (schema) is controlled by the user.

## JDBC
This module offers a service which, once given a data connection, will manage the persistence of both the user-defined object _definitions_ and the user-defined objects themselves.  

## Demo

```java
public static void main(String[] args) {
    // Initialize Service
    DataSource myDataSource = createMyDataSource();
    UdoService udoService = new UdoService(myDataSource);

    // At runtime, define a new type of object
    List<FieldDefinition> fieldDefinitions = Arrays.asList(
    new FieldDefinition("integer", "id", true),
    new FieldDefinition("integer", "age"),
    new FieldDefinition("string", "name"));
    ObjectDefinition myUdoDef = new ObjectDefinition("my_custom_object", fieldDefinitions);
    
    // Will create the table in the underlying database
    udoService.deployDefinition(myUdoDef);

    // Create and persist a new user-defined-object
    UserDefinedObject myUdo = new UserDefinedObject(myUdoDef);
    myUdo.putData("id", 1);
    myUdo.putData("age", 42);
    myUdo.putData("name", "George");
    udoService.saveNewUdo(myUdo);
}
```

## Build
`./gradlew build`
