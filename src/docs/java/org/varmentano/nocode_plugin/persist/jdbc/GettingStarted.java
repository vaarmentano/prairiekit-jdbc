package org.varmentano.nocode_plugin.persist.jdbc;

import org.postgresql.ds.PGSimpleDataSource;
import org.varmentano.nocode_plugin.domain.UserDefinedObject;
import org.varmentano.nocode_plugin.domain.definition.FieldDefinition;
import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;
import org.varmentano.nocode_plugin.service.UdoService;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

public class GettingStarted {
    public static void main(String[] args) {
        // Initialize Service
        DataSource myDataSource = createMyDataSource();
        UdoService udoService = new UdoServiceJdbc(myDataSource);

        // At runtime, define a new type of object
        List<FieldDefinition> fieldDefinitions = Arrays.asList(
                new FieldDefinition("integer", "id", true),
                new FieldDefinition("integer", "age"),
                new FieldDefinition("string", "name"));
        ObjectDefinition myUdoDef = new ObjectDefinition("my_custom_object", fieldDefinitions);

        // Will create the table in the underlying database
        udoService.getDefinitionService().deployDefinition(myUdoDef);

        // Create and persist a new user-defined-object
        UserDefinedObject myUdo = new UserDefinedObject(myUdoDef);
        myUdo.putData("id", 1);
        myUdo.putData("age", 42);
        myUdo.putData("name", "George");
        udoService.saveNewUdo(myUdo);

        // Find the object by id (queries from database)
        UserDefinedObject queriedUdo = udoService.getUdoById(myUdoDef, 1);
        queriedUdo.getData("id"); // 1
        queriedUdo.getData("age"); // 42
        queriedUdo.getData("name"); // "George"

        // Add another object for this example
        UserDefinedObject anotherUdo = new UserDefinedObject(myUdoDef);
        anotherUdo.putData("id", 2);
        anotherUdo.putData("age", 23);
        anotherUdo.putData("name", "Roger");
        udoService.saveNewUdo(anotherUdo);

        // List all objects
        List<UserDefinedObject> udos = udoService.listUdos(myUdoDef);
        udos.size();// 2

        // Update data and persist
        anotherUdo.putData("age", 24);
        udoService.updateUdo(anotherUdo);

        // Or delete
        udoService.deleteUdo(myUdoDef, 2);
    }

    private static DataSource createMyDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerNames(new String[]{"localhost"});
        ds.setDatabaseName("nocode_plugin");
        ds.setUser("postgres");
        return ds;
    }


}