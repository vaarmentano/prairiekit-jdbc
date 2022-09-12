public class GettingStarted {
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

        // Find the object by id (queries from database)
        UserDefinedObject queriedUdo = udoService.getUdoById(myUdoDef, 1);
        udo.getData("id"); // 1
        udo.getData("age"); // 42
        udo.getData("name"); // "George"

        // Add another object for this example
        UserDefinedObject anotherUdo = new UserDefinedObject(myUdoDef);
        myUdo.putData("id", 2);
        myUdo.putData("age", 23);
        myUdo.putData("name", "Roger");
        udoService.saveNewUdo(myUdo);

        // List all objects
        List<UserDefinedObject> udos = udoService.listUdos(myUdoDef);
        udos.size(); // 2

        // Update data and persist
        myUdo.putData("age", 24);
        udoService.updateUdo(anotherUdo);

        // Or delete
        udoService.deleteUdo(myUdoDef, 2);
    }
}