package org.prairiekit.persist.jdbc;

import org.postgresql.ds.PGSimpleDataSource;
import org.prairiekit.UdoServiceProvider;
import org.prairiekit.domain.UserDefinedObject;
import org.prairiekit.domain.definition.FieldDefinition;
import org.prairiekit.domain.definition.FieldType;
import org.prairiekit.domain.definition.UdoDefinition;
import org.prairiekit.service.UdoRepository;
import org.prairiekit.service.UdoService;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "UnusedAssignment", "ResultOfMethodCallIgnored"})
public class GettingStarted {
    public static void main(String[] args) {
        // Initialize Service
        DataSource myDataSource = createMyDataSource();
        UdoService udoService = UdoServiceProvider.getJdbcService(myDataSource);

        // At runtime, define a new type of object
        List<FieldDefinition> fieldDefinitions = Arrays.asList(
                new FieldDefinition(FieldType.INTEGER, "age"),
                new FieldDefinition(FieldType.TEXT, "name"),
                new FieldDefinition(FieldType.DATE, "birthday"));
        UdoDefinition myUdoDef = new UdoDefinition("my_custom_object", fieldDefinitions);

        // Will create the table in the underlying database
        udoService.deployDefinition(myUdoDef);
        UdoRepository myObjectRepo = udoService.getUdoRepository("my_custom_object");

        // Create and persist a new user-defined-object
        UserDefinedObject myUdo = new UserDefinedObject(myUdoDef);
        myUdo.putData("age", 42);
        myUdo.putData("name", "George");
        myUdo.putData("birthday", LocalDate.of(2022, Month.JANUARY, 1));
        myUdo = myObjectRepo.saveNew(myUdo);

        // Find the object by id (queries from database)
        UserDefinedObject queriedUdo = myObjectRepo.findById(myUdo.getId()).get();
        queriedUdo.getId(); // Exists, generated by database
        queriedUdo.getData("age"); // 42
        queriedUdo.getData("name"); // "George"
        queriedUdo.getData("birthday");

        // Add another object for this example
        UserDefinedObject anotherUdo = new UserDefinedObject(myUdoDef);
        anotherUdo.putData("age", 23);
        anotherUdo.putData("name", "Roger");
        anotherUdo = myObjectRepo.saveNew(anotherUdo);

        // List all objects
        Iterable<UserDefinedObject> itr = myObjectRepo.findAll();
        itr.forEach(System.out::println); // Two UDOs

        // Update data and persist
        anotherUdo.putData("age", 24);
        anotherUdo = myObjectRepo.saveUpdate(anotherUdo);

        // Or delete
        myObjectRepo.deleteById(2);
    }

    private static DataSource createMyDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerNames(new String[]{"localhost"});
        ds.setDatabaseName("nocode_plugin_demo");
        ds.setUser("postgres");
        return ds;
    }


}