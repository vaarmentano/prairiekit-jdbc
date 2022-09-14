package org.varmentano.nocode_plugin.persist.jdbc;

import org.junit.jupiter.api.*;
import org.postgresql.ds.PGSimpleDataSource;
import org.varmentano.nocode_plugin.domain.UserDefinedObject;
import org.varmentano.nocode_plugin.domain.definition.FieldDefinition;
import org.varmentano.nocode_plugin.domain.definition.FieldType;
import org.varmentano.nocode_plugin.domain.definition.UdoDefinition;
import org.varmentano.nocode_plugin.service.UdoRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UdoServiceJdbcTests {
    private static DataSource dataSource;
    private static UdoServiceJdbc udoService;
    private static UdoRepository myUdoRepository;
    private static final UdoDefinition myUdoDef =
            new UdoDefinition("my_custom_object", Arrays.asList(
                    new FieldDefinition(FieldType.INTEGER, "age"),
                    new FieldDefinition(FieldType.TEXT, "name"),
                    new FieldDefinition(FieldType.DATE, "birthday")));

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
        udoService.deployDefinition(myUdoDef);
        myUdoRepository = udoService.getUdoRepository("my_custom_object");

        //Then
        String query = """
                SELECT *\s
                FROM information_schema.tables LEFT JOIN information_schema.columns\s
                ON information_schema.tables.table_name = information_schema.columns.table_name
                WHERE tables.table_schema = 'public' AND tables.table_name = 'my_custom_object'
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
            rs.next();
            assertEquals("birthday", rs.getString("column_name"));
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
        myUdo.putData("age", 42);
        myUdo.putData("name", "George");
        myUdo.putData("birthday", LocalDate.of(2022, Month.JANUARY, 1));
        myUdo = myUdoRepository.saveNew(myUdo);

        //Then
        assertNotNull(myUdo);
        assertEquals(1, myUdo.getId());
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
        Optional<UserDefinedObject> opt = myUdoRepository.findById(1);

        //Then
        assert (opt.isPresent());
        UserDefinedObject udo = opt.get();
        assertNotNull(udo);
        assertEquals(1, udo.getId());
        assertEquals(42, udo.getData("age"));
        assertEquals("George", udo.getData("name"));
        assertEquals(LocalDate.of(2022, Month.JANUARY, 1), udo.getData("birthday"));
        assertNull(udo.getData("$type$"));
    }

    @Test
    @Order(4)
    void listAll() {
        //Given
        UserDefinedObject myUdo = new UserDefinedObject(myUdoDef);
        myUdo.putData("age", 23);
        myUdo.putData("name", "Roger");
        myUdoRepository.saveNew(myUdo);

        //When
        Iterable<UserDefinedObject> udos = myUdoRepository.findAll();
        Iterator<UserDefinedObject> itr = udos.iterator();

        //Then
        UserDefinedObject udo = itr.next();
        assertEquals(1, udo.getId());
        assertEquals(42, udo.getData("age"));
        assertEquals("George", udo.getData("name"));
        udo = itr.next();
        assertEquals(2, udo.getId());
        assertEquals(23, udo.getData("age"));
        assertEquals("Roger", udo.getData("name"));
        assertThrows(NoSuchElementException.class, itr::next);
    }

    @Test
    @Order(5)
    void updateField() {
        //Given myUdoDef
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        UserDefinedObject myUdo = myUdoRepository.findById(1).get();

        //When
        myUdo.putData("age", 43);
        myUdo = myUdoRepository.saveUpdate(myUdo);

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
        myUdoRepository.deleteById(2);

        //Then
        Iterator<UserDefinedObject> itr = myUdoRepository.findAll().iterator();
        UserDefinedObject udo = itr.next();
        assertEquals(1, udo.getId());
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
