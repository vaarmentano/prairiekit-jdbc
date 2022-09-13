package org.varmentano.nocode_plugin.persist.jdbc;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.varmentano.nocode_plugin.domain.UserDefinedObject;
import org.varmentano.nocode_plugin.domain.definition.ObjectDefinition;
import org.varmentano.nocode_plugin.persist.jdbc.mapping.DynamicEntityMapper;
import org.varmentano.nocode_plugin.persist.jdbc.mapping.SessionFactoryMapper;
import org.varmentano.nocode_plugin.service.UdoRepository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.varmentano.nocode_plugin.persist.jdbc.mapping.DynamicEntityMapper.ID_COL_NAME;

public class UdoRepositoryJdbc implements UdoRepository {

    private final DataSource dataSource;
    private final ObjectDefinition udoDef;
    private final SessionFactoryMapper factoryMapper;
    private final DynamicEntityMapper entityMapper;

    public UdoRepositoryJdbc(DataSource dataSource, ObjectDefinition udoDef, SessionFactoryMapper factoryMapper) {
        this.dataSource = dataSource;
        this.udoDef = udoDef;
        this.factoryMapper = factoryMapper;
        this.entityMapper = new DynamicEntityMapper();
    }

    public Optional<UserDefinedObject> findById(int id) {
        return performSessionAction(session -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> objectMap = (Map<String, Object>) session.get(udoDef.name(), id);
            if (objectMap == null) {
                return Optional.empty();
            }
            return Optional.of(entityMapper.mapToUdo(objectMap, udoDef));
        }, udoDef);
    }

    @SuppressWarnings("unchecked")
    public Iterable<UserDefinedObject> findAll() {
        return performSessionAction(session -> session
                .createQuery("from " + udoDef.name(), Object.class)
                .getResultList().stream()
                .map(obj -> (Map<String, Object>) obj)
                .map(map -> entityMapper.mapToUdo(map, udoDef))
                .collect(Collectors.toList()), udoDef);
    }

    @SuppressWarnings("unchecked")
    public UserDefinedObject saveNew(UserDefinedObject myUdo) {
        return performTransaction(session -> {
            Map<String, Object> mapObject = entityMapper.mapToMap(myUdo);
            int newId = (int) session.save(udoDef.name(), mapObject);
            mapObject = (Map<String, Object>) session.get(udoDef.name(), newId);
            return entityMapper.mapToUdo(mapObject, udoDef);
        }, udoDef);
    }

    @SuppressWarnings("unchecked")
    public UserDefinedObject saveUpdate(UserDefinedObject myUdo) {
        return performTransaction(session -> {
            Map<String, Object> mapObject = entityMapper.mapToMap(myUdo);
            session.update(udoDef.name(), mapObject);
            mapObject = (Map<String, Object>) session.get(udoDef.name(), myUdo.getId());
            return entityMapper.mapToUdo(mapObject, udoDef);
        }, udoDef);
    }

    public void deleteById(int id) {
        performTransaction(session -> {
            Map<String, Object> mapObject = new HashMap<>(1);
            mapObject.put(ID_COL_NAME, id);
            session.delete(udoDef.name(), mapObject);
            return null;
        }, udoDef);
    }

    private UserDefinedObject performTransaction(Function<Session, UserDefinedObject> action, ObjectDefinition udoDef) {
        return performSessionAction(session -> {
            Transaction transaction = session.beginTransaction();
            UserDefinedObject udo = action.apply(session);
            transaction.commit();
            return udo;
        }, udoDef);
    }

    private <R> R performSessionAction(Function<Session, R> action, ObjectDefinition udoDef) {
        Map<String, Object> settings = Collections.singletonMap(AvailableSettings.DEFAULT_ENTITY_MODE, EntityMode.MAP.getExternalName());
        SessionFactory sessionFactory = factoryMapper.mapToSessionFactoryBuilder(udoDef, dataSource, settings).build();
        Session session = sessionFactory.openSession();
        R result = action.apply(session);
        session.close();
        sessionFactory.close();
        return result;
    }
}
