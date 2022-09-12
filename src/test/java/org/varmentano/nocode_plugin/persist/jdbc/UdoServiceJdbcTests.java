package org.varmentano.nocode_plugin.persist.jdbc;

import org.junit.jupiter.api.*;
import org.postgresql.ds.PGSimpleDataSource;
import org.varmentano.nocode_plugin.domain.UserDefinedObject;
import org.varmentano.nocode_plugin.domain.definition.FieldDefinition;
import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UdoServiceJdbcTests {
    private static DataSource dataSource;
    private static UdoServiceJdbc udoService;
    private static final ObjectDefinition myUdoDef =
            new ObjectDefinition("my_custom_object", Arrays.asList(
                    new FieldDefinition("integer", "id", true),
                    new FieldDefinition("integer", "age"),
                    new FieldDefinition("string", "name"))
            );

    @BeforeAll
    static void setup() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerNames(new String[]{"localhost"});
        ds.setDatabaseName("nocode_plugin");
        ds.setUser("postgres");
        dataSource = ds;
        clearTables();

        udoService = new UdoServiceJdbc(dataSource);
    }

    @Test
    @Order(1)
    void definitionDeployment() {
        //Given myUdoDef

        //When
        udoService.getDefinitionService().deployDefinition(myUdoDef);

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
        //Given myUdoDef

        //When
        UserDefinedObject myUdo = new UserDefinedObject(myUdoDef);
        myUdo.putData("id", 1);
        myUdo.putData("age", 42);
        myUdo.putData("name", "George");
        myUdo = udoService.saveNew(myUdo);

        //Then
        assertNotNull(myUdo);
        assertEquals(1, myUdo.getData("id"));
        assertEquals(42, myUdo.getData("age"));
        assertEquals("George", myUdo.getData("name"));
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
        //Given myUdoDef

        //When
        Optional<UserDefinedObject> opt = udoService.findById(myUdoDef, 1);

        //Then
        assert (opt.isPresent());
        UserDefinedObject udo = opt.get();
        assertNotNull(udo);
        assertEquals(1, udo.getData("id"));
        assertEquals(42, udo.getData("age"));
        assertEquals("George", udo.getData("name"));
        assertNull(udo.getData("$type$"));
    }

    @Test
    @Order(4)
    void listAll() {
        //Given
        UserDefinedObject myUdo = new UserDefinedObject(myUdoDef);
        myUdo.putData("id", 2);
        myUdo.putData("age", 23);
        myUdo.putData("name", "Roger");
        udoService.saveNew(myUdo);

        //When
        Iterable<UserDefinedObject> udos = udoService.findAll(myUdoDef);
        Iterator<UserDefinedObject> itr = udos.iterator();

        //Then
        UserDefinedObject udo = itr.next();
        assertEquals(1, udo.getData("id"));
        assertEquals(42, udo.getData("age"));
        assertEquals("George", udo.getData("name"));
        udo = itr.next();
        assertEquals(2, udo.getData("id"));
        assertEquals(23, udo.getData("age"));
        assertEquals("Roger", udo.getData("name"));
        assertThrows(NoSuchElementException.class, itr::next);
    }

    @Test
    @Order(5)
    void updateField() {
        //Given myUdoDef
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        UserDefinedObject myUdo = udoService.findById(myUdoDef, 1).get();

        //When
        myUdo.putData("age", 43);
        myUdo = udoService.saveUpdate(myUdo);

        //Then
        assertEquals(43, myUdo.getData("age"));
        String query = "SELECT * FROM my_custom_object WHERE id = 1";
        try {
            Connection connection = dataSource.getConnection();
            ResultSet rs = connection.prepareStatement(query).executeQuery();
            boolean hasNext = rs.next();
            assert (hasNext);
            assertEquals(1, rs.getInt("id"));
            assertEquals(43, rs.getInt("age"));
            assertEquals("George", rs.getString("name"));
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(6)
    void deleteOne() {
        //Given myUdoDef

        //When
        udoService.deleteById(myUdoDef, 2);

        //Then
        Iterator<UserDefinedObject> itr = udoService.findAll(myUdoDef).iterator();
        UserDefinedObject udo = itr.next();
        assertEquals(1, udo.getData("id"));
        assertEquals("George", udo.getData("name"));
        assertThrows(NoSuchElementException.class, itr::next);
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
