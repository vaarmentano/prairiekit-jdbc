package org.varmentano.nocode_plugin.jdbc;

import org.junit.jupiter.api.*;
import org.postgresql.ds.PGSimpleDataSource;
import org.varmentano.nocode_plugin.jdbc.domain.UserDefinedObject;
import org.varmentano.nocode_plugin.jdbc.domain.definition.FieldDefinition;
import org.varmentano.nocode_plugin.jdbc.domain.definition.ObjectDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UdoServiceTests {
    private static DataSource dataSource;
    private static UdoService udoService;

    @BeforeAll
    static void setup() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerNames(new String[]{"localhost"});
        ds.setDatabaseName("nocode_plugin");
        ds.setUser("postgres");
        dataSource = ds;
        clearTables();

        udoService = new UdoService(dataSource);
    }

    @Test
    @Order(1)
    void definitionDeployment() {
        //Given
        List<FieldDefinition> fieldDefinitions = Arrays.asList(
                new FieldDefinition("integer", "id", true),
                new FieldDefinition("integer", "age"),
                new FieldDefinition("string", "name"));
        ObjectDefinition myUdoDef = new ObjectDefinition("my_custom_object", fieldDefinitions);

        //When
        udoService.deployDefinition(myUdoDef);

        //Then
        String query = """
                SELECT *\s
                FROM information_schema.tables LEFT JOIN information_schema.columns\s
                ON information_schema.tables.table_name = information_schema.columns.table_name
                WHERE tables.table_schema = 'public'
                ORDER BY ordinal_position""";
        try {
            Connection connection = dataSource.getConnection();
            ResultSet rs = connection.prepareStatement(query).executeQuery();
            rs.next();
            assertEquals("my_custom_object", rs.getString("table_name"));
            assertEquals("id", rs.getString("column_name"));
            rs.next();
            assertEquals("age", rs.getString("column_name"));
            rs.next();
            assertEquals("name", rs.getString("column_name"));
            assertFalse(rs.next());
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    void saveNew() {
        //Given
        List<FieldDefinition> fieldDefinitions = Arrays.asList(
                new FieldDefinition("integer", "id", true),
                new FieldDefinition("integer", "age"),
                new FieldDefinition("string", "name"));
        ObjectDefinition myUdoDef = new ObjectDefinition("my_custom_object", fieldDefinitions);

        //When
        UserDefinedObject myUdo = new UserDefinedObject(myUdoDef);
        myUdo.putData("id", 1);
        myUdo.putData("age", 42);
        myUdo.putData("name", "George");
        udoService.saveNewUdo(myUdo);

        //Then
        String query = "SELECT * FROM my_custom_object";
        try {
            Connection connection = dataSource.getConnection();
            ResultSet rs = connection.prepareStatement(query).executeQuery();
            boolean hasNext = rs.next();
            assert (hasNext);
            assertEquals(1, rs.getInt("id"));
            assertEquals(42, rs.getInt("age"));
            assertEquals("George", rs.getString("name"));
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(3)
    void queryById() {
        //Given
        List<FieldDefinition> fieldDefinitions = Arrays.asList(
                new FieldDefinition("integer", "id", true),
                new FieldDefinition("integer", "age"),
                new FieldDefinition("string", "name"));
        ObjectDefinition myUdoDef = new ObjectDefinition("my_custom_object", fieldDefinitions);

        //When
        UserDefinedObject udo = udoService.getUdoById(myUdoDef, 1);

        //Then
        assertNotNull(udo);
        assertEquals(1, udo.getData("id"));
        assertEquals(42, udo.getData("age"));
        assertEquals("George", udo.getData("name"));
        assertNull(udo.getData("$type$"));
    }

    private static void clearTables() {
        try {
            Connection connection = dataSource.getConnection();
            connection.prepareStatement("DROP SCHEMA public CASCADE").execute();
            connection.prepareStatement("CREATE SCHEMA public").execute();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
